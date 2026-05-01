package space.chunks.msmp.shared.api.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import space.chunks.msmp.shared.discovery.EndpointGroup;
import space.chunks.msmp.shared.discovery.MethodDescriptor;
import space.chunks.msmp.shared.discovery.ParameterDescriptor;
import space.chunks.msmp.shared.discovery.ResultDescriptor;
import space.chunks.msmp.shared.discovery.SchemaCatalog;
import space.chunks.msmp.shared.discovery.SchemaDescriptor;
import space.chunks.msmp.shared.api.MethodHandler;
import space.chunks.msmp.shared.jsonrpc.JsonRpcParamReader;
import space.chunks.msmp.shared.jsonrpc.JsonRpcRequest;
import space.chunks.msmp.shared.model.IncomingIpBan;
import space.chunks.msmp.shared.model.IpBan;
import space.chunks.msmp.shared.model.KickPlayer;
import space.chunks.msmp.shared.model.Operator;
import space.chunks.msmp.shared.model.Player;
import space.chunks.msmp.shared.model.SystemMessage;
import space.chunks.msmp.shared.model.UntypedGameRule;
import space.chunks.msmp.shared.model.UserBan;
import space.chunks.msmp.shared.protocol.MethodName;
import space.chunks.msmp.shared.spi.ManagementOperations;

/**
 * Defines the regular management routes in one place: discovery metadata and
 * the JSON-RPC handler are intentionally kept together.
 *
 * Server setting routes are generated from {@code ServerSettingKey} in
 * {@code MinecraftProtocolCatalog} and handled dynamically by
 * {@code ManagementMethodBinder}.
 */
public final class ManagementMethodRoutes {
    private static final List<Route> ROUTES = createRoutes();

    private ManagementMethodRoutes() {
    }

    public static List<MethodDescriptor> descriptors() {
        List<MethodDescriptor> descriptors = new ArrayList<MethodDescriptor>();
        for (Route route : ROUTES) {
            descriptors.add(route.descriptor);
        }
        return Collections.unmodifiableList(descriptors);
    }

    public static Map<MethodName, MethodHandler> bindAll(ManagementOperations operations, JsonRpcParamReader params, ObjectMapper mapper) {
        RouteContext context = new RouteContext(operations, params, mapper);
        Map<MethodName, MethodHandler> handlers = new LinkedHashMap<MethodName, MethodHandler>();
        for (Route route : ROUTES) {
            handlers.put(route.descriptor.name(), route.factory.bind(context));
        }
        return handlers;
    }

