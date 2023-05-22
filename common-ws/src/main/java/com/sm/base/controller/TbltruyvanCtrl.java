/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sm.base.controller;

import com.opencsv.CSVWriter;
import com.sm.base.common.Constant;
import com.sm.base.common.DataUtil;
import com.sm.base.common.ResourceBundle;
import com.sm.base.dto.ExecutionResult;
import com.sm.base.dto.TbltruyvanDTO;
import com.sm.base.model.modelReport.ConfigParams;
import com.sm.base.model.modelReport.Logs;
import com.sm.base.model.modelReport.MappingParamReport;
import com.sm.base.model.modelReport.Tbltruyvan;
import com.sm.base.repo.repoReport.LogsRepo;
import com.sm.base.service.ConfigParamsService;
import com.sm.base.service.TbltruyvanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/api")
@Slf4j
public class TbltruyvanCtrl {


    @Autowired
    private TbltruyvanService tbltruyvanService;

    @Autowired
    private ConfigParamsService configParamsService;
    @Autowired
    ServletContext servletContext;

    @Autowired
    private LogsRepo logsRepo;

    @Autowired
    @PersistenceContext(unitName = Constant.CONFIG_DB_REPORT.UNIT_NAME_ENTITIES_REPORT)
    private EntityManager emdbReport;


    private static CellStyle cellStyleFormatNumber = null;

    private static final int BUFFER_SIZE = 4096;

    private static HttpServletResponse servletResponse;

    private String ExcelPath;

    public String getExcelPath() {
        return ExcelPath;
    }

    public void setExcelPath(String excelPath) {
        ExcelPath = excelPath;
    }

