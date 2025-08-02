package ject.petfit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PetfitApplication { /// ///

	public static void main(String[] args) {
		SpringApplication.run(PetfitApplication.class, args);
	}

}
