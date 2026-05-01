package space.chunks.msmp.bungeecord;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import space.chunks.msmp.shared.model.KickPlayer;
import space.chunks.msmp.shared.model.ServerState;
import space.chunks.msmp.shared.model.SystemMessage;
import space.chunks.msmp.shared.model.Version;
import space.chunks.msmp.shared.protocol.MinecraftJavaProtocolVersions;
import space.chunks.msmp.shared.protocol.PlatformCapabilities;
import space.chunks.msmp.shared.spi.PlayerOperations;
import space.chunks.msmp.shared.spi.ServerOperations;
import space.chunks.msmp.shared.spi.UnsupportedManagementOperations;
import space.chunks.msmp.shared.text.PlainTextMessageFormatter;

final class BungeeManagementOperations extends UnsupportedManagementOperations {
    private final ProxyServer proxy;
    private final PlayerOperations players = new BungeePlayerOperations();
    private final ServerOperations server = new BungeeServerOperations();

    BungeeManagementOperations(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public PlatformCapabilities capabilities() {
        return BungeeManagementCapabilities.create();
    }

    @Override
    public PlayerOperations players() {
        return players;
    }

    @Override
    public ServerOperations server() {
        return server;
    }

    space.chunks.msmp.shared.model.Player toModel(ProxiedPlayer player) {
        return new space.chunks.msmp.shared.model.Player(player.getName(), player.getUniqueId());
    }

    List<space.chunks.msmp.shared.model.Player> onlinePlayers() {
        List<space.chunks.msmp.shared.model.Player> players = new ArrayList<space.chunks.msmp.shared.model.Player>();
        for (ProxiedPlayer player : proxy.getPlayers()) {
            players.add(toModel(player));
        }
        return players;
    }

    private ProxiedPlayer find(space.chunks.msmp.shared.model.Player player) {
        if (player.getId() != null) {
            return proxy.getPlayer(player.getId());
        }
        return player.getName() == null ? null : proxy.getPlayer(player.getName());
    }

    private final class BungeePlayerOperations implements PlayerOperations {
        @Override
        public CompletableFuture<List<space.chunks.msmp.shared.model.Player>> getPlayers() {
            return CompletableFuture.completedFuture(onlinePlayers());
        }

        @Override
        public CompletableFuture<List<space.chunks.msmp.shared.model.Player>> kickPlayers(List<KickPlayer> players) {
            List<space.chunks.msmp.shared.model.Player> kicked = new ArrayList<space.chunks.msmp.shared.model.Player>();
            for (KickPlayer kick : players) {
                ProxiedPlayer player = find(kick.getPlayer());
                if (player != null) {
                    player.disconnect(TextComponent.fromLegacyText(PlainTextMessageFormatter.format(kick.getMessage())));
                    kicked.add(toModel(player));
                }
            }
            return CompletableFuture.completedFuture(kicked);
        }
    }

    private final class BungeeServerOperations implements ServerOperations {
        @Override
        public CompletableFuture<ServerState> status() {
            return CompletableFuture.completedFuture(new ServerState(onlinePlayers(), true, version()));
        }

        @Override
        public CompletableFuture<Boolean> save(boolean flush) {
            CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
            future.completeExceptionally(new UnsupportedOperationException("Operation is not supported by this platform"));
            return future;
        }

        @Override
        public CompletableFuture<Boolean> stop() {
            proxy.stop();
            return CompletableFuture.completedFuture(Boolean.TRUE);
        }

        @Override
        public CompletableFuture<Boolean> sendSystemMessage(SystemMessage message) {
            BaseComponent[] text = TextComponent.fromLegacyText(PlainTextMessageFormatter.format(message.getMessage()));
            if (message.getReceivingPlayers() == null) {
                proxy.broadcast(text);
                return CompletableFuture.completedFuture(Boolean.TRUE);
            }
            if (message.getReceivingPlayers().isEmpty()) {
                return CompletableFuture.completedFuture(Boolean.FALSE);
            }
            for (space.chunks.msmp.shared.model.Player target : message.getReceivingPlayers()) {
                ProxiedPlayer player = find(target);
                if (player != null) {
                    player.sendMessage(ChatMessageType.CHAT, text);
                }
            }
            return CompletableFuture.completedFuture(Boolean.TRUE);
        }
    }

    Version version() {
        String version = proxy.getVersion();
        int protocol = MinecraftJavaProtocolVersions.protocolForVersionText(version);
        return new Version(protocol, version);
    }
}
