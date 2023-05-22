package com.sm.base.service.Impl;

import com.sm.base.common.Constant;
import com.sm.base.common.DataUtil;
import com.sm.base.dto.LogsDTO;
import com.sm.base.dto.MappingParamReportDTO;
import com.sm.base.dto.TbltruyvanDTO;
import com.sm.base.model.modelReport.Logs;
import com.sm.base.model.modelReport.MappingParamReport;
import com.sm.base.service.ConfigParamsService;
import com.sm.base.service.LogsService;
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
public class LogsServiceImpl implements LogsService {

    @Autowired
    Environment env;

    @Autowired
    @PersistenceContext(unitName = Constant.CONFIG_DB_REPORT.UNIT_NAME_ENTITIES_REPORT)
    private EntityManager emdbReport;

    @Override
    public List<Logs> searchLogs(String sql) throws Exception{
        Query query = emdbReport.createNativeQuery(sql, Logs.class);
        List<Logs> lst = query.getResultList();
        return lst;
    }


}
