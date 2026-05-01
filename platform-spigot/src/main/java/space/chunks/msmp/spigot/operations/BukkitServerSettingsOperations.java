package space.chunks.msmp.spigot.operations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import space.chunks.msmp.spigot.BukkitServerThreadExecutor;
import space.chunks.msmp.shared.json.ObjectMapperFactory;
import space.chunks.msmp.shared.protocol.ServerSettingKey;
import space.chunks.msmp.shared.spi.ServerSettingsOperations;

final class BukkitServerSettingsOperations implements ServerSettingsOperations {
    private final BukkitServerThreadExecutor executor;
    private final ObjectMapper mapper = ObjectMapperFactory.create();

    BukkitServerSettingsOperations(BukkitServerThreadExecutor executor) {
        this.executor = executor;
    }

    @Override
    public CompletableFuture<JsonNode> getSetting(ServerSettingKey key) {
        return executor.call(() -> settingValue(key));
    }

    @Override
    public CompletableFuture<JsonNode> setSetting(ServerSettingKey key, JsonNode value) {
        return executor.call(() -> {
            switch (key) {
                case AUTOSAVE:
                    for (World world : Bukkit.getWorlds()) {
                        world.setAutoSave(value.asBoolean());
                    }
                    break;
                case DIFFICULTY:
                    for (World world : Bukkit.getWorlds()) {
                        world.setDifficulty(Difficulty.valueOf(value.asText().toUpperCase(java.util.Locale.ROOT)));
                    }
                    break;
                case USE_ALLOWLIST:
                    Bukkit.setWhitelist(value.asBoolean());
                    break;
                case PLAYER_IDLE_TIMEOUT:
                    Bukkit.setIdleTimeout(value.asInt());
                    break;
                case SPAWN_PROTECTION_RADIUS:
                    Bukkit.setSpawnRadius(value.asInt());
                    break;
                case GAME_MODE:
                    Bukkit.setDefaultGameMode(GameMode.valueOf(value.asText().toUpperCase(java.util.Locale.ROOT)));
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported server setting: " + key.path());
            }
            return settingValue(key);
        });
    }

    private JsonNode settingValue(ServerSettingKey key) {
        World primaryWorld = primaryWorld();
        switch (key) {
            case AUTOSAVE:
                return mapper.valueToTree(primaryWorld != null && primaryWorld.isAutoSave());
            case DIFFICULTY:
                return mapper.valueToTree(primaryWorld == null ? "normal" : primaryWorld.getDifficulty().name().toLowerCase(java.util.Locale.ROOT));
            case USE_ALLOWLIST:
                return mapper.valueToTree(Bukkit.hasWhitelist());
            case PLAYER_IDLE_TIMEOUT:
                return mapper.valueToTree(Bukkit.getIdleTimeout());
            case ALLOW_FLIGHT:
                return mapper.valueToTree(Bukkit.getAllowFlight());
            case MOTD:
                return mapper.valueToTree(Bukkit.getMotd());
            case SPAWN_PROTECTION_RADIUS:
                return mapper.valueToTree(Bukkit.getSpawnRadius());
            case GAME_MODE:
                return mapper.valueToTree(Bukkit.getDefaultGameMode().name().toLowerCase(java.util.Locale.ROOT));
            case VIEW_DISTANCE:
                return mapper.valueToTree(Bukkit.getViewDistance());
            default:
                throw new IllegalArgumentException("Unsupported server setting: " + key.path());
        }
    }

    private static World primaryWorld() {
        return Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
    }
}
