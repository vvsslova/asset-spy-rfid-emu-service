package asset.spy.rfid.emu.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StateSequenceStrategyFactory {
    private final Map<String, StateSequenceStrategy> strategies;

    @Autowired
    public StateSequenceStrategyFactory(List<StateSequenceStrategy> strategyList) {
        strategies = new HashMap<>();
        strategyList.forEach(strategy -> strategies.put(strategy.getType(), strategy));
    }

    public StateSequenceStrategy getStrategy(String type) {
        StateSequenceStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown type of strategy" + type);
        }
        return strategy;
    }
}
