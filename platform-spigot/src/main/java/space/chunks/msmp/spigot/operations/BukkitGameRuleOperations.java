package space.chunks.msmp.spigot.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.World;
import space.chunks.msmp.spigot.BukkitManagementNotifications;
import space.chunks.msmp.spigot.mapping.BukkitGameRuleMapper;
import space.chunks.msmp.spigot.BukkitServerThreadExecutor;
import space.chunks.msmp.shared.model.TypedGameRule;
import space.chunks.msmp.shared.model.UntypedGameRule;
import space.chunks.msmp.shared.spi.GameRuleOperations;

final class BukkitGameRuleOperations implements GameRuleOperations {
    private final BukkitServerThreadExecutor executor;
    private final BukkitGameRuleMapper gameRules;
    private final BukkitManagementNotifications notifications;

    BukkitGameRuleOperations(BukkitServerThreadExecutor executor, BukkitGameRuleMapper gameRules, BukkitManagementNotifications notifications) {
        this.executor = executor;
        this.gameRules = gameRules;
        this.notifications = notifications;
    }

    @Override
    public CompletableFuture<List<TypedGameRule>> getGameRules() {
        return executor.call(() -> {
            List<TypedGameRule> rules = new ArrayList<TypedGameRule>();
            World world = primaryWorld();
            if (world == null) {
                return rules;
            }
            for (String key : world.getGameRules()) {
                rules.add(gameRules.typed(key, world.getGameRuleValue(key)));
            }
            return rules;
        });
    }

    @Override
    public CompletableFuture<TypedGameRule> updateGameRule(UntypedGameRule gameRule) {
        return executor.call(() -> {
            World world = primaryWorld();
            if (world == null) {
                throw new IllegalStateException("No worlds are loaded");
            }
            String value = gameRule.getValue().isTextual() ? gameRule.getValue().asText() : gameRule.getValue().toString();
            if (!world.setGameRuleValue(gameRule.getKey(), value)) {
                throw new IllegalArgumentException("Unknown gamerule: " + gameRule.getKey());
            }
            TypedGameRule updated = gameRules.typed(gameRule.getKey(), world.getGameRuleValue(gameRule.getKey()));
            notifications.publish("gamerules/updated", updated);
            return updated;
        });
    }

    private static World primaryWorld() {
        return Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
    }
}
