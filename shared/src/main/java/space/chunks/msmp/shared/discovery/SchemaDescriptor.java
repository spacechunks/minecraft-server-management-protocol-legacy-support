package space.chunks.msmp.shared.discovery;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class SchemaDescriptor {
    private final String reference;
    private final List<String> type;
    private final SchemaDescriptor items;
    private final Map<String, SchemaDescriptor> properties;
    private final List<String> enumValues;

    private SchemaDescriptor(
        String reference,
        List<String> type,
        SchemaDescriptor items,
        Map<String, SchemaDescriptor> properties,
        List<String> enumValues
    ) {
        this.reference = reference;
        this.type = type == null ? Collections.<String>emptyList() : Collections.unmodifiableList(type);
        this.items = items;
        this.properties = properties == null
            ? Collections.<String, SchemaDescriptor>emptyMap()
            : Collections.unmodifiableMap(new LinkedHashMap<String, SchemaDescriptor>(properties));
        this.enumValues = enumValues == null ? Collections.<String>emptyList() : Collections.unmodifiableList(enumValues);
    }

    public static SchemaDescriptor ref(String name) {
        return new SchemaDescriptor("#/components/schemas/" + name, null, null, null, null);
    }

    public static SchemaDescriptor type(String type) {
        return new SchemaDescriptor(null, Collections.singletonList(type), null, null, null);
    }

    public static SchemaDescriptor types(List<String> types) {
        return new SchemaDescriptor(null, types, null, null, null);
    }

    public static SchemaDescriptor array(SchemaDescriptor items) {
        return new SchemaDescriptor(null, Collections.singletonList("array"), items, null, null);
    }

    public static SchemaDescriptor object(Map<String, SchemaDescriptor> properties) {
        return new SchemaDescriptor(null, Collections.singletonList("object"), null, properties, null);
    }

    public static SchemaDescriptor stringEnum(List<String> enumValues) {
        return new SchemaDescriptor(null, Collections.singletonList("string"), null, null, enumValues);
    }

    @JsonProperty("$ref")
    public String getReference() {
        return reference;
    }

    public List<String> getType() {
        return type;
    }

    public SchemaDescriptor getItems() {
        return items;
    }

    public Map<String, SchemaDescriptor> getProperties() {
        return properties;
    }

    @JsonProperty("enum")
    public List<String> getEnumValues() {
        return enumValues;
    }
}
