package space.chunks.msmp.shared.protocol;

public enum ProtocolVersion {
    V1_0_0("1.0.0"),
    V1_1_0("1.1.0"),
    V2_0_0("2.0.0"),
    V3_0_0("3.0.0");

    private final String wireName;

    ProtocolVersion(String wireName) {
        this.wireName = wireName;
    }

    public String wireName() {
        return wireName;
    }
}
