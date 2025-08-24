package ject.petfit.global.actuator;

import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

// 액추에이터 커스터마이징
@Component
public class CustomInfoContributor implements InfoContributor {
    @Override
    public void contribute(Builder builder) {
        Map<String, Object> contents = new HashMap<>();
        contents.put("code-info", "InfoContributor 구현체에서 정의한 정보");
        builder.withDetail("custom-info-contributor", contents);
    }
}
