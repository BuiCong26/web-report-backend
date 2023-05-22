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
@Data
@Builder
@Table(name = "tbldistrict")
@NoArgsConstructor
@AllArgsConstructor
@Component
public class TblDistrict {

    @Id
    @Column(name = "district_id", nullable = false, updatable = false)
    private Long districtId;

    @Column(name = "province_id", nullable = false)
    private Long provinceId;

    @Column(name = "district_code", nullable = false)
    private String districtCode;

    @Column(name = "district_name", nullable = false)
    private String districtName;

    @Column(name = "note", nullable = false)
    private String note;

    @Column(name = "status", nullable = false)
    private Long status;
}
