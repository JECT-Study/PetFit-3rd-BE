package ject.petfit.global.actuator;

import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

// 커스텀 엔드포인트 생성
@Component
@Endpoint(id = "note")
public class NoteEndpoint {
    private Map<String, Object> noteContent = new HashMap<>();

    /**
     각각 GET/POST/DELETE 등 HTTP 메서드에 매핑
    */
    @ReadOperation
    public Map<String, Object> getNoteContent() {
        return noteContent;
    }

    @WriteOperation
    public Map<String, Object> writeNoteContent(String key, String value) {
        noteContent.put(key, value);
        return noteContent;
    }

    @DeleteOperation
    public Map<String, Object> deleteNoteContent(String key) {
        if (noteContent.containsKey(key)) {
            noteContent.remove(key);
        } else {
            throw new IllegalArgumentException("Key not found: " + key);
        }
        return noteContent;
    }
}
