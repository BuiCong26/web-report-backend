package com.sm.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
// causes Lombok to implement the Builder design pattern for the Pojo class.
// usage can be seen in DefaultBeersLoader.java -> createNewBeer() method.
@Builder
// causes Lombok to generate a constructor with no parameters.
@NoArgsConstructor
// causes Lombok to generate a constructor with 1 parameter for each field in your class.
@AllArgsConstructor
@Component
public class CommonInputDTO {

    private String userName;
    private String password;
    private String functionName;
    private String language;
    //umoney
    private String serviceCode;
    private StaffDTO staffDTO;
    private String errorCode;
    private String errorDesc;
}
