package com.sm.base.model.modelReport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mapping_params_report")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class MappingParamReport {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "report_id", nullable = false)
    private Long reportId;

    @Column(name = "param_code", nullable = false)
    private String paramCode;

    @Column(name = "column_name", nullable = false)
    private String columnName;

    @Column(name = "status", nullable = false)
    private Long status;

    @Column(name = "type", nullable = false)
    private Long type;

    @Column(name = "display_name_vi", nullable = false)
    private String displayNameVi;

    @Column(name = "display_name_la", nullable = false)
    private String displayNameLa;

    @Column(name = "display_name_en", nullable = false)
    private String displayNameEn;

    @Column(name = "order_display", nullable = false)
    private Long orderDisplay;
}