    @PostMapping(value = "/getInforReport")
    public ExecutionResult getInforReport(@RequestBody Tbltruyvan dto, @RequestHeader("Accept-Language") String locate) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (dto == null || DataUtil.isNullOrEmpty(dto.getId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("id.is.null"));
                return res;
            }
            Tbltruyvan tbltruyvan = tbltruyvanService.getInforReportForId(dto.getId());
            if (tbltruyvan == null) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("report.id.is.invalid"));
                log.error(r.getResourceMessage("report.id.is.invalid"));
                return res;
            }
            res.setData(tbltruyvan);
            res.setDescription("");
            return res;

        } catch (Exception e) {
            log.error("Error get info report type: ", e);
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(e.getMessage());
        }
        return res;
    }


    @GetMapping(value = "/updateDataForOldReport")
    public ExecutionResult updateDataForOldReport(@RequestHeader("Accept-Language") String locate) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            List<Tbltruyvan> lstReport = tbltruyvanService.getAllReport();
            if (lstReport != null && lstReport.size() > 1) {
                for (Tbltruyvan dto : lstReport) {
                    List<String> lstParams = new ArrayList<>();
                    String sql = " select * from billing_web.$table_name a  where a.date_time >= to_date(:from_date,'dd/mm/yyyy') and a.date_time < to_date(:to_date,'dd/mm/yyyy')+1 $where ";
                    lstParams.add("from_date");
                    lstParams.add("to_date");
                    if (dto.getTableName() == "vas_service_daily_by_province") {
                        sql = "select sum(spent_basic),total_sub_spent, partner_bsd, code_center_business,service_code_bsd, date_time from vas_service_daily_by_province where "
                                + " date_time >= to_date(:from_date,'dd/mm/yyyy') and  date_time < to_date(:to_date,'dd/mm/yyyy') +1 group by date_time, code_center_business,service_code_bsd,total_sub_spent,partner_bsd ";
                    } else {
                        sql = sql.replace("$table_name", dto.getTableName());
                        if (dto.getTableName() == "tbl_register_sev3g_daily_ex" || dto.getTableName() == "tbl_register_sev3g_sum_ex") {
                            sql = sql.replace("$where", " and a.province_name =:province_name ");
                            lstParams.add("province_name");
                        } else {
                            sql = sql.replace("$where", " and a.province_code=:province_code ");
                            lstParams.add("province_code");
                        }
                    }
                    List<String> header = tbltruyvanService.getHeader(dto.getTableName());
                    String strHeader = "";
                    if (header.size() > 0) {
                        strHeader = header.toString().replace("[", "");
                        strHeader = strHeader.replace("]", "");
                    }
                    lstParams.add("header");

                    tbltruyvanService.updateSqlForReport(sql, dto.getId());
                    System.out.println("-----done id------" + dto.getId());
                    for (String param : lstParams) {
                        MappingParamReport configParams = new MappingParamReport();
                        configParams.setParamCode(param);
                        configParams.setReportId(dto.getId());
                        configParams.setColumnName(param);
                        if (param == "header") {
                            configParams.setDisplayNameEn(strHeader);
                            configParams.setDisplayNameVi(strHeader);
                            configParams.setDisplayNameVi(strHeader);
                        } else {
                            configParams.setDisplayNameEn(param.replace("_", " "));
                            configParams.setDisplayNameVi(param.replace("_", " "));
                            configParams.setDisplayNameVi(param.replace("_", " "));
                        }
                        configParamsService.insertParamsForReport(configParams);
                    }
                }
            }
            res.setData(null);
            res.setDescription("");
            return res;


        } catch (Exception e) {
            log.error("Error when update SQL: ", e);
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(e.getMessage());
        }
        return res;
    }

    @GetMapping(value = "/viewReport")
    public ExecutionResult viewReport(@RequestParam Map<String, String> map, @RequestHeader("Accept-Language") String locate) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (map.get("reportId") == null) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("id.is.null"));
                return res;
            }
            Long reportId = Long.parseLong(map.get("reportId").trim());
            List<MappingParamReport> lstParams = configParamsService.getParamsFromIdRport(reportId);
                Tbltruyvan tbltruyvan = tbltruyvanService.getInforReportForId(reportId);
            String sql = tbltruyvan.getSql();
            if (!DataUtil.isNullObject(sql)) {
                for (MappingParamReport dto : lstParams) {
                    if (!map.containsKey(dto.getParamCode())) {
                        sql = sql.replace(":" + dto.getParamCode(), dto.getParamCode());
                    } else {
                        sql = sql.replace(":" + dto.getParamCode(), map.get(dto.getParamCode()));
                    }
                }
            }
            List lst = tbltruyvanService.excuteSql(sql);
            res.setData(lst);
            res.setDescription("");
            return res;

        } catch (Exception e) {
            log.error("Error when view report: ", e);
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(e.getMessage());
        }
        return res;
    }


    @GetMapping(value = "/exportFileExcel")
    public ResponseEntity exportFileExcel(@RequestParam Map<String, String> map, @RequestHeader("Accept-Language") String locate) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK);
        File file = null;
        try {
            if (DataUtil.isNullOrEmpty(map.get("reportId"))) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("id.is.null"));
                log.error(r.getResourceMessage("id.is.null"));
                return responseEntity;
            }

            if (DataUtil.isNullOrEmpty(map.get("staffCode")) ) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("staff.code.is.null"));
                log.error(r.getResourceMessage("staff.code.is.null"));
                return responseEntity;
            }

            Long reportId = Long.parseLong(map.get("reportId").trim());
            String staffCode = map.get("staffCode").trim();
            Tbltruyvan tbltruyvan = tbltruyvanService.getInforReportForId(reportId);
            if (tbltruyvan == null) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("report.id.is.invalid"));
                log.error(r.getResourceMessage("report.id.is.invalid"));
                return responseEntity;
            }

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String dateFormat = formatter.format(date);
            if (map.get("from_date") == null) {
                map.put("from_date", "'" + dateFormat + "'");
            }
            if (map.get("to_date") == null) {
                map.put("to_date", "'" + dateFormat + "'");
            }

            List<MappingParamReport> lstParams = configParamsService.getParamsFromIdRport(reportId);
            String sql = tbltruyvan.getSql();
            String header = null;
            MappingParamReport mappingParamReport = lstParams.stream().filter(x -> "header".equals(x.getParamCode())).findAny().orElse(null);
            if (mappingParamReport != null) {
                header = mappingParamReport.getDisplayNameEn();
            }

            if (!DataUtil.isNullObject(sql)) {
                for (MappingParamReport dto : lstParams) {
                    if(dto.getParamCode().equals(Constant.CONSTANT_REPORT.PROVICE_CODE)){
                        if(!header.contains(dto.getParamCode().toUpperCase())){
                            sql = sql.replace(Constant.CONSTANT_REPORT.SQL_PROVICE_CODE, "");
                        }
                    }
                    if (!map.containsKey(dto.getParamCode())|| map.get(dto.getParamCode()).isEmpty()) {
                        sql = sql.replace(":" + dto.getParamCode(), dto.getParamCode());
                    } else {
                        sql = sql.replace(":" + dto.getParamCode(), "'" + map.get(dto.getParamCode()) + "'");
                    }
                }
            }
            log.info("-----query export-----"+sql);
            responseEntity = exportExcel(header, sql, staffCode, tbltruyvan.getReportName(), true);


        } catch (Exception e) {
            log.error("Error when export Excel: ", e);
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(e.getMessage());
            log.error(e.getMessage());
        }
        return responseEntity;
    }

    public ResponseEntity exportExcel(String header, String sql, String staffCode, String reportName, Boolean checkSaveLogs) {
        File file = null;
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK);
        DateFormat dateFormatOrder = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        try {
            String[] lstHerder = header.split(",");
            List<Object[]> lst = tbltruyvanService.excuteSql(sql);
            Date now = Calendar.getInstance().getTime();
            String strNow = new SimpleDateFormat("yyyyMMddHHmmss").format(now);
            String strDateTime = new SimpleDateFormat("yyyyMMdd").format(now);
            String fileName = reportName + "_" + staffCode + strNow + ".xlsx";
            String excelFilePath = servletContext.getRealPath("/templates/" + fileName);
            setExcelPath(excelFilePath);
            File fileFolder = new File(excelFilePath.replace(fileName,""));
            if (!fileFolder.exists()){
                fileFolder.mkdirs();
            }
            writeExcel(lst, getExcelPath(), lstHerder);
            file = new File(getExcelPath());
            String mimeType = "application/vnd.ms-excel";
            String typeFile = "xlsx";
            InputStreamResource resource = new InputStreamResource(new FileInputStream(getExcelPath()));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("content-disposition", "attachment; filename=" + getExcelPath() + dateFormatOrder.format(date) + "." + typeFile);
            if (typeFile.equals("pdf")) {
                mimeType = "application/pdf";
            }
            responseEntity = ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType(mimeType))
                    .body(resource);
            if(checkSaveLogs){
                saveLogs(fileName,3L, staffCode);
            }

        } catch (Exception e) {
            log.error("Error when export excel: ", e);
            log.error(e.getMessage());
        }
        return responseEntity;
    }

    public void writeExcel(List<Object[]> lstObject, String excelFilePath, String[] lstHeader) throws IOException {
        // Create Workbook
        Workbook workbook = getWorkbook(excelFilePath);

        // Create sheet
        Sheet sheet = workbook.createSheet("Books"); // Create sheet with sheet name

        int rowIndex = 0;

        // Write header
        writeHeader(sheet, rowIndex, lstHeader);

        // Write data
        rowIndex++;
        for (Object[] obj : lstObject) {
            // Create row
            Row row = sheet.createRow(rowIndex);
            // Write data on row
            writeBook(obj, row, lstHeader.length);
            rowIndex++;
        }
        // Auto resize column witdth
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        autosizeColumn(sheet, numberOfColumn);

        // Create file excel
        createOutputFile(workbook, excelFilePath);
        System.out.println("Done!!!");
    }

    // Create workbook
    private Workbook getWorkbook(String excelFilePath) throws IOException {
        Workbook workbook = null;

        if (excelFilePath.endsWith("xlsx")) {
            workbook = new XSSFWorkbook();
        } else if (excelFilePath.endsWith("xls")) {
            workbook = new HSSFWorkbook();
        } else if (excelFilePath.endsWith("csv")) {
            workbook = new HSSFWorkbook();
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }

        return workbook;
    }

    // Write header with format
    private void writeHeader(Sheet sheet, int rowIndex, String[] lstHeader) {
        // create CellStyle
        CellStyle cellStyle = createStyleForHeader(sheet);

        // Create row
        Row row = sheet.createRow(rowIndex);

        // Create cells
        for (int i = 0; i < lstHeader.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(lstHeader[i]);
        }
    }

    // Write data
    private void writeBook(Object[] obj, Row row, int size) {
        if (cellStyleFormatNumber == null) {
            // Format number
            short format = (short) BuiltinFormats.getBuiltinFormat("#,##0");
            // DataFormat df = workbook.createDataFormat();
            // short format = df.getFormat("#,##0");

            //Create CellStyle
            Workbook workbook = row.getSheet().getWorkbook();
            cellStyleFormatNumber = workbook.createCellStyle();
            cellStyleFormatNumber.setDataFormat(format);
        }

        for (int i = 0; i < size; i++) {
            Cell cell = row.createCell(i);
            if (obj[i] != null) {
                cell.setCellValue(obj[i].toString());
            }

        }
    }

    // Create CellStyle for header
    private CellStyle createStyleForHeader(Sheet sheet) {
        // Create font
        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setBold(true);
        font.setFontHeightInPoints((short) 14); // font size
        font.setColor(IndexedColors.WHITE.getIndex()); // text color

        // Create CellStyle
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        return cellStyle;
    }

    // Auto resize column width
    private void autosizeColumn(Sheet sheet, int lastColumn) {
        for (int columnIndex = 0; columnIndex < lastColumn; columnIndex++) {
            sheet.autoSizeColumn(columnIndex);
        }
    }

    // Create output file
    private void createOutputFile(Workbook workbook, String excelFilePath) throws IOException {
        try (OutputStream os = new FileOutputStream(excelFilePath)) {
            workbook.write(os);
        }
    }


    @GetMapping(value = "/exportFileCSV")
    public ResponseEntity exportFileCSV(@RequestParam Map<String, String> map, @RequestHeader("Accept-Language") String locate) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK);
        DateFormat dateFormatOrder = new SimpleDateFormat("yyyyMMddHHmmss");
        File file = null;
        try {
            if (DataUtil.isNullOrEmpty(map.get("reportId"))) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("id.is.null"));
                log.error(r.getResourceMessage("id.is.null"));
                return responseEntity;
            }

            if (DataUtil.isNullOrEmpty(map.get("staffCode"))) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("staff.code.is.null"));
                log.error(r.getResourceMessage("staff.code.is.null"));
                return responseEntity;
            }


            Long reportId = Long.parseLong(map.get("reportId").trim());
            String staffCode = map.get("staffCode").trim();
            Tbltruyvan tbltruyvan = tbltruyvanService.getInforReportForId(reportId);

            if (tbltruyvan == null) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("report.id.is.invalid"));
                log.error(r.getResourceMessage("report.id.is.invalid"));
                return responseEntity;
            }

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String dateFormat = formatter.format(date);
            if (map.get("from_date") == null) {
                map.put("from_date", "'" + dateFormat + "'");
            }
            if (map.get("to_date") == null) {
                map.put("to_date", "'" + dateFormat + "'");
            }

            List<MappingParamReport> lstParams = configParamsService.getParamsFromIdRport(reportId);
            String sql = tbltruyvan.getSql();
            String header = null;
            MappingParamReport mappingParamReport = lstParams.stream().filter(x -> "header".equals(x.getParamCode())).findAny().orElse(null);
            if (mappingParamReport != null) {
                header = mappingParamReport.getDisplayNameEn();
            }
            if (!DataUtil.isNullObject(sql)) {
                for (MappingParamReport dto : lstParams) {
                    if(dto.getParamCode().equals(Constant.CONSTANT_REPORT.PROVICE_CODE)){
                        if(!header.contains(dto.getParamCode().toUpperCase())){
                            sql = sql.replace(Constant.CONSTANT_REPORT.SQL_PROVICE_CODE, "");
                        }
                    }
                    if (!map.containsKey(dto.getParamCode()) || map.get(dto.getParamCode()).isEmpty()) {
                        sql = sql.replace(":" + dto.getParamCode(), dto.getParamCode());
                    } else {
                        sql = sql.replace(":" + dto.getParamCode(), "'" + map.get(dto.getParamCode()) + "'");
                    }
                }
            }
            String[] lstHeader = header.split(",");
            List<Object[]> lst = tbltruyvanService.excuteSql(sql);
            List<String[]> lstData = new ArrayList<>();
            lstData.add(lstHeader);
            for (Object[] obj : lst) {
                String[] s = new String[lstHeader.length + 1];
                for (int i = 0; i < obj.length; i++) {
                    if (obj[i] == null) {
                        obj[i] = "     ";
                    }
                    s[i] = obj[i].toString();
                }
                lstData.add(s);
            }
            Date now = Calendar.getInstance().getTime();
            String strNow = new SimpleDateFormat("yyyyMMddHHmmss").format(now);
            String strDateTime = new SimpleDateFormat("yyyyMMdd").format(now);
            String fileName = tbltruyvan.getReportName() + "_" + staffCode + strNow + ".csv";
            final String csvFilePath = servletContext.getRealPath("/templates/" + fileName);
            CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
            File fileFolder = new File(csvFilePath.replace(fileName,""));
            if (!fileFolder.exists()){
                System.out.println("------Createn file-----");
                fileFolder.mkdirs();
            }
            writeExcel(lst, csvFilePath, lstHeader);
            file = new File(csvFilePath);
            String mimeType = "text/csv";
            String typeFile = "csv";
            InputStreamResource resource = new InputStreamResource(new FileInputStream(csvFilePath));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("content-disposition", "attachment; filename=" + csvFilePath + dateFormatOrder.format(date) + "." + typeFile);
            if (typeFile.equals("pdf")) {
                mimeType = "application/pdf";
            }
            responseEntity = ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType(mimeType))
                    .body(resource);


            saveLogs(fileName,3L, staffCode);
            res.setData(csvFilePath);

        } catch (Exception e) {
            log.error("Error when export CSV: ", e);
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(e.getMessage());
            log.error(e.getMessage());
        }
        return responseEntity;
    }

    //Start: CongBT
    @GetMapping(value = "/exportTXT")
    public ResponseEntity exportTXT(@RequestParam Map<String, String> map, @RequestHeader("Accept-Language") String locate) {
        Writer writer = null;
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK);
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {

            if (DataUtil.isNullOrEmpty(map.get("reportId"))) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("id.is.null"));
                log.error(r.getResourceMessage("id.is.null"));
                return responseEntity;
            }

            if (DataUtil.isNullOrEmpty(map.get("staffCode"))) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("staff.code.is.null"));
                log.error(r.getResourceMessage("staff.code.is.null"));
                return responseEntity;
            }

            Long reportId = Long.parseLong(map.get("reportId").trim());
            String staffCode = map.get("staffCode").trim();

            DateFormat dateFormatOrder = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date();
            Tbltruyvan tbltruyvan = tbltruyvanService.getInforReportForId(reportId);

            if (tbltruyvan == null) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("report.id.is.invalid"));
                log.error(r.getResourceMessage("report.id.is.invalid"));
                return responseEntity;
            }

            Date now = Calendar.getInstance().getTime();
            String strNow = new SimpleDateFormat("yyyyMMddHHmmss").format(now);
            String fileNameTxt = tbltruyvan.getReportName() + "_" + staffCode + strNow + ".txt";

            List<MappingParamReport> lstParams = configParamsService.getParamsFromIdRport(reportId);
            String sql = tbltruyvan.getSql();

            String header = null;
            MappingParamReport mappingParamReport = lstParams.stream().filter(x -> "header".equals(x.getParamCode())).findAny().orElse(null);
            if (mappingParamReport != null) {
                header = mappingParamReport.getDisplayNameEn();
            }
            if (!DataUtil.isNullObject(sql)) {
                for (MappingParamReport dto : lstParams) {
                    if(dto.getParamCode().equals(Constant.CONSTANT_REPORT.PROVICE_CODE)){
                        if(!header.contains(dto.getParamCode().toUpperCase())){
                            sql = sql.replace(Constant.CONSTANT_REPORT.SQL_PROVICE_CODE, "");
                        }
                    }
                    if (!map.containsKey(dto.getParamCode()) || map.get(dto.getParamCode()).isEmpty()) {
                        sql = sql.replace(":" + dto.getParamCode(), dto.getParamCode());
                    } else {
                        sql = sql.replace(":" + dto.getParamCode(), "'" + map.get(dto.getParamCode()) + "'");
                    }
                }
            }
            responseEntity = exportExcel(header, sql, staffCode, tbltruyvan.getReportName(), false);
            final String txtFilePath = servletContext.getRealPath("/templates/" + fileNameTxt);
            FileInputStream inputStream = new FileInputStream(getExcelPath());
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet firstSheet = workbook.getSheetAt(0);
            File file = new File(txtFilePath);
            writer = new BufferedWriter(new FileWriter(file));
            Iterator<Row> iterator = firstSheet.iterator();
            String s = System.lineSeparator();
            int tabSpace = 0;
            List<Integer> maxString = new ArrayList<>();
            maxString = maxString();
            while (iterator.hasNext()) {
                int i = 0;
                Row nextRow = iterator.next();
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                while (cellIterator.hasNext()) {
                    boolean check = true;
                    Cell cell = cellIterator.next();
                    int indexCol = cell.getColumnIndex();
                    String checkString = cell.getStringCellValue();
                    while (checkString.length() < maxString.get(indexCol)) {
                        int minusCol = maxString.get(indexCol) - checkString.length();
                        for (int lengthStr = 0; lengthStr < minusCol; lengthStr++) {
                            checkString = checkString + " ";
                        }
                        int a = checkString.length();
                        cell.setCellValue(checkString);
                    }
                    writer.write(cell.getStringCellValue());
                    writer.write("\t\t");
                    tabSpace += 1;
                    if (indexCol == nextRow.getLastCellNum() - 1) {
                        writer.write(s);
                    }
                }
            }

            String mimeType = "text/plain";
            String typeFile = "txt";
            InputStreamResource resource = new InputStreamResource(new FileInputStream(txtFilePath));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("content-disposition", "attachment; filename=" + txtFilePath + dateFormatOrder.format(date) + "." + typeFile);
            if (typeFile.equals("pdf")) {
                mimeType = "application/pdf";
            }
            responseEntity = ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType(mimeType))
                    .body(resource);
            saveLogs(fileNameTxt,3L,staffCode);

            res.setData(txtFilePath);
            workbook.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error when export TXT: ", e);
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(e.getMessage());
            log.error(e.getMessage());
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return responseEntity;
    }

    public List<Integer> maxString() {
        try {
            FileInputStream inputStream = new FileInputStream(getExcelPath());

            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = firstSheet.iterator();
            int tabSpace = 0;
            List<Integer> maxString = new ArrayList<>();
            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    int getIndexCol = cell.getColumnIndex();
                    if (tabSpace < nextRow.getLastCellNum()) {
                        maxString.add(cell.getStringCellValue().length());
                    } else {
                        long lengthCol = cell.getStringCellValue().length();
                        long maxLengthCol = maxString.get(getIndexCol);
                        if (lengthCol > maxLengthCol) {
                            maxString.remove(getIndexCol);
                            maxString.add(getIndexCol, cell.getStringCellValue().length());
                        }
                    }
                    tabSpace += 1;
                }
            }
            workbook.close();
            inputStream.close();
            return maxString;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    @GetMapping(value = "/getListReport")
    public ExecutionResult getListReport(@RequestHeader("Accept-Language") String locate) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            List<Tbltruyvan> lstReport = tbltruyvanService.getAllReport();
            res.setData(lstReport);
            res.setDescription("");
            return res;
        } catch (Exception e) {
            log.error("Error get list report: ", e);
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(e.getMessage());
        }
        return res;
    }

    @GetMapping(value = "/getListReportForAdmin")
    public ExecutionResult getListReportForAdmin(@RequestHeader("Accept-Language") String locate) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            List<Tbltruyvan> lstReportForAdmin = tbltruyvanService.getAllReportForAdmin();
            res.setData(lstReportForAdmin);
            res.setDescription("");
            return res;
        } catch (Exception e) {
            log.error("Error get list report: ", e);
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(e.getMessage());
        }
        return res;
    }

