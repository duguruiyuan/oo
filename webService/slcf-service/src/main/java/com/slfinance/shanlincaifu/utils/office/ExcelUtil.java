/** 
 * @(#)ExcelUtil.java 1.0.0 2014年12月31日 下午4:04:11  
 *  
 * Copyright © 2014 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.utils.office;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * excel文档操作工具类
 * 
 * @author 孟山
 * @version $Revision:1.0.0, $Date: 2014年12月31日 下午4:04:11 $
 */
public class ExcelUtil {

	private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

	/**
	 * excel文档生成
	 * 
	 * @param dataList
	 *            待生成的数据
	 * @param outFilePathDir
	 *            文档输出目录
	 * @param fileName
	 *            文档名称 不带扩展名
	 * @param titleColumIndex2keyMap
	 *            列号和标题对应关系
	 * @param columIndex2keyMap
	 *            列号和数据key值对应关系
	 * @return
	 * @throws Exception
	 */
	public static String createExcel(List<Map<String, Object>> dataList,
			String outFilePathDir, String fileName,
			Map<Integer, String> titleColumIndex2keyMap,
			Map<Integer, String> columIndex2keyMap) throws Exception {
		String filePath = null;
		String filePathTemp = DateFormatUtils.format(new Date(), "yyyy/MM/dd");
		fileName = fileName
				+ DateFormatUtils.format(System.currentTimeMillis(),
						"yyyyMMddHHmmssSS") + ".xlsx";

		File file = forceCreateDirAndFile(outFilePathDir + filePathTemp,
				fileName);
		OutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			Workbook workbook = null;
			try {
				// 创建文档对象
				workbook = Excel.createWorkbook(null, true);
				Sheet sheet = workbook.createSheet();
				sheet.setDefaultColumnWidth(21);
				sheet.setDefaultRowHeightInPoints(25);

				Excel.populateSheet(sheet, 0, titleColumIndex2keyMap,
						columIndex2keyMap, dataList, 20f, 20f, null, null,
						createTitleCellStyle(workbook), createColumnCellStyle(workbook));
				workbook.write(fileOutputStream);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (workbook != null) {
					workbook.close();
				}
			}
			filePath = filePathTemp + "/" + fileName;
			logger.info("创建成功,地址为:{},总记录数{}条", file.toString(),
					dataList.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		}
		return filePath;
	}

	/**
	 * 标题标题单元格样式
	 * 
	 * <pre>
	 * 样式：
	 * 	水平居中
	 * 	垂直居中
	 * 	绿色背景
	 * 字体：
	 * 	宋体
	 * 	12号
	 * 	加粗
	 * 	不倾斜
	 * 	无删除线
	 * 	无下划线任何样式
	 * </pre>
	 * 
	 * @param workbook
	 * @return
	 */
	private static CellStyle createTitleCellStyle(Workbook workbook) {
		return Excel.createCellStyle(workbook, Excel.createFont(workbook, "宋体",
				new Short("12"), Font.BOLDWEIGHT_BOLD, false, false,
				Font.U_NONE), CellStyle.ALIGN_CENTER,
				CellStyle.VERTICAL_CENTER, HSSFColor.GREEN.index, null,
				CellStyle.SOLID_FOREGROUND);
	}
	/**
	 * 标题单元格样式
	 * 
	 * <pre>
	 * 样式：
	 * 	水平居中
	 * 	垂直居中
	 * 	无背景
	 * 字体：
	 * 	宋体
	 * 	12号
	 * 	加粗
	 * 	不倾斜
	 * 	无删除线
	 * 	无下划线任何样式
	 * </pre>
	 * 
	 * @param workbook
	 * @return
	 */
	private static CellStyle createColumnCellStyle(
				Workbook workbook) {
			return Excel.createCellStyle(workbook, Excel.createFont(workbook, "宋体",
					new Short("10"), Font.BOLDWEIGHT_NORMAL, false, false,
					Font.U_NONE), CellStyle.ALIGN_CENTER,
					CellStyle.VERTICAL_CENTER, HSSFColor.WHITE.index, null,
					CellStyle.SOLID_FOREGROUND);
	}
	/**
	 * 创建目录和文件(存在则直接返回,不存在则创建)
	 * 
	 * @param dirPath
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static File forceCreateDirAndFile(String dirPath, String fileName)
			throws IOException {
		File file = null;
		if (dirPath != null) {
			file = new File(dirPath);
			if (!file.exists() || !file.isDirectory()) {
				file.mkdirs();
				logger.info("创建目录成功，目录路径为:{}", file.getAbsolutePath());
			} else {
				logger.info("目录已存在,目录路径:{}", file.getAbsolutePath());
			}
			if (fileName != null) {
				file = new File(file.getAbsolutePath() + "/" + fileName);
				if (!file.exists() || !file.isFile()) {
					file.createNewFile();
					logger.info("创建文件成功,文件地址为：{}", file.getAbsolutePath());
				} else {
					logger.info("文件已存在,文件地址为：{}", file.getAbsolutePath());
				}
			}
		}
		return file;
	}

	public static void main(String[] args) throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for( int i = 0; i < 3;i++ ){
			Map<String, Object> map = Maps.newHashMap();
			map.put("loanCode", "001");
			map.put("loanAmount", "122");
			map.put("holdAmount", "122");
			map.put("holdScale", "122");
			map.put("loanTerm", "122");
			map.put("investStartDate", "20120203");
			map.put("investEndDate", "20120203");
			map.put("parameterName", "2jjj"); 
			map.put("currentTerm", "2"); 
			map.put("expectRepaymentDate", "20120203");
			map.put("repaymentTotalAmount", "21");
			map.put("loanDesc", "借款");
			map.put("repaymentMethod", "借款方式");
			list.add(map);
		}
		System.out.println(createExcel(list, "d:", "", ExcelConstants.REPAYMENT_TITLE_COLUMINDEX2KEY_MAP, ExcelConstants.REPAYMENT_COLUMINDEX2KEY_MAP));
	}
}
