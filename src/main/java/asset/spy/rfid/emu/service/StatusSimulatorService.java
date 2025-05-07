package asset.spy.rfid.emu.service;

import asset.spy.rfid.emu.message.ProductStatusResponse;
import asset.spy.rfid.emu.model.ProductStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatusSimulatorService {

    private final KafkaProducerService kafkaProducerService;

    public void simulate(String topic, String itemId, Integer article, List<ProductStatus> statuses,
                         int minTimeoutMin, int maxTimeoutMin) {
        Random random = new Random(itemId.hashCode());
        for (int i = 0; i < statuses.size(); i++) {
            ProductStatus status = statuses.get(i);
            ProductStatusResponse message = createMessage(itemId, article, status);
            sendMessage(topic, itemId, message);

            if (i < statuses.size() - 1) {
                delay(minTimeoutMin, maxTimeoutMin, random, itemId);
            }
        }
    }

    private ProductStatusResponse createMessage(String itemId, Integer article, ProductStatus status) {
        return ProductStatusResponse.builder()
                .itemId(itemId)
                .article(article)
                .productStatus(status.getValue())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    private void sendMessage(String topic, String key, ProductStatusResponse message) {
        kafkaProducerService.sendMessage(topic, key, message);
    }

    private void delay(int minTimeoutMin, int maxTimeoutMin, Random random, String itemId) {
        int delay = calculateDelay(minTimeoutMin, maxTimeoutMin, random);
        try {
            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Delay interrupted for item {}", itemId, e);
        }
    }

    private int calculateDelay(int min, int max, Random random) {
        if (min == max) {
            return min * 60_000;
        }
        int lower = Math.min(min, max);
        int upper = Math.max(min, max);
        int delayMin = lower + random.nextInt(upper - lower + 1);

        return Math.max(delayMin * 60_000, 0);
    }
}
