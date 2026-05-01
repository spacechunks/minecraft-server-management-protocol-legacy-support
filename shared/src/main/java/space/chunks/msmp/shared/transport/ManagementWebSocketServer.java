package space.chunks.msmp.shared.transport;

import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;
import space.chunks.msmp.shared.config.ManagementProtocolSettings;
import space.chunks.msmp.shared.PlatformBridge;
import space.chunks.msmp.shared.jsonrpc.JsonRpcFrameHandler;
import space.chunks.msmp.shared.api.RequestContext;
import space.chunks.msmp.shared.security.AccessDecision;
import space.chunks.msmp.shared.security.AccessPolicy;
import space.chunks.msmp.shared.transport.TlsContextFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import space.chunks.msmp.shared.jsonrpc.JsonRpcConstants;
import space.chunks.msmp.shared.json.ObjectMapperFactory;
import space.chunks.msmp.shared.discovery.NotificationDescriptor;

public final class ManagementWebSocketServer extends WebSocketServer {
    private static final int UNAUTHORIZED_CLOSE_CODE = 1008;

    private final PlatformBridge platform;
    private final ManagementProtocolSettings settings;
    private final AccessPolicy accessPolicy;
    private final JsonRpcFrameHandler frameHandler;
    private final ObjectMapper mapper = ObjectMapperFactory.create();

    public ManagementWebSocketServer(
        PlatformBridge platform,
        ManagementProtocolSettings settings,
        JsonRpcFrameHandler frameHandler
    ) {
        super(new InetSocketAddress(settings.getHost(), settings.getPort()));
        this.platform = Objects.requireNonNull(platform, "platform");
        this.settings = Objects.requireNonNull(settings, "settings");
        this.accessPolicy = new AccessPolicy(settings);
        this.frameHandler = Objects.requireNonNull(frameHandler, "frameHandler");
        setDaemon(true);
        if (settings.isTlsEnabled()) {
            setWebSocketFactory(new DefaultSSLWebSocketServerFactory(
                TlsContextFactory.create(settings.getTlsKeystore(), settings.getTlsKeystorePassword())
            ));
        }
    }

    @Override
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(
        WebSocket connection,
        Draft draft,
        ClientHandshake request
    ) throws InvalidDataException {
        RequestContext context = requestContext(connection, request);
        AccessDecision decision = accessPolicy.authorize(context);
        if (!decision.isAllowed()) {
            throw new InvalidDataException(UNAUTHORIZED_CLOSE_CODE, decision.reason());
        }

        ServerHandshakeBuilder response = super.onWebsocketHandshakeReceivedAsServer(connection, draft, request);
        String protocol = request.getFieldValue("Sec-WebSocket-Protocol");
        if (protocol != null && protocol.trim().startsWith(AccessPolicy.WEB_SOCKET_PROTOCOL_PREFIX)) {
            response.put("Sec-WebSocket-Protocol", AccessPolicy.WEB_SOCKET_PROTOCOL);
        }
        return response;
    }

    @Override
    public void onOpen(WebSocket connection, ClientHandshake handshake) {
        platform.info("Management connection opened from " + connection.getRemoteSocketAddress() + ".");
    }

    @Override
    public void onClose(WebSocket connection, int code, String reason, boolean remote) {
        platform.info("Management connection closed from " + connection.getRemoteSocketAddress() + ".");
    }

    @Override
    public void onMessage(WebSocket connection, String message) {
        frameHandler.handle(message, requestContext(connection, null)).thenAccept(response -> {
            if (response != null && connection.isOpen()) {
                connection.send(response);
            }
        }).exceptionally(error -> {
            platform.warn("Failed to handle management JSON-RPC frame: " + error.getMessage());
            return null;
        });
    }

    @Override
    public void onError(WebSocket connection, Exception exception) {
        platform.warn("Management WebSocket error: " + exception.getMessage());
    }

    @Override
    public void onStart() {
        platform.info("Management WebSocket server listening on " + settings.getHost() + ":" + getPort() + ".");
    }

    public CompletableFuture<Void> publish(NotificationDescriptor descriptor, JsonNode params) {
        String frame;
        try {
            frame = mapper.writeValueAsString(notificationFrame(descriptor, params));
        } catch (JsonProcessingException exception) {
            CompletableFuture<Void> failed = new CompletableFuture<Void>();
            failed.completeExceptionally(exception);
            return failed;
        }

        for (WebSocket connection : getConnections()) {
            if (connection.isOpen()) {
                try {
                    connection.send(frame);
                } catch (RuntimeException exception) {
                    platform.warn("Failed to send management notification to "
                        + connection.getRemoteSocketAddress() + ": " + exception.getMessage());
                    connection.closeConnection(1011, "Failed to send management notification");
                }
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    private ObjectNode notificationFrame(NotificationDescriptor descriptor, JsonNode params) {
        ObjectNode frame = mapper.createObjectNode();
        frame.put("jsonrpc", JsonRpcConstants.VERSION);
        frame.put("method", descriptor.name().value());
        if (params != null && !params.isNull()) {
            ArrayNode array = mapper.createArrayNode();
            array.add(params);
            frame.set("params", array);
        }
        return frame;
    }

    private static RequestContext requestContext(WebSocket connection, ClientHandshake handshake) {
        Map<String, String> headers = new LinkedHashMap<String, String>();
        String origin = null;
        if (handshake != null) {
            for (java.util.Iterator<String> fields = handshake.iterateHttpFields(); fields.hasNext();) {
                String name = fields.next();
                String value = handshake.getFieldValue(name);
                headers.put(name, value);
                if ("origin".equalsIgnoreCase(name)) {
                    origin = value;
                }
            }
        }
        return new RequestContext(String.valueOf(connection.getRemoteSocketAddress()), origin, headers);
    }
}
