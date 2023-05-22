package com.sm.base.model.modelReport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff")
// causes Lombok to generate toString(), equals(), hashCode(), getter() & setter(), and Required arguments constructor in one go.
@Data
// causes Lombok to implement the Builder design pattern for the Pojo class.
// usage can be seen in DefaultBeersLoader.java -> createNewBeer() method.
@Builder
// causes Lombok to generate a constructor with no parameters.
@NoArgsConstructor
// causes Lombok to generate a constructor with 1 parameter for each field in your class.
@AllArgsConstructor
@Component
public class Staff {

    @Id
    @Column(name = "staff_id", nullable = false, updatable = false)
    private Long staffId;
        
    @Column(name = "staff_code", nullable = false)
    private String staffCode;

    @Column(name = "staff_name", nullable = true)
    private String staffName;

    @Column(name = "status", nullable = false)
    private Long status;

    @Column(name = "province_code", nullable = true)
    private String provinceCode;

    @Column(name = "create_date", nullable = true)
    private LocalDateTime createDate;

   @Transient
    private String provinceName;

    @Transient
    private String oldStaffCode;


}
