package asset.spy.rfid.emu.service;

import asset.spy.rfid.emu.message.EmulationRequest;
import asset.spy.rfid.emu.model.ProductStatus;
import asset.spy.rfid.emu.service.strategy.StrategyType;
import asset.spy.rfid.emu.service.strategy.StateSequenceStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductStateSequenceService {

    private final StateSequenceStrategyFactory strategyFactory;
    private final Random random = new Random();

    private record SequenceDistribution(int defective, int lost, int quickSale) {
    }

    public Map<String, List<ProductStatus>> generateStateSequence(List<String> itemIds, EmulationRequest request) {
        SequenceDistribution distribution = calculateDistribution(
                itemIds.size(),
                request.getDefectiveFrequency(),
                request.getLostFrequency(),
                request.getQuickSaleFrequency()
        );

        List<String> shuffledIds = new ArrayList<>(itemIds);
        Collections.shuffle(shuffledIds, random);

        return assignSequencesToItems(shuffledIds, distribution);
    }

    private SequenceDistribution calculateDistribution(int total, double defectiveFreq,
                                                       double lostFreq, double quickSaleFreq) {
        int defective = calcCount(total, defectiveFreq);
        int lost = calcCount(total, lostFreq);
        int quickSale = calcCount(total, quickSaleFreq);
        int[] adjusted = adjustCounts(defective, lost, quickSale, total);
        return new SequenceDistribution(adjusted[0], adjusted[1], adjusted[2]);
    }

    private Map<String, List<ProductStatus>> assignSequencesToItems(List<String> itemIds,
                                                                    SequenceDistribution distribution) {
        Map<String, List<ProductStatus>> flows = new HashMap<>();
        List<String> remainingIds = new ArrayList<>(itemIds);

        assignSequence(flows, remainingIds, distribution.defective, StrategyType.DEFECTIVE.getValue());
        assignSequence(flows, remainingIds, distribution.lost, StrategyType.LOST.getValue());
        assignSequence(flows, remainingIds, distribution.quickSale, StrategyType.QUICK_SALE.getValue());

        for (String id : remainingIds) {
            flows.put(id, strategyFactory.getStrategy(StrategyType.FULL.getValue()).buildSequence());
        }
        return flows;
    }

    private int calcCount(int total, double freq) {
        return BigDecimal.valueOf(total)
                .multiply(BigDecimal.valueOf(freq))
                .setScale(0, RoundingMode.UP)
                .intValueExact();
    }

    private int[] adjustCounts(int defective, int lost, int quickSale, int total) {
        int sum = defective + lost + quickSale;
        if (sum <= total) {
            return new int[]{defective, lost, quickSale};
        }

        double ratio = (double) total / sum;
        int[] adjusted = new int[]{
                (int) Math.floor(defective * ratio),
                (int) Math.floor(lost * ratio),
                (int) Math.floor(quickSale * ratio)
        };

        int remaining = total - (adjusted[0] + adjusted[1] + adjusted[2]);

        while (remaining-- > 0) {
            if (adjusted[0] < defective) adjusted[0]++;
            else if (adjusted[1] < lost) adjusted[1]++;
            else if (adjusted[2] < quickSale) adjusted[2]++;
        }
        return adjusted;
    }

    private void assignSequence(Map<String, List<ProductStatus>> flows, List<String> availableIds,
                                int count, String strategyType) {
        if (count <= 0 || availableIds.isEmpty()) {
            return;
        }

        int toAssign = Math.min(count, availableIds.size());
        List<String> idsToAssign = new ArrayList<>(availableIds.subList(0, toAssign));

        for (String itemId : idsToAssign) {
            List<ProductStatus> sequence = strategyFactory.getStrategy(strategyType).buildSequence();
            flows.put(itemId, sequence);
        }
        availableIds.subList(0, toAssign).clear();
    }
}
