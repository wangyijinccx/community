package com.ipeaksoft.moneyday.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ipeaksoft.moneyday.admin.util.CommonUtil;
import com.ipeaksoft.moneyday.admin.util.JsonTransfer;
import com.ipeaksoft.moneyday.core.dto.PageResult;
import com.ipeaksoft.moneyday.core.entity.AdminUser;
import com.ipeaksoft.moneyday.core.entity.StatDay;
import com.ipeaksoft.moneyday.core.entity.StatDayTask;
import com.ipeaksoft.moneyday.core.entity.StatMember;
import com.ipeaksoft.moneyday.core.entity.TaskFastCompleteInfo;
import com.ipeaksoft.moneyday.core.entity.UserSurvey;
import com.ipeaksoft.moneyday.core.entity.UserSurveyDetail;
import com.ipeaksoft.moneyday.core.service.SearchService;
import com.ipeaksoft.moneyday.core.service.StatDayService;
import com.ipeaksoft.moneyday.core.service.StatDayTaskService;
import com.ipeaksoft.moneyday.core.service.TaskFastService;

@Controller
@RequestMapping(value = "/search")
public class SearchController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private SearchService       searchService;

    @Autowired
    private StatDayService      statDayService;

    @Autowired
    private StatDayTaskService  statDayTaskService;

    @Autowired
    private TaskFastService     taskFastService;

    /**
     * 用户概览
     * 
     * @param map
     * @param principal
     * @param request
     * @return
     */
    @RequestMapping(value = "/search_user")
    public String Index(ModelMap map, Principal principal, HttpServletRequest request) {
        UserSurvey usr = this.searchService.getUserSurvey("20150111", "20150111");
        map.put("userInfo", usr);
        return "/search/search_user";
    }

    /**
     * 快速任务
     */
    @RequestMapping(value = "/quick_task")
    public String quick_task_index(ModelMap map, Principal principal, HttpServletRequest request) {
        // int count = statDayService.findAllEarnings(1);
        // map.put("income", count);
        return "/search/search_quick_task";
    }

    /**
     * 快速任务列表
     */
    @RequestMapping(value = "/quick_task_list", method = { RequestMethod.GET })
    public String quick_task_list(ModelMap map, Principal principal, HttpServletRequest request) {
        String day = request.getParameter("day");
        map.put("day", day);
        return "/search/search_quick_task_list";
    }

    /**
     * 快速任务详情
     */
    @RequestMapping(value = "/quick_task_list_detail", method = { RequestMethod.GET })
    public String quick_task_list_detail(ModelMap map, Principal principal, HttpServletRequest request) throws Exception {
        String taskId = request.getParameter("taskid");
        String day = request.getParameter("day");
        String taskName = request.getParameter("taskname");
        String comp = request.getParameter("comp");
        map.put("taskid", taskId);
        map.put("day", URLDecoder.decode(day, "utf-8"));
        map.put("taskName", new String(taskName.getBytes("ISO-8859-1"), "UTF-8"));
        map.put("comp", comp);
        return "/search/search_quick_task_list_detail";
    }

    /**
     * 快速任务详情
     */
    @RequestMapping(value = "/general_task")
    public String general_task_index(ModelMap map, Principal principal, HttpServletRequest request) {
        // int count = statDayService.findAllEarnings(2);
        // map.put("income", count);
        return "/search/search_general_task";
    }

    /**
     * 快速任务详情
     */
    @RequestMapping(value = "/general_task_list")
    public String general_task_list(ModelMap map, Principal principal, HttpServletRequest request) {
        return "/search/search_general_task_detail";
    }

    /**
     * 快速任务详情
     */
    @RequestMapping(value = "/member_list")
    public String member_list(ModelMap map, Principal principal, HttpServletRequest request) {
        return "/search/search_member_list";
    }

    /**
     * 单日任务
     */
    @RequestMapping(value = "/member_day_task")
    public String member_day_task(ModelMap map, Principal principal, HttpServletRequest request) {
        return "/search/search_member_task_day";
    }

    /**
     * 用户收益
     */
    @RequestMapping(value = "/member_earnings")
    public String member_earnings(ModelMap map, Principal principal, HttpServletRequest request) {

        return "/search/search_member_earnings";
    }

    /**
     * 用户收益
     */
    @RequestMapping(value = "/member_detail")
    public String member_detail(ModelMap map, Principal principal, HttpServletRequest request) {
        return "/search/search_member_detail";
    }

    /**
     * 用户概览列表
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/load_users_page")
    public String GetPageList(HttpServletRequest request, ModelMap map) {
        int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
        int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
        String start_date = request.getParameter("start_date");
        String end_date = request.getParameter("end_date");

        HashMap<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("currentPage", start);
        parameterMap.put("pageSize", pageSize);
        parameterMap.put("startDate", start_date == "" ? null : start_date);
        parameterMap.put("endDate", end_date == "" ? null : end_date);
        List<UserSurveyDetail> list = searchService.findByWhere(parameterMap);
        int total = searchService.findByWhereCount(parameterMap);
        String rs = JsonTransfer.getJsonFromList("", list);

        String result = "{\"draw\":" + "\"\"" + ",\"recordsTotal\":" + pageSize + ",\"recordsFiltered\":" + total + ",\"data\":" + rs + "}";
        System.out.println("json is:" + JsonTransfer.getJsonFromList("", list));
        // System.out.println("result is:"+result);
        // String result=JsonTransfer.getJsonFromList(sEcho,list);
        return result;

    }

    /**
     * 快速任务
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/quicksearch_list")
    public String QuickSearch_list(HttpServletRequest request) {
        int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
        int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
        String sEcho = request.getParameter("draw");// 搜索内容
        String startDate = request.getParameter("start_date");
        String endDate = request.getParameter("end_date");

        Map<String, Object> where = new HashMap<String, Object>();
        where.put("currentPage", start);
        where.put("pageSize", pageSize);
        where.put("startDate", startDate.equals("") ? null : startDate);
        where.put("endDate", endDate.equals("") ? null : endDate);
        where.put("taskType", "1");
        List<StatDay> items = statDayService.findPageList(where);

        // List<TaskFastStatistics>items =
        // searchService.findFastTaskStatistics(startDate.equals("")?null:startDate,
        // endDate.equals("")?null:endDate,start,pageSize);
        int total = statDayService.findPageListCount(where);
        String strlist = JsonTransfer.getJsonFromList("", items);

        String result = "{\"draw\":" + sEcho + ",\"recordsTotal\":" + pageSize + ",\"recordsFiltered\":" + total + ",\"data\":" + strlist + "}";
        return result;

    }

    /**
     * 快速任务列表
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/quick_task_list", method = { RequestMethod.POST })
    public String QuickSearch_list2(HttpServletRequest request) throws Exception {
        try {
            String day = request.getParameter("day");
            int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
            int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
            String sEcho = request.getParameter("draw");// 搜索内容
            Map<String, Object> where = new HashMap<String, Object>();
            where.put("day", day);
            where.put("currentPage", start);
            where.put("pageSize", pageSize);
            List<StatDayTask> items = statDayTaskService.findPageList(where);
            int total = statDayTaskService.findPageListCount(where);
            String rs = JsonTransfer.getJsonFromList("", items);

            String result = "{\"draw\":" + sEcho + ",\"recordsTotal\":" + total + ",\"recordsFiltered\":" + total + ",\"data\":" + rs.toString() + "}";
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }

    }

    /**
     * 快速任务详情
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/quick_task_list_detail", method = { RequestMethod.POST })
    public String QuickSearch_list3(HttpServletRequest request) {
        int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
        int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
        String sEcho = request.getParameter("draw");// 搜索内容
        String taskId = request.getParameter("taskid");// 获取任务ID
        String day = request.getParameter("day");

        Map<String, Object> where = new HashMap<String, Object>();
        where.put("taskId", taskId);
        where.put("day", day);
        where.put("currentPage", start);
        where.put("pageSize", pageSize);
        List<TaskFastCompleteInfo> items = taskFastService.findPageListTaskCompleteInfo(where);
        int total = taskFastService.findPageListTaskCompleteInfoCount(where);
        String rs = JsonTransfer.getJsonFromList("", items);
        String result = "{\"draw\":" + sEcho + ",\"recordsTotal\":" + total + ",\"recordsFiltered\":" + total + ",\"data\":" + rs.toString() + "}";
        return result;

    }

    /**
     * 导出
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/export_task_list", method = { RequestMethod.GET })
    public ModelAndView export_task_list(ModelMap model, HttpServletRequest request) {
        // int start= Integer.parseInt(request.getParameter("start")) ;//开始记录数
        // int pageSize = Integer.parseInt(request.getParameter("length"));//美页记录数
        // String sEcho = request.getParameter("draw");//搜索内容
        String taskId = request.getParameter("taskid");// 获取任务ID
        String day = request.getParameter("day");
        String taskname = request.getParameter("taskname");
        try {
            taskname = new String(taskname.getBytes("ISO8859_1"), "utf8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Map<String, Object> where = new HashMap<String, Object>();
        where.put("taskId", taskId);
        where.put("day", day);
        // where.put("currentPage", start);
        // where.put("pageSize", pageSize);
        List<TaskFastCompleteInfo> items = taskFastService.findPageListTaskCompleteInfo(where);
        ViewExcel viewExcel = new ViewExcel();
        model.put("list", items);
        model.put("taskid", taskId);
        model.put("taskname", taskname);
        return new ModelAndView(viewExcel, model);
    }

    /**
     * 快速任务详情
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/data_general_task_list")
    public String data_General_task_list(HttpServletRequest request) {
        int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
        int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
        String sEcho = request.getParameter("draw");// 搜索内容
        String startDate = request.getParameter("start_date");
        String endDate = request.getParameter("end_date");

        Map<String, Object> where = new HashMap<String, Object>();
        where.put("currentPage", start);
        where.put("pageSize", pageSize);
        where.put("startDate", startDate.equals("") ? null : startDate);
        where.put("endDate", endDate.equals("") ? null : endDate);
        where.put("taskType", "2");
        List<StatDay> items = statDayService.findPageList(where);

        // List<TaskFastStatistics>items =
        // searchService.findFastTaskStatistics(startDate.equals("")?null:startDate,
        // endDate.equals("")?null:endDate,start,pageSize);
        int total = statDayService.findPageListCount(where);
        String strlist = JsonTransfer.getJsonFromList("", items);

        String result = "{\"draw\":" + sEcho + ",\"recordsTotal\":" + pageSize + ",\"recordsFiltered\":" + total + ",\"data\":" + strlist + "}";
        return result;
    }

    /**
     * 快速任务详情
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/data_general_task_list2")
    public String data_General_task_list2(HttpServletRequest request) {
        int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
        int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
        String sEcho = request.getParameter("draw");// 搜索内容

        int limit = start + pageSize;
        int total = 100;
        if (limit >= total - pageSize) {
            limit = total;
        }
        StringBuilder rs = new StringBuilder();
        for (int i = start; i < limit; i++) {
            if (!rs.toString().equals("")) {
                rs.append(",");
            }
            rs.append("{\"pkid\":\"" + i + 1 + "\",\"rq\":\"2014-12-19\",\"ksrwwccs\":\"" + i + 2 + "\",\"cyrs\":\"" + i + 3 + "\",\"xsyh\":\"" + i + 4 + "\",\"xssy\":\"" + i + 5 + "\",\"xxyh\":\""
                    + i + 6 + "\",\"xxsy\":\"" + i + 7 + "\",\"drzsy\":\"" + i + 8 + "\"}");
        }

        String result = "{\"draw\":" + sEcho + ",\"recordsTotal\":" + pageSize + ",\"recordsFiltered\":" + total + ",\"data\":[" + rs.toString() + "]}";
        return result;

    }

    /**
     * 成员查询
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/data_member_list")
    public String data_member_list(HttpServletRequest request) {
        int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
        int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
        String sEcho = request.getParameter("draw");// 搜索内容
        String startDate = request.getParameter("start_date");
        String endDate = request.getParameter("end_date");
        Map<String, Object> where = new HashMap<String, Object>();

        where.put("currentPage", start);
        where.put("pageSize", pageSize);
        where.put("startDate", startDate.equals("") ? null : startDate);
        where.put("endDate", endDate.equals("") ? null : endDate);
        List<StatMember> item = searchService.findPageList(where);

        int total = searchService.findPageListCount(where);
        String rs = JsonTransfer.getJsonFromList("", item);
        String result = "{\"draw\":" + sEcho + ",\"recordsTotal\":" + pageSize + ",\"recordsFiltered\":" + total + ",\"data\":" + rs + "}";
        return result;

    }

    /**
     * 成员查询
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/data_member_day_task")
    public String data_member_day_task(HttpServletRequest request) {
        int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
        int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
        String sEcho = request.getParameter("draw");// 搜索内容

        int limit = start + pageSize;
        int total = 100;
        if (limit >= total - pageSize) {
            limit = total;
        }
        StringBuilder rs = new StringBuilder();
        for (int i = start; i < limit; i++) {
            if (!rs.toString().equals("")) {
                rs.append(",");
            }
            rs.append("{\"pkid\":\"" + i + 1 + "\",\"rq\":\"2014-12-19\",\"ksrwwccs\":\"" + i + 2 + "\",\"cyrs\":\"" + i + 3 + "\",\"xsyh\":\"" + i + 4 + "\",\"xssy\":\"" + i + 5 + "\",\"xxyh\":\""
                    + i + 6 + "\",\"xxsy\":\"" + i + 7 + "\",\"drzsy\":\"" + i + 8 + "\"}");
        }

        String result = "{\"draw\":" + sEcho + ",\"recordsTotal\":" + pageSize + ",\"recordsFiltered\":" + total + ",\"data\":[" + rs.toString() + "]}";
        return result;

    }

    /**
     * 用户收益
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/data_member_earnings")
    public String data_member_earnings(HttpServletRequest request) {
        int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
        int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
        String sEcho = request.getParameter("draw");// 搜索内容

        int limit = start + pageSize;
        int total = 100;
        if (limit >= total - pageSize) {
            limit = total;
        }
        StringBuilder rs = new StringBuilder();
        for (int i = start; i < limit; i++) {
            if (!rs.toString().equals("")) {
                rs.append(",");
            }
            rs.append("{\"pkid\":\"" + i + 1 + "\",\"rq\":\"2014-12-19\",\"ksrwwccs\":\"" + i + 2 + "\",\"cyrs\":\"" + i + 3 + "\",\"xsyh\":\"" + i + 4 + "\",\"xssy\":\"" + i + 5 + "\",\"xxyh\":\""
                    + i + 6 + "\",\"xxsy\":\"" + i + 7 + "\",\"drzsy\":\"" + i + 8 + "\"}");
        }

        String result = "{\"draw\":" + sEcho + ",\"recordsTotal\":" + pageSize + ",\"recordsFiltered\":" + total + ",\"data\":[" + rs.toString() + "]}";
        return result;

    }

    /**
     * 收益详细
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/data_get_money_detail")
    public String data_get_money_detail(HttpServletRequest request) {
        int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
        int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
        String sEcho = request.getParameter("draw");// 搜索内容
        String order = request.getParameter("order[0][column]");
        int limit = start + pageSize;
        int total = 100;
        if (limit >= total - pageSize) {
            limit = total;
        }
        StringBuilder rs = new StringBuilder();
        for (int i = start; i < limit; i++) {
            if (!rs.toString().equals("")) {
                rs.append(",");
            }
            rs.append("{\"pkid\":\"" + i + 1 + "\",\"date\":\"2014-12-19\",\"newuser\":\"" + i + 2 + "\",\"visitor\":\"" + i + 3 + "\",\"alluser\":\"" + i + 4 + "\",\"visitortouser\":\"" + i + 5
                    + "\",\"pecent\":\"" + i + 6 + "\",\"ptzgj\":\"" + i + 7 + "\",\"ptzgjpecent\":\"" + i + 8 + "\",\"loginactive\":\"" + i + 9 + "\",\"downloadactive\":\"" + i + 10 + "\"}");
        }

        String result = "{\"draw\":" + sEcho + ",\"recordsTotal\":" + pageSize + ",\"recordsFiltered\":" + total + ",\"data\":[" + rs.toString() + "]}";
        return result;

    }

    /**
     * 兑换详细
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/data_get_exchange")
    public String data_get_exchange(HttpServletRequest request) {
        int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
        int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
        String sEcho = request.getParameter("draw");// 搜索内容
        String order = request.getParameter("order[0][column]");
        int limit = start + pageSize;
        int total = 100;
        if (limit >= total - pageSize) {
            limit = total;
        }
        StringBuilder rs = new StringBuilder();
        for (int i = start; i < limit; i++) {
            if (!rs.toString().equals("")) {
                rs.append(",");
            }
            rs.append("{\"pkid\":\"" + i + 1 + "\",\"date\":\"2014-12-19\",\"newuser\":\"" + i + 2 + "\",\"visitor\":\"" + i + 3 + "\",\"alluser\":\"" + i + 4 + "\",\"visitortouser\":\"" + i + 5
                    + "\",\"pecent\":\"" + i + 6 + "\",\"ptzgj\":\"" + i + 7 + "\",\"ptzgjpecent\":\"" + i + 8 + "\",\"loginactive\":\"" + i + 9 + "\",\"downloadactive\":\"" + i + 10 + "\"}");
        }

        String result = "{\"draw\":" + sEcho + ",\"recordsTotal\":" + pageSize + ",\"recordsFiltered\":" + total + ",\"data\":[" + rs.toString() + "]}";
        return result;

    }

    /**
     * 获取快速任务和常规任务的总收益
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findAllEarnings")
    public int findAllEarnings(HttpServletRequest request) {
        int total = 0;
        String type = request.getParameter("type");
        if (type != null && !type.equals("")) {
            total = statDayService.findAllEarnings(Integer.parseInt(type));
        }
        return total;
    }

    /**
     * 推广员业绩查询Page
     */
    @RequestMapping(value = "/search_performance")
    public String searchPerformance(ModelMap map, Principal principal, HttpServletRequest request) {
        AdminUser adminUser = this.getUser();
        map.put("adminUser", adminUser);
        return "/search/search_performance";
    }

    /**
     * 推广员业绩查询Data
     * 
     * @author qianqian
     * @date 2015-03-03
     * @param request
     * @param userId
     * @param startDate
     * @param endData
     * @param start
     * @param length
     * @param draw
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/load_performance", method = RequestMethod.POST)
    public String loadPerformance(HttpServletRequest request, @RequestParam(value = "user_id") int userId, @RequestParam(value = "start_date") String startDate,
            @RequestParam(value = "end_data") String endData, @RequestParam(value = "start") int start, @RequestParam(value = "length") int length, @RequestParam(value = "draw") String draw) {
        PageResult result = searchService.getPerformanceData(userId, start, length, startDate, endData);
        result.setDraw(draw);
        result.setRecordsTotal(length);

        String resultStr = JsonTransfer.getJsonFromBean(result);
        logger.info("业绩查询结果：" + resultStr);
        return resultStr;
    }

    /**
     * 推广员提现汇总导出
     * 
     * @author qianqian
     * @date 2015-03-03
     * @param request
     * @param userId
     * @param startDate
     * @param endData
     * @param start
     * @param length
     * @param draw
     * @return
     */
    @RequestMapping(value = "/export_performance", method = RequestMethod.POST)
    public void exportPerformance(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "user_id") int userId, @RequestParam(value = "start_date") String startDate,
            @RequestParam(value = "end_date") String endData) {
        PageResult result = searchService.getPerformanceData(userId, startDate, endData);

        List<Map<String, Object>> data = result.getData();
        AdminUser admin = this.getUser();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(admin.getUsername() + "_提现汇总");

        CellStyle cellStyle = null; // 小数样式

        // 列头
        XSSFRow row = sheet.createRow(0);
        row.setHeight((short) 300);
        row.createCell(0).setCellValue("一级推广员");
        row.createCell(1).setCellValue("二级推广员");
        row.createCell(2).setCellValue("三级推广员");
        row.createCell(3).setCellValue("下载金额");
        row.createCell(4).setCellValue("坏账");
        if (admin.getLevel() < 1) {
            row.createCell(5).setCellValue("提现金额");
            cellStyle = workbook.createCellStyle();
            cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        }

        XSSFCell cell = null;

        for (int i = 0; i < data.size(); i++) {
            row = sheet.createRow(i + 1);
            row.setHeight((short) 300);
            row.createCell(0).setCellValue(data.get(i).get("user_level_1").toString());
            row.createCell(1).setCellValue(data.get(i).get("user_level_2").toString());
            row.createCell(2).setCellValue(data.get(i).get("user_level_3").toString());
            cell = row.createCell(3); // 下载金额
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(Integer.valueOf(data.get(i).get("download_money").toString()));
            cell = row.createCell(4); // 坏账
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(Integer.valueOf(data.get(i).get("bad_debt").toString()));
            if (admin.getLevel() < 1) {
                cell = row.createCell(5);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(Double.valueOf(data.get(i).get("withdraw_deposit").toString()));
            }
        }

        String filename = "提现汇总查询_" + endData + ".xls";// 设置下载时客户端Excel的名称
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
            logger.error("导出提现汇总查询失败：" + e.getMessage());
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
     * 推广员任务查询明细导出
     * 
     * @author qianqian
     * @date 2015-03-03
     * @param request
     * @param userId
     * @param startDate
     * @param endData
     * @param start
     * @param length
     * @param draw
     * @return
     */
    @RequestMapping(value = "/export_performance_item", method = RequestMethod.POST)
    public void exportPerformanceItem(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "user_id") int userId, @RequestParam(value = "start_date") String startDate,
            @RequestParam(value = "end_date") String endDate) {
        List<Map<String, Object>> data = searchService.getPerformanceItemData(userId, startDate, endDate);

        AdminUser admin = this.getUser();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(admin.getUsername() + "_任务明细");

        // 列头
        XSSFRow row = sheet.createRow(0);
        row.setHeight((short) 300);
        row.createCell(0).setCellValue("下载日期");
        row.createCell(1).setCellValue("一级推广员");
        row.createCell(2).setCellValue("二级推广员");
        row.createCell(3).setCellValue("三级推广员");
        row.createCell(4).setCellValue("微信昵称");
        row.createCell(5).setCellValue("手机号码");
        row.createCell(6).setCellValue("IDFA");
        row.createCell(7).setCellValue("注册时间");
        row.createCell(8).setCellValue("用户类型");
        row.createCell(9).setCellValue("用户状态");
        row.createCell(10).setCellValue("任务名称");
        row.createCell(11).setCellValue("任务金额");
        row.createCell(12).setCellValue("任务来源");
        row.createCell(13).setCellValue("IP");
        row.createCell(14).setCellValue("国家");
        row.createCell(15).setCellValue("地区");
        row.createCell(16).setCellValue("省份");
        row.createCell(17).setCellValue("城市");
        row.createCell(18).setCellValue("区县");
        row.createCell(19).setCellValue("isp");

        XSSFCell cell = null;
        for (int i = 0; i < data.size(); i++) {
            row = sheet.createRow(i + 1);
            row.setHeight((short) 300);
            row.createCell(0).setCellValue(data.get(i).get("download_date").toString());
            row.createCell(1).setCellValue(data.get(i).get("user_level_1").toString());
            row.createCell(2).setCellValue(data.get(i).get("user_level_2").toString());
            row.createCell(3).setCellValue(data.get(i).get("user_level_3").toString());
            row.createCell(4).setCellValue(data.get(i).get("nickname").toString());
            row.createCell(5).setCellValue(data.get(i).get("mobile").toString());
            row.createCell(6).setCellValue(data.get(i).get("idfa").toString());
            row.createCell(7).setCellValue(data.get(i).get("register_date").toString());
            row.createCell(8).setCellValue(data.get(i).get("type").toString());
            row.createCell(9).setCellValue(data.get(i).get("status").toString());
            row.createCell(10).setCellValue(data.get(i).get("taskname").toString());
            cell = row.createCell(11);
            Double _award = Double.valueOf(data.get(i).get("award").toString()) / 100;
            String award = _award.toString().concat("元");
            cell.setCellValue(award);
            row.createCell(12).setCellValue(data.get(i).get("task_source").toString());
            row.createCell(13).setCellValue(data.get(i).get("clientip")!=null?data.get(i).get("clientip").toString():"");
            row.createCell(14).setCellValue(data.get(i).get("country")!=null?data.get(i).get("country").toString():"");
            row.createCell(15).setCellValue(data.get(i).get("area")!=null?data.get(i).get("area").toString():"");
            row.createCell(16).setCellValue(data.get(i).get("province")!=null?data.get(i).get("province").toString():"");
            row.createCell(17).setCellValue(data.get(i).get("city")!=null?data.get(i).get("city").toString():"");
            row.createCell(18).setCellValue(data.get(i).get("county")!=null?data.get(i).get("county").toString():"");
            row.createCell(19).setCellValue(data.get(i).get("isp")!=null?data.get(i).get("isp").toString():"");
        }

        String filename = "任务明细查询_" + endDate + ".xls";// 设置下载时客户端Excel的名称
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
            logger.error("导出任务明细查询失败：" + e.getMessage());
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
     * 推广员提现明细导出
     * 
     * @param request
     * @param response
     * @param userId
     * @param startDate
     * @param endData
     * @return
     */
    @RequestMapping(value = "/export_performance_billItem", method = RequestMethod.POST)
    public void exportPerformanceBillItem(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "user_id") int userId, @RequestParam(value = "start_date") String startDate,
            @RequestParam(value = "end_date") String endDate) {
        List<Map<String, Object>> data = searchService.getPerformanceBillItemData(userId, startDate, endDate);

        AdminUser admin = this.getUser();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(admin.getUsername() + "_提现明细");

        // 列头
        XSSFRow row = sheet.createRow(0);
        row.setHeight((short) 300);
        row.createCell(0).setCellValue("申请时间");
        row.createCell(1).setCellValue("一级推广员");
        row.createCell(2).setCellValue("二级推广员");
        row.createCell(3).setCellValue("三级推广员");
        row.createCell(4).setCellValue("微信昵称");
        row.createCell(5).setCellValue("手机号码");
        row.createCell(6).setCellValue("IDFA");
        row.createCell(7).setCellValue("注册时间");
        row.createCell(8).setCellValue("用户类型");
        row.createCell(9).setCellValue("用户状态");
        row.createCell(10).setCellValue("提现金额");
        row.createCell(11).setCellValue("状态");

        XSSFCell cell = null;
        for (int i = 0; i < data.size(); i++) {
            row = sheet.createRow(i + 1);
            row.setHeight((short) 300);
            row.createCell(0).setCellValue(data.get(i).get("apply_date").toString());
            row.createCell(1).setCellValue(data.get(i).get("user_level_1").toString());
            row.createCell(2).setCellValue(data.get(i).get("user_level_2").toString());
            row.createCell(3).setCellValue(data.get(i).get("user_level_3").toString());
            row.createCell(4).setCellValue(data.get(i).get("nickname").toString());
            row.createCell(5).setCellValue(data.get(i).get("mobile").toString());
            row.createCell(6).setCellValue(data.get(i).get("idfa").toString());
            row.createCell(7).setCellValue(data.get(i).get("register_date").toString());
            row.createCell(8).setCellValue(data.get(i).get("type").toString());
            row.createCell(9).setCellValue(data.get(i).get("status").toString());
            cell = row.createCell(10);
            Double _award = Double.valueOf(data.get(i).get("amount").toString()) / 100;
            String award = _award.toString().concat("元");
            cell.setCellValue(award);
            row.createCell(11).setCellValue("充值成功");
        }

        String filename = "提现明细查询_" + endDate + ".xls";// 设置下载时客户端Excel的名称
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
            logger.error("导出提现明细查询失败：" + e.getMessage());
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
     * 推广员余额明细导出
     * 
     * @param request
     * @param response
     * @param userId
     * @param startDate
     * @param endDate 
     */
    @RequestMapping(value = "/export_performance_moneyItem", method = RequestMethod.POST)
    public void exportPerformanceMoneyItem(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "user_id") int userId, @RequestParam(value = "start_date") String startDate,
            @RequestParam(value = "end_date") String endDate) {
        List<Map<String, Object>> data = searchService.getPerformanceMoneyData(userId, startDate, endDate);

        AdminUser admin = this.getUser();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(admin.getUsername() + "_余额明细_" + startDate + "~" + endDate);

        // 列头
        XSSFRow row = sheet.createRow(0);

        //        HSSFWorkbook workbook = new HSSFWorkbook();
        //        HSSFSheet sheet = workbook.createSheet(admin.getUsername() + "_余额明细_"+startDate+"~"+endDate);
        //        HSSFRow row = sheet.createRow(0);

        row.setHeight((short) 300);
        row.createCell(0).setCellValue("一级推广员");
        row.createCell(1).setCellValue("二级推广员");
        row.createCell(2).setCellValue("三级推广员");
        row.createCell(3).setCellValue("微信昵称");
        row.createCell(4).setCellValue("手机号码");
        row.createCell(5).setCellValue("IDFA");
        row.createCell(6).setCellValue("注册时间");
        row.createCell(7).setCellValue("用户类型");
        row.createCell(8).setCellValue("用户状态");
        row.createCell(9).setCellValue("余额");

        XSSFCell cell = null;
        //        HSSFCell cell = null;
        for (int i = 0; i < data.size(); i++) {
            row = sheet.createRow(i + 1);
            row.setHeight((short) 300);
            row.createCell(0).setCellValue(data.get(i).get("user_level_1").toString());
            row.createCell(1).setCellValue(data.get(i).get("user_level_2").toString());
            row.createCell(2).setCellValue(data.get(i).get("user_level_3").toString());
            row.createCell(3).setCellValue(data.get(i).get("nickname").toString());
            row.createCell(4).setCellValue(data.get(i).get("mobile").toString());
            row.createCell(5).setCellValue(data.get(i).get("idfa").toString());
            row.createCell(6).setCellValue(data.get(i).get("register_date").toString());
            row.createCell(7).setCellValue(data.get(i).get("type").toString());
            row.createCell(8).setCellValue(data.get(i).get("status").toString());
            row.createCell(9).setCellValue(data.get(i).get("award").toString());
        }

        String filename = "余额明细查询_" + endDate + ".xls";// 设置下载时客户端Excel的名称
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
            logger.error("导出余额明细查询失败：" + e.getMessage());
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

    @RequestMapping(value = "/export_performance_channelItem", method = RequestMethod.POST)
    public void exportPerformanceChannelItem(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "user_id") int userId,
            @RequestParam(value = "start_date") String startDate, @RequestParam(value = "end_date") String endDate) {
        List<Map<String, Object>> data = searchService.getPerformanceChannelData(userId, startDate, endDate);

        AdminUser admin = this.getUser();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(admin.getUsername() + "_广告主明细_" + startDate + "~" + endDate);

        // 列头
        XSSFRow row = sheet.createRow(0);
        row.setHeight((short) 300);
        row.createCell(0).setCellValue("下载日期");
        row.createCell(1).setCellValue("一级推广员");
        row.createCell(2).setCellValue("二级推广员");
        row.createCell(3).setCellValue("三级推广员");
        row.createCell(4).setCellValue("微信昵称");
        row.createCell(5).setCellValue("手机号码");
        row.createCell(6).setCellValue("IDFA");
        row.createCell(7).setCellValue("注册时间");
        row.createCell(8).setCellValue("用户类型");
        row.createCell(9).setCellValue("用户状态");
        row.createCell(10).setCellValue("任务名称");
        row.createCell(11).setCellValue("任务金额");
        row.createCell(12).setCellValue("任务来源");

        XSSFCell cell = null;
        try {
			for (int i = 0; i < data.size(); i++) {
			    row = sheet.createRow(i + 1);
			    row.setHeight((short) 300);
			    row.createCell(0).setCellValue(data.get(i).get("download_date").toString());
			    row.createCell(1).setCellValue(data.get(i).get("user_level_1").toString());
			    row.createCell(2).setCellValue(data.get(i).get("user_level_2").toString());
			    row.createCell(3).setCellValue(data.get(i).get("user_level_3").toString());
			    row.createCell(4).setCellValue(data.get(i).get("nickname").toString());
			    row.createCell(5).setCellValue(data.get(i).get("mobile").toString());
			    row.createCell(6).setCellValue(data.get(i).get("idfa").toString());
			    row.createCell(7).setCellValue(data.get(i).get("register_date").toString());
			    row.createCell(8).setCellValue(data.get(i).get("type").toString());
			    row.createCell(9).setCellValue(data.get(i).get("status").toString());
			    row.createCell(10).setCellValue(data.get(i).get("taskname").toString());
			    cell = row.createCell(11);
			    Double _award = Double.valueOf(data.get(i).get("award").toString()) / 100;
			    String award = _award.toString().concat("元");
			    cell.setCellValue(award);
			    row.createCell(12).setCellValue(data.get(i).get("task_source").toString());
			}
		} catch (NumberFormatException e1) {
			logger.error("ERROR:", e1);
		}

        String filename = "广告主明细查询_" + endDate + ".xls";// 设置下载时客户端Excel的名称
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
            logger.error("导出广告主明细查询失败：" + e.getMessage());
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

    @RequestMapping(value = "/export_performance_channel", method = RequestMethod.POST)
    public void exportPerformanceChannel(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "user_id") int userId, @RequestParam(value = "start_date") String startDate,
            @RequestParam(value = "end_date") String endDate) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = format.parse(endDate);
		} catch (ParseException e1) {
			date = new Date();
		}		
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date); 
		calendar.add(Calendar.DATE, 1);
		date = calendar.getTime();
		endDate = format.format(date);

        List<Map<String, Object>> data = searchService.getPerformanceChannel(startDate, endDate);

        AdminUser admin = this.getUser();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(admin.getUsername() + "_渠道明细_" + startDate + "~" + endDate);

        // 列头
        XSSFRow row = sheet.createRow(0);
        row.setHeight((short) 300);
        row.createCell(0).setCellValue("激活日期");
        row.createCell(1).setCellValue("广告ID");
        row.createCell(2).setCellValue("IDFA");
        row.createCell(3).setCellValue("appid");
        row.createCell(4).setCellValue("任务名称");
        row.createCell(5).setCellValue("任务金额");
        row.createCell(6).setCellValue("任务来源");
        row.createCell(7).setCellValue("渠道名称");

        XSSFCell cell = null;
        for (int i = 0; i < data.size(); i++) {
            row = sheet.createRow(i + 1);
            row.setHeight((short) 300);
            row.createCell(0).setCellValue(data.get(i).get("create_time").toString());
            row.createCell(1).setCellValue(data.get(i).get("task_id").toString());
            row.createCell(2).setCellValue(data.get(i).get("idfa").toString());
            row.createCell(3).setCellValue(data.get(i).get("appid").toString());
            row.createCell(4).setCellValue(data.get(i).get("taskname").toString());
            cell = row.createCell(5);
            Double _award = Double.valueOf(data.get(i).get("award").toString()) / 100;
            String award = _award.toString().concat("元");
            cell.setCellValue(award);
            row.createCell(6).setCellValue(data.get(i).get("task_source").toString());
            if (data.get(i).get("channel_name") != null){
                row.createCell(7).setCellValue(data.get(i).get("channel_name").toString());
            }
        }

        String filename = "渠道明细查询_" + endDate + ".xls";// 设置下载时客户端Excel的名称
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
            logger.error("导出渠道明细查询失败：" + e.getMessage());
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

    @RequestMapping(value = "/export_performance_channel_click", method = RequestMethod.POST)
    public void exportPerformanceChannelClick(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "user_id") int userId,
            @RequestParam(value = "start_date") String startDate, @RequestParam(value = "end_date") String endDate) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = format.parse(endDate);
		} catch (ParseException e1) {
			date = new Date();
		}		
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date); 
		calendar.add(Calendar.DATE, 1);
		date = calendar.getTime();
		endDate = format.format(date);
		
        AdminUser admin = this.getUser();
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
//        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(admin.getUsername() + "_渠道点击_" + startDate + "~" + endDate);
        OutputStream outputStream = null;
		try {
	        long total = Runtime.getRuntime().totalMemory(); // byte
	        long m1 = Runtime.getRuntime().freeMemory();
	        logger.debug("total: {}, free:{}, use:{}" ,total/1024, m1/1024, (total - m1)/1024);
	        
			logger.debug("start query");
			List<Map<String, Object>> data = searchService.getPerformanceChannelClick(startDate, endDate);
			logger.debug("complete query");
	        m1 = Runtime.getRuntime().freeMemory();
	        logger.debug("total: {}, free:{}, use:{}" ,total/1024, m1/1024, (total - m1)/1024);
//	        XSSFWorkbook workbook = new XSSFWorkbook();
//	        XSSFSheet sheet = workbook.createSheet(admin.getUsername() + "_渠道点击_" + startDate + "~" + endDate);

	        // 列头
	        Row row = sheet.createRow(0);
	        row.setHeight((short) 300);
	        row.createCell(0).setCellValue("点击日期");
	        row.createCell(1).setCellValue("广告ID");
	        row.createCell(2).setCellValue("IDFA");
	        row.createCell(3).setCellValue("appid");
	        row.createCell(4).setCellValue("任务名称");
	        row.createCell(5).setCellValue("任务金额");
	        row.createCell(6).setCellValue("任务来源");
	        row.createCell(7).setCellValue("渠道名称");
	
	        row.createCell(8).setCellValue("IP");
	        row.createCell(9).setCellValue("国家");
	        row.createCell(10).setCellValue("地区");
	        row.createCell(11).setCellValue("省份");
	        row.createCell(12).setCellValue("城市");
	        row.createCell(13).setCellValue("区县");
	        row.createCell(14).setCellValue("ISP");
	
	        Cell cell = null;
	        for (int i = 0; i < data.size(); i++) {
	            row = sheet.createRow(i + 1);
	            row.setHeight((short) 300);
	            row.createCell(0).setCellValue(data.get(i).get("create_time").toString());
	            row.createCell(1).setCellValue(data.get(i).get("task_id").toString());
	            row.createCell(2).setCellValue(data.get(i).get("idfa").toString());
	            row.createCell(3).setCellValue(data.get(i).get("appid").toString());
	            row.createCell(4).setCellValue(data.get(i).get("taskname").toString());
	            cell = row.createCell(5);
	            Double _award = Double.valueOf(data.get(i).get("award").toString()) / 100;
	            String award = _award.toString().concat("元");
	            cell.setCellValue(award);
	            row.createCell(6).setCellValue(data.get(i).get("task_source").toString());
	            if (data.get(i).get("channel_name") != null){
	                row.createCell(7).setCellValue(data.get(i).get("channel_name").toString());
	            }
	            row.createCell(8).setCellValue(data.get(i).get("clientip")!=null?data.get(i).get("clientip").toString():"");
	            row.createCell(9).setCellValue(data.get(i).get("country")!=null?data.get(i).get("country").toString():"");
	            row.createCell(10).setCellValue(data.get(i).get("area")!=null?data.get(i).get("area").toString():"");
	            row.createCell(11).setCellValue(data.get(i).get("province")!=null?data.get(i).get("province").toString():"");
	            row.createCell(12).setCellValue(data.get(i).get("city")!=null?data.get(i).get("city").toString():"");
	            row.createCell(13).setCellValue(data.get(i).get("county")!=null?data.get(i).get("county").toString():"");
	            row.createCell(14).setCellValue(data.get(i).get("isp")!=null?data.get(i).get("isp").toString():"");
	        }
			logger.debug("complete construct excel");
	        m1 = Runtime.getRuntime().freeMemory();
	        logger.debug("total: {}, free:{}, use:{}" ,total/1024, m1/1024, (total - m1)/1024);

	        String filename = "渠道点击查询_" + endDate + ".xlsx";// 设置下载时客户端Excel的名称
	        filename = CommonUtil.encodeFilename(filename, request);// 处理中文文件名
            // 表示以附件的形式把文件发送到客户端
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);//设定输出文件头
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型 
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
			logger.debug("complete all.");
	        m1 = Runtime.getRuntime().freeMemory();
	        logger.debug("total: {}, free:{}, use:{}" ,total/1024, m1/1024, (total - m1)/1024);
        } catch (Exception e) {
            logger.error("导出渠道点击查询失败：" , e);
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
