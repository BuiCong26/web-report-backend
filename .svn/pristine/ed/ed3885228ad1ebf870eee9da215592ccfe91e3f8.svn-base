/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sm.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class TbltruyvanDTO {
    Long id;
    String tableName;
    String reportName;
    Long type;
    Long status;
    Long typeRole;
    String sql;
    String description;
    String header;
    List<MappingParamReportDTO> lstParams;


}
