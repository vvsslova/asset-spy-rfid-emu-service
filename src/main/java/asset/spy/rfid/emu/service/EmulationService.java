package asset.spy.rfid.emu.service;

import asset.spy.rfid.emu.dto.http.kafka.EmulationRequestDto;
import asset.spy.rfid.emu.dto.context.SimulationContext;
import asset.spy.rfid.emu.dto.context.TimeoutSettingContext;
import asset.spy.rfid.emu.model.ProductStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class EmulationService {

    private final ProductStateSequenceService productFlowBuilder;
    private final StatusSimulatorService statusSimulator;
    private final TaskExecutor taskExecutor;
    private final String topicVendorPrefix;

    @Autowired
    public EmulationService(
            ProductStateSequenceService productFlowBuilder,
            StatusSimulatorService statusSimulator,
            @Qualifier("kafkaTaskExecutor") TaskExecutor taskExecutor,
            @Value("${kafka.topic.vendor.prefix:topic_vendor_}") String topicVendorPrefix) {
        this.productFlowBuilder = productFlowBuilder;
        this.statusSimulator = statusSimulator;
        this.taskExecutor = taskExecutor;
        this.topicVendorPrefix = topicVendorPrefix;
    }

    public void processEmulationRequest(EmulationRequestDto request) {
        log.info("Processing emulation request: {}", request);
        String topic = formatTopicName(request.getVendorName());
        List<String> itemIds = generateItemIds(request.getCount());
        Map<String, List<ProductStatus>> flows = productFlowBuilder.generateStateSequence(itemIds, request);

        TimeoutSettingContext timeoutSettings = TimeoutSettingContext.builder()
                .minTimeoutMin(request.getMinTimeoutMin())
                .maxTimeoutMin(request.getMaxTimeoutMin())
                .build();

        simulateProductFlows(topic, request.getArticle(), flows, timeoutSettings);
    }

    private void simulateProductFlows(String topic, Long article,
                                      Map<String, List<ProductStatus>> flows,
                                      TimeoutSettingContext timeoutSettings) {
        List<CompletableFuture<Void>> tasks = flows.entrySet().stream()
                .map(entry -> {
                    SimulationContext context = buildSimulationContext(topic, article, entry.getKey(), entry.getValue());

                    return CompletableFuture.runAsync(
                            () -> statusSimulator.simulate(context, timeoutSettings),
                            taskExecutor);
                })
                .toList();

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]))
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Error during async execution", ex);
                    } else {
                        log.info("Completed emulation of all items");
                    }
                });
    }

    private String formatTopicName(String vendorName) {
        return topicVendorPrefix + vendorName.toLowerCase().replaceAll("[^a-z0-9_]", "_");
    }

    private List<String> generateItemIds(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> UUID.randomUUID().toString())
                .collect(Collectors.toList());
    }

    private SimulationContext buildSimulationContext(String topic, Long article, String itemId,
                                                     List<ProductStatus> statuses) {
        return SimulationContext.builder()
                .topic(topic)
                .itemId(itemId)
                .article(article)
                .statuses(statuses)
                .build();
    }
}
