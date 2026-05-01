package space.chunks.msmp.shared.protocol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MinecraftJavaProtocolVersions {
    public static final int UNKNOWN_PROTOCOL = -1;

    private static final Pattern RELEASE_VERSION = Pattern.compile("\\b(1\\.\\d+(?:\\.\\d+)?)\\b");
    private static final ReleaseRange[] RELEASE_RANGES = {
        range("1.7.6", "1.7.10", 5),
        range("1.8", "1.8.9", 47),
        range("1.9", "1.9", 107),
        range("1.9.1", "1.9.1", 108),
        range("1.9.2", "1.9.2", 109),
        range("1.9.3", "1.9.4", 110),
        range("1.10", "1.10.2", 210),
        range("1.11", "1.11", 315),
        range("1.11.1", "1.11.2", 316),
        range("1.12", "1.12", 335),
        range("1.12.1", "1.12.1", 338),
        range("1.12.2", "1.12.2", 340),
        range("1.13", "1.13", 393),
        range("1.13.1", "1.13.1", 401),
        range("1.13.2", "1.13.2", 404),
        range("1.14", "1.14", 477),
        range("1.14.1", "1.14.1", 480),
        range("1.14.2", "1.14.2", 485),
        range("1.14.3", "1.14.3", 490),
        range("1.14.4", "1.14.4", 498),
        range("1.15", "1.15", 573),
        range("1.15.1", "1.15.1", 575),
        range("1.15.2", "1.15.2", 578),
        range("1.16", "1.16", 735),
        range("1.16.1", "1.16.1", 736),
        range("1.16.2", "1.16.2", 751),
        range("1.16.3", "1.16.3", 753),
        range("1.16.4", "1.16.5", 754),
        range("1.17", "1.17", 755),
        range("1.17.1", "1.17.1", 756),
        range("1.18", "1.18.1", 757),
        range("1.18.2", "1.18.2", 758),
        range("1.19", "1.19", 759),
        range("1.19.1", "1.19.2", 760),
        range("1.19.3", "1.19.3", 761),
        range("1.19.4", "1.19.4", 762),
        range("1.20", "1.20.1", 763),
        range("1.20.2", "1.20.2", 764),
        range("1.20.3", "1.20.4", 765),
        range("1.20.5", "1.20.6", 766),
        range("1.21", "1.21.1", 767),
        range("1.21.2", "1.21.3", 768),
        range("1.21.4", "1.21.4", 769),
        range("1.21.5", "1.21.5", 770),
        range("1.21.6", "1.21.6", 771),
        range("1.21.7", "1.21.8", 772),
        range("1.21.9", "1.21.10", 773)
    };

    private MinecraftJavaProtocolVersions() {
    }

    public static int protocolForVersionText(String versionText) {
        ReleaseVersion release = parseReleaseVersion(versionText);
        if (release == null) {
            return UNKNOWN_PROTOCOL;
        }

        for (ReleaseRange range : RELEASE_RANGES) {
            if (range.contains(release)) {
                return range.protocol;
            }
        }
        return UNKNOWN_PROTOCOL;
    }

    public static String releaseVersion(String versionText) {
        if (versionText == null) {
            return null;
        }

        Matcher matcher = RELEASE_VERSION.matcher(versionText);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static ReleaseVersion parseReleaseVersion(String versionText) {
        String release = releaseVersion(versionText);
        return release == null ? null : ReleaseVersion.parse(release);
    }

    private static ReleaseRange range(String first, String last, int protocol) {
        return new ReleaseRange(ReleaseVersion.parse(first), ReleaseVersion.parse(last), protocol);
    }

    private static final class ReleaseRange {
        private final ReleaseVersion first;
        private final ReleaseVersion last;
        private final int protocol;

        private ReleaseRange(ReleaseVersion first, ReleaseVersion last, int protocol) {
            this.first = first;
            this.last = last;
            this.protocol = protocol;
        }

        private boolean contains(ReleaseVersion version) {
            return version.compareTo(first) >= 0 && version.compareTo(last) <= 0;
        }
    }

    private static final class ReleaseVersion implements Comparable<ReleaseVersion> {
        private final int major;
        private final int minor;
        private final int patch;

        private ReleaseVersion(int major, int minor, int patch) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
        }

        private static ReleaseVersion parse(String version) {
            String[] parts = version.split("\\.");
            int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
            return new ReleaseVersion(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), patch);
        }

        @Override
        public int compareTo(ReleaseVersion other) {
            if (major != other.major) {
                return major - other.major;
            }
            if (minor != other.minor) {
                return minor - other.minor;
            }
            return patch - other.patch;
        }
    }
}
