package ject.petfit.domain.alarm.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmUpdateRequest {
    private String title;        // 일정 제목
    private String content;      // 일정 내용
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime targetDateTime;   // 대상 날짜(yyyy-MM-dd)
}
