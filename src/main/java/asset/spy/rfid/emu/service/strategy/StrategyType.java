package asset.spy.rfid.emu.service.strategy;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StrategyType {
    DEFECTIVE("defective"),
    LOST("lost"),
    QUICK_SALE("quickSale"),
    FULL("full");

    private final String value;
}