package main.Driver;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DriverConfig {

    @Bean
    public Driver driver(){
        return new Driver();
    }
}
