package com.sm.base.controller;

import com.sm.base.common.Constant;
import com.sm.base.common.DataUtil;
import com.sm.base.common.ResourceBundle;
import com.sm.base.dto.ExecutionResult;
import com.sm.base.model.modelReport.TblProvince;
import com.sm.base.service.ConfigParamsService;
import com.sm.base.service.TblProvinceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
@Slf4j
public class TblProvinceCtrl {

    @Autowired
    private TblProvinceService tblProvinceService;

    @Autowired
    private ConfigParamsService configParamsService;

    @GetMapping(value = "/getAllProvince")
    public ExecutionResult getAllProvince(@RequestHeader("Accept-Language") String locate) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            List<TblProvince>  lstProvince = tblProvinceService.getAllReport();
            res.setData(lstProvince);
            res.setDescription("");
            return res;
        } catch (Exception e) {
            log.error("Error get info report type: ", e);
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(e.getMessage());
        }
        return res;
    }

    @GetMapping(value = "/getProvinceByProviceCode")
    public ExecutionResult getProvinceByProviceCode(@RequestParam String provinceCode ,@RequestHeader("Accept-Language") String locate) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if(DataUtil.isNullOrEmpty(provinceCode)){
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.id.is.null"));
                return res;
            }
            provinceCode = provinceCode.trim();
            TblProvince  province = tblProvinceService.getProvinceByProviceCode(provinceCode);
            res.setData(province);
            res.setDescription("");
            return res;
        } catch (Exception e) {
            log.error("Error get info report type: ", e);
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(e.getMessage());
        }
        return res;
    }
}
