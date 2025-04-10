package com.mpole.wearable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication(exclude = {R2dbcAutoConfiguration.class})
@ComponentScans({
		@ComponentScan("com.mpole.imp.framework"),
		@ComponentScan("com.mpole.wearable")
})
public class WearableApplication {

	public static void main(String[] args) {
		SpringApplication.run(WearableApplication.class, args);
	}

}
