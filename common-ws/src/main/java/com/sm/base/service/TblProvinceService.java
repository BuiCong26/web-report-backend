package com.sm.base.service;

import com.sm.base.model.modelReport.TblProvince;

import java.util.List;

public interface TblProvinceService {

    List<TblProvince> getAllReport() throws Exception;

    TblProvince getProvinceByProviceCode(String provinceCode) throws Exception;


}
