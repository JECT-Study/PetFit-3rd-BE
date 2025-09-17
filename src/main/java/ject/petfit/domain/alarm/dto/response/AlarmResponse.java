package ject.petfit.domain.alarm.dto.response;

import ject.petfit.domain.alarm.entity.Alarm;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmResponse {
    private Long alarmId;
    private String title;
    private String content;
    private boolean isRead;
    private LocalDateTime targetDateTime;

    public static AlarmResponse from(Alarm alarm) {
        return AlarmResponse.builder()
                .alarmId(alarm.getAlarmId())
                .title(alarm.getTitle())
                .content(alarm.getContent())
                .isRead(alarm.isRead())
                .targetDateTime(alarm.getTargetDateTime())
                .build();
    }
}
