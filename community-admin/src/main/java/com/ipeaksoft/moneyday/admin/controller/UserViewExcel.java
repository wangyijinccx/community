package com.ipeaksoft.moneyday.admin.controller;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.ipeaksoft.moneyday.admin.util.CommonUtil;
import com.ipeaksoft.moneyday.core.entity.UserSurveyDetail;

public class UserViewExcel extends AbstractExcelView {
	@Override
	protected void buildExcelDocument(Map<String, Object> obj,
			HSSFWorkbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	
		List<UserSurveyDetail> list = (List<UserSurveyDetail>) obj.get("list");
		String startDate=(String)obj.get("startDate");
		String endDate=(String)obj.get("endDate");
		HSSFSheet sheet = workbook.createSheet("用户概览");
		 //  sheet.autoSizeColumn(( short ) 30 ); // 调整第一列宽度 
		   sheet.setDefaultColumnWidth((short) 14);
		HSSFCell cell = getCell(sheet, 0, 0);
		setText(cell, "日期");
		cell = getCell(sheet, 0, 1);
		setText(cell, "新增用户(人)");
		cell = getCell(sheet, 0, 2);
		setText(cell, "游客(人)");
		cell = getCell(sheet, 0, 3);
		setText(cell, "注册人数(人)");
		cell = getCell(sheet, 0, 4);
		setText(cell, "游客转注册(人)");
		cell = getCell(sheet, 0, 5);
		setText(cell, "转化率");
		cell = getCell(sheet, 0, 6);
		setText(cell, "普通转高级(人)");
		cell = getCell(sheet, 0, 7);
		setText(cell, "转化率");
		cell = getCell(sheet, 0, 8);
		setText(cell, "登录活跃(人)");
		cell = getCell(sheet, 0, 9);
		setText(cell, "下载活跃(人)");
		//sheet.setColumnWidth((short)4, 40);
		for (short i = 0; i < list.size(); i++) {
			HSSFRow sheetRow = sheet.createRow(i + 1);
			UserSurveyDetail entity = list.get(i);
			sheetRow.createCell(0).setCellValue(entity.getDate());
			sheetRow.createCell(1).setCellValue(entity.getNewuser());
			sheetRow.createCell(2).setCellValue(entity.getVisitor());
			sheetRow.createCell(3).setCellValue(entity.getAlluser());
			sheetRow.createCell(4).setCellValue(entity.getVisitortouser());
			sheetRow.createCell(5).setCellValue(entity.getPecent());
			sheetRow.createCell(6).setCellValue(entity.getPtzgj());
			if(entity.getPtzgjpecent()!=null){
				sheetRow.createCell(7).setCellValue(entity.getPtzgjpecent());
			}else{
				sheetRow.createCell(7).setCellValue("");
			}
			sheetRow.createCell(8).setCellValue(entity.getLoginactive());
			sheetRow.createCell(9).setCellValue(entity.getDownloadactive());
		}
		String filename = "用户概览.xls";// 设置下载时客户端Excel的名称
		filename = CommonUtil.encodeFilename(filename, request);// 处理中文文件名
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment;filename="
				+ filename);
		OutputStream ouputStream = response.getOutputStream();
		workbook.write(ouputStream);
		ouputStream.flush();
		ouputStream.close();
	}
}
