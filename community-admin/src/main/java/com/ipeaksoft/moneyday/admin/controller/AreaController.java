package com.ipeaksoft.moneyday.admin.controller;

import java.awt.geom.Area;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ipeaksoft.moneyday.admin.util.JsonTransfer;
import com.ipeaksoft.moneyday.core.service.AreaService;

@Controller
@RequestMapping(value = "/Area")
public class AreaController extends BaseController {
	@Autowired
	private AreaService AreaService;

	@ResponseBody
	@RequestMapping(value = "/findAreaByParent")
	public String findAreaByParent(HttpServletRequest request) {
		String result = "";
		try {
			String parentId = request.getParameter("pid");
			if (parentId != null && !parentId.equals("")) {
				java.util.List<com.ipeaksoft.moneyday.core.entity.Area> items = AreaService
						.findAreaByParentCode(Integer.parseInt(parentId));
				result=JsonTransfer.getJsonFromList("",items);
			}
		} catch (Exception ex) {
			result = "获取数据出错";
		}
		return result;

	}
}