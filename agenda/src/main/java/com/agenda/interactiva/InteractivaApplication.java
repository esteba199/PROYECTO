package com.agenda.interactiva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Habilita la ejecución automática de tareas con @Scheduled
public class InteractivaApplication {

	public static void main(String[] args) {
		SpringApplication.run(InteractivaApplication.class, args);
	}

}
