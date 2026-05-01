package space.chunks.msmp.shared.protocol;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class MinecraftJavaProtocolVersionsTest {
    @Test
    public void resolvesLegacyBukkitVersionText() {
        assertEquals(47, MinecraftJavaProtocolVersions.protocolForVersionText("1.8.8-R0.1-SNAPSHOT"));
    }

    @Test
    public void resolvesPaperVersionTextWithMinecraftMarker() {
        assertEquals(765, MinecraftJavaProtocolVersions.protocolForVersionText("git-Paper-497 (MC: 1.20.4)"));
    }

    @Test
    public void resolvesLatestKnownReleaseRange() {
        assertEquals(773, MinecraftJavaProtocolVersions.protocolForVersionText("1.21.10"));
    }

    @Test
    public void returnsUnknownForProxyOnlyVersionText() {
        assertEquals(MinecraftJavaProtocolVersions.UNKNOWN_PROTOCOL, MinecraftJavaProtocolVersions.protocolForVersionText("Velocity 3.4.0"));
    }

    @Test
    public void returnsUnknownForFutureRelease() {
        assertEquals(MinecraftJavaProtocolVersions.UNKNOWN_PROTOCOL, MinecraftJavaProtocolVersions.protocolForVersionText("1.22"));
    }
}
