package ject.petfit.global.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ConsumerService {
    private final List<String> messages = new CopyOnWriteArrayList<>();

    @KafkaListener(topics = "test", groupId = "test-group")
    public void listen(String message) {
        messages.add(message);
    }

    public List<String> getMessages() {
        return messages;
    }
}