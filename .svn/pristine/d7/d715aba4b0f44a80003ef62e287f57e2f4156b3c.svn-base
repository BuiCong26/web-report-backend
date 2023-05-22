package com.sm.base.service.Impl;

import com.sm.base.common.Constant;
import com.sm.base.common.DataUtil;
import com.sm.base.common.JwtTokenUtil;
import com.sm.base.common.ResourceBundle;
import com.sm.base.dto.*;
import com.sm.base.model.modelReport.Staff;
import com.sm.base.model.modelReport.TblProvince;
import com.sm.base.repo.repoReport.StaffRepo;
import com.sm.base.service.TblProvinceService;
import com.sm.base.service.UserService;
import com.viettel.security.PassTranformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import viettel.passport.client.ObjectToken;
import viettel.passport.client.UserToken;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    Environment env;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private TblProvinceService tblProvinceService;

    @Autowired
    @PersistenceContext(unitName = Constant.CONFIG_DB_REPORT.UNIT_NAME_ENTITIES_REPORT)
    private EntityManager emdbReport;


    @Override
    public LoginDTO loginAuthentication(CommonInputDTO commonInputDTO, String locate) throws Exception {
        VSAValidate vsaValidate = new VSAValidate();
        LoginDTO loginDTO = null;
        ResourceBundle r = new ResourceBundle(locate);
        if (commonInputDTO.getPassword() == null) {
            throw new IllegalArgumentException(r.getResourceMessage("password.is.null"));
        }
        vsaValidate.setUser(commonInputDTO.getUserName());
        vsaValidate.setPassword(commonInputDTO.getPassword());
        vsaValidate.setDomainCode(env.getProperty("server.system.name"));
        vsaValidate.setCasValidateUrl(env.getProperty("server.vsa.url"));
        vsaValidate.setIp(env.getProperty("server.ip.local"));
        try {
            vsaValidate.validate();
            List<MenuParent> menuParentList = new ArrayList<>();
            loginDTO = new LoginDTO();
            if (vsaValidate.isSuccessfulAuthentication()) {
                UserToken userToken = vsaValidate.getUserToken();
                if (userToken != null) {
                    List<Staff> lstStaff = getStaffByStaffCode(userToken);
                    if (lstStaff.size() == 0) {
                        loginDTO.setReasonFail("STAFF_NOT_FOUND");
                        return loginDTO;
                    }
                    loginDTO.setUserName(userToken.getUserName());
                    String provinceCode  = lstStaff.get(0).getProvinceCode();
                    loginDTO.setProvinceCode(provinceCode);
                    if(provinceCode != null){
                        TblProvince provice = tblProvinceService.getProvinceByProviceCode(provinceCode);
                        loginDTO.setProvinceName(provice.getProvinceName());
                    }
                    loginDTO.setUserFullName(userToken.getFullName());
                    if (userToken.getRolesList() != null && userToken.getRolesList().size() > 0) {
                        loginDTO.setRoleCode(userToken.getRolesList().get(0).getRoleCode());
                        loginDTO.setRoleName(userToken.getRolesList().get(0).getRoleName());
                        loginDTO.setRoleDescription(userToken.getRolesList().get(0).getDescription());
                    }

                    ArrayList objects = userToken.getParentMenu();
                    for (int i = 0; i < objects.size(); i++) {
                        ObjectToken objectToken = (ObjectToken) objects.get(i);
                        if ("M".equals(objectToken.getObjectType())) {
                            MenuParent menuParent = new MenuParent();
                            menuParent.setTitle(r.getResourceMessage(objectToken.getObjectCode()));
                            menuParent.setCode(objectToken.getObjectCode());
                            menuParent.setRoot(true);
                            menuParent.setIcon(objectToken.getDescription());
                            menuParent.setPage(objectToken.getObjectUrl());
                            menuParent.setOrd(objectToken.getOrd());
                            ArrayList childs = objectToken.getChildObjects();
                            List<SubMenu> subMenus = new ArrayList<>();
                            for (int j = 0; j < childs.size(); j++) {
                                ObjectToken childToken = (ObjectToken) childs.get(j);
                                if ("M".equals(childToken.getObjectType())) {
                                    SubMenu subMenu = new SubMenu();
                                    subMenu.setPage(childToken.getObjectUrl());
                                    subMenu.setOrd(childToken.getOrd());
                                    subMenu.setTitle(childToken.getObjectCode());
                                    subMenus.add(subMenu);
                                }
                            }
                            menuParent.setSubmenu(subMenus);
                            menuParentList.add(menuParent);
                        }
                    }
                } else {
                    log.error("authentication unsuccessful: Token is null");
                    throw new IllegalArgumentException(r.getResourceMessage("sign.in.false"));
                }

                loginDTO.setLstMenu(menuParentList);
                PassTranformer.setInputKey(env.getProperty("key.token.secret"));
                String password = PassTranformer.encrypt(String.format("%s%s", commonInputDTO.getPassword(), env.getProperty("key.public.salt")));
                UserDetails userDetails = new User(String.format("%s%s%s", commonInputDTO.getUserName(), env.getProperty("key.public.separator"), password), password, new ArrayList<>());
                String token = jwtTokenUtil.generateToken(userDetails);
                loginDTO.setToken(token);
            } else {
                loginDTO.setReasonFail(vsaValidate.getReasonFail());
            }
        } catch (Exception ex) {
            log.error("Error loginAuthentication: ", ex);
            throw new IllegalArgumentException(r.getResourceMessage("sign.in.false"));
        }
        return loginDTO;
    }

    @Override
    public UserDetails loadUserByUsername(String decodedToken) {
        String userName;
        String password;
        try {
            if (decodedToken != null) {
                String[] decodedSplits = decodedToken.split(env.getProperty("key.public.separator"));
                if (decodedSplits != null && decodedSplits.length > 1) {
                    userName = decodedSplits[0];
                    password = decodedSplits[1];
                }else{
                    log.error("decodedToken does not match on login token decode "+ decodedToken);
                    return null;
                }
                PassTranformer.setInputKey(env.getProperty("key.token.secret"));
                password = PassTranformer.decrypt(password);
                password = password.replace(env.getProperty("key.public.salt"), "");
            } else {
                log.error("Password invalid on login token decode "+ decodedToken);
                return null;
            }
            VSAValidate vsaValidate = new VSAValidate();
            vsaValidate.setUser(userName);
            vsaValidate.setPassword(password);
            vsaValidate.setDomainCode(env.getProperty("server.system.name"));
            vsaValidate.setCasValidateUrl(env.getProperty("server.vsa.url"));
            vsaValidate.setIp(env.getProperty("server.ip.local"));
            vsaValidate.validate();
            if (vsaValidate.isSuccessfulAuthentication()) {
                password = PassTranformer.encrypt(String.format("%s%s", password, env.getProperty("key.public.salt")));
                return new User(String.format("%s%s%s", userName, env.getProperty("key.public.separator"), password), password, new ArrayList<>());
            } else {
                log.error("Authentication unsuccessful on login username " + userName);
                return null;
            }
        } catch (Exception e) {
            log.error("Authentication unsuccessful on login token decode "+ decodedToken+": ", e);
            return null;
        }
    }


    private List<Staff> getStaffByStaffCode(UserToken userToken) {
        try {
            String sql = " SELECT  *  "
                    + "    FROM   staff s "
                    + "WHERE  s.staff_code =:staffCode  and status = 1";
            Query query = emdbReport.createNativeQuery(sql, Staff.class);
            query.setParameter("staffCode", userToken.getUserName().toLowerCase());
            List<Staff> lst = query.getResultList();
            return lst;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Staff getStaffByStaffCode (String staffCode)throws Exception{
        Staff object = new Staff();
        String sql = " SELECT  *  "
                + "    FROM   staff s "
                + "WHERE  s.staff_code =:staffCode  and status = 1";
        Query query = emdbReport.createNativeQuery(sql, Staff.class);
        query.setParameter("staffCode", staffCode.trim().toLowerCase());
        List<Staff> lst = query.getResultList();
        if(lst.size() > 0){
            object = lst.get(0);
        }
        return object;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStaff(Staff staff) throws Exception{
        String sql = " UPDATE staff\n" +
                "   SET staff_code = :staffcode,\n" +
                "       staff_name = :staffname,\n" +
                "       province_code = :provincecode\n" +
                " WHERE staff_id = :staffid and status = 1 ";
        Query query = emdbReport.createNativeQuery(sql);
        query.setParameter("staffcode", staff.getStaffCode().trim().toLowerCase());
        query.setParameter("staffname", staff.getStaffName().trim());
        query.setParameter("provincecode", staff.getProvinceCode().trim());
        query.setParameter("staffid", staff.getStaffId());
        query.executeUpdate();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatusForStaff(List<String> staffCode, Long status) throws Exception{
        String sql = " UPDATE staff\n" +
                "   SET    status = :status , create_date = sysdate \n" +
                " WHERE staff_code in (:staffCode)  and status = 1";
        Query query = emdbReport.createNativeQuery(sql);
        query.setParameter("status", status);
        query.setParameter("staffCode",staffCode);
        query.executeUpdate();
    }

    @Override
    public List<StaffDTO> findStaff(Staff staff, String locate) throws Exception{
        ResourceBundle r = new ResourceBundle(locate);
        List<StaffDTO> lstResult = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT *\n" +
                "  FROM ( SELECT s.staff_code, s.staff_name, s.province_code, p.province_name\n, s.status, s.staff_id, s.create_date " +
                "FROM staff s\n" +
                "LEFT JOIN tblprovince p\n" +
                "ON s.province_code = p.province_code \n" +
                "where p.status = 1  ");
        if(!DataUtil.isNullOrEmpty(staff.getStaffCode())){
            sql.append(" AND LOWER (s.staff_code) LIKE LOWER ('%' || :staffcode || '%') ");
        }

        if(!DataUtil.isNullOrEmpty(staff.getStaffName())){
            sql.append(" AND LOWER (s.staff_name) LIKE LOWER ('%' || :staffname || '%') ");
        }

        if(!DataUtil.isNullOrEmpty(staff.getProvinceName())){
            sql.append(" AND LOWER (p.province_name) LIKE LOWER ('%' || :provincename || '%') ");
        }

        if(staff.getStatus() != null){
            sql.append(" AND s.status = :status ");
        }
        if(DataUtil.isNullOrEmpty(staff.getProvinceName())) {
            sql.append(" UNION ALL\n" +
                    "SELECT s.staff_code,\n" +
                    "       s.staff_name,\n" +
                    "       s.province_code,\n" +
                    "       NULL AS province_name\n, s.status, s.staff_id, s.create_date " +
                    "  FROM staff s\n" +
                    " WHERE     1 = 1\n" +
                    "       AND s.province_code IS NULL ");
            if(!DataUtil.isNullOrEmpty(staff.getStaffCode())){
                sql.append(" AND LOWER (s.staff_code) LIKE LOWER ('%' || :staffcode || '%') ");
            }

            if(!DataUtil.isNullOrEmpty(staff.getStaffName())){
                sql.append(" AND LOWER (s.staff_name) LIKE LOWER ('%' || :staffname || '%') ");
            }

            if(staff.getStatus() != null){
                sql.append(" AND s.status = :status ");
            }

        }

        sql.append(" ) data ");
        sql.append(" order by data.create_date desc ");
        Query query = emdbReport.createNativeQuery(sql.toString());
        if(!DataUtil.isNullOrEmpty(staff.getStaffCode())){
            query.setParameter("staffcode", DataUtil.getString(staff.getStaffCode()));
        }
        if(!DataUtil.isNullOrEmpty(staff.getStaffName())){
            query.setParameter("staffname", DataUtil.getString(staff.getStaffName()));
        }
        if(!DataUtil.isNullOrEmpty(staff.getProvinceName())){
            query.setParameter("provincename", DataUtil.getString(staff.getProvinceName()));
        }

        if(staff.getStatus() != null){
            query.setParameter("status", staff.getStatus());
        }

        List<Object[]> lst = query.getResultList();
        if(lst.size() > 0){
            for(Object[] obj : lst ){
                StaffDTO staffDTO = new StaffDTO();
                staffDTO.setStaffCode(DataUtil.getString(obj[0]));
                staffDTO.setStaffName(DataUtil.getString(obj[1]));
                staffDTO.setProvinceCode(DataUtil.getString(obj[2]));
                if(obj[3]== null){
                    staffDTO.setProvinceName("All Province");
                }else {
                    staffDTO.setProvinceName(DataUtil.getString(obj[3]));
                }
                staffDTO.setStatus(Long.parseLong(DataUtil.getString(obj[4])));
                staffDTO.setStaffId(Long.parseLong(DataUtil.getString(obj[5])));
                if(obj[6] != null){
                    staffDTO.setCreateDate(DataUtil.stringToLocalDateTme(DataUtil.getString(obj[6])));
                    staffDTO.setStrCreateDate(DataUtil.getStringDate(staffDTO.getCreateDate()));
                }
                if(staffDTO.getStatus().equals(1L)){
                    staffDTO.setStrStatus(r.getResourceMessage("status.is.active"));
                }else
                if(staffDTO.getStatus().equals(0L)){
                    staffDTO.setStrStatus(r.getResourceMessage("status.not.active"));
                }
                lstResult.add(staffDTO);

            }
        }
        return lstResult;
    }

}
