package ject.petfit.global.health;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Tag(name = "헬스 체크 API")
@RestController
public class HealthCheckController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/health")
    @Operation(summary = "서버 헬스 체크", description = "서버가 정상적으로 작동하는지 확인합니다.")
    public String healthCheck() {
        return "Server Success Health Check";
    }

    @GetMapping("/health/db")
    @Operation(summary = "DB 헬스 체크", description = "데이터베이스 연결 상태를 확인합니다.")
    public String healthCheckDb() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(2)) {
//                String url = conn.getMetaData().getURL();
//                return "DB Success Health Check - " + url;
                return "DB Success Health Check";
            } else {
                return "DB Connection Invalid";
            }
        } catch (SQLException e) {
            return "DB Connection Failed: " + e.getMessage();
        }
    }
}
