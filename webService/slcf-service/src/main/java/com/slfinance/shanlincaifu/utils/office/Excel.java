/** 
 * @(#)Excell.java 1.0.0 2015年1月5日 下午3:25:38  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.utils.office;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * excel生成和解析处理类
 * 
 * @author 孟山
 * @version $Revision:1.0.0, $Date: 2015年1月5日 下午3:25:38 $
 */
public class Excel {

	/**
	 * 创建 excel
	 * 
	 * @param templateFile
	 *            模版文件
	 * @param xlsx
	 *            是否是创建xlsx文件默认是 不然创建 xls文件
	 * @return {@code Workbook} excel 文档对象
	 * @throws Exception
	 */
	public static Workbook createWorkbook(File templateFile, boolean xlsx) throws Exception {
		Workbook workbook = null;
		String templateFilePath = null;
		if (templateFile != null && templateFile.exists()) {
			templateFilePath = templateFile.getPath();
		}
		if (StringUtils.isNotBlank(templateFilePath)) {
			if (templateFilePath.trim().toLowerCase().endsWith("xls")) {
				workbook = new HSSFWorkbook(new FileInputStream(templateFile));
			} else if (templateFilePath.trim().toLowerCase().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(new FileInputStream(templateFile));
			} else {
				throw new IllegalArgumentException("不支持除：xls/xlsx以外的文件格式!!!");
			}
		} else {
			if (xlsx) {
				workbook = new XSSFWorkbook();
			} else {
				workbook = new HSSFWorkbook();
			}
		}
		return workbook;
	}

	/**
	 * 根据 数据 ，索引对应关系 和 数据对应关系组装sheet
	 * 
	 * @param sheet
	 *            待组装的sheet
	 * @param startRowIndex
	 *            开始组装的起始行
	 * @param titleColumIndex2keyMap
	 *            标题索引号-标题名称 映射map
	 * @param columIndex2keyMap
	 *            数据封装时 索引号-数据获取key值的映射map
	 * @param dataList
	 *            待组装的数据(目前只支持List<Map<String,Object>>)
	 * @param titleHeightInPoints
	 *            设置标题单元格的高度 此值可以在sheet中设置默认值
	 * @param heightInPoints
	 *            设置单元格的高度 此值可以在sheet中设置默认值
	 * @param titleRowStyle
	 *            标题行样式
	 * @param rowStyle
	 *            行样式
	 * @param titleCellStyle
	 *            标题单元格样式
	 * @param cellStyle
	 *            单元格样式
	 */
	public static void populateSheet(Sheet sheet, int startRowIndex, Map<Integer, String> titleColumIndex2keyMap, Map<Integer, String> columIndex2keyMap, List<Map<String, Object>> dataList, Float titleHeightInPoints, Float heightInPoints, CellStyle titleRowStyle, CellStyle rowStyle, CellStyle titleCellStyle, CellStyle cellStyle) {
		createTitleRow(sheet, startRowIndex++, titleHeightInPoints, titleColumIndex2keyMap, titleRowStyle, titleCellStyle);
		populateData(sheet, startRowIndex, heightInPoints, columIndex2keyMap, dataList, rowStyle, cellStyle);
	}

	/**
	 * 数据组装(需要映射和数据两个来完成自动组装)
	 * 
	 * @param sheet
	 *            待组装的sheet
	 * @param rowStartIndex
	 *            开始组装的起始行
	 * @param heightInPoints
	 *            设置单元格的高度 此值可以在sheet中设置默认值
	 * @param columIndex2keyMap
	 *            列索引号-和数据map中对应的key值映射map
	 * @param dataList
	 *            待组装的数据(目前只支持List<Map<String,Object>>)
	 * @param rowStyle
	 *            行样式
	 * @param cellStyle
	 *            单元格样式
	 */
	public static void populateData(Sheet sheet, int rowStartIndex, float heightInPoints, Map<Integer, String> columIndex2keyMap, List<Map<String, Object>> dataList, CellStyle rowStyle, CellStyle cellStyle) {
		if (columIndex2keyMap != null && columIndex2keyMap.size() > 0 && dataList != null && dataList.size() > 0) {
			for (Map<String, Object> data : dataList) {
				createRow(sheet, rowStartIndex++, heightInPoints, rowStyle, columIndex2keyMap, data, cellStyle);
			}
		}
	}

	/**
	 * 常见常规 标题头行创建 会自动创建默认的行样式和单元格样式
	 * 
	 * @param sheet
	 *            待组装的sheet
	 * @param rowIndex
	 *            标题放在的行索引位置
	 * @param columIndex2keyMap
	 *            标题索引号-标题名称 映射map
	 * @return
	 */
	public static Row createCommonTitleRow(Sheet sheet, int rowIndex, Map<Integer, String> columIndex2keyMap) {
		return createTitleRow(sheet, rowIndex, 20, columIndex2keyMap, createCommonTitleCellStyle(sheet.getWorkbook()), createCommonTitleCellStyle(sheet.getWorkbook()));
	}

	/**
	 * 常规行创建 会自动创建默认的行样式和单元格样式
	 * 
	 * @param sheet
	 *            待组装的sheet
	 * @param rowIndex
	 *            行索引位置
	 * @param columIndex2keyMap
	 *            列索引号-和数据map中对应的key值映射map
	 * @param dataMap
	 *            待组装数据map
	 * @return
	 * 
	 */
	public static Row createCommonNomalRow(Sheet sheet, int rowIndex, Map<Integer, String> columIndex2keyMap, Map<String, Object> dataMap) {
		return createRow(sheet, rowIndex, 20, createCommonNomalCellStyle(sheet.getWorkbook()), columIndex2keyMap, dataMap, createCommonNomalCellStyle(sheet.getWorkbook()));
	}

	/**
	 * 创建标题行
	 * 
	 * @param sheet
	 *            待组装的sheet
	 * @param rowIndex
	 *            行索引位置
	 * @param heightInPoints
	 *            设置单元格的高度 此值可以在sheet中设置默认值
	 * @param rowStyle
	 *            行样式
	 * @param columIndex2keyMap
	 *            标题索引号-标题名称 映射map
	 * @param cellStyle
	 *            单元格样式
	 * @return
	 */
	public static Row createTitleRow(Sheet sheet, int rowIndex, float heightInPoints, Map<Integer, String> columIndex2keyMap, CellStyle rowStyle, CellStyle cellStyle) {
		Row row = sheet.createRow(rowIndex);
		row.setHeightInPoints(heightInPoints);
		if (rowStyle != null)
			row.setRowStyle(rowStyle);
		if (columIndex2keyMap != null && columIndex2keyMap.size() > 0) {
			for (Iterator<Integer> iterator = columIndex2keyMap.keySet().iterator(); iterator.hasNext();) {
				int currentColumIndex = iterator.next();
				Cell cell = row.createCell(currentColumIndex);
				if (cellStyle != null)
					cell.setCellStyle(cellStyle);
				cell.setCellValue(columIndex2keyMap.get(currentColumIndex));
			}
		}
		return row;
	}

	/**
	 * 创建行
	 * 
	 * @param sheet
	 *            待组装的sheet
	 * @param rowIndex
	 *            行索引位置
	 * @param heightInPoints
	 *            设置单元格的高度 此值可以在sheet中设置默认值
	 * @param rowStyle
	 *            行样式
	 * @param columIndex2keyMap
	 *            标题索引号-标题名称 映射map
	 * @param dataMap
	 *            待组装数据map
	 * @param cellStyle
	 *            单元格样式
	 * @return
	 */
	public static Row createRow(Sheet sheet, int rowIndex, float heightInPoints, CellStyle rowStyle, Map<Integer, String> columIndex2keyMap, Map<String, Object> dataMap, CellStyle cellStyle) {
		Row row = sheet.createRow(rowIndex);
		row.setHeightInPoints(heightInPoints);
		if (rowStyle != null)
			row.setRowStyle(rowStyle);
		if (columIndex2keyMap != null && columIndex2keyMap.size() > 0 && dataMap != null) {
			for (Iterator<Integer> iterator = columIndex2keyMap.keySet().iterator(); iterator.hasNext();) {
				int currentRowIndex = iterator.next();
				Cell cell = row.createCell(currentRowIndex);
				if (cellStyle != null)
					cell.setCellStyle(cellStyle);
				Object currentValue = dataMap.get(columIndex2keyMap.get(currentRowIndex));
				cell.setCellValue(currentValue != null ? currentValue.toString() : "");
			}
		}
		return row;
	}

	/**
	 * 创建单元格
	 * 
	 * @param workbook
	 *            文档对象
	 * @param row
	 *            行对象
	 * @param columIndex
	 *            列索引号
	 * @param value
	 *            单元格值
	 * @return
	 */
	public static Cell createCommonTitleCell(Workbook workbook, Row row, int columIndex, Object value) {
		return createCell(row, columIndex, Cell.CELL_TYPE_STRING, value, createCommonTitleCellStyle(workbook), null);
	}

	/**
	 * 常见常规单元格
	 * 
	 * @param workbook
	 *            文档对象
	 * @param row
	 *            行对象
	 * @param columIndex
	 *            列索引号
	 * @param value
	 *            单元格值
	 * @return
	 */
	public static Cell createCommonNomalCell(Workbook workbook, Row row, int columIndex, Object value) {
		return createCell(row, columIndex, Cell.CELL_TYPE_STRING, value, createCommonNomalCellStyle(workbook), null);
	}

	/**
	 * 创建单元格
	 * 
	 * @param row
	 *            单元格所在行
	 * @param columIndex
	 *            列索引号
	 * @param dataType
	 *            数据类型
	 * @param value
	 *            数据值
	 * @param cellStyle
	 *            单元格样式
	 * @param hyperlink
	 *            连接地址
	 * @return
	 */
	public static Cell createCell(Row row, int columIndex, Integer dataType, Object value, CellStyle cellStyle, Hyperlink hyperlink) {
		Cell cell = row.createCell(columIndex, dataType == null ? Cell.CELL_TYPE_STRING : dataType);
		cell.setCellValue(value.toString());
		if (cellStyle != null)
			cell.setCellStyle(cellStyle);
		if (hyperlink != null)
			cell.setHyperlink(hyperlink);
		return cell;
	}

	/**
	 * <p>
	 * 设置一般的标题头样式
	 * </p>
	 * 
	 * <pre>
	 * 	1.设置水平对齐方式为 居中
	 * 	2.设置垂直对齐方式为 居中
	 * 	3.设置字体
	 * 		3.1.当字体为空时：则创建默认的字体
	 * 		3.2.当字体不为空时：则用传递过来的字体
	 * </pre>
	 * 
	 * @param workbook
	 * @param font
	 * @return
	 */
	public static CellStyle createCommonTitleCellStyle(Workbook workbook) {
		return createCellStyle(workbook, createCommonTitleFont(workbook), CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, HSSFColor.GREEN.index, CellStyle.SOLID_FOREGROUND, null);
	}

	/**
	 * 创建常规行|单元格 样式
	 * 
	 * @param workbook
	 * @return
	 */
	public static CellStyle createCommonNomalCellStyle(Workbook workbook) {
		return createCellStyle(workbook, createCommonNomalFont(workbook), CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, null, null, null);
	}

	/**
	 * 创建单元格|行 样式
	 * 
	 * @param workbook
	 *            excel对象
	 * @param font
	 *            字体对象 (如果为空则设置默认字体参考createCommonNomalFont)
	 * @param horizontalAlignment
	 *            水平对其方式
	 * @param verticalAlignment
	 *            垂直对其方式
	 * @param fgColor
	 *            前景颜色
	 * @param bgColor
	 *            背景颜色
	 * @param fillPattern
	 *            设置填充模版参考CellStyle.NO_FILL-CellStyle.LEAST_DOTS
	 * @return
	 */
	public static CellStyle createCellStyle(Workbook workbook, Font font, short horizontalAlignment, short verticalAlignment, Short fgColor, Short bgColor, Short fillPattern) {
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(horizontalAlignment);
		cellStyle.setVerticalAlignment(verticalAlignment);
		if (font == null) {
			cellStyle.setFont(createCommonNomalFont(workbook));
		} else {
			cellStyle.setFont(font);
		}
		if (fgColor != null)
			cellStyle.setFillForegroundColor(fgColor);
		if (bgColor != null)
			cellStyle.setFillBackgroundColor(bgColor);
		if (fillPattern != null)
			cellStyle.setFillPattern(fillPattern);
		cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		return cellStyle;
	}

	/**
	 * 创建常规字体:宋体,12字号,加粗,不倾斜,无下划线,无删除线样式
	 * 
	 * @param workbook
	 * @return
	 */
	public static Font createCommonTitleFont(Workbook workbook) {
		return createFont(workbook, "宋体", new Short("12"), Font.BOLDWEIGHT_BOLD, false, false, Font.U_NONE);
	}

	/**
	 * 创建常规标题字体: 宋体,11字号,不加粗,不倾斜,无下划线,无删除线样式
	 * 
	 * @param workbook
	 *            待组装的sheet
	 * @return
	 */
	public static Font createCommonNomalFont(Workbook workbook) {
		return createFont(workbook, "宋体", new Short("11"), Font.BOLDWEIGHT_NORMAL, false, false, Font.U_NONE);
	}

	/**
	 * 创建字体 具体参数常量可以参考 {@code Font}
	 * 
	 * @param workbook
	 *            待组装的sheet
	 * @param fontName
	 *            字体名称
	 * @param fontHeightInPoints
	 *            字体大小
	 * @param boldWeight
	 *            是否加粗
	 * @param isItalic
	 *            是否倾斜
	 * @param isStrickout
	 *            是否删除线
	 * @param underLine
	 *            下划线样式
	 * @return
	 */
	public static Font createFont(Workbook workbook, String fontName, short fontHeightInPoints, short boldWeight, boolean isItalic, boolean isStrickout, byte underLine) {
		Font font = workbook.createFont();
		font.setBoldweight(boldWeight);
		font.setFontName(fontName);
		font.setFontHeightInPoints(fontHeightInPoints);
		font.setItalic(isItalic);
		font.setStrikeout(isStrickout);
		font.setUnderline(underLine);
		return font;
	}
}
