package asset.spy.rfid.emu.dto.context;

import asset.spy.rfid.emu.dto.kafka.ProductStatusMessage;
import asset.spy.rfid.emu.model.ProductStatus;

import java.util.concurrent.atomic.AtomicInteger;

public record MessageSendContext(
        String topic,
        String key,
        ProductStatusMessage message,
        ProductStatus status,
        SimulationContext simulationContext,
        TimeoutSettingContext timeoutSettingContext,
        AtomicInteger index
) {}
