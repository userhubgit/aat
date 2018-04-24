package fr.cnam;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.zaxxer.hikari.HikariDataSource;

@SpringBootApplication
public class SpringBootWebApplication {
	
//	@Bean(name = "datasource")
//	@ConfigurationProperties(prefix="spring.datasource")
//	public DataSource dataSource(){
//		return DataSourceBuilder.create().build();
//	}
	@Bean
    @ConfigurationProperties("spring.datasource")
    public HikariDataSource dataSource(DataSourceProperties properties) {
        return (HikariDataSource) properties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
    }
	
    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringBootWebApplication.class, args);
    }
}