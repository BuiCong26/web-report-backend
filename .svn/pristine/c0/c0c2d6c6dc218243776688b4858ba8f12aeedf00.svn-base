package com.sm.base.database;

import com.sm.base.common.Constant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {Constant.CONFIG_DB_REPORT.PACKAGE_REPO_REPORT},
        entityManagerFactoryRef = "reportEntityManagerFactory",
        transactionManagerRef = "reportTransactionManager")
public class ReportDatasourceConfiguration {

  @Bean("reportDatasource")
  @ConfigurationProperties(prefix = "report.datasource")
  public DataSource reportDatasource() {
    return DataSourceBuilder.create().build();
  }

  @Bean("reportEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean db1EntityManager(@Qualifier("reportDatasource") DataSource bccs1Datasource) {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(bccs1Datasource);
    em.setPersistenceUnitName(Constant.CONFIG_DB_REPORT.UNIT_NAME_ENTITIES_REPORT);
    em.setPackagesToScan(Constant.CONFIG_DB_REPORT.PACKAGE_MODEL_REPORT);
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    HashMap<String, Object> properties = new HashMap<>();
    properties.put("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
    em.setJpaPropertyMap(properties);
    return em;
  }


  @Bean("reportTransactionManager")
  public JpaTransactionManager reportTransactionManager(@Qualifier("reportEntityManagerFactory") LocalContainerEntityManagerFactoryBean reportEntityManager) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(reportEntityManager.getObject());
    return transactionManager;
  }
}
