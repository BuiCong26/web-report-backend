package com.sm.base.service.Impl;

import com.sm.base.common.DataUtil;
import com.sm.base.model.modelReport.TblProvince;
import com.sm.base.service.TblProvinceService;
import com.sm.base.common.Constant;
import com.sm.base.common.JwtTokenUtil;
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
public class TblProvinceServiceImp implements TblProvinceService {

    @Autowired
    Environment env;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    @PersistenceContext(unitName = Constant.CONFIG_DB_REPORT.UNIT_NAME_ENTITIES_REPORT)
    private EntityManager emdbReport;

    @Override
    public List<TblProvince> getAllReport() throws Exception {
        String sql = "select * from tblprovince";
        Query query = emdbReport.createNativeQuery(sql, TblProvince.class);
        return query.getResultList();

    }

    @Override
    public TblProvince getProvinceByProviceCode(String provinceCode) throws Exception{
        String sql = "select * from tblprovince where province_code = :provinceCode ";
        Query query = emdbReport.createNativeQuery(sql, TblProvince.class);
        query.setParameter("provinceCode", DataUtil.getString(provinceCode));
        List<TblProvince> lst = query.getResultList();
        if(lst.size() > 0){
            return lst.get(0);
        }
        return null;
    }

}
