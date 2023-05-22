package com.sm.base.service;

import com.sm.base.dto.CommonInputDTO;
import com.sm.base.dto.LoginDTO;
import com.sm.base.dto.StaffDTO;
import com.sm.base.model.modelReport.Staff;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    LoginDTO loginAuthentication(CommonInputDTO commonInputDTO, String locate)  throws Exception;

    Staff getStaffByStaffCode(String staffCode) throws Exception;

    void updateStaff(Staff staff) throws Exception;

    void updateStatusForStaff(List<String> staffCode, Long status) throws Exception;

    List<StaffDTO> findStaff(Staff staff, String locate) throws Exception;


}
