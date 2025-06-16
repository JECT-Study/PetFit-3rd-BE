package ject.petfit.global.health;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden // swagger에 노출 안되도록
@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public String healthCheck() {
        return "Success Health Check";
    }
}