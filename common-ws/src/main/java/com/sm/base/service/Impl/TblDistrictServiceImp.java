package com.sm.base.service.Impl;

import com.sm.base.common.Constant;
import com.sm.base.model.modelReport.TblDistrict;
import com.sm.base.service.TblDistrictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Service
@Slf4j
public class TblDistrictServiceImp implements TblDistrictService {

    @Autowired
    Environment env;

    @Autowired
    @PersistenceContext(unitName = Constant.CONFIG_DB_REPORT.UNIT_NAME_ENTITIES_REPORT)
    private EntityManager emdbReport;

    @Override
    public List<TblDistrict> getInforDistrictForProvinceId(Long provinceId) throws Exception{
        String sql="select * FROM   tbldistrict where   province_id = :provinceid and status = 1";
        Query query = emdbReport.createNativeQuery(sql, TblDistrict.class);
        query.setParameter("provinceid", provinceId);
        List<TblDistrict> lst = query.getResultList();
        if (lst != null && lst.size() > 0) {
            return lst;
        }
        return null;
    }
}
