package com.simulador.emprestimo.config.dbconfig;


import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.simulador.emprestimo.repository.sqlserver",
        entityManagerFactoryRef = "sqlServerEntityManager",
        transactionManagerRef = "sqlServerTransactionManager"
)


public class SqlServerConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.sqlserver")
    public DataSource sqlServerDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean sqlServerEntityManager(
            EntityManagerFactoryBuilder builder) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        properties.put("hibernate.hbm2ddl.auto", "none"); // ou "update" se quiser criar/alterar tabelas
        properties.put("hibernate.show_sql", "true");

        return builder
                .dataSource(sqlServerDataSource())
                .packages("com.simulador.emprestimo.model.sqlserver")
                .persistenceUnit("PRODUTO")
                .properties(properties)
                .build();
    }

    @Bean
    public PlatformTransactionManager sqlServerTransactionManager(
            @Qualifier("sqlServerEntityManager") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
