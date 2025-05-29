package asset.spy.rfid.emu.service.strategy;

import asset.spy.rfid.emu.model.ProductStatus;

import java.util.List;

public interface StateSequenceStrategy {
    List<ProductStatus> buildSequence();
    StrategyType getType();
}
