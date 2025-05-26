package asset.spy.rfid.emu.service;

import asset.spy.rfid.emu.dto.http.kafka.ProductStatusMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, ProductStatusMessage> kafkaTemplate;

    public void sendMessage(String topic, String key, ProductStatusMessage message) {
        log.info("Sending message to topic {}: {}", topic, message);
        CompletableFuture<SendResult<String, ProductStatusMessage>> future =
                kafkaTemplate.send(topic, key, message);

        future.whenComplete((result, exception) -> {
            if (exception != null) {
                log.warn("Failed to send message to Kafka: {}", exception.getMessage());
            } else {
                log.debug("Message successfully sent to topic '{}'",
                        result.getRecordMetadata().topic());
            }
        });
    }
}
