package space.chunks.msmp.spigot.mapping;

import java.time.Instant;
import java.util.Date;
import org.bukkit.BanEntry;
import space.chunks.msmp.shared.model.IpBan;
import space.chunks.msmp.shared.model.UserBan;

public final class BukkitBanMapper {
    private static final String DEFAULT_SOURCE = "Management server";

    public UserBan userBan(BanEntry entry) {
        return new UserBan(entry.getReason(), instant(entry.getExpiration()), entry.getSource(), new space.chunks.msmp.shared.model.Player(entry.getTarget(), null));
    }

    public IpBan ipBan(BanEntry entry) {
        return new IpBan(entry.getReason(), instant(entry.getExpiration()), entry.getTarget(), entry.getSource());
    }

    public Date expiration(Instant instant) {
        return instant == null ? null : Date.from(instant);
    }

    public String source(String source) {
        return source == null ? DEFAULT_SOURCE : source;
    }

    private Instant instant(Date date) {
        return date == null ? null : date.toInstant();
    }
}
