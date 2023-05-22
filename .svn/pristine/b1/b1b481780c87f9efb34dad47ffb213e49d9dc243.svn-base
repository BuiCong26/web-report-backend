package com.sm.base.controller;

import com.sm.base.common.Constant;
import com.sm.base.common.DataUtil;
import com.sm.base.common.ResourceBundle;
import com.sm.base.dto.ExecutionResult;
import com.sm.base.dto.StaffDTO;
import com.sm.base.model.modelReport.Staff;
import com.sm.base.model.modelReport.TblProvince;
import com.sm.base.repo.repoReport.StaffRepo;
import com.sm.base.service.ConfigParamsService;
import com.sm.base.service.TblProvinceService;
import com.sm.base.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
@Slf4j
public class StaffCtrl {


    @Autowired
    private UserService userService;

    @Autowired
    private StaffRepo staffRepo;

    @Autowired
    private TblProvinceService tblProvinceService;

    @Autowired
    @PersistenceContext(unitName = Constant.CONFIG_DB_REPORT.UNIT_NAME_ENTITIES_REPORT)
    private EntityManager emdbReport;


    @PostMapping(value = "/findStaff")
    public ExecutionResult findStaff(@RequestBody Staff staff, @RequestHeader("Accept-Language") String locate) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (!DataUtil.isNullOrEmpty(staff.getStaffCode())) {
                staff.setStaffCode(staff.getStaffCode().trim());
            }
            if (!DataUtil.isNullOrEmpty(staff.getStaffName())) {
                staff.setStaffName(staff.getStaffName().trim());
            }
            if (!DataUtil.isNullOrEmpty(staff.getProvinceName())) {
                staff.setProvinceName(staff.getProvinceName().trim());
            }

            List<StaffDTO> lst = userService.findStaff(staff, locate);
            res.setData(lst);
            res.setDescription("OK");
            return res;
        } catch (Exception e) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(e.getMessage());
        }
        return res;
    }

    @PostMapping(value = "/addNewStaff")
    public ExecutionResult addNewStaff(@RequestBody Staff staff, @RequestHeader("Accept-Language") String locate) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (DataUtil.isNullOrEmpty(staff.getStaffCode()) || staff.getStaffCode().trim().length() > 50) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("staff.code.is.null"));
                return res;
            }

            if (!DataUtil.isNullOrEmpty(staff.getStaffName()) && staff.getStaffName().trim().length() > 50) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("staff.name.invalid"));
                return res;
            }

            if (!DataUtil.isNullOrEmpty(staff.getProvinceCode()) && staff.getProvinceCode().trim().length() > 50) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.code.invalid"));
                return res;
            }


            Staff object = userService.getStaffByStaffCode(staff.getStaffCode());
            if (object != null && !DataUtil.isNullOrEmpty(object.getStaffCode())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("staff.code.is.exits"));
                return res;
            }
            staff.setStaffId(DataUtil.getSequence(emdbReport, "staff_seq"));
            staff.setStatus(1L);
            staff.setCreateDate(LocalDateTime.now());
            staffRepo.save(staff);
            res.setDescription(r.getResourceMessage("add.new.staff.success"));
            return res;
        } catch (Exception e) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("add.new.staff.fail"));
        }
        return res;
    }


    @PostMapping(value = "/editStaff")
    public ExecutionResult editStaff(@RequestBody List<Staff> lstStaff, @RequestHeader("Accept-Language") String locate) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (lstStaff.size() < 1) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("old.staff.code.is.null"));
                return res;
            }
            for (Staff staff : lstStaff) {
                if (DataUtil.isNullOrEmpty(staff.getOldStaffCode())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("old.staff.code.is.null"));
                    return res;
                }

                if (DataUtil.isNullOrEmpty(staff.getStaffCode())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("staff.code.is.null"));
                    return res;
                }

                if (!DataUtil.isNullOrEmpty(staff.getStaffName()) && staff.getStaffName().trim().length() > 50) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("staff.name.invalid"));
                    return res;
                }

                if (!DataUtil.isNullOrEmpty(staff.getProvinceCode()) && staff.getProvinceCode().trim().length() > 50) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("province.code.invalid"));
                    return res;
                }


                Staff object = userService.getStaffByStaffCode(staff.getOldStaffCode().trim());
                if (object == null || object.getStaffCode() == null) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("staff.code.is.invalid"));
                    return res;
                }

                if (!staff.getStaffCode().equals(staff.getOldStaffCode()) ) {
                    Staff newStaff = userService.getStaffByStaffCode(staff.getStaffCode().trim());
                    if (newStaff != null && !DataUtil.isNullOrEmpty(newStaff.getStaffCode()) ) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("staff.code.is.exits"));
                        return res;
                    }
                }

                staff.setStaffId(object.getStaffId());
                staff.setStatus(1L);
                staff.setCreateDate(LocalDateTime.now());
            }
            staffRepo.saveAll(lstStaff);
            res.setDescription(r.getResourceMessage("edit.staff.success"));
            return res;
        } catch (Exception e) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("edit.staff.fail"));
        }
        return res;
    }

    @PostMapping(value = "/deleteStaff")
    public ExecutionResult deleteStaff(@RequestBody List<String> lstStaffCode, @RequestHeader("Accept-Language") String locate) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        String staffCodeError = "";
        try {
            if (lstStaffCode.size() < 1) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("old.staff.code.is.null"));
                return res;
            }
            for (String staffCode : lstStaffCode) {
                staffCode = staffCode.trim();
                Staff object = userService.getStaffByStaffCode(staffCode);
                if (object == null || DataUtil.isNullOrEmpty(object.getStaffCode())) {
                    if (!DataUtil.isNullOrEmpty(staffCodeError)) {
                        staffCodeError = staffCodeError + ", " + staffCode;
                    } else {
                        staffCodeError = staffCode;
                    }

                }
            }
            if (!DataUtil.isNullOrEmpty(staffCodeError)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(String.format(r.getResourceMessage("staff.code.is.invalid.input.params"), staffCodeError));
                return res;
            }
            userService.updateStatusForStaff(lstStaffCode, 0L);
            res.setDescription(r.getResourceMessage("delete.staff.success"));
            return res;
        } catch (Exception e) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("delete.staff.fail"));
        }
        return res;
    }


}
