package com.ipeaksoft.moneyday.admin.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ipeaksoft.moneyday.admin.util.JsonTransfer;
import com.ipeaksoft.moneyday.core.entity.AdminUser;
import com.ipeaksoft.moneyday.core.entity.Notice;
import com.ipeaksoft.moneyday.core.service.NoticeService;

@Controller
@RequestMapping(value = "/notice")
public class noteiceController extends BaseController {
	@Autowired
	private NoticeService noticeService;

	/**
	 * 公告列表Page
	 * 
	 * @return
	 */
	@RequestMapping(value = "/list")
	public ModelAndView notice_list() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/notice/list");
		return mv;
	}

	/**
	 * 分页查询公告内容Action
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/list_do", method = { RequestMethod.POST })
	public String list_do(HttpServletRequest request,
			HttpServletResponse response) {
		String result = "";
		// 获取当前登录用户信息
		try {
			AdminUser adminSession = getUser();
			if (adminSession == null) {
				return result;
			}
			int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
			int pageSize = Integer.parseInt(request.getParameter("length"));// 每页记录数
			String searchContent = request.getParameter("search[value]");// 搜索内容
			String orderColumnIndex = request.getParameter("order[0][column]");// 排序列索引
			String orderDir = request.getParameter("order[0][dir]");// 排序方向
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");

			String ColumnName = null;
			if (orderColumnIndex != null && !orderColumnIndex.equals("")
					&& orderDir != null && !orderDir.equals("")) {
				switch (orderColumnIndex) {
				case "0":
					ColumnName = " title " + orderDir;
					break;
				case "1":
					ColumnName = " create_time " + orderDir;
					break;
				case "2":
					ColumnName = " end_time " + orderDir;
					break;
				case "3":
					ColumnName = " update_time " + orderDir;
					break;
				case "4":
					ColumnName = " operator " + orderDir;
					break;
				
				}
			}

			int total = 0;
			Map<String, Object> where = new HashMap<String, Object>();
			where.put("keys", searchContent.equals("") ? null : searchContent);
			where.put("start", start);
			where.put("limit", pageSize);
			where.put("orderStr", ColumnName);
			where.put("startTime", startTime);
			where.put("endTime", endTime);

			List<Notice> items = noticeService.findPageList(where);
			total = noticeService.findPageListCount(where);
			String rs = JsonTransfer.getJsonFromList(searchContent, items);
			result = "{\"draw\":\"" + searchContent + "\",\"recordsTotal\":"
					+ pageSize + ",\"recordsFiltered\":" + total + ",\"data\":"
					+ rs + "}";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 添加公告Page
	 * 
	 * @param noticeRecord
	 * @return
	 */
	@RequestMapping(value = "/add", method = { RequestMethod.GET })
	public ModelAndView notice_add() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/notice/add");
		return mv;
	}

	/**
	 * 添加公告Action
	 * 
	 * @param noticeRecord
	 */
	@ResponseBody
	@RequestMapping(value = "add_do", method = { RequestMethod.POST })
	public String add_do(Notice noticeRecord, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String t = request.getParameter("tempenddate");
		System.out.println(t);
		boolean status = false;
		String data = "添加失败";
		try {
			AdminUser currentSessionInfo = getUser();

			if (StringUtils.isEmpty(noticeRecord.getTitle())) {

				data = "公告标题不能为空";
			} else if (StringUtils.isEmpty(noticeRecord.getContent())) {

				data = "公告内容不能为空";
			} else {
				status = true;
			}
			if (status) {
				noticeRecord.setContent(StringEscapeUtils.escapeHtml(noticeRecord.getContent()));
				noticeRecord.setCreateTime(new Date());
				noticeRecord.setUpdateTime(new Date());
				SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date endTime=  sdf.parse(t);
				noticeRecord.setEndTime(endTime);
				noticeRecord.setOperator(currentSessionInfo.getId());
				if (noticeService.addNotice(noticeRecord) > 0) {
					data = "添加成功";
				} else {
					status = false;
					data = "添加失败";
				}
			}
		} catch (Exception e) {
			status = false;
			data = "程序异常，请联系管理员";
			e.printStackTrace();
		}

		return "{\"status\":" + status + ",\"msg\":\"" + data + "\"}";

	}

	/**
	 * 更新公告 Page
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/edit")
	public ModelAndView notice_edit(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		String id = request.getParameter("id");
		Notice noticeRecord =new Notice();
		if(!StringUtils.isEmpty(id))
		{
			noticeRecord=noticeService.selectByPrimaryKey(Long.parseLong(id));
			noticeRecord.setContent(StringEscapeUtils.unescapeHtml(noticeRecord.getContent()));
			mv.addObject("noticeRecord", noticeRecord);
		}
		mv.setViewName("/notice/edit");
		return mv;
	}

	/**
	 * 更新公告 Page
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/edit_do")
	public String notice_edit_do(Notice noticeRecord, HttpServletRequest request)throws Exception {
		String t = request.getParameter("tempenddate");
		
		String result = "";
		String data = "";
		boolean status = false;
		Notice noticeSource = noticeService.selectByPrimaryKey(noticeRecord
				.getId());
		if (noticeSource == null) {
			data = "不存在该记录";
		} else if (StringUtils.isEmpty(noticeRecord.getTitle())) {
			data = "标题不能为空";
		} else {
			status = true;
		}
		if (status) {
			noticeSource.setTitle(noticeRecord.getTitle());
			noticeSource.setSummary(noticeRecord.getSummary());
			noticeSource.setContent(StringEscapeUtils.escapeHtml(noticeRecord.getContent()));
			noticeSource.setImg(noticeRecord.getImg());
			noticeSource.setUpdateTime(new Date());
			SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date endTime=  sdf.parse(t);
			noticeSource.setEndTime(endTime);
			if (noticeService.updateNotice(noticeSource) > 0) {
				data = "更新成功";
			} else {
				data = "更新失败";
				status = false;
			}
		}
		return "{\"status\":" + status + ",\"msg\":\"" + data + "\"}";
		
	}
	@ResponseBody
	@RequestMapping(value="del")
	public String notice_del_do(HttpServletRequest request){
		String id = request.getParameter("id");
		String data="删除失败";
		boolean status=false;
		if(StringUtils.isEmpty(id)){
			data="id不能为空";
		}else if(noticeService.selectByPrimaryKey(Long.parseLong(id))==null){
			data="该记录不存在";
		}else{
			status=true;
		}
		if(noticeService.deleteByPrimaryKey(Long.parseLong(id))>0){
			data="删除成功";
			
		}else{
			status=false;
		}
		
		return "{\"status\":" + status + ",\"msg\":\"" + data + "\"}";
		
	}
}
