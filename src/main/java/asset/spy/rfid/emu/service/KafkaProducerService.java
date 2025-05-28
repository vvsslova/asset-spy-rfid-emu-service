package asset.spy.rfid.emu.service;

import asset.spy.rfid.emu.dto.kafka.ProductStatusMessage;
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

    public CompletableFuture<SendResult<String, ProductStatusMessage>> sendMessage(
            String topic, String key, ProductStatusMessage productStatusMessage) {
        log.info("Sending product status message to topic {}", topic);
        return kafkaTemplate.send(topic, key, productStatusMessage);
    }

}
