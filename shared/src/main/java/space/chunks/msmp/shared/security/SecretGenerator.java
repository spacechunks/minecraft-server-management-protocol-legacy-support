package space.chunks.msmp.shared.security;

import java.security.SecureRandom;

public final class SecretGenerator {
    public static final int SECRET_LENGTH = 40;
    private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    private final SecureRandom random;

    public SecretGenerator() {
        this(new SecureRandom());
    }

    public SecretGenerator(SecureRandom random) {
        this.random = random;
    }

    public String generate() {
        char[] value = new char[SECRET_LENGTH];
        for (int i = 0; i < value.length; i++) {
            value[i] = ALPHABET[random.nextInt(ALPHABET.length)];
        }
        return new String(value);
    }

    public static boolean isValid(String secret) {
        if (secret == null || secret.length() != SECRET_LENGTH) {
            return false;
        }

        for (int i = 0; i < secret.length(); i++) {
            char current = secret.charAt(i);
            if (!isAlphaNumeric(current)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isAlphaNumeric(char value) {
        return value >= 'A' && value <= 'Z'
            || value >= 'a' && value <= 'z'
            || value >= '0' && value <= '9';
    }
}