    private static List<Route> createRoutes() {
        List<Route> routes = new ArrayList<Route>();

        routes.add(route("allowlist", EndpointGroup.ALLOWLIST, "Get the allowlist", null, result("allowlist", array(ref("player"))),
            ctx -> ctx.noParams(request -> ctx.operations.allowlist().getAllowlist())));
        routes.add(route("allowlist/set", EndpointGroup.ALLOWLIST, "Set the allowlist", param("players", array(ref("player"))), result("allowlist", array(ref("player"))),
            ctx -> ctx.oneParam("players", request -> ctx.operations.allowlist().setAllowlist(ctx.readList(request, "players", Player.class)))));
        routes.add(route("allowlist/add", EndpointGroup.ALLOWLIST, "Add players to allowlist", param("add", array(ref("player"))), result("allowlist", array(ref("player"))),
            ctx -> ctx.oneParam("add", request -> ctx.operations.allowlist().addToAllowlist(ctx.readList(request, "add", Player.class)))));
        routes.add(route("allowlist/remove", EndpointGroup.ALLOWLIST, "Remove players from allowlist", param("remove", array(ref("player"))), result("allowlist", array(ref("player"))),
            ctx -> ctx.oneParam("remove", request -> ctx.operations.allowlist().removeFromAllowlist(ctx.readList(request, "remove", Player.class)))));
        routes.add(route("allowlist/clear", EndpointGroup.ALLOWLIST, "Clear all players in allowlist", null, result("allowlist", array(ref("player"))),
            ctx -> ctx.noParams(request -> ctx.operations.allowlist().clearAllowlist())));

        routes.add(route("bans", EndpointGroup.BANS, "Get the ban list", null, result("banlist", array(ref("user_ban"))),
            ctx -> ctx.noParams(request -> ctx.operations.bans().getBans())));
        routes.add(route("bans/set", EndpointGroup.BANS, "Set the banlist", param("bans", array(ref("user_ban"))), result("banlist", array(ref("user_ban"))),
            ctx -> ctx.oneParam("bans", request -> ctx.operations.bans().setBans(ctx.readList(request, "bans", UserBan.class)))));
        routes.add(route("bans/add", EndpointGroup.BANS, "Add players to ban list", param("add", array(ref("user_ban"))), result("banlist", array(ref("user_ban"))),
            ctx -> ctx.oneParam("add", request -> ctx.operations.bans().addBans(ctx.readList(request, "add", UserBan.class)))));
        routes.add(route("bans/remove", EndpointGroup.BANS, "Remove players from ban list", param("remove", array(ref("player"))), result("banlist", array(ref("user_ban"))),
            ctx -> ctx.oneParam("remove", request -> ctx.operations.bans().removeBans(ctx.readList(request, "remove", Player.class)))));
        routes.add(route("bans/clear", EndpointGroup.BANS, "Clear all players in ban list", null, result("banlist", array(ref("user_ban"))),
            ctx -> ctx.noParams(request -> ctx.operations.bans().clearBans())));

        routes.add(route("ip_bans", EndpointGroup.IP_BANS, "Get the ip ban list", null, result("banlist", array(ref("ip_ban"))),
            ctx -> ctx.noParams(request -> ctx.operations.ipBans().getIpBans())));
        routes.add(route("ip_bans/set", EndpointGroup.IP_BANS, "Set the ip banlist", param("banlist", array(ref("ip_ban"))), result("banlist", array(ref("ip_ban"))),
            ctx -> ctx.oneParam("banlist", request -> ctx.operations.ipBans().setIpBans(ctx.readList(request, "banlist", IpBan.class)))));
        routes.add(route("ip_bans/add", EndpointGroup.IP_BANS, "Add ip to ban list", param("add", array(ref("incoming_ip_ban"))), result("banlist", array(ref("ip_ban"))),
            ctx -> ctx.oneParam("add", request -> ctx.operations.ipBans().addIpBans(ctx.readList(request, "add", IncomingIpBan.class)))));
        routes.add(route("ip_bans/remove", EndpointGroup.IP_BANS, "Remove ip from ban list", param("ip", array(SchemaCatalog.STRING)), result("banlist", array(ref("ip_ban"))),
            ctx -> ctx.oneParam("ip", request -> ctx.operations.ipBans().removeIpBans(ctx.readList(request, "ip", String.class)))));
        routes.add(route("ip_bans/clear", EndpointGroup.IP_BANS, "Clear all ips in ban list", null, result("banlist", array(ref("ip_ban"))),
            ctx -> ctx.noParams(request -> ctx.operations.ipBans().clearIpBans())));

        routes.add(route("players", EndpointGroup.PLAYERS, "Get all connected players", null, result("players", array(ref("player"))),
            ctx -> ctx.noParams(request -> ctx.operations.players().getPlayers())));
        routes.add(route("players/kick", EndpointGroup.PLAYERS, "Kick players", param("kick", array(ref("kick_player"))), result("kicked", array(ref("player"))),
            ctx -> ctx.oneParam("kick", request -> ctx.operations.players().kickPlayers(ctx.readList(request, "kick", KickPlayer.class)))));

        routes.add(route("operators", EndpointGroup.OPERATORS, "Get all oped players", null, result("operators", array(ref("operator"))),
            ctx -> ctx.noParams(request -> ctx.operations.operators().getOperators())));
        routes.add(route("operators/set", EndpointGroup.OPERATORS, "Set all oped players", param("operators", array(ref("operator"))), result("operators", array(ref("operator"))),
            ctx -> ctx.oneParam("operators", request -> ctx.operations.operators().setOperators(ctx.readList(request, "operators", Operator.class)))));
        routes.add(route("operators/add", EndpointGroup.OPERATORS, "Op players", param("add", array(ref("operator"))), result("operators", array(ref("operator"))),
            ctx -> ctx.oneParam("add", request -> ctx.operations.operators().addOperators(ctx.readList(request, "add", Operator.class)))));
        routes.add(route("operators/remove", EndpointGroup.OPERATORS, "Deop players", param("remove", array(ref("player"))), result("operators", array(ref("operator"))),
            ctx -> ctx.oneParam("remove", request -> ctx.operations.operators().removeOperators(ctx.readList(request, "remove", Player.class)))));
        routes.add(route("operators/clear", EndpointGroup.OPERATORS, "Deop all players", null, result("operators", array(ref("operator"))),
            ctx -> ctx.noParams(request -> ctx.operations.operators().clearOperators())));

        routes.add(route("server/status", EndpointGroup.SERVER, "Get server status", null, result("status", ref("server_state")), true,
            ctx -> ctx.noParams(request -> ctx.operations.server().status())));
        routes.add(route("server/save", EndpointGroup.SERVER, "Save server state", param("flush", SchemaCatalog.BOOLEAN), result("saving", SchemaCatalog.BOOLEAN),
            ctx -> ctx.oneParam("flush", request -> ctx.operations.server().save(ctx.params.required(request, "flush", Boolean.class)))));
        routes.add(route("server/stop", EndpointGroup.SERVER, "Stop server", null, result("stopping", SchemaCatalog.BOOLEAN),
            ctx -> ctx.noParams(request -> ctx.operations.server().stop())));
        routes.add(route("server/system_message", EndpointGroup.SERVER, "Send a system message", param("message", ref("system_message")), result("sent", SchemaCatalog.BOOLEAN),
            ctx -> ctx.oneParam("message", request -> ctx.operations.server().sendSystemMessage(ctx.params.required(request, "message", SystemMessage.class)))));

        routes.add(route("gamerules", EndpointGroup.GAMERULES, "Get the available game rule keys and their current values", null, result("gamerules", array(ref("typed_game_rule"))),
            ctx -> ctx.noParams(request -> ctx.operations.gameRules().getGameRules())));
        routes.add(route("gamerules/update", EndpointGroup.GAMERULES, "Update game rule value", param("gamerule", ref("untyped_game_rule")), result("gamerule", ref("typed_game_rule")),
            ctx -> ctx.oneParam("gamerule", request -> ctx.operations.gameRules().updateGameRule(ctx.params.required(request, "gamerule", UntypedGameRule.class)))));

        return Collections.unmodifiableList(routes);
    }

