package usingSpring;//package chat_enty;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
//import org.springframework.core.env.Environment;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.thymeleaf.spring5.SpringTemplateEngine;
//import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
//import org.thymeleaf.spring5.view.ThymeleafViewResolver;
//
//import javax.sql.DataSource;
//import java.util.Objects;
//import java.util.Properties;
//
//@Configuration
//@ComponentScan(basePackages = "chat_enty")
//@PropertySource("classpath:../main/resources/application.properties")
//@EnableTransactionManagement
//@EnableJpaRepositories(basePackages = "chat_storage")
//@EnableWebMvc
//public class SpringConfig implements WebMvcConfigurer
//{
//    private final ApplicationContext applicationContext;
//    private final Environment environment;
//
//    @Autowired
//    public SpringConfig(ApplicationContext applicationContext, Environment environment) {
//        this.applicationContext = applicationContext;
//        this.environment = environment;
//    }
//    @Bean
//    public SpringResourceTemplateResolver templateResolver()
//    {
//        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
//        //templateResolver.setApplicationContext(applicationContext);
////        templateResolver.setPrefix("/WEB-INF/views/");
////        templateResolver.setSuffix(".html");
//        return templateResolver;
//    }
//
//    @Bean
//    public SpringTemplateEngine templateEngine()
//    {
//        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
////        templateEngine.setTemplateResolver(templateResolver());
////        templateEngine.setEnableSpringELCompiler(true);
//        return templateEngine;
//    }
//
//    @Override
//    public void configureViewResolvers(ViewResolverRegistry registry)
//    {
//        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
//        resolver.setTemplateEngine(templateEngine());
//        registry.viewResolver(resolver);
//    }
//
//    @Bean
//    public DataSource dataSource()
//    {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//
//        dataSource.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name"));
//        dataSource.setUrl(environment.getProperty("spring.datasource.url"));
//        dataSource.setUsername(environment.getProperty("spring.datasource.username"));
//        dataSource.setPassword(environment.getProperty("spring.datasource.password"));
//
//        return dataSource;
//    }
//
//    @Bean
//    public JdbcTemplate jdbcTemplate()
//    {
//        return new JdbcTemplate(dataSource());
//    }
//
//    private Properties hibernateProperties()
//    {
//        Properties properties = new Properties();
//        properties.put("hibernate.dialect", environment.getProperty("hibernate.dialect"));
//        properties.put("hibernate.show_sql", environment.getProperty("hibernate.show_sql"));
//
//        return properties;
//    }
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory()
//    {
//        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(dataSource());
//        em.setPackagesToScan("webapp.code.spring.Model");
//
//        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        em.setJpaVendorAdapter(vendorAdapter);
//        em.setJpaProperties(hibernateProperties());
//
//        return em;
//    }
//
//    @Bean
//    public PlatformTransactionManager transactionManager()
//    {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
//
//        return transactionManager;
//    }
//}

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Objects;

@PropertySource("classpath:../main/resources/application.properties")
@Configuration
@ComponentScan(basePackages = "chat_enty") // Specify the base package where your components are located
@EnableJpaRepositories(basePackages = "chat_storage") // Specify the base package where your repositories are located
public class SpringConfig {
    private final ApplicationContext applicationContext;
    private final Environment environment;

    @Autowired
    public SpringConfig(ApplicationContext applicationContext, Environment environment) {
        this.applicationContext = applicationContext;
        this.environment = environment;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(Objects.requireNonNull(environment.getProperty("spring.datasource.driver-class-name")));
        dataSource.setUrl(environment.getProperty("spring.datasource.url"));
        dataSource.setUsername(environment.getProperty("spring.datasource.username"));
        dataSource.setPassword(environment.getProperty("spring.datasource.password"));

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        // Configure your Entity Manager Factory here
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setPackagesToScan("chat_enty"); // Specify the packages where your entities are located
        entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return entityManagerFactory;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        // Configure your Transaction Manager here
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}


