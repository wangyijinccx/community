package com.ipeaksoft.moneyday.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.admin.util.CommonUtil;
import com.ipeaksoft.moneyday.admin.util.DateUtil;
import com.ipeaksoft.moneyday.core.entity.AdminUser;
import com.ipeaksoft.moneyday.core.entity.User;
import com.ipeaksoft.moneyday.core.entity.UserValidate;
import com.ipeaksoft.moneyday.core.service.UserService;
import com.ipeaksoft.moneyday.core.service.UserValidateService;

@Controller
@RequestMapping(value = "/WeightAccount")
public class WeightAccountController extends BaseController {

    public final static String    USER_SESSION_KEY = "user_session_key";

    @Autowired
    protected UserService         userService;

    @Autowired
    protected UserValidateService userValidateService;

    /**
     * 分页加载vip账号列表
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getWeightAccountList")
    public Object getWeightAccountList(HttpServletRequest request) {
        int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
        int pageSize = Integer.parseInt(request.getParameter("length"));// 每页记录数

        // 返回
        Map<String, String> map = new HashMap<String, String>();
        try {
            Map<String, Object> where = new HashMap<String, Object>();
            where.put("currentPage", start);
            where.put("pageSize", pageSize);
            List<User> list = userService.pageWeightAccountNew(where);
            int total = userService.weightAccountAmountNew(where);
            JSONObject result = new JSONObject();
            result.put("draw", "");
            result.put("recordsTotal", total);
            result.put("recordsFiltered", total);
            result.put("data", list);
            logger.info("result: " + result);
            return result.toString();
        } catch (Exception e) {
            map.put("code", "1002");
            map.put("message", "数据错误");
            return JSONObject.toJSON(map);
        }
    }

    /**
     * 分页加载检测权重日志列表
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getUserValidateList")
    public Object getUserValidateList(HttpServletRequest request) {
        int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
        int pageSize = Integer.parseInt(request.getParameter("length"));// 每页记录数
        String mobile = request.getParameter("mobile");// 手机号

        // 返回
        JSONObject result = new JSONObject();
        try {
            Map<String, Object> where = new HashMap<String, Object>();
            where.put("currentPage", start);
            where.put("pageSize", pageSize);
            where.put("mobile", mobile);
            List<UserValidate> list = userValidateService.pageUserValidateLog(where);
            int total = userValidateService.userValidateAmount(where);
            result.put("draw", "");
            result.put("recordsTotal", total);
            result.put("recordsFiltered", total);
            result.put("data", list);
            return result;
        } catch (Exception e) {
            result.put("code", "1002");
            result.put("message", "数据错误");
            return result;
        }
    }

    @RequestMapping(value = "/updateUserWeightFlag")
    public String updateUserWeightFlag(Long userId, Integer weightFlag) {

        User user = new User();
        user.setId(userId);
        user.setWeightFlag(weightFlag);
        userService.updateByPrimaryKey(user);
        return "/weightAccount/weight_account_list";
    }

    /**
     * vip账号导出
     * @param request
     * @param response
     * @param userId
     * @param startDate
     * @param endDate 
     */
    @RequestMapping(value = "/export_weight_account", method = RequestMethod.POST)
    public void exportWeightAccount(HttpServletRequest request, HttpServletResponse response) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        OutputStream outputStream = null;
        XSSFSheet sheet = null;
        try {
            List<Map<String, Object>> data = userService.getAllWeightAccount();
            AdminUser admin = this.getUser();
            sheet = workbook.createSheet(admin.getUsername() + "_vip账号汇总");
            // 列头
            XSSFRow row = sheet.createRow(0);
            row.setHeight((short) 300);
            row.createCell(0).setCellValue("一级推广员");
            row.createCell(1).setCellValue("二级推广员");
            row.createCell(2).setCellValue("三级推广员");
            row.createCell(3).setCellValue("微信昵称");
            row.createCell(4).setCellValue("手机号码");
            row.createCell(5).setCellValue("注册时间");
            row.createCell(6).setCellValue("用户级别");
            row.createCell(7).setCellValue("检测次数");
            row.createCell(8).setCellValue("最后一次下载时间");

            // HSSFCell cell = null;
            for (int i = 0; i < data.size(); i++) {
                row = sheet.createRow(i + 1);
                row.setHeight((short) 300);
                row.createCell(0).setCellValue(data.get(i).get("puser1").toString());
                row.createCell(1).setCellValue(data.get(i).get("puser2").toString());
                row.createCell(2).setCellValue(data.get(i).get("puser3").toString());
                row.createCell(3).setCellValue(data.get(i).get("nickname").toString());
                row.createCell(4).setCellValue(data.get(i).get("mobile").toString());
                row.createCell(5).setCellValue(data.get(i).get("createTime").toString());
                String weightFlag = data.get(i).get("weightFlag").toString();
                if (weightFlag.equals("1")) {
                    row.createCell(6).setCellValue("等待检测");
                } else if (weightFlag.equals("2")) {
                    row.createCell(6).setCellValue("vip账号");
                } else {
                    row.createCell(6).setCellValue("普通用户");
                }
                row.createCell(7).setCellValue(data.get(i).get("weightCount").toString());
                Object lastDownloadTime = data.get(i).get("lastDownloadTime");
                row.createCell(8).setCellValue((null == lastDownloadTime) ? "" : lastDownloadTime.toString());
            }

            String filename = "vip账号汇总_" + DateUtil.date2Str(new Date()) + ".xls";// 设置下载时客户端Excel的名称
            filename = CommonUtil.encodeFilename(filename, request);// 处理中文文件名
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;filename=" + filename);

            outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("导出vip账号汇总失败：" + e.getMessage());
        } finally {
            try {
                workbook.close();
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {

            }
        }
    }

