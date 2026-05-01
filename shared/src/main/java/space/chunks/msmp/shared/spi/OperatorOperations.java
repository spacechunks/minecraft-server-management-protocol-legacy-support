package space.chunks.msmp.shared.spi;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import space.chunks.msmp.shared.model.Operator;
import space.chunks.msmp.shared.model.Player;

public interface OperatorOperations {
    CompletableFuture<List<Operator>> getOperators();

    CompletableFuture<List<Operator>> setOperators(List<Operator> operators);

    CompletableFuture<List<Operator>> addOperators(List<Operator> operators);

    CompletableFuture<List<Operator>> removeOperators(List<Player> players);

    CompletableFuture<List<Operator>> clearOperators();
}