//End: CongBT

    public static void writeUnicodeJava11(String fileName, String lines) {

        try (FileWriter fw = new FileWriter(new File(fileName), StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(fw)) {
            writer.append(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @PostMapping(value = "/createNewReport")
    public ExecutionResult createNewReport(@RequestBody TbltruyvanDTO dto, @RequestHeader("Accept-Language") String locate) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (DataUtil.isNullObject(dto.getReportName()) || dto.getReportName().trim().length() > 200) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("report.name.is.null"));
                return res;
            }


            dto.setReportName(dto.getReportName().trim());
            if (DataUtil.isNullObject(dto.getSql()) || dto.getSql().trim().length() > 4000) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("sql.is.null"));
                return res;
            }

            if (!DataUtil.isNullObject(dto.getDescription()) && dto.getDescription().trim().length() > 500) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("desc.is.invalid"));
                return res;
            }

            if (!DataUtil.isNullObject(dto.getTableName()) && dto.getTableName().trim().length() > 100) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("table.name.is.null"));
                return res;
            }
            dto.setSql(dto.getSql().trim());
            int checkSave = tbltruyvanService.saveInfoRepoprt(dto);
            if (checkSave != 1) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("add.new.report.is.error"));
            }
            res.setDescription("OK");
            return res;

        } catch (Exception e) {
            log.error("Error when view report: ", e);
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(e.getMessage());
        }
        return res;
    }


    @PostMapping(value = "/changeLanguageParams")
    public ExecutionResult changeLanguageParams(@RequestBody MappingParamReport dto, @RequestHeader("Accept-Language") String locate) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            configParamsService.updateLanguageParams(dto);
            res.setDescription("OK");
            return res;

        } catch (Exception e) {
            log.error("Error get info report type: ", e);
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(e.getMessage());
        }
        return res;
    }

    @GetMapping(value = "/getHeader")
    public ExecutionResult getHeader(@RequestHeader("Accept-Language") String locate) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            List<Tbltruyvan> lstReport = tbltruyvanService.getAllReport();
            for (Tbltruyvan dto : lstReport) {
                List<String> lstParams = new ArrayList<>();
                List<String> header = tbltruyvanService.getHeader(dto.getTableName());
                String strHeader = "";
                if (header.size() > 0) {
                    strHeader = header.toString().replace("[", "");
                    strHeader = strHeader.replace("]", "");
                }
                MappingParamReport configParams = new MappingParamReport();
                configParams.setDisplayNameEn(strHeader);
                configParams.setReportId(dto.getId());
                configParamsService.insertHeader(configParams);

            }
            res.setDescription("OK");
            return res;

        } catch (Exception e) {
            log.error("Error get info report type: ", e);
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(e.getMessage());
        }
        return res;
    }

    public void saveLogs(String action, Long actionType, String userName) {
        // lưu logs
        action = "Download " + action;
        Logs model = new Logs();
        model.setLogTime(new Date());
        model.setUserName(userName);
        model.setAction(action);
        model.setActionType(actionType);
        model.setId(DataUtil.getSequence(emdbReport, "logs_seq"));
        model.setLogTime(new Date());
        logsRepo.save(model);
        // end lưu logs
    }

}