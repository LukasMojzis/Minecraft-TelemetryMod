package cz.lukasmojzis.telemetrymod;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class extends WebSocketClient to provide the necessary functionality for the Telemetry Mod.
 * It maintains a connection to the server and handles message sending and receiving.
 */
public class TelemetryWebSocketClient extends WebSocketClient {

    /**
     * The delay in milliseconds before attempting a reconnection after a connection failure.
     */
    private static final int RECONNECT_DELAY_MS = 5000;  // 5 seconds

    /**
     * The URI of the server to connect to.
     */
    private final URI serverUri;

    /**
     * Atomic boolean to track if the client is currently connecting to the server.
     */
    private final AtomicBoolean isConnecting = new AtomicBoolean();

    /**
     * The constructor initializes the WebSocket client with a server URI.
     *
     * @param serverUri The URI of the server to connect to.
     */
    public TelemetryWebSocketClient(URI serverUri) {
        super(serverUri, new Draft_6455(), new HashMap<>(), ModConfig.websocket.connectionTimeout);
        this.isConnecting.set(false);
        this.serverUri = serverUri;
        startConnectionWatcher();
    }

    /**
     * This method is called when a connection is opened to the WebSocket server.
     *
     * @param serverHandshake The handshake data received from the server.
     */
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        TelemetryMod.logger.info(String.format("Opened connection to telemetry server at: %s", this.serverUri));
        this.isConnecting.set(false);
    }

    /**
     * This method is called when a message is received from the WebSocket server.
     *
     * @param message The received message.
     */
    @Override
    public void onMessage(String message) {
        // Handle any messages from the server here if needed
    }

    /**
     * This method is called when the connection to the WebSocket server is closed.
     *
     * @param code   The exit code.
     * @param reason The reason for closing.
     * @param remote True if the closure was initiated by the server, false otherwise.
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        TelemetryMod.logger.info(String.format("Closed connection to telemetry server at: %s, Code: %d, Reason: %s, Remote: %s", this.serverUri, code, reason, remote));
        this.isConnecting.set(false);
    }

    /**
     * This method is called when an error occurs during WebSocket operations.
     *
     * @param e The exception that was thrown.
     */
    @Override
    public void onError(Exception e) {
        TelemetryMod.logger.error(String.format("Error in telemetry connection to server at: %s", this.serverUri), e);
        this.isConnecting.set(false);
    }

    /**
     * Attempt to reconnect to the WebSocket server.
     * This method blocks until the reconnection is successful or an error occurs.
     */
    private void doReconnect() {
        this.isConnecting.set(true);
        try {
            TelemetryWebSocketClient.this.reconnectBlocking();
        } catch (InterruptedException | IllegalStateException e) {
            TelemetryMod.logger.error("Failed to reconnect", e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt(); // Best practice for handling InterruptedException
            }
        }
    }

    /**
     * Start a thread to watch the connection status and attempt reconnection if necessary.
     */
    private void startConnectionWatcher() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            try {
                if (!isOpen() && !isConnecting.get()) {
                    doReconnect();
                }
            } catch (Exception e) {
                TelemetryMod.logger.error("Connection watcher encountered an error", e);
            }
        }, 0, RECONNECT_DELAY_MS, TimeUnit.MILLISECONDS);
    }
}
