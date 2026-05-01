package space.chunks.msmp.shared.spi;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import space.chunks.msmp.shared.model.IncomingIpBan;
import space.chunks.msmp.shared.model.IpBan;
import space.chunks.msmp.shared.model.KickPlayer;
import space.chunks.msmp.shared.model.Operator;
import space.chunks.msmp.shared.model.Player;
import space.chunks.msmp.shared.model.ServerState;
import space.chunks.msmp.shared.model.SystemMessage;
import space.chunks.msmp.shared.model.TypedGameRule;
import space.chunks.msmp.shared.model.UntypedGameRule;
import space.chunks.msmp.shared.model.UserBan;
import space.chunks.msmp.shared.protocol.PlatformCapabilities;
import space.chunks.msmp.shared.protocol.ServerSettingKey;

public abstract class UnsupportedManagementOperations implements ManagementOperations {
    private final AllowlistOperations allowlist = new UnsupportedAllowlistOperations();
    private final BanOperations bans = new UnsupportedBanOperations();
    private final IpBanOperations ipBans = new UnsupportedIpBanOperations();
    private final PlayerOperations players = new UnsupportedPlayerOperations();
    private final OperatorOperations operators = new UnsupportedOperatorOperations();
    private final ServerOperations server = new UnsupportedServerOperations();
    private final ServerSettingsOperations serverSettings = new UnsupportedServerSettingsOperations();
    private final GameRuleOperations gameRules = new UnsupportedGameRuleOperations();

    @Override
    public PlatformCapabilities capabilities() {
        return PlatformCapabilities.discoveryOnly();
    }

    @Override
    public AllowlistOperations allowlist() {
        return allowlist;
    }

    @Override
    public BanOperations bans() {
        return bans;
    }

    @Override
    public IpBanOperations ipBans() {
        return ipBans;
    }

    @Override
    public PlayerOperations players() {
        return players;
    }

    @Override
    public OperatorOperations operators() {
        return operators;
    }

    @Override
    public ServerOperations server() {
        return server;
    }

    @Override
    public ServerSettingsOperations serverSettings() {
        return serverSettings;
    }

    @Override
    public GameRuleOperations gameRules() {
        return gameRules;
    }

    private static <T> CompletableFuture<T> unsupported() {
        CompletableFuture<T> future = new CompletableFuture<T>();
        future.completeExceptionally(new UnsupportedOperationException("Operation is not supported by this platform"));
        return future;
    }

    private static final class UnsupportedAllowlistOperations implements AllowlistOperations {
        public CompletableFuture<List<Player>> getAllowlist() { return unsupported(); }
        public CompletableFuture<List<Player>> setAllowlist(List<Player> players) { return unsupported(); }
        public CompletableFuture<List<Player>> addToAllowlist(List<Player> players) { return unsupported(); }
        public CompletableFuture<List<Player>> removeFromAllowlist(List<Player> players) { return unsupported(); }
        public CompletableFuture<List<Player>> clearAllowlist() { return unsupported(); }
    }

    private static final class UnsupportedBanOperations implements BanOperations {
        public CompletableFuture<List<UserBan>> getBans() { return unsupported(); }
        public CompletableFuture<List<UserBan>> setBans(List<UserBan> bans) { return unsupported(); }
        public CompletableFuture<List<UserBan>> addBans(List<UserBan> bans) { return unsupported(); }
        public CompletableFuture<List<UserBan>> removeBans(List<Player> players) { return unsupported(); }
        public CompletableFuture<List<UserBan>> clearBans() { return unsupported(); }
    }

    private static final class UnsupportedIpBanOperations implements IpBanOperations {
        public CompletableFuture<List<IpBan>> getIpBans() { return unsupported(); }
        public CompletableFuture<List<IpBan>> setIpBans(List<IpBan> bans) { return unsupported(); }
        public CompletableFuture<List<IpBan>> addIpBans(List<IncomingIpBan> bans) { return unsupported(); }
        public CompletableFuture<List<IpBan>> removeIpBans(List<String> ips) { return unsupported(); }
        public CompletableFuture<List<IpBan>> clearIpBans() { return unsupported(); }
    }

    private static final class UnsupportedPlayerOperations implements PlayerOperations {
        public CompletableFuture<List<Player>> getPlayers() { return unsupported(); }
        public CompletableFuture<List<Player>> kickPlayers(List<KickPlayer> players) { return unsupported(); }
    }

    private static final class UnsupportedOperatorOperations implements OperatorOperations {
        public CompletableFuture<List<Operator>> getOperators() { return unsupported(); }
        public CompletableFuture<List<Operator>> setOperators(List<Operator> operators) { return unsupported(); }
        public CompletableFuture<List<Operator>> addOperators(List<Operator> operators) { return unsupported(); }
        public CompletableFuture<List<Operator>> removeOperators(List<Player> players) { return unsupported(); }
        public CompletableFuture<List<Operator>> clearOperators() { return unsupported(); }
    }

    private static final class UnsupportedServerOperations implements ServerOperations {
        public CompletableFuture<ServerState> status() { return unsupported(); }
        public CompletableFuture<Boolean> save(boolean flush) { return unsupported(); }
        public CompletableFuture<Boolean> stop() { return unsupported(); }
        public CompletableFuture<Boolean> sendSystemMessage(SystemMessage message) { return unsupported(); }
    }

    private static final class UnsupportedServerSettingsOperations implements ServerSettingsOperations {
        public CompletableFuture<JsonNode> getSetting(ServerSettingKey key) { return unsupported(); }
        public CompletableFuture<JsonNode> setSetting(ServerSettingKey key, JsonNode value) { return unsupported(); }
    }

    private static final class UnsupportedGameRuleOperations implements GameRuleOperations {
        public CompletableFuture<List<TypedGameRule>> getGameRules() { return unsupported(); }
        public CompletableFuture<TypedGameRule> updateGameRule(UntypedGameRule gameRule) { return unsupported(); }
    }
}
