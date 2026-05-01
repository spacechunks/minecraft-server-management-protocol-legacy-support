package space.chunks.msmp.shared.discovery;

public final class ResultDescriptor {
    private final String name;
    private final SchemaDescriptor schema;

    public ResultDescriptor(String name, SchemaDescriptor schema) {
        this.name = name;
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public SchemaDescriptor getSchema() {
        return schema;
    }
}
