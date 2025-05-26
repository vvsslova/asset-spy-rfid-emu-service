package asset.spy.rfid.emu.dto.context;

import asset.spy.rfid.emu.model.ProductStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record SimulationContext(String topic, String itemId, Long article, List<ProductStatus> statuses) {
}
