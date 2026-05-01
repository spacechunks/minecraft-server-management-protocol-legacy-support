package space.chunks.msmp.shared.discovery;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SchemaCatalog {
    public static final SchemaDescriptor BOOLEAN = SchemaDescriptor.type("boolean");
    public static final SchemaDescriptor INTEGER = SchemaDescriptor.type("integer");
    public static final SchemaDescriptor STRING = SchemaDescriptor.type("string");
    public static final SchemaDescriptor BOOLEAN_OR_INTEGER = SchemaDescriptor.types(Arrays.asList("boolean", "integer"));

    private SchemaCatalog() {
    }

    public static Map<String, SchemaDescriptor> schemas() {
        Map<String, SchemaDescriptor> schemas = new LinkedHashMap<String, SchemaDescriptor>();

        schemas.put("difficulty", SchemaDescriptor.stringEnum(Arrays.asList("peaceful", "easy", "normal", "hard")));
        schemas.put("game_type", SchemaDescriptor.stringEnum(Arrays.asList("survival", "creative", "adventure", "spectator")));
        schemas.put("player", object()
            .field("id", STRING)
            .field("name", STRING)
            .build());
        schemas.put("version", object()
            .field("name", STRING)
            .field("protocol", INTEGER)
            .build());
        schemas.put("server_state", object()
            .field("started", BOOLEAN)
            .field("players", array(ref("player")))
            .field("version", ref("version"))
            .build());
        schemas.put("typed_game_rule", object()
            .field("key", STRING)
            .field("value", BOOLEAN_OR_INTEGER)
            .field("type", SchemaDescriptor.stringEnum(Arrays.asList("integer", "boolean")))
            .build());
        schemas.put("untyped_game_rule", object()
            .field("key", STRING)
            .field("value", BOOLEAN_OR_INTEGER)
            .build());
        schemas.put("message", object()
            .field("literal", STRING)
            .field("translatable", STRING)
            .field("translatableParams", array(STRING))
            .build());
        schemas.put("system_message", object()
            .field("message", ref("message"))
            .field("overlay", BOOLEAN)
            .field("receivingPlayers", array(ref("player")))
            .build());
        schemas.put("kick_player", object()
            .field("message", ref("message"))
            .field("player", ref("player"))
            .build());
        schemas.put("operator", object()
            .field("player", ref("player"))
            .field("bypassesPlayerLimit", BOOLEAN)
            .field("permissionLevel", INTEGER)
            .build());
        schemas.put("incoming_ip_ban", object()
            .field("player", ref("player"))
            .field("ip", STRING)
            .field("reason", STRING)
            .field("source", STRING)
            .field("expires", STRING)
            .build());
        schemas.put("ip_ban", object()
            .field("ip", STRING)
            .field("reason", STRING)
            .field("source", STRING)
            .field("expires", STRING)
            .build());
        schemas.put("user_ban", object()
            .field("player", ref("player"))
            .field("reason", STRING)
            .field("source", STRING)
            .field("expires", STRING)
            .build());

        return schemas;
    }

    public static SchemaDescriptor ref(String name) {
        return SchemaDescriptor.ref(name);
    }

    public static SchemaDescriptor array(SchemaDescriptor items) {
        return SchemaDescriptor.array(items);
    }

    private static ObjectBuilder object() {
        return new ObjectBuilder();
    }

    private static final class ObjectBuilder {
        private final Map<String, SchemaDescriptor> properties = new LinkedHashMap<String, SchemaDescriptor>();

        private ObjectBuilder field(String name, SchemaDescriptor schema) {
            properties.put(name, schema);
            return this;
        }

        private SchemaDescriptor build() {
            return SchemaDescriptor.object(properties);
        }
    }
}
