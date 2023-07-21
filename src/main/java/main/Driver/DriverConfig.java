package main.Driver;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
public class DriverConfig {

    @Bean
    public Driver driver(){
        return new Driver();
    }
}
