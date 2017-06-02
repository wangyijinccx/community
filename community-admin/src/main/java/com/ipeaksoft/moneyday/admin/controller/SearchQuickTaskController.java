package com.ipeaksoft.moneyday.admin.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.core.entity.AdminUser;
import com.ipeaksoft.moneyday.core.service.AdminUserService;

@Controller
@RequestMapping(value = "/search")
public class SearchQuickTaskController extends BaseController{
 
	@RequestMapping(value = "/search_quick_task")
	public String Index(ModelMap map, Principal principal,
			HttpServletRequest request) {


		return "/search/search_quick_task";
	}
	@ResponseBody
	@RequestMapping(value="/load_quicktask_page")
	public String GetPageList(HttpServletRequest request){
		int start= Integer.parseInt(request.getParameter("start")) ;//开始记录数
		int pageSize = Integer.parseInt(request.getParameter("length"));//美页记录数
		String sEcho = request.getParameter("draw");//搜索内容
		
		int limit=start+pageSize;
		int total=100;
		if(limit>=total-pageSize){
			limit=total;
		}
		StringBuilder rs= new StringBuilder();
		for(int i=start;i<limit;i++){
			if(!rs.toString().equals("")){
				rs.append(",");
			}
			rs.append("{\"pkid\":\""+ i+1 +"\",\"rq\":\"2014-12-19\",\"ksrwwccs\":\""+ i+2 +"\",\"cyrs\":\""+ i+3 +"\",\"xsyh\":\""+ i+4 +"\",\"xssy\":\""+ i+5 +"\",\"xxyh\":\""+ i+6 +"\",\"xxsy\":\""+ i+7 +"\",\"drzsy\":\""+ i+8 +"\"}");
		}
		
		String result="{\"draw\":" + sEcho + ",\"recordsTotal\":" + pageSize + ",\"recordsFiltered\":" + total + ",\"data\":[" + rs.toString() + "]}";
		return result;
		
	}

}
