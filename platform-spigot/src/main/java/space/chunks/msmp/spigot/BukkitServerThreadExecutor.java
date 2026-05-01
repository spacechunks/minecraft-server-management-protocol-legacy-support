package space.chunks.msmp.spigot;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public final class BukkitServerThreadExecutor {
    private final Plugin plugin;

    public BukkitServerThreadExecutor(Plugin plugin) {
        this.plugin = plugin;
    }

    public <T> CompletableFuture<T> call(Callable<T> callable) {
        if (Bukkit.isPrimaryThread()) {
            try {
                return CompletableFuture.completedFuture(callable.call());
            } catch (Exception exception) {
                CompletableFuture<T> failed = new CompletableFuture<T>();
                failed.completeExceptionally(exception);
                return failed;
            }
        }

        CompletableFuture<T> future = new CompletableFuture<T>();
        int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            try {
                future.complete(callable.call());
            } catch (Exception exception) {
                future.completeExceptionally(exception);
            }
        });
        if (taskId == -1) {
            future.completeExceptionally(new IllegalStateException("Unable to schedule management operation on the server thread"));
        }
        return future;
    }
}
