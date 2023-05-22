package com.sm.base.controller;

import com.sm.base.common.Constant;
import com.sm.base.common.ResourceBundle;
import com.sm.base.dto.ExecutionResult;
import com.sm.base.model.modelReport.TblDistrict;
import com.sm.base.service.TblDistrictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api")
@Slf4j
public class TblDistrictCtrl {

    @Autowired
    private TblDistrictService tblDistrictService;

    @GetMapping(value = "/getDistrict")
    public ExecutionResult getDistrict(@RequestParam Map<String, String> map, @RequestHeader("Accept-Language") String locate){
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if ( map.get("provinceId") == null ||  map.get("provinceId") == ""){
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.id.is.null"));
                return res;
            }
            Long provinceId = Long.parseLong(map.get("provinceId").trim());
            List<TblDistrict> tblDistrict = tblDistrictService.getInforDistrictForProvinceId(provinceId);
            res.setData(tblDistrict);
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
