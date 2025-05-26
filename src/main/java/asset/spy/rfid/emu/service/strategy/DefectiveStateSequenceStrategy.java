package asset.spy.rfid.emu.service.strategy;

import asset.spy.rfid.emu.model.ProductStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DefectiveStateSequenceStrategy extends BaseStateSequenceStrategy {

    @Override
    public List<ProductStatus> buildSequence() {
        return Arrays.asList(
                ProductStatus.RECEIVED_IN_WAREHOUSE,
                ProductStatus.SORTED,
                ProductStatus.DEFECTIVE
        );
    }

    @Override
    public StrategyType getType() {
        return StrategyType.DEFECTIVE;
    }
}
