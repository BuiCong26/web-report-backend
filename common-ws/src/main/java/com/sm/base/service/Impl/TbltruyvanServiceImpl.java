package com.sm.base.service.Impl;

import com.sm.base.common.Constant;
import com.sm.base.common.DataUtil;
import com.sm.base.common.JwtTokenUtil;
import com.sm.base.dto.MappingParamReportDTO;
import com.sm.base.dto.TbltruyvanDTO;
import com.sm.base.model.modelReport.Tbltruyvan;
import com.sm.base.repo.repoReport.TbltruyvanRepo;
import com.sm.base.service.MappingParamReportService;
import com.sm.base.service.TbltruyvanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TbltruyvanServiceImpl implements TbltruyvanService {

    @Autowired
    Environment env;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    TbltruyvanRepo repo;

    @Autowired
    MappingParamReportService mappingParamReportService;

    @Autowired
    @PersistenceContext(unitName = Constant.CONFIG_DB_REPORT.UNIT_NAME_ENTITIES_REPORT)
    private EntityManager emdbReport;


    @Override
    public Tbltruyvan getInforReportForId(Long id) throws Exception {
        String sql = "select * from tbltruyvan where id = :id and type_role = 2";
        Query query = emdbReport.createNativeQuery(sql, Tbltruyvan.class);
        query.setParameter("id", id);
        List<Tbltruyvan> lst = query.getResultList();
        if (lst != null && lst.size() > 0) {
            return lst.get(0);
        }
        return null;
    }

    @Override
    public Tbltruyvan getInforReportForAdmin(Long id) throws Exception {
        String sql = "select * from tbltruyvan where id = :id and type_role = 1";
        Query query = emdbReport.createNativeQuery(sql, Tbltruyvan.class);
        query.setParameter("id", id);
        List<Tbltruyvan> lst = query.getResultList();
        if (lst != null && lst.size() > 0) {
            return lst.get(0);
        }
        return null;
    }

    @Override
    public List<Tbltruyvan> getAllReport() throws Exception {
        String sql = "select * from tbltruyvan where status = 1 and type_role = 2";
        Query query = emdbReport.createNativeQuery(sql, Tbltruyvan.class);
        return query.getResultList();
    }

    @Override
    public List<Tbltruyvan> getAllReportForAdmin() throws Exception {
        String sql = "select * from tbltruyvan where status = 1 and type_role = 1";
        Query query = emdbReport.createNativeQuery(sql, Tbltruyvan.class);
        return query.getResultList();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSqlForReport(String script, Long id) throws Exception {
        String sqlStr = "update billing_web.tbltruyvan set sql = :sql where  status = 1 and id = :id";
        Query query = emdbReport.createNativeQuery(sqlStr, Tbltruyvan.class);
        query.setParameter("sql", DataUtil.getString(script));
        query.setParameter("id", id);
        return query.executeUpdate();
    }


    @Override
    public List<Object[]> excuteSql(String sql) throws Exception {
        List<Object[]> lst = new ArrayList<>();
        if (!DataUtil.isNullObject(sql)) {
            sql = sql + " and rownum < 400";
            Query query = emdbReport.createNativeQuery(sql);
            lst = query.getResultList();
            return lst;
        }
        return lst;
    }

    @Override
    public List<String> getHeader (String table) throws Exception{
        List<String> lstHeader = new ArrayList<>();
        String sqlStr = "SELECT   column_name\n" +
                "  FROM   USER_TAB_COLUMNS\n" +
                " WHERE   table_name = UPPER (:tableName)";
        Query query = emdbReport.createNativeQuery(sqlStr);
        query.setParameter("tableName", DataUtil.getString(table));
        lstHeader = query.getResultList();
        return lstHeader;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveInfoRepoprt(TbltruyvanDTO dto) throws Exception{
        int result = 1;
        String sqlStr = "INSERT INTO tbltruyvan " +
                "  VALUES   (:reportName,:tableName,:id, 1,:sql, :desc, :typeRole) ";
        Query query = emdbReport.createNativeQuery(sqlStr);
        query.setParameter("reportName", DataUtil.getString(dto.getReportName()));
        query.setParameter("tableName", DataUtil.getString(dto.getTableName()));
        query.setParameter("sql", DataUtil.getString(dto.getSql()));
        query.setParameter("typeRole", DataUtil.getString(dto.getTypeRole()));
        query.setParameter("desc", DataUtil.getString(dto.getDescription()));
        Long reportId = DataUtil.getSequence(emdbReport,"tbltruyvan_seq");
        query.setParameter("id", reportId);
        int check =  query.executeUpdate();
        if(check == 1){
            if(dto.getLstParams().size() >0){
                for(MappingParamReportDTO paramDTO : dto.getLstParams()){
                    paramDTO.setReportId(reportId);
                    result = mappingParamReportService.saveParamsForNewReport(paramDTO);
                }
            }

        }
        return result;
    }


}
