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
@Table(name = "tblprovince")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class TblProvince {
    @Column(name = "province_name", nullable = false)
    private String provinceName;

    @Id
    @Column(name = "province_code", nullable = false)
    private String provinceCode;
}
