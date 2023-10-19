package main.ErrandData;

import main.Errand.Errand;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ErrandDataConfig {
    @Bean
    public ErrandData errandData(){
        return new ErrandData();
    }
}
