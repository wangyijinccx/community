package com.ipeaksoft.moneyday.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ipeaksoft.moneyday.admin.util.CommonUtil;
import com.ipeaksoft.moneyday.core.entity.AdminUser;
import com.ipeaksoft.moneyday.core.entity.IdfaApp;
import com.ipeaksoft.moneyday.core.entity.IdfaComp;
import com.ipeaksoft.moneyday.core.service.IdfaAppService;
import com.ipeaksoft.moneyday.core.service.IdfaCompService;

@Controller
public class IdfaCompController extends BaseController {
	@Autowired
	IdfaCompService idfaCompService;
	@Autowired
	IdfaAppService idfaAppService;
	
	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 

	@RequestMapping(value = "/checkidfa")
	public String list(ModelMap map, Principal principal, HttpServletRequest request) {
		return "/checkidfa/index";
	}
	
    @RequestMapping(value = "/checkidfa/export_import", method = RequestMethod.POST)
    public void export_import(HttpServletRequest request, 
    		HttpServletResponse response, 
    		@RequestParam(value = "appid") String appid,
    		@RequestParam(value = "startTime") String startTime,
            @RequestParam(value = "endTime") String endTime) {
    	List<IdfaComp> data = idfaCompService.selectAll();
        AdminUser admin = this.getUser();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(admin.getUsername() + "_提现汇总");

        // 列头
        XSSFRow row = sheet.createRow(0);
        row.setHeight((short) 300);
        row.createCell(0).setCellValue("IDFA");

        for (int i = 0; i < data.size(); i++) {
            row = sheet.createRow(i + 1);
            row.setHeight((short) 300);
            row.createCell(0).setCellValue(data.get(i).getIdfa());
        }

        String filename = "导入表_" + appid + ".xls";// 设置下载时客户端Excel的名称
        filename = CommonUtil.encodeFilename(filename, request);// 处理中文文件名
     // 表示以附件的形式把文件发送到客户端
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);//设定输出文件头
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型 
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
        } catch (Exception e) {
            logger.error("导出失败：" + e.getMessage());
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
    
    @RequestMapping(value = "/checkidfa/export_total", method = RequestMethod.POST)
    public void export_total(HttpServletRequest request, 
    		HttpServletResponse response, 
    		@RequestParam(value = "appid") String appid,
    		@RequestParam(value = "startTime") String startTime,
            @RequestParam(value = "endTime") String endTime) {
    	List<IdfaApp> list = idfaAppService.selectByAppidAndTime(appid, startTime, endTime);
        AdminUser admin = this.getUser();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(admin.getUsername() + "_提现汇总");

        // 列头
        XSSFRow row = sheet.createRow(0);
        row.setHeight((short) 300);
        row.createCell(0).setCellValue("IDFA");
        row.createCell(1).setCellValue("时间");
        row.createCell(2).setCellValue("重复次数");
        row.createCell(3).setCellValue("APPID");

        for (int i = 0; i < list.size(); i++) {
            row = sheet.createRow(i + 1);
            row.setHeight((short) 300);
            IdfaApp record = list.get(i);
            row.createCell(0).setCellValue(record.getIdfa());
            row.createCell(1).setCellValue(sdf.format(record.getCreateTime()));
            row.createCell(2).setCellValue(record.getNumorder());
            row.createCell(3).setCellValue(record.getAppid());
        }

        String filename = "总表_" + appid + ".xls";// 设置下载时客户端Excel的名称
        filename = CommonUtil.encodeFilename(filename, request);// 处理中文文件名
     // 表示以附件的形式把文件发送到客户端
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);//设定输出文件头
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型 
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
        } catch (Exception e) {
            logger.error("导出失败：" + e.getMessage());
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
    
    @RequestMapping(value = "/checkidfa/export_diff", method = RequestMethod.POST)
    public void export_diff(HttpServletRequest request, 
    		HttpServletResponse response, 
    		@RequestParam(value = "appid") String appid,
    		@RequestParam(value = "startTime") String startTime,
            @RequestParam(value = "endTime") String endTime) {
    	List<IdfaApp> data = idfaAppService.selectDiffComp(appid, startTime, endTime);
        AdminUser admin = this.getUser();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(admin.getUsername() + "_提现汇总");

        // 列头
        XSSFRow row = sheet.createRow(0);
        row.setHeight((short) 300);
        row.createCell(0).setCellValue("IDFA");
        row.createCell(1).setCellValue("时间");
        row.createCell(2).setCellValue("重复次数");
        row.createCell(3).setCellValue("APPID");

        for (int i = 0; i < data.size(); i++) {
            row = sheet.createRow(i + 1);
            row.setHeight((short) 300);
            IdfaApp record = data.get(i);
            row.createCell(0).setCellValue(record.getIdfa());
            if (record.getCreateTime()!=null){
                row.createCell(1).setCellValue(sdf.format(record.getCreateTime()));
            }
            if (record.getNumorder()!=null){
                row.createCell(2).setCellValue(record.getNumorder());
            }
            if (record.getAppid()!=null){
                row.createCell(3).setCellValue(record.getAppid());
            }
        }

        String filename = "差异表_" + appid + ".xls";// 设置下载时客户端Excel的名称
        filename = CommonUtil.encodeFilename(filename, request);// 处理中文文件名
     // 表示以附件的形式把文件发送到客户端
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);//设定输出文件头
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型 
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
        } catch (Exception e) {
            logger.error("导出失败：" + e.getMessage());
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
    
    @RequestMapping(value = "/checkidfa/export_join", method = RequestMethod.POST)
    public void export_join(HttpServletRequest request, 
    		HttpServletResponse response, 
    		@RequestParam(value = "appid") String appid,
    		@RequestParam(value = "startTime") String startTime,
            @RequestParam(value = "endTime") String endTime) {
    	List<IdfaApp> data = idfaAppService.selectJoinComp(appid, startTime, endTime);
        AdminUser admin = this.getUser();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(admin.getUsername() + "_提现汇总");

        // 列头
        XSSFRow row = sheet.createRow(0);
        row.setHeight((short) 300);
        row.createCell(0).setCellValue("IDFA");
        row.createCell(1).setCellValue("时间");
        row.createCell(2).setCellValue("重复次数");
        row.createCell(3).setCellValue("APPID");

        for (int i = 0; i < data.size(); i++) {
            row = sheet.createRow(i + 1);
            row.setHeight((short) 300);
            IdfaApp record = data.get(i);
            row.createCell(0).setCellValue(record.getIdfa());

            if (record.getCreateTime()!=null){
                row.createCell(1).setCellValue(sdf.format(record.getCreateTime()));
            }
            if (record.getNumorder()!=null){
                row.createCell(2).setCellValue(record.getNumorder());
            }
            if (record.getAppid()!=null){
                row.createCell(3).setCellValue(record.getAppid());
            }
        }

        String filename = "相同表_" + appid + ".xls";// 设置下载时客户端Excel的名称
        filename = CommonUtil.encodeFilename(filename, request);// 处理中文文件名
     // 表示以附件的形式把文件发送到客户端
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);//设定输出文件头
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型 
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
        } catch (Exception e) {
            logger.error("导出失败：" + e.getMessage());
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