    private static Route route(String path, EndpointGroup group, String description, ParameterDescriptor parameter, ResultDescriptor result, RouteHandlerFactory factory) {
        return route(path, group, description, parameter, result, false, factory);
    }

    private static Route route(
        String path,
        EndpointGroup group,
        String description,
        ParameterDescriptor parameter,
        ResultDescriptor result,
        boolean availableBeforeServerStart,
        RouteHandlerFactory factory
    ) {
        return new Route(new MethodDescriptor(MethodName.minecraft(path), group, description, parameter, result, true, true, availableBeforeServerStart), factory);
    }

    private static ParameterDescriptor param(String name, SchemaDescriptor schema) {
        return new ParameterDescriptor(name, schema);
    }

    private static ResultDescriptor result(String name, SchemaDescriptor schema) {
        return new ResultDescriptor(name, schema);
    }

    private static SchemaDescriptor ref(String name) {
        return SchemaCatalog.ref(name);
    }

    private static SchemaDescriptor array(SchemaDescriptor items) {
        return SchemaCatalog.array(items);
    }

    private static final class Route {
        private final MethodDescriptor descriptor;
        private final RouteHandlerFactory factory;

        private Route(MethodDescriptor descriptor, RouteHandlerFactory factory) {
            this.descriptor = descriptor;
            this.factory = factory;
        }
    }

    private static final class RouteContext {
        private final ManagementOperations operations;
        private final JsonRpcParamReader params;
        private final ObjectMapper mapper;

        private RouteContext(ManagementOperations operations, JsonRpcParamReader params, ObjectMapper mapper) {
            this.operations = operations;
            this.params = params;
            this.mapper = mapper;
        }

        private MethodHandler noParams(Invocation invocation) {
            return (request, context) -> {
                params.requireNoParams(request);
                return invocation.invoke(request).thenApply(mapper::valueToTree);
            };
        }

        private MethodHandler oneParam(String name, Invocation invocation) {
            return (request, context) -> {
                params.requiredNode(request, name);
                return invocation.invoke(request).thenApply(mapper::valueToTree);
            };
        }

        private <T> List<T> readList(JsonRpcRequest request, String name, Class<T> type) {
            return params.requiredList(request, name, type);
        }
    }

    private interface RouteHandlerFactory {
        MethodHandler bind(RouteContext context);
    }

    private interface Invocation {
        CompletableFuture<?> invoke(JsonRpcRequest request);
    }
}
