package com.sm.base.model.modelReport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Logs {

    @Id
    @Column(name = "id", nullable = false,updatable = false)
    private Long id;

    @Column(name = "username", nullable = true)
    private String userName;

    @Column(name = "action_type", nullable = true)
    private Long actionType;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "log_time" ,nullable = true)
    private Date logTime;

    @Transient
    private String strLogTime;

    @Transient
    private String strActionType;
}
