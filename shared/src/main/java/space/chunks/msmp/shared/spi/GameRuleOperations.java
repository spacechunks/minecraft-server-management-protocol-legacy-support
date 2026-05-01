package space.chunks.msmp.shared.spi;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import space.chunks.msmp.shared.model.TypedGameRule;
import space.chunks.msmp.shared.model.UntypedGameRule;

public interface GameRuleOperations {
    CompletableFuture<List<TypedGameRule>> getGameRules();

    CompletableFuture<TypedGameRule> updateGameRule(UntypedGameRule gameRule);
}
