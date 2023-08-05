package main.Location;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocationConfig {
    @Bean
    public Location location(){
        return new Location();
    }

    @Bean
    public JtsModule jtsModule(){
        return new JtsModule();
    }
}
