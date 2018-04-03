package fr.cnam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootWebApplication {
	
//	@Bean(name = "datasource")
//	@ConfigurationProperties(prefix="spring.datasource")
//	public DataSource dataSource(){
//		return DataSourceBuilder.create().build();
//	}

	
    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringBootWebApplication.class, args);
    }
}