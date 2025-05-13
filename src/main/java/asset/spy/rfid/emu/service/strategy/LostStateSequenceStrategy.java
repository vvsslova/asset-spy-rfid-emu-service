package asset.spy.rfid.emu.service.strategy;

import asset.spy.rfid.emu.model.ProductStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component("lost")
@RequiredArgsConstructor
public class LostStateSequenceStrategy extends BaseStateSequenceStrategy {

    @Override
    public List<ProductStatus> buildSequence() {
        int fullSize = FULL_SEQUENCE.size();
        int minIndex = 1;
        int maxIndex = fullSize - 2;
        int lostIndex = ThreadLocalRandom.current().nextInt(minIndex, maxIndex + 1);

        List<ProductStatus> path = new ArrayList<>(FULL_SEQUENCE.subList(0, lostIndex + 1));
        path.set(lostIndex, ProductStatus.LOST);
        return path;
    }

    @Override
    public String getType() {
        return StrategyType.LOST.getValue();
    }
}