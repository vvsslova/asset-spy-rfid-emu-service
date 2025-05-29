package asset.spy.rfid.emu.service.strategy;

import asset.spy.rfid.emu.model.ProductStatus;

import java.util.List;

public abstract class BaseStateSequenceStrategy implements StateSequenceStrategy {

    protected static final List<ProductStatus> FULL_SEQUENCE = List.of(
            ProductStatus.RECEIVED_IN_WAREHOUSE,
            ProductStatus.SORTED,
            ProductStatus.READY_FOR_SHIPPING,
            ProductStatus.ON_THE_WAY_TO_STORE,
            ProductStatus.ARRIVED_AT_STORE,
            ProductStatus.SOLD
    );
}
