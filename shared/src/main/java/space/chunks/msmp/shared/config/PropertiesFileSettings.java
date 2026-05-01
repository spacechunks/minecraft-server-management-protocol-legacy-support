package space.chunks.msmp.shared.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class PropertiesFileSettings {
    private PropertiesFileSettings() {
    }

    public static ManagementProtocolSettings loadOrCreate(Path path) {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            if (!Files.exists(path)) {
                Properties defaults = ManagementSettingsProperties.defaults();
                try (OutputStream output = Files.newOutputStream(path)) {
                    defaults.store(output, "Minecraft Server Management Protocol");
                }
            }

            Properties properties = new Properties();
            try (InputStream input = Files.newInputStream(path)) {
                properties.load(input);
            }
            return ManagementSettingsProperties.from(properties);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load management settings from " + path, exception);
        }
    }
}
