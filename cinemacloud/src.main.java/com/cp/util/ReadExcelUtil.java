package com.cp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadExcelUtil {
	
	public static List<List<Object>> readExcel(File file) throws IOException {
		String fileName = file.getName();
		String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName
				.substring(fileName.lastIndexOf(".") + 1);
		if ("xls".equals(extension)) {
			return read2003Excel(file);
		} else if ("xlsx".equals(extension)) {
			return read2007Excel(file);
		} else {
			throw new IOException("不支持的文件类型");
		}
	}
	
	private static List<List<Object>> read2003Excel(File file) throws IOException{
		List<List<Object>> list=new LinkedList<List<Object>>();
		//构造XSSFWorkbook对象，strpath传入文件路径
		HSSFWorkbook hwb=new HSSFWorkbook(new FileInputStream(file));
		HSSFSheet sheet=hwb.getSheetAt(0);
		Object value=null;
		HSSFRow row=null;
		HSSFCell cell=null;
		int count=0;
		
		for(int i=sheet.getFirstRowNum();count<sheet.getPhysicalNumberOfRows();i++){
			row=sheet.getRow(i);
			if(row==null){
				continue;
			}else if("".equals(row.getCell(0)) && "".equals(row.getCell(1)) && "".equals(row.getCell(2))){
				continue;
			}else {
				count++;
			}
			List<Object> linked=new LinkedList<Object>();
			for(int j=0;j<=row.getLastCellNum();j++){
				cell=row.getCell(j);
				if(cell==null){
					continue;
				}
				//格式化number String
				DecimalFormat df=new DecimalFormat("0");	
				//格式化日期字符串
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				DecimalFormat nf=new DecimalFormat("0");
				switch (cell.getCellType()) {
				case HSSFCell.CELL_TYPE_STRING:
					value = cell.getStringCellValue();
					break;
				case HSSFCell.CELL_TYPE_NUMERIC:
					/*if ("@".equals(cell.getCellStyle().getDataFormatString())) {
						value = df.format(cell.getNumericCellValue());
					} else if ("General".equals(cell.getCellStyle()
							.getDataFormatString())) {
						value = nf.format(cell.getNumericCellValue());
					} else {
						value = sdf.format(HSSFDateUtil.getJavaDate(cell
								.getNumericCellValue()));
					}*/
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					value=cell.getStringCellValue();
					break;
				case HSSFCell.CELL_TYPE_BOOLEAN:
					value = cell.getBooleanCellValue();
					break;
				case HSSFCell.CELL_TYPE_FORMULA:
					value=cell.getNumericCellValue();
					break;
				case HSSFCell.CELL_TYPE_BLANK:
					value = "";
					break;
				
				default:
					value = cell.toString();
				}
				/*if(value==null || "".equals(value)){
					continue;
				}*/
				linked.add(value);
			}
			list.add(linked);
		}
		return list;
	}
	
	/**
	 * 读取Office 2007 excel
	 * */
	@SuppressWarnings({ "resource", "deprecation" })
	private static List<List<Object>> read2007Excel(File file)
			throws IOException {
		List<List<Object>> list = new LinkedList<List<Object>>();
		// 构造 XSSFWorkbook 对象，strPath 传入文件路径
		XSSFWorkbook xwb = new XSSFWorkbook(new FileInputStream(file));
		// 读取第一章表格内容
		XSSFSheet sheet = xwb.getSheetAt(0);
		Object value = null;
		XSSFRow row = null;
		XSSFCell cell = null;
		int count = 0;
		for (int i = sheet.getFirstRowNum(); count < sheet
				.getPhysicalNumberOfRows(); i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}else if("".equals(row.getCell(0)) && "".equals(row.getCell(1)) && "".equals(row.getCell(2))){
				continue;
			}else {
				count++;
			}
			List<Object> linked = new LinkedList<Object>();
			for (int j = 0; j <= row.getLastCellNum(); j++) {
				cell = row.getCell(j);
				if (cell == null) {
					value="";
					linked.add(value);
					continue;
				}
				DecimalFormat df = new DecimalFormat("0");// 格式化 number String
															// 字符
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");// 格式化日期字符串
				DecimalFormat nf = new DecimalFormat("0");// 格式化数字
				switch (cell.getCellType()) {
				case XSSFCell.CELL_TYPE_STRING:
					value = cell.getStringCellValue();
					break;
				case XSSFCell.CELL_TYPE_NUMERIC:
					/*if ("@".equals(cell.getCellStyle().getDataFormatString())) {
						value = df.format(cell.getNumericCellValue());
					} else if ("General".equals(cell.getCellStyle()
							.getDataFormatString())) {
						value = nf.format(cell.getNumericCellValue());
						//value=cell.getNumericCellValue();
					} else {
						value = sdf.format(HSSFDateUtil.getJavaDate(cell
								.getNumericCellValue()));
					}*/
					cell.setCellType(XSSFCell.CELL_TYPE_STRING);
					value=cell.getStringCellValue();
					break;
				case XSSFCell.CELL_TYPE_BOOLEAN:
					value = cell.getBooleanCellValue();
					break;
				case HSSFCell.CELL_TYPE_FORMULA:
					value=cell.getNumericCellValue();
					break;
				case XSSFCell.CELL_TYPE_BLANK:
					value = "";
					break;
				default:
					value = cell.toString();
				}
				/*if (value == null || "".equals(value)) {
					continue;
				}*/
				linked.add(value);
			}
			list.add(linked);
		}
		return list;
	}
	
	public static void main(String[] args) throws IOException {
		File file=new File("C:\\Users\\29632\\Desktop\\潇湘表格\\潇湘院线硬盘发运表2017.1.xlsx");
		List<List<Object>> list=readExcel(file);
		long s_time=System.currentTimeMillis();
		for(int i=2;i<list.size();i++){
			System.out.println(list.get(i).get(0));
		}
		long e_time=System.currentTimeMillis();
		System.out.println(e_time-s_time+"=============");
		s_time=System.currentTimeMillis();
		int count=0;
		for(List<Object> l:list){
			if(count++<2) continue;
			System.out.println(l.get(0));
		}
		e_time=System.currentTimeMillis();
		System.out.println(e_time-s_time+"=====================");
	}


}
