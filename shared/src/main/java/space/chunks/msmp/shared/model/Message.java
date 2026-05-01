package space.chunks.msmp.shared.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class Message {
    private final String translatable;
    private final List<String> translatableParams;
    private final String literal;

    @JsonCreator
    public Message(
        @JsonProperty("translatable") String translatable,
        @JsonProperty("translatableParams") List<String> translatableParams,
        @JsonProperty("literal") String literal
    ) {
        this.translatable = translatable;
        this.translatableParams = translatableParams == null
            ? null
            : Collections.unmodifiableList(new ArrayList<String>(translatableParams));
        this.literal = literal;
    }

    public static Message literal(String literal) {
        return new Message(null, null, literal);
    }

    public String getTranslatable() {
        return translatable;
    }

    public List<String> getTranslatableParams() {
        return translatableParams;
    }

    public String getLiteral() {
        return literal;
    }
}
