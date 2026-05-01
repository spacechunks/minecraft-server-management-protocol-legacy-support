package space.chunks.msmp.spigot.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import space.chunks.msmp.shared.json.ObjectMapperFactory;
import space.chunks.msmp.shared.model.TypedGameRule;
import space.chunks.msmp.shared.protocol.GameRuleType;

public final class BukkitGameRuleMapper {
    private final ObjectMapper mapper = ObjectMapperFactory.create();

    public BukkitGameRuleMapper() {
    }

    public TypedGameRule typed(String key, String value) {
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return new TypedGameRule(GameRuleType.BOOLEAN, key, mapper.valueToTree(Boolean.valueOf(value)));
        }
        try {
            return new TypedGameRule(GameRuleType.INTEGER, key, mapper.valueToTree(Integer.valueOf(value)));
        } catch (NumberFormatException ignored) {
            return new TypedGameRule(GameRuleType.INTEGER, key, mapper.valueToTree(0));
        }
    }
}
