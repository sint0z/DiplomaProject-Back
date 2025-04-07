package ru.shze.demo_project.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
@EnableJpaRepositories(basePackages = "ru.shze.demo_project.repository")
public class DBConfig {

    @Autowired
    private final Environment environment;

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(environment.getProperty("driver-class-name")));
        dataSource.setUrl(environment.getProperty("url"));
        dataSource.setUsername(environment.getProperty("username"));
        dataSource.setPassword(environment.getProperty("password"));
        return dataSource;
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("ru.shze.demo_project.model");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaProperties(additionalProperties());
        return em;
    }


    final Properties additionalProperties() {
        final Properties hibernateProperties = new Properties();
        if (environment.getProperty("hibernate.hbm2ddl.auto") != null) {
            hibernateProperties.setProperty("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto"));
        }
        if (environment.getProperty("hibernate.dialect") != null) {
            hibernateProperties.setProperty("hibernate.dialect", environment.getProperty("hibernate.dialect"));
        }
        if (environment.getProperty("hibernate.show_sql") != null) {
            hibernateProperties.setProperty("hibernate.show_sql", environment.getProperty("hibernate.show_sql"));
        }
        if (environment.getProperty("hibernate.c3p0.min_size") != null) {
            hibernateProperties.setProperty("hibernate.c3p0.min_size", environment.getProperty("hibernate.c3p0.min_size"));
        }
        if (environment.getProperty("hibernate.c3p0.max_size") != null) {
            hibernateProperties.setProperty("hibernate.c3p0.max_size", environment.getProperty("hibernate.c3p0.max_size"));
        }
        hibernateProperties.setProperty(org.hibernate.cfg.Environment.STATEMENT_BATCH_SIZE, "5");
        hibernateProperties.setProperty(org.hibernate.cfg.Environment.ORDER_INSERTS, "true");
        return hibernateProperties;
    }
}