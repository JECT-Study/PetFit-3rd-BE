package ject.petfit.global.kafka;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Hidden
@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
public class KafkaController {
    private final ProducerService producerService;
    private final ConsumerService consumerService;

    @PostMapping("/publish")
    public void send(@RequestParam String topic, @RequestParam String message) {
        producerService.sendMessage(topic, message);
    }

    @GetMapping("/messages")
    public List<String> getMessages() {
        return consumerService.getMessages();
    }
}
