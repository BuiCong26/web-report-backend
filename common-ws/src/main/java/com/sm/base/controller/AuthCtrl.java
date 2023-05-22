/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sm.base.controller;

import com.sm.base.common.Constant;
import com.sm.base.common.DataUtil;
import com.sm.base.common.ResourceBundle;
import com.sm.base.dto.CommonInputDTO;
import com.sm.base.dto.ExecutionResult;
import com.sm.base.dto.LoginDTO;
import com.sm.base.model.modelReport.Logs;
import com.sm.base.repo.repoReport.LogsRepo;
import com.sm.base.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping(value = "/api")
@Slf4j
public class AuthCtrl {


  @Autowired
  private UserService userService;

  @Autowired
  private LogsRepo logsRepo;

  @Autowired
  @PersistenceContext(unitName = Constant.CONFIG_DB_REPORT.UNIT_NAME_ENTITIES_REPORT)
  private EntityManager emdbReport;

  @PostMapping(value = "/loginAuthentication")
  public ExecutionResult loginAuthentication(@RequestBody CommonInputDTO commonInputDTO, @RequestHeader("Accept-Language") String locate) {
    ResourceBundle r = new ResourceBundle(locate);
    ExecutionResult res = new ExecutionResult();
    res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
    try {
      if (DataUtil.isNullOrEmpty(commonInputDTO.getUserName())) {
        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
        res.setDescription(r.getResourceMessage("userName.is.null"));
        return res;
      }
      commonInputDTO.setUserName(commonInputDTO.getUserName());
      if (DataUtil.isNullOrEmpty(commonInputDTO.getPassword())) {
        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
        res.setDescription(r.getResourceMessage("password.is.null"));
        return res;
      }
      commonInputDTO.setPassword(commonInputDTO.getPassword());

      LoginDTO loginDTO = userService.loginAuthentication(commonInputDTO, locate);
      if (loginDTO!=null && loginDTO.getReasonFail()!=null) {
        String message = "";
        switch (loginDTO.getReasonFail()) {
          case "u/p wrong" :
            message = r.getResourceMessage("user.or.pass.invalid");
            break;
          case "STAFF_NOT_FOUND" :
            message = r.getResourceMessage("user.disabled");
            break;
          default:
            message = r.getResourceMessage("sign.in.false");
        }
        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
        res.setDescription(message);
        return res;
      }else{
        // lưu logs
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String strDate = formatter.format(new Date());
        String action = "login system at " + strDate;
        Logs model = new Logs();
        model.setLogTime(new Date());
        model.setUserName(commonInputDTO.getUserName());
        model.setAction(action);
        model.setActionType(1L);
        model.setId(DataUtil.getSequence(emdbReport, "logs_seq"));
        model.setLogTime(new Date());
        logsRepo.save(model);
        // end lưu logs
        res.setData(loginDTO);
      }
    } catch (Exception e) {
      log.error("Error loginAuthentication: ",e);
      res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
      res.setDescription(e.getMessage());
    }
    return res;
  }

  @GetMapping(value = "/test")
  public String test(@RequestHeader("Accept-Language") String locate) {
    return "test ok";
  }

}
