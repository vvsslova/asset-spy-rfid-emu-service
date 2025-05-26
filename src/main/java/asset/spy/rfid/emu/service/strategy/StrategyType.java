package asset.spy.rfid.emu.service.strategy;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StrategyType {
    DEFECTIVE,
    LOST,
    QUICK_SALE,
    FULL
}
