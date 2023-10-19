package main.Errand;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ErrandConfig {

    @Bean
    public Errand errand(){
        return new Errand();
    }
}
