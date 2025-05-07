package asset.spy.rfid.emu.service;

import asset.spy.rfid.emu.message.EmulationRequest;
import asset.spy.rfid.emu.model.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductStateSequenceService {

    private static final List<ProductStatus> FULL_SEQUENCE = List.of(
            ProductStatus.RECEIVED_IN_WAREHOUSE,
            ProductStatus.SORTED,
            ProductStatus.READY_FOR_SHIPPING,
            ProductStatus.ON_THE_WAY_TO_STORE,
            ProductStatus.ARRIVED_AT_STORE,
            ProductStatus.SOLD
    );
    private final Random random = new Random();

    public Map<String, List<ProductStatus>> generateStateSequence(List<String> itemIds, EmulationRequest request) {
        int total = itemIds.size();
        int defective = calcCount(total, request.getDefectiveFrequency());
        int lost = calcCount(total, request.getLostFrequency());
        int quickSale = calcCount(total, request.getQuickSaleFrequency());

        int[] adjusted = adjustCounts(defective, lost, quickSale, total);
        defective = adjusted[0];
        lost = adjusted[1];
        quickSale = adjusted[2];

        Collections.shuffle(itemIds);

        Map<String, List<ProductStatus>> map = new HashMap<>();
        int i = 0;
        i = assignSequence(itemIds, map, i, defective, SequenceType.DEFECTIVE);
        i = assignSequence(itemIds, map, i, lost, SequenceType.LOST);
        i = assignSequence(itemIds, map, i, quickSale, SequenceType.QUICK_SALE);

        for (; i < itemIds.size(); i++) {
            map.put(itemIds.get(i), fullStateSequence());
        }
        return map;
    }

    private int calcCount(int total, double freq) {
        return (int) Math.round(total * freq);
    }

    private int[] adjustCounts(int defective, int lost, int quickSale, int total) {
        int sum = defective + lost + quickSale;
        if (sum > total) {
            double ratio = (double) total / sum;
            defective = (int) Math.floor(defective * ratio);
            lost = (int) Math.floor(lost * ratio);
            quickSale = (int) Math.floor(quickSale * ratio);
        }
        return new int[]{defective, lost, quickSale};
    }

    private int assignSequence(List<String> ids, Map<String, List<ProductStatus>> flows, int start, int count,
                               SequenceType type) {
        if (count <= 0) {
            return start;
        }

        List<String> availableIds = ids.stream()
                .filter(id -> !flows.containsKey(id))
                .collect(Collectors.toList());

        if (availableIds.isEmpty()) {
            return start;
        }

        int toAssign = Math.min(count, availableIds.size());

        Collections.shuffle(availableIds, random);

        for (int i = 0; i < toAssign; i++) {
            flows.put(availableIds.get(i), buildStateSequence(type));
        }

        return start + toAssign;
    }

    private List<ProductStatus> fullStateSequence() {
        return List.copyOf(FULL_SEQUENCE);
    }

    private List<ProductStatus> buildStateSequence(SequenceType type) {
        return switch (type) {
            case DEFECTIVE -> Arrays.asList(
                    ProductStatus.RECEIVED_IN_WAREHOUSE,
                    ProductStatus.SORTED,
                    ProductStatus.DEFECTIVE
            );
            case LOST -> generateLostSequence(random);
            case QUICK_SALE -> Arrays.asList(
                    ProductStatus.RECEIVED_IN_WAREHOUSE,
                    ProductStatus.SORTED,
                    ProductStatus.SOLD
            );
        };
    }

    private List<ProductStatus> generateLostSequence(Random random) {
        int index = random.nextInt(FULL_SEQUENCE.size() - 1);
        List<ProductStatus> path = new ArrayList<>();

        for (int i = 0; i <= index; i++) {
            path.add(FULL_SEQUENCE.get(i));
        }
        path.add(ProductStatus.LOST);
        return path;
    }

    private enum SequenceType {
        DEFECTIVE, LOST, QUICK_SALE
    }
}
