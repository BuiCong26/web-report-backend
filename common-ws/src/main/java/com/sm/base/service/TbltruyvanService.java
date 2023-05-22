package com.sm.base.service;

import com.sm.base.dto.TbltruyvanDTO;
import com.sm.base.model.modelReport.Tbltruyvan;

import java.util.List;
import java.util.Map;

public interface TbltruyvanService {
    Tbltruyvan getInforReportForId(Long id)  throws Exception;

    Tbltruyvan getInforReportForAdmin(Long id)  throws Exception;

    List<Tbltruyvan> getAllReport() throws Exception;

    List<Tbltruyvan> getAllReportForAdmin() throws Exception;

    int updateSqlForReport(String script, Long id) throws Exception;

    List<Object[]> excuteSql(String sql) throws Exception;

    List<String> getHeader (String table) throws Exception;

    int saveInfoRepoprt(TbltruyvanDTO dto) throws Exception;



}
