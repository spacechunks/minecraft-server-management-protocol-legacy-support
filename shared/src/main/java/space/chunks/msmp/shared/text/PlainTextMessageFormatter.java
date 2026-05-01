package space.chunks.msmp.shared.text;

import space.chunks.msmp.shared.model.Message;

public final class PlainTextMessageFormatter {
    private PlainTextMessageFormatter() {
    }

    public static String format(Message message) {
        if (message == null) {
            return "";
        }
        if (message.getLiteral() != null) {
            return message.getLiteral();
        }
        if (message.getTranslatable() == null) {
            return "";
        }
        if (message.getTranslatableParams() == null || message.getTranslatableParams().isEmpty()) {
            return message.getTranslatable();
        }
        return message.getTranslatable() + " " + message.getTranslatableParams();
    }
}
