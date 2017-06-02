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
import com.ipeaksoft.moneyday.core.entity.TaskFastCompleteInfo;

public class ViewExcel extends AbstractExcelView {
	@Override
	protected void buildExcelDocument(Map<String, Object> obj,
			HSSFWorkbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	
		List<TaskFastCompleteInfo> list = (List<TaskFastCompleteInfo>) obj.get("list");
		String taskid=(String)obj.get("taskid");
		String taskname=(String)obj.get("taskname");
		HSSFSheet sheet = workbook.createSheet("任务号--"+taskid);
		 //  sheet.autoSizeColumn(( short ) 30 ); // 调整第一列宽度 
		   sheet.setDefaultColumnWidth((short) 14);
		HSSFCell cell = getCell(sheet, 0, 0);
		setText(cell, "完成时间");
		cell = getCell(sheet, 0, 1);
		setText(cell, "用户ID");
		cell = getCell(sheet, 0, 2);
		setText(cell, "用户类型");
		cell = getCell(sheet, 0, 3);
		setText(cell, "IDFA");
		cell = getCell(sheet, 0, 4);
		setText(cell, "姓名");
		cell = getCell(sheet, 0, 5);
		setText(cell, "电话");
		cell = getCell(sheet, 0, 6);
		setText(cell, "地址");
		cell = getCell(sheet, 0, 7);
		setText(cell, "任务Id");
		cell = getCell(sheet, 0, 8);
		setText(cell, "任务名称");
		//sheet.setColumnWidth((short)4, 40);
		for (short i = 0; i < list.size(); i++) {
			HSSFRow sheetRow = sheet.createRow(i + 1);
			TaskFastCompleteInfo entity = list.get(i);
			sheetRow.createCell(0).setCellValue(entity.getCreateTime());
			sheetRow.createCell(1).setCellValue(entity.getUserid());
			sheetRow.createCell(2).setCellValue(entity.getFromTo());
			sheetRow.createCell(3).setCellValue(entity.getIdfa());
			sheetRow.createCell(4).setCellValue(entity.getName());
			sheetRow.createCell(5).setCellValue(entity.getMobile());
			sheetRow.createCell(6).setCellValue(entity.getAddress());
			sheetRow.createCell(7).setCellValue(taskid);
			sheetRow.createCell(8).setCellValue(taskname);
			
		}
		String filename = "任务列表.xls";// 设置下载时客户端Excel的名称
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
