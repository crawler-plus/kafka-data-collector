package site.it4u.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaDataCollectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaDataCollectorApplication.class, args);
	}
}
