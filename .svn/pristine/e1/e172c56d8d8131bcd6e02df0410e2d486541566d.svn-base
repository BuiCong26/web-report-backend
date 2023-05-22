package com.sm.base.controller;

import com.sm.base.common.Constant;
import com.sm.base.common.ResourceBundle;
import com.sm.base.dto.ExecutionResult;
import com.sm.base.model.modelReport.MappingParamReport;
import com.sm.base.service.MappingParamReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api")
@Slf4j
public class MappingParamReportCtrl {

    @Autowired
    MappingParamReportService mappingParamReportService;

    @GetMapping(value = "/getParamsCofig")
    public ExecutionResult getParamsCofig(@RequestParam Map<String, String> map, @RequestHeader("Accept-Language") String locate){
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (map.get("reportId") == null || map.get("reportId") == "" ){
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("report.id.is.null"));
                return res;
            }
            Long reportId = Long.parseLong(map.get("reportId").trim());
            List<MappingParamReport> mappingParamReport = mappingParamReportService.getInforReportForId(reportId);
            res.setData(mappingParamReport);
            res.setDescription("");
            return res;
        } catch (Exception e){
            log.error("Error get info report type: ", e);
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(e.getMessage());
        }
        return res;
    }
}
