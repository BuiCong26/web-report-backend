package com.sm.base.controller;

import com.sm.base.common.Constant;
import com.sm.base.common.DataUtil;
import com.sm.base.common.ResourceBundle;
import com.sm.base.dto.ExecutionResult;
import com.sm.base.dto.LogsDTO;
import com.sm.base.model.modelReport.Logs;
import com.sm.base.model.modelReport.Tbltruyvan;
import com.sm.base.repo.repoReport.LogsRepo;
import com.sm.base.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping(value = "/api")
@Slf4j
public class LogsCtrl {


    @Autowired
    private LogsRepo logsRepo;

    @Autowired
    LogsService logsService;

    @Autowired
    TbltruyvanService tbltruyvanService;

    @Autowired
    @PersistenceContext(unitName = Constant.CONFIG_DB_REPORT.UNIT_NAME_ENTITIES_REPORT)
    private EntityManager emdbReport;

    private static CellStyle cellStyleFormatNumber = null;

    private static final int BUFFER_SIZE = 4096;

    private static HttpServletResponse servletResponse;

    @PostMapping(value = "/saveLogsAction")
    public ExecutionResult saveLogsAction(@RequestBody Logs dto, @RequestHeader("Accept-Language") String locate) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (DataUtil.isNullOrEmpty(dto.getUserName()) || dto.getUserName().trim().length() > 50) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("user.name.is.null"));
                return res;
            }
            if (DataUtil.isNullOrEmpty(dto.getActionType())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("action.type.is.null"));
                return res;
            }
            if (DataUtil.isNullOrEmpty(dto.getAction()) || dto.getAction().trim().length() > 200) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("action.is.null"));
                return res;
            }
            dto.setId(DataUtil.getSequence(emdbReport, "logs_seq"));
            dto.setLogTime(new Date());
            logsRepo.save(dto);

        } catch (Exception e) {
            log.error("Error get info report type: ", e);
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(e.getMessage());
        }
        return res;
    }

    @PostMapping(value = "/searchLogs")
    public ExecutionResult searchLogs(@RequestBody LogsDTO dto, @RequestHeader("Accept-Language") String locate) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        String sql = "";
        Tbltruyvan tbltruyvan = new Tbltruyvan();
        try {

            if (DataUtil.isNullOrEmpty(dto.getFromDate())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("fromdate.is.null"));
                return res;
            }

            if (DataUtil.isNullOrEmpty(dto.getToDate())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("todate.is.null"));
                return res;
            }

            if (!DataUtil.isNullOrEmpty(dto.getFromDate())&&!DataUtil.isNullOrEmpty(dto.getToDate())){
                String fromDate = dto.getFromDate();
                String toDate = dto.getToDate();
                if (fromDate.compareTo(toDate)>0) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("fromdate.invalid"));
                    return res;
                }
            }
            if (DataUtil.isNullOrEmpty(dto.getReportId())) {
                sql = "SELECT * FROM logs where log_time >= to_date(:from_date,'dd/mm/yyyy') AND log_time < to_date(:to_date,'dd/mm/yyyy')+1 order by log_time DESC";
            }else {
                tbltruyvan = tbltruyvanService.getInforReportForAdmin(dto.getReportId());
                if (tbltruyvan == null) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("report.id.is.invalid"));
                    log.error(r.getResourceMessage("report.id.is.invalid"));
                    return res;
                }
                sql = tbltruyvan.getSql().trim();
            }
            if(sql != null){
                if(dto.getFromDate() != null){
                    sql = sql.replace(":from_date", "'"+dto.getFromDate()+"'");
                }else {
                    sql = sql.replace(":from_date", "log_time");
                }

                if(dto.getToDate() != null){
                    sql = sql.replace(":to_date", "'"+dto.getToDate()+"'");
                }else {
                    sql = sql.replace(":to_date", "log_time");
                }
                dto.setSql(sql);
                List<Logs> lstLogs = logsService.searchLogs(sql);
                for(Logs obj: lstLogs ){
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String strDate = formatter.format(obj.getLogTime());
                    if (obj.getActionType() == 1L) {
                        obj.setStrActionType("Login");
                    } else if (obj.getActionType() == 2L) {
                        obj.setStrActionType("Logout");
                    } else if (obj.getActionType() == 3L) {
                        obj.setStrActionType("Download file");
                    }

                    obj.setStrLogTime(strDate);
                }
                res.setData(lstLogs);
                res.setDescription("OK");
            }



        } catch (Exception e) {
            log.error("Error get info report type: ", e);
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(e.getMessage());
        }
        return res;
    }

}
