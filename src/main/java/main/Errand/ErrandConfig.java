package main.Errand;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ErrandConfig {
    public Errand errand(){
        return new Errand();
    }
}
