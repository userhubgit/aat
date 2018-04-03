package fr.cnam;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.zaxxer.hikari.HikariDataSource;

@SpringBootApplication
public class SpringBootWebApplication {
	
	@Bean(name = "datasource")
	@ConfigurationProperties(prefix="spring.datasource")
	public DataSource dataSource(){
		return DataSourceBuilder.create().build();
	}
//	public HikariDataSource dataSource() {
//		return (HikariDataSource) DataSourceBuilder.create().type(HikariDataSource.class).build();
//	}
	
    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringBootWebApplication.class, args);
    }
}