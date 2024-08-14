package kr.gooroom.gpms.common.service.impl;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ExcelCommonService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("excelCommonService")
public class ExcelCommonServiceImpl implements ExcelCommonService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelCommonServiceImpl.class);

    @Override
    public Workbook getWorkbook(MultipartFile file) throws Exception {
        Workbook workbook = null;

        String fileName = file.getOriginalFilename();
        try {
            if(fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else if(fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(file.getInputStream());
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }

        return workbook;
    }

    @Override
    public List<List<String>> read(MultipartFile file, String fileType) throws Exception {

        Workbook wb = getWorkbook(file);

        int cellBaseType = 7;
        if(fileType.equals(GPMSConstants.RULE_GRADE_DEPT)) {
            cellBaseType = 4;
        }
        List<List<String>> dataList = new ArrayList<List<String>>();
        try {
            Sheet sheet = wb.getSheetAt(0);
            for (int rowIdx = 0; rowIdx < sheet.getPhysicalNumberOfRows(); rowIdx++) {
                List<String> rowData = new ArrayList<>();

                Row row = sheet.getRow(rowIdx);
                if(row == null) {
                    break;
                }

                Cell cell = null;
                for(int cellIdx = 0; cellIdx < cellBaseType; cellIdx++) {
                    cell = row.getCell(cellIdx);
                    if(cell == null) {
                        rowData.add(""); //빈공간채우기
                        continue;
                    }
                    //cell.setCellType(CellType.STRING);
                    rowData.add(getValue(cell));
                }

                //data check - is real data
                boolean isData = false;
                for(String data : rowData) {
                    if(!data.equals("")) {
                        isData = true;
                    }
                }
                if(isData) {
                    dataList.add(rowData);
                }
            }
        } catch (Exception ex) {
            throw new Exception(ex.getMessage(), ex);
        }

        return dataList;
    }

    @Override
    public XSSFWorkbook write(List<List<String>> list) {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        XSSFRow row = null;

        int rowIndex = 0;
        int colIndex = 0;

        XSSFCell cell = null;

        for (List<String> rowList : list) {

            row = sheet.createRow(rowIndex++);

            colIndex = 0;

            for (String colStr : rowList) {

                cell = row.createCell(colIndex++);
                cell.setCellValue(colStr);
            }
        }

        return workbook;
    }

    @Override
    public String getValue(Cell cell) {

        if(cell == null) {
            return "";
        }

       //cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }

    @Override
    public boolean deptCdRegex(String deptCd) {
        Matcher userIdMatcher = Pattern.compile("(^[a-zA-Z0-9_.-]*$)").matcher(deptCd);
        if(userIdMatcher.find()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean userIdRegex(String userId) {
        Matcher userIdMatcher = Pattern.compile("(^[a-z0-9]*$)").matcher(userId);
        if(userIdMatcher.find()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean emailRegex(String email) {

        Matcher emailMatcher = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$").matcher(email);
        if(emailMatcher.find()) {
            return true;
        }
        return false;
    }
}
