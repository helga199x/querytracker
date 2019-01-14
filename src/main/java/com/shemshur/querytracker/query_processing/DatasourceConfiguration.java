package com.shemshur.querytracker.query_processing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

@EnableAutoConfiguration(exclude = { //
        DataSourceAutoConfiguration.class, //
        DataSourceTransactionManagerAutoConfiguration.class })

public class DatasourceConfiguration {


    @Bean(name = "dataSource1")
    public DataSource getDataSource1() throws SQLException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        // See: datasouce-cfg.properties
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/test1");
        dataSource.setUsername("root");
        dataSource.setPassword("1111");
        dataSource.setSchema("spring.datasource.schema.1");

        System.out.println("## DataSource1: " + dataSource);
        return dataSource;
    }

    @Bean(name = "dataSource2")
    public DataSource getDataSource2() throws SQLException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        // See: datasouce-cfg.properties
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/test2");
        dataSource.setUsername("root");
        dataSource.setPassword("1111");
        dataSource.setSchema("test1");

        System.out.println("## DataSource2: " + dataSource);

        return dataSource;
    }

    @Autowired
    @Bean(name = "transactionManager")
    public DataSourceTransactionManager getTransactionManager(DataSource dataSource) {
        DataSourceTransactionManager txManager = new DataSourceTransactionManager();

        txManager.setDataSource(dataSource);

        return txManager;
    }

}