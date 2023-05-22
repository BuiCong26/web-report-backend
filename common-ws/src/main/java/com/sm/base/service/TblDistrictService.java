package com.sm.base.service;

import com.sm.base.model.modelReport.TblDistrict;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

public interface TblDistrictService {

    List<TblDistrict> getInforDistrictForProvinceId(Long provinceId) throws Exception;
}
