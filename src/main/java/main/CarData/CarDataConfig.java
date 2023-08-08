package main.CarData;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CarDataConfig {

    @Bean
    public CarData carData(){
        return new CarData();
    }
}
