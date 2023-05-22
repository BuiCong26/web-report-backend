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
@Table(name = "tbltruyvan")
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
public class Tbltruyvan {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "table_name", nullable = false)
    private String tableName;

    @Column(name = "report_name", nullable = false)
    private String reportName;

    @Column(name = "status", nullable = false)
    private Long status;

    @Column(name = "sql", nullable = false)
    private String sql;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "type_role", nullable = false)
    private Long typeRole;
}
