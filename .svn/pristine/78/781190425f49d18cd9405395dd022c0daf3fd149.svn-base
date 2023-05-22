package com.sm.base.service.Impl;

import com.sm.base.common.Constant;
import com.sm.base.common.DataUtil;
import com.sm.base.common.JwtTokenUtil;
import com.sm.base.model.modelReport.ConfigParams;
import com.sm.base.model.modelReport.MappingParamReport;
import com.sm.base.model.modelReport.Tbltruyvan;
import com.sm.base.repo.repoReport.TbltruyvanRepo;
import com.sm.base.service.ConfigParamsService;
import com.sm.base.service.TbltruyvanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ConfigParamsServiceImpl implements ConfigParamsService {

    @Autowired
    Environment env;
    @Autowired
    @PersistenceContext(unitName = Constant.CONFIG_DB_REPORT.UNIT_NAME_ENTITIES_REPORT)
    private EntityManager emdbReport;


    @Override
    public  List<MappingParamReport> getParamsFromIdRport(Long id) throws Exception{
        String sqlStr = " select * from mapping_params_report where status = 1 and  report_id = :id ";
        Query query = emdbReport.createNativeQuery(sqlStr, MappingParamReport.class);
        query.setParameter("id",id);
         List<MappingParamReport> lst = query.getResultList();
        return lst;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertHeader(MappingParamReport dto) throws Exception{
        String sqlStr = "update mapping_params_report set \n" +
                "display_name_en = :header, display_name_vi = :header, display_name_la = :header where report_id =:id ";
        Query query = emdbReport.createNativeQuery(sqlStr);
        query.setParameter("header",DataUtil.getString(dto.getDisplayNameEn()));
        query.setParameter("id",DataUtil.getLong(dto.getReportId()));
        return query.executeUpdate();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLanguageParams(MappingParamReport dto) throws Exception{
        String sqlStr = "UPDATE   mapping_params_report\n" +
                "   SET   display_name_vi = :vi,\n" +
                "         display_name_la = :la,\n" +
                "         display_name_en = :en\n" +
                " WHERE   param_code = 'header' ";
        Query query = emdbReport.createNativeQuery(sqlStr);
        query.setParameter("vi", DataUtil.getString(dto.getDisplayNameVi()));
        query.setParameter("la",DataUtil.getString(dto.getDisplayNameLa()));
        query.setParameter("en",DataUtil.getString(dto.getDisplayNameEn()));
         query.executeUpdate();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertParamsForReport(MappingParamReport dto) throws Exception{
        String sqlStr = "INSERT INTO mapping_params_report   " +
                "(id, param_code,status, report_id, column_name, type,  display_name_vi, display_name_la, display_name_en)   " +
                "VALUES  " +
                "(config_params_seq.nextval, :paramCode, 1,:reportId, :columnName, :type, :displayNameVi, :displayNameLa, :displayNameEn )";
        Query query = emdbReport.createNativeQuery(sqlStr);
        query.setParameter("paramCode",DataUtil.getString(dto.getParamCode()));
        query.setParameter("reportId",DataUtil.getLong(dto.getReportId()));
        query.setParameter("columnName",DataUtil.getString(dto.getColumnName()));
        query.setParameter("displayNameVi",DataUtil.getString(dto.getDisplayNameVi()));
        query.setParameter("displayNameLa",DataUtil.getString(dto.getDisplayNameLa()));
        query.setParameter("displayNameEn",DataUtil.getString(dto.getDisplayNameEn()));
        if(dto.getParamCode() == "from_date" || dto.getParamCode() == "to_date"){
            query.setParameter("type",2);
        } else {
            query.setParameter("type",1);
        }
        return query.executeUpdate();
    }

}
