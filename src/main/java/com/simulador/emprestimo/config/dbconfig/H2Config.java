package com.simulador.emprestimo.config.dbconfig;


import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        basePackages = "com.simulador.emprestimo.repository.h2",
        entityManagerFactoryRef = "h2EntityManager",
        transactionManagerRef = "h2TransactionManager"
)
public class H2Config {

    @Primary
    @Bean(name = "h2DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.h2")
    public DataSource h2DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "h2EntityManager")
    public LocalContainerEntityManagerFactoryBean h2EntityManager(
            EntityManagerFactoryBuilder builder) {

        return builder
                .dataSource(h2DataSource())
                .packages("com.simulador.emprestimo.model.h2")
                .persistenceUnit("simulacao")
                .build();
    }

    @Primary
    @Bean(name = "h2TransactionManager")
    public PlatformTransactionManager h2TransactionManager(
            @Qualifier("h2EntityManager") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}


