package space.chunks.msmp.spigot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.bukkit.plugin.java.JavaPlugin;
import space.chunks.msmp.shared.ManagementProtocolService;
import space.chunks.msmp.shared.config.ManagementProtocolSettings;
import space.chunks.msmp.shared.config.ManagementSettingsProperties;
import space.chunks.msmp.shared.PlatformBridge;
import space.chunks.msmp.shared.protocol.ProtocolVersion;
import space.chunks.msmp.shared.api.ManagementMethodBinder;
import space.chunks.msmp.shared.api.ManagementNotificationPublisher;
import space.chunks.msmp.spigot.operations.BukkitManagementOperations;

public final class MsmpSpigotPlugin extends JavaPlugin implements PlatformBridge {
    private ManagementProtocolService service;
    private BukkitManagementNotifications notifications;

    @Override
    public void onEnable() {
        ManagementNotificationPublisher notificationPublisher = new ManagementNotificationPublisher();
        notifications = new BukkitManagementNotifications(this, this, notificationPublisher, new BukkitPlayerLookup());
        BukkitManagementOperations operations = new BukkitManagementOperations(this, notifications);
        service = new ManagementProtocolService(
            this,
            loadSettings(),
            new ManagementMethodBinder().bind(operations, ProtocolVersion.V3_0_0),
            notificationPublisher
        );
        service.start();
        if (service.isRunning()) {
            notifications.start();
        }
    }

    @Override
    public void onDisable() {
        if (notifications != null) {
            notifications.stop();
        }
        if (service != null) {
            service.stop();
        }
    }

    @Override
    public String platformName() {
        return "spigot";
    }

    @Override
    public void info(String message) {
        getLogger().info(message);
    }

    @Override
    public void warn(String message) {
        getLogger().warning(message);
    }

    private ManagementProtocolSettings loadSettings() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            ManagementProtocolSettings serverPropertiesSettings = loadServerPropertiesSettings();
            if (serverPropertiesSettings != null) {
                info("Using management server settings from server.properties because no plugin config.yml exists.");
                return serverPropertiesSettings;
            }

            saveDefaultConfig();
        }

        String keystore = getConfig().getString("management-server-tls-keystore", "");
        return ManagementProtocolSettings.builder()
            .enabled(getConfig().getBoolean("management-server-enabled", false))
            .host(getConfig().getString("management-server-host", "localhost"))
            .port(getConfig().getInt("management-server-port", 0))
            .secret(getConfig().getString("management-server-secret", ""))
            .allowedOrigins(ManagementSettingsProperties.parseCsv(getConfig().getString("management-server-allowed-origins", "")))
            .tlsEnabled(getConfig().getBoolean("management-server-tls-enabled", false))
            .tlsKeystore(keystore == null || keystore.trim().isEmpty() ? null : java.nio.file.Paths.get(keystore))
            .tlsKeystorePassword(getConfig().getString("management-server-tls-keystore-password", ""))
            .build();
    }

    private ManagementProtocolSettings loadServerPropertiesSettings() {
        Path serverProperties = getServer().getWorldContainer().toPath().resolve("server.properties");
        if (!Files.exists(serverProperties)) {
            return null;
        }

        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(serverProperties)) {
            properties.load(input);
        } catch (IOException exception) {
            warn("Failed to read server.properties for management server settings: " + exception.getMessage());
            return null;
        }

        if (!ManagementSettingsProperties.containsManagementSetting(properties)) {
            return null;
        }

        return ManagementSettingsProperties.from(properties);
    }
}
