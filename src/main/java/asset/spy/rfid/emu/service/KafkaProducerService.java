package asset.spy.rfid.emu.service;

import asset.spy.rfid.emu.message.ProductStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, ProductStatusResponse> kafkaTemplate;

    @Async("kafkaTaskExecutor")
    @Retryable(retryFor = {RuntimeException.class}, backoff = @Backoff(delay = 2000))
    public void sendMessage(String topic, String key, ProductStatusResponse message) {
        try {
            kafkaTemplate.executeInTransaction(kt -> {
                log.info("Sending message to topic {}: {}", topic, message);
                kt.send(topic, key, message);
                kt.flush();
                return true;
            });
        } catch (Exception e) {
            log.error("Error while sending message to Kafka", e);
            throw new RuntimeException("Kafka send failed", e);
        }
    }
}
