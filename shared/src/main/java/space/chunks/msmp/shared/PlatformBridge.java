package space.chunks.msmp.shared;

public interface PlatformBridge {
    String platformName();

    void info(String message);

    void warn(String message);
}
