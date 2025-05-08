package asset.spy.rfid.emu.service;

import asset.spy.rfid.emu.message.ProductStatusResponse;
import asset.spy.rfid.emu.model.ProductStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;

import static java.util.concurrent.CompletableFuture.delayedExecutor;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class StatusSimulatorService {

    private final KafkaProducerService kafkaProducerService;
    private final TaskExecutor kafkaTaskExecutor;

    public StatusSimulatorService(
            KafkaProducerService kafkaProducerService,
            @Qualifier("kafkaTaskExecutor") TaskExecutor kafkaTaskExecutor) {
        this.kafkaProducerService = kafkaProducerService;
        this.kafkaTaskExecutor = kafkaTaskExecutor;
    }

    @Async("kafkaTaskExecutor")
    public void simulate(String topic, String itemId, Long article, List<ProductStatus> statuses,
                         int minTimeoutMin, int maxTimeoutMin) {
        processStatuses(topic, itemId, article, statuses, minTimeoutMin, maxTimeoutMin, 0);
    }

    private void processStatuses(String topic, String itemId, Long article, List<ProductStatus> statuses,
                                 int minTimeoutMin, int maxTimeoutMin, int index) {
        if (index >= statuses.size()) {
            return;
        }

        ProductStatus status = statuses.get(index);
        ProductStatusResponse message = createMessage(itemId, article, status);
        sendMessage(topic, itemId, message);

        if (index < statuses.size() - 1) {
            Random random = new Random(itemId.hashCode());
            int delay = calculateDelay(minTimeoutMin, maxTimeoutMin, random);

            delayedExecutor(delay, TimeUnit.MILLISECONDS, kafkaTaskExecutor)
                    .execute(() -> processStatuses(topic, itemId, article, statuses,
                            minTimeoutMin, maxTimeoutMin, index + 1));
        }
    }

    private ProductStatusResponse createMessage(String itemId, Long article, ProductStatus status) {
        return ProductStatusResponse.builder()
                .itemId(itemId)
                .article(article)
                .productStatus(status.getValue())
                .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
    }

    private void sendMessage(String topic, String key, ProductStatusResponse message) {
        kafkaProducerService.sendMessage(topic, key, message);
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
