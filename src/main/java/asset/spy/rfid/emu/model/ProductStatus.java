package asset.spy.rfid.emu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum ProductStatus {
    RECEIVED_IN_WAREHOUSE("поступил_на_склад", false),
    SORTED("отсортирован", false),
    READY_FOR_SHIPPING("готов_к_отгрузке", false),
    ON_THE_WAY_TO_STORE("на_пути_в_магазин", false),
    ARRIVED_AT_STORE("товар_поступил_в_магазин", false),
    SOLD("продан", true),
    DEFECTIVE("отбракован", true),
    LOST("утерян", true);

    private static final Map<String, ProductStatus> BY_VALUE = Arrays.stream(values())
            .collect(Collectors.toMap(ProductStatus::getValue, e -> e));

    private final String value;
    private final boolean isFinal;

    public static ProductStatus findByValue(String value) {
        return BY_VALUE.get(value);
    }

    public boolean isFinal() {
        return isFinal;
    }
}
