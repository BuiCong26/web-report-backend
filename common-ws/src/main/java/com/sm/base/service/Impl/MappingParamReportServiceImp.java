package com.sm.base.service.Impl;

import com.sm.base.common.Constant;
import com.sm.base.common.DataUtil;
import com.sm.base.dto.MappingParamReportDTO;
import com.sm.base.dto.TbltruyvanDTO;
import com.sm.base.model.modelReport.MappingParamReport;
import com.sm.base.service.MappingParamReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Service
@Slf4j
public class MappingParamReportServiceImp implements MappingParamReportService {

    @Autowired
    Environment env;

    @Autowired
    @PersistenceContext(unitName = Constant.CONFIG_DB_REPORT.UNIT_NAME_ENTITIES_REPORT)
    private EntityManager emdbReport;

    @Override
    public List<MappingParamReport> getInforReportForId(Long reportId) throws Exception {
        String sql = "select * from   mapping_params_report where   status = 1 and report_id = :reportId order by order_display asc";
        Query query = emdbReport.createNativeQuery(sql, MappingParamReport.class);
        query.setParameter("reportId", reportId);
        List<MappingParamReport> lst = query.getResultList();
        if (lst != null && lst.size() > 0) {
            return lst;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveParamsForNewReport(MappingParamReportDTO dto) throws Exception {
        String sqlStr = "INSERT INTO mapping_params_report " +
                "  VALUES   (mapping_params_report_seq.NEXTVAL,\n" +
                "            :reportId,\n" +
                "            :paramCode,\n" +
                "            :columnName,\n" +
                "            1,\n" +
                "            :type,\n" +
                "            :displayNameVi,\n" +
                "            :displayNameLa,\n" +
                "            :displayNameEn,\n " +
                "            :orderDisplay )\n ";
        Query query = emdbReport.createNativeQuery(sqlStr);
        query.setParameter("reportId", dto.getReportId());
        query.setParameter("paramCode", DataUtil.getString(dto.getParamCode()));
        query.setParameter("columnName", DataUtil.getString(dto.getColumnName()));
        query.setParameter("type", dto.getTypeParam());
        query.setParameter("displayNameVi", DataUtil.getString(dto.getDisplayNameVi()));
        query.setParameter("displayNameLa", DataUtil.getString(dto.getDisplayNameLa()));
        query.setParameter("displayNameEn", DataUtil.getString(dto.getDisplayNameEn()));
        query.setParameter("orderDisplay", DataUtil.getString(dto.getOrderDisplay()));
        return query.executeUpdate();
    }
}
