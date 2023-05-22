package com.sm.base.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ExecutionResult {
    private Object data;
    private String errorCode;
    private String description;
    private int totalSuccess;
    private int totalError;
}
