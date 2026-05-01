package space.chunks.msmp.spigot.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import space.chunks.msmp.spigot.BukkitManagementNotifications;
import space.chunks.msmp.spigot.BukkitPlayerLookup;
import space.chunks.msmp.spigot.BukkitServerThreadExecutor;
import space.chunks.msmp.shared.model.Operator;
import space.chunks.msmp.shared.spi.OperatorOperations;

final class BukkitOperatorOperations implements OperatorOperations {
    private static final int FULL_OPERATOR_PERMISSION_LEVEL = 4;

    private final BukkitServerThreadExecutor executor;
    private final BukkitPlayerLookup players;
    private final BukkitManagementNotifications notifications;

    BukkitOperatorOperations(BukkitServerThreadExecutor executor, BukkitPlayerLookup players, BukkitManagementNotifications notifications) {
        this.executor = executor;
        this.players = players;
        this.notifications = notifications;
    }

    @Override
    public CompletableFuture<List<Operator>> getOperators() {
        return executor.call(this::operators);
    }

    @Override
    public CompletableFuture<List<Operator>> setOperators(List<Operator> operators) {
        return executor.call(() -> {
            for (OfflinePlayer current : Bukkit.getOperators()) {
                notifications.publish("operators/removed", operator(current));
                current.setOp(false);
            }
            setOperatorStatus(operators, true);
            return operators();
        });
    }

    @Override
    public CompletableFuture<List<Operator>> addOperators(List<Operator> operators) {
        return executor.call(() -> {
            setOperatorStatus(operators, true);
            return operators();
        });
    }

    @Override
    public CompletableFuture<List<Operator>> removeOperators(List<space.chunks.msmp.shared.model.Player> players) {
        return executor.call(() -> {
            for (space.chunks.msmp.shared.model.Player player : players) {
                this.players.offline(player).setOp(false);
                notifications.publish("operators/removed", new Operator(FULL_OPERATOR_PERMISSION_LEVEL, false, player));
            }
            return operators();
        });
    }

    @Override
    public CompletableFuture<List<Operator>> clearOperators() {
        return executor.call(() -> {
            for (OfflinePlayer current : Bukkit.getOperators()) {
                notifications.publish("operators/removed", operator(current));
                current.setOp(false);
            }
            return operators();
        });
    }

    private List<Operator> operators() {
        List<Operator> operators = new ArrayList<Operator>();
        for (OfflinePlayer player : Bukkit.getOperators()) {
            operators.add(operator(player));
        }
        return operators;
    }

    private void setOperatorStatus(List<Operator> operators, boolean operatorStatus) {
        for (Operator operator : operators) {
            players.offline(operator.getPlayer()).setOp(operatorStatus);
            notifications.publish(operatorStatus ? "operators/added" : "operators/removed", operator);
        }
    }

    private Operator operator(OfflinePlayer player) {
        return new Operator(FULL_OPERATOR_PERMISSION_LEVEL, false, players.toModel(player));
    }
}