    /**
     * vip账号导出
     * @param request
     * @param response
     * @param userId
     * @param startDate
     * @param endDate 
     */
    @RequestMapping(value = "/export_weight_account_info", method = RequestMethod.POST)
    public void exportWeightAccountInfo(HttpServletRequest request, HttpServletResponse response) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        OutputStream outputStream = null;
        XSSFSheet sheet = null;
        try {
            List<Map<String, Object>> data = userService.getAllWeightAccountInfo();
            AdminUser admin = this.getUser();
            sheet = workbook.createSheet();
            workbook.setSheetName(0, admin.getUsername() + "_vip账号明细");//工作簿名称
            XSSFFont font = workbook.createFont();
            font.setColor(XSSFFont.COLOR_NORMAL);
            font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
            XSSFCellStyle cellStyle = workbook.createCellStyle();//创建格式
            cellStyle.setFont(font);
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            //创建第一行标题 
            XSSFRow row = sheet.createRow((short) 0);//第一行标题

            row.setHeight((short) 300);
            row.createCell(0).setCellValue("一级推广员");
            row.createCell(1).setCellValue("二级推广员");
            row.createCell(2).setCellValue("三级推广员");
            row.createCell(3).setCellValue("微信昵称");
            row.createCell(4).setCellValue("手机号码");
            row.createCell(5).setCellValue("省份");
            row.createCell(6).setCellValue("城市");
            row.createCell(7).setCellValue("注册时间");
            row.createCell(8).setCellValue("用户级别");
            row.createCell(9).setCellValue("检测次数");
            row.createCell(10).setCellValue("下载时间");
            row.createCell(11).setCellValue("appid");
            row.createCell(12).setCellValue("应用名称");
            row.createCell(13).setCellValue("检测前排名");
            row.createCell(14).setCellValue("3小时45分后排名");
            row.createCell(15).setCellValue("6小时40分后排名");

            XSSFCell cell = null;
            XSSFCellStyle style = workbook.createCellStyle();//创建格式
            for (int i = 0; i < data.size(); i++) {
                row = sheet.createRow(i + 1);
                row.setHeight((short) 300);
                row.createCell(0).setCellValue(data.get(i).get("puser1").toString());
                row.createCell(1).setCellValue(data.get(i).get("puser2").toString());
                row.createCell(2).setCellValue(data.get(i).get("puser3").toString());
                Object nickname = data.get(i).get("nickname");
                cell = row.createCell(3);
                cell.setCellType(XSSFCell.CELL_TYPE_STRING);
                row.createCell(3).setCellValue((null == nickname) ? "" : nickname.toString());
                cell.setCellStyle(style);
                Object mobile = data.get(i).get("mobile");
                row.createCell(4).setCellValue((null == mobile) ? "" : mobile.toString());
                Object provinceName = data.get(i).get("provinceName");
                row.createCell(5).setCellValue((null == provinceName) ? "" : provinceName.toString());
                Object cityName = data.get(i).get("cityName");
                row.createCell(6).setCellValue((null == cityName) ? "" : cityName.toString());
                Object createTime = data.get(i).get("create_time");
                row.createCell(7).setCellValue((null == createTime) ? "" : createTime.toString());
                Object _weightFlag = data.get(i).get("weight_flag");
                String weightFlag = (null == _weightFlag) ? "" : _weightFlag.toString();
                if (weightFlag.equals("1")) {
                    row.createCell(8).setCellValue("等待检测");
                } else if (weightFlag.equals("2")) {
                    row.createCell(8).setCellValue("vip账号");
                } else {
                    row.createCell(8).setCellValue("普通用户");
                }
                Object weightCount = data.get(i).get("weight_count");
                row.createCell(9).setCellValue((null == weightCount) ? "" : weightCount.toString());
                Object downloadTime = data.get(i).get("download_time");
                row.createCell(10).setCellValue((null == downloadTime) ? "" : downloadTime.toString());
                Object appid = data.get(i).get("app_id");
                row.createCell(11).setCellValue((null == appid) ? "" : appid.toString());
                Object appname = data.get(i).get("appname");
                row.createCell(12).setCellValue((null == appname) ? "" : appname.toString());
                Object rank = data.get(i).get("rank");
                row.createCell(13).setCellValue((null == rank) ? "" : rank.toString());
                Object rankLater = data.get(i).get("rank_later");
                row.createCell(14).setCellValue((null == rankLater) ? "" : rankLater.toString());
                Object rankLater2 = data.get(i).get("rank_later_second");
                row.createCell(15).setCellValue((null == rankLater2) ? "" : rankLater2.toString());
            }

            String filename = "vip账号明细_" + DateUtil.date2Str(new Date()) + ".xlsx";// 设置下载时客户端Excel的名称
            filename = CommonUtil.encodeFilename(filename, request);// 处理中文文件名

            response.setCharacterEncoding("utf-8");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");// // 指定文件的保存类型。
            response.setHeader("Content-disposition", "attachment;filename=" + filename);
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("导出vip账号明细失败：" + e.getMessage());
        } finally {
            try {
                workbook.close();
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {

            }
        }
    }
}
