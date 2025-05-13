package asset.spy.rfid.emu.service;

import asset.spy.rfid.emu.message.ProductStatusResponse;
import asset.spy.rfid.emu.model.ProductStatus;
import asset.spy.rfid.emu.service.strategy.StrategyType;
import asset.spy.rfid.emu.service.strategy.StateSequenceStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;

import static java.util.concurrent.CompletableFuture.delayedExecutor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class StatusSimulatorService {

    private final KafkaProducerService kafkaProducerService;
    private final TaskExecutor kafkaTaskExecutor;
    private final StateSequenceStrategyFactory strategyFactory;

    public StatusSimulatorService(
            KafkaProducerService kafkaProducerService,
            @Qualifier("kafkaTaskExecutor") TaskExecutor kafkaTaskExecutor,
            StateSequenceStrategyFactory stateSequenceStrategyFactory) {
        this.kafkaProducerService = kafkaProducerService;
        this.kafkaTaskExecutor = kafkaTaskExecutor;
        this.strategyFactory = stateSequenceStrategyFactory;
    }

    public void simulate(String topic, String itemId, Long article, List<ProductStatus> statuses,
                         int minTimeoutMin, int maxTimeoutMin) {
        AtomicInteger index = new AtomicInteger(0);
        processStatuses(topic, itemId, article, statuses, minTimeoutMin, maxTimeoutMin, index, new Random());
    }

    private void processStatuses(String topic, String itemId, Long article, List<ProductStatus> statuses,
                                 int minTimeoutMin, int maxTimeoutMin, AtomicInteger index, Random random) {
        List<ProductStatus> safeStatuses = getDefaultStatusSequence(statuses);
        if (!hasNextStatus(index.get(), safeStatuses)) {
            return;
        }

        ProductStatus status = getCurrentStatus(safeStatuses, index.get());
        sendMessage(topic, itemId, createMessage(itemId, article, status));

        if (!status.isFinal()) {
            scheduleNextStatus(topic, itemId, article, safeStatuses, minTimeoutMin, maxTimeoutMin, index, random);
        }
    }

    private List<ProductStatus> getDefaultStatusSequence(List<ProductStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return strategyFactory.getStrategy(StrategyType.FULL.getValue()).buildSequence();
        }
        return statuses;
    }

    private boolean hasNextStatus(int currentIndex, List<ProductStatus> statuses) {
        return currentIndex >= 0 && currentIndex < statuses.size();
    }

    private ProductStatus getCurrentStatus(List<ProductStatus> statuses, int index) {
        return statuses.get(index);
    }

    private void scheduleNextStatus(String topic, String itemId, Long article, List<ProductStatus> statuses,
                                    int minTimeoutMin, int maxTimeoutMin, AtomicInteger index, Random random) {
        int delay = calculateDelay(minTimeoutMin, maxTimeoutMin, random);
        index.incrementAndGet();

        delayedExecutor(delay, TimeUnit.MILLISECONDS, kafkaTaskExecutor).execute(() ->
                processStatuses(topic, itemId, article, statuses, minTimeoutMin, maxTimeoutMin, index, random)
        );
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
