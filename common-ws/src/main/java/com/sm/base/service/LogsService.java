package com.sm.base.service;

import com.sm.base.controller.LogsCtrl;
import com.sm.base.dto.LogsDTO;
import com.sm.base.dto.TbltruyvanDTO;
import com.sm.base.model.modelReport.Logs;
import org.springframework.context.annotation.Bean;

import java.util.List;

public interface LogsService {
    List<Logs> searchLogs(String sql) throws Exception;


}
