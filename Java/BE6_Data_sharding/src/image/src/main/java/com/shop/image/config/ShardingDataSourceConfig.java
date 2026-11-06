package com.shop.image.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@EnableConfigurationProperties(ShardProperties.class)
@Configuration
public class ShardingDataSourceConfig {
    private final ShardProperties properties;

    public ShardingDataSourceConfig(ShardProperties properties) {
        this.properties = properties;
    }

    @Bean(name = "shard0DataSource")
    public DataSource shard0DataSource() {
        String url = properties.getShard0().getUrl();
        String username = properties.getShard0().getUsername();
        String password = properties.getShard0().getPassword();
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "shard0EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean shard0EntityManagerFactory(
            @Qualifier("shard0DataSource") DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.shop.image.model");
        emf.setPersistenceUnitName("shard0");
        emf.setJpaVendorAdapter(vendorAdapter);
        return emf;
    }

    @Bean(name = "shard0TransactionManager")
    public PlatformTransactionManager shard0TransactionManager(
            @Qualifier("shard0EntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }


    @Bean(name = "shard1DataSource")
    public DataSource shard1DataSource() {
        String url = properties.getShard1().getUrl();
        String username = properties.getShard1().getUsername();
        String password = properties.getShard1().getPassword();
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "shard1EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean shard1EntityManagerFactory(
            @Qualifier("shard1DataSource") DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.shop.image.model");
        emf.setPersistenceUnitName("shard1");
        emf.setJpaVendorAdapter(vendorAdapter);
        return emf;
    }

    @Bean(name = "shard1TransactionManager")
    public PlatformTransactionManager shard1TransactionManager(
            @Qualifier("shard1EntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }


    @Bean(name = "shard2DataSource")
    public DataSource shard2DataSource() {
        String url = properties.getShard2().getUrl();
        String username = properties.getShard2().getUsername();
        String password = properties.getShard2().getPassword();
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "shard2EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean shard2EntityManagerFactory(
            @Qualifier("shard2DataSource") DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.shop.image.model");
        emf.setPersistenceUnitName("shard2");
        emf.setJpaVendorAdapter(vendorAdapter);
        return emf;
    }

    @Bean(name = "shard2TransactionManager")
    public PlatformTransactionManager shard2TransactionManager(
            @Qualifier("shard2EntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }


    @Bean(name = "shard3DataSource")
    public DataSource shard3DataSource() {
        String url = properties.getShard3().getUrl();
        String username = properties.getShard3().getUsername();
        String password = properties.getShard3().getPassword();
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "shard3EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean shard3EntityManagerFactory(
            @Qualifier("shard3DataSource") DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.shop.image.model");
        emf.setPersistenceUnitName("shard3");
        emf.setJpaVendorAdapter(vendorAdapter);
        return emf;
    }

    @Bean(name = "shard3TransactionManager")
    public PlatformTransactionManager shard3TransactionManager(
            @Qualifier("shard3EntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }


    @Configuration
    @EnableJpaRepositories(
            basePackages = "com.shop.image.repository.shard0",
            entityManagerFactoryRef = "shard0EntityManagerFactory",
            transactionManagerRef = "shard0TransactionManager")
    static class Shard0JpaConfig { }

    @Configuration
    @EnableJpaRepositories(
            basePackages = "com.shop.image.repository.shard1",
            entityManagerFactoryRef = "shard1EntityManagerFactory",
            transactionManagerRef = "shard1TransactionManager")
    static class Shard1JpaConfig { }

    @Configuration
    @EnableJpaRepositories(
            basePackages = "com.shop.image.repository.shard2",
            entityManagerFactoryRef = "shard2EntityManagerFactory",
            transactionManagerRef = "shard2TransactionManager")
    static class Shard2JpaConfig { }

    @Configuration
    @EnableJpaRepositories(
            basePackages = "com.shop.image.repository.shard3",
            entityManagerFactoryRef = "shard3EntityManagerFactory",
            transactionManagerRef = "shard3TransactionManager")
    static class Shard3JpaConfig { }
}
