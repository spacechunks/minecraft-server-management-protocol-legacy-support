package space.chunks.msmp.shared.discovery;

public final class ParameterDescriptor {
    private final String name;
    private final SchemaDescriptor schema;
    private final boolean required;

    public ParameterDescriptor(String name, SchemaDescriptor schema) {
        this(name, schema, true);
    }

    public ParameterDescriptor(String name, SchemaDescriptor schema, boolean required) {
        this.name = name;
        this.schema = schema;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public SchemaDescriptor getSchema() {
        return schema;
    }

    public boolean isRequired() {
        return required;
    }
}
