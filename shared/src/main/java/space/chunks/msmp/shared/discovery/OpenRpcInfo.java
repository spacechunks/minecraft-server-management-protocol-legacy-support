package space.chunks.msmp.shared.discovery;

public final class OpenRpcInfo {
    private final String title;
    private final String version;

    public OpenRpcInfo(String title, String version) {
        this.title = title;
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public String getVersion() {
        return version;
    }
}
