package asset.spy.rfid.emu.service;

import asset.spy.rfid.emu.message.EmulationRequest;
import asset.spy.rfid.emu.model.ProductStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
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

    @Async("kafkaTaskExecutor")
    public void processEmulationRequest(EmulationRequest request) {
        log.info("Processing emulation request: {}", request);

        String topic = formatTopicName(request.getVendorName());
        List<String> itemIds = generateItemIds(request.getCount());
        Map<String, List<ProductStatus>> flows = productFlowBuilder.generateStateSequence(itemIds, request);

        List<CompletableFuture<Void>> tasks = flows.entrySet().stream()
                .map(entry -> CompletableFuture.runAsync(() ->
                        statusSimulator.simulate(
                                topic,
                                entry.getKey(),
                                request.getArticle(),
                                entry.getValue(),
                                request.getMinTimeoutMin(),
                                request.getMaxTimeoutMin()
                        ), taskExecutor))
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
}
