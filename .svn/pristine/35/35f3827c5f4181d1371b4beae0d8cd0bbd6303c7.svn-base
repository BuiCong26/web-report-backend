package com.sm.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Data
// causes Lombok to implement the Builder design pattern for the Pojo class.
// usage can be seen in DefaultBeersLoader.java -> createNewBeer() method.
@Builder
// causes Lombok to generate a constructor with no parameters.
@NoArgsConstructor
// causes Lombok to generate a constructor with 1 parameter for each field in your class.
@AllArgsConstructor
@Component
public class StaffDTO {
    private Long staffId;
    private Long shopId;
    private String staffCode;
    private String staffName;
    private String tel;
    private Long status;
    private String shopCode;
    private String shopPath;
    private String provinceCode;
    private String provinceName;
    private String strStatus;
    private LocalDateTime createDate;
    private String strCreateDate;
}
