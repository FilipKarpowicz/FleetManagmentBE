package main.car;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Configuration
public class CarConfig {

    @Bean
    CommandLineRunner commandLineRunner(
            CarRepository repository){
        return args -> {
            Car opel = new Car(
                            "Opel",
                            "Corsa",
                            "super samcohod",
                            LocalDate.of(2023, Month.OCTOBER,25)
            );

            Car ford = new Car(
                    "Ford",
                    "Focus",
                    "bardzo super samcohod",
                    LocalDate.of(2024, Month.MAY,15)
            );


        };
    }
}
