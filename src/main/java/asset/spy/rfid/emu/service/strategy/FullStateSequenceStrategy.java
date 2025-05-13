package asset.spy.rfid.emu.service.strategy;

import asset.spy.rfid.emu.model.ProductStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("full")
public class FullStateSequenceStrategy extends BaseStateSequenceStrategy {

    @Override
    public List<ProductStatus> buildSequence() {
        return List.copyOf(FULL_SEQUENCE);
    }

    @Override
    public String getType() {
        return StrategyType.FULL.getValue();
    }
}
