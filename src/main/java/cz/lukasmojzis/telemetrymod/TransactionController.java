package cz.lukasmojzis.telemetrymod;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.item.ItemStack;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The TransactionController class manages a queue of transaction data for the Telemetry Mod.
 * The transactions are state updates organized by game tick and player, which can be sent via the WebSocket Client.
 */
public class TransactionController {

    private static final int MAX_SENT_COUNT_PER_TICK = 2;
    private static final int DELAY_TICKS_AFTER_FAILURE = 100;
    private static final int MAX_TRANSACTION_COUNT_BEFORE_WARNING = 1000;
    private static final int WARNING_TRANSACTION_COUNT_FREQUENCY = 100;
    private static final ConcurrentSkipListMap<Long, Map<String, Map<String, Map<PlayerProperty, Object>>>> transactionBundles = new ConcurrentSkipListMap<>();
    private static final AtomicInteger transactionCount = new AtomicInteger(0);
    private static Deque<Map.Entry<Long, Map<String, Map<String, Map<PlayerProperty, Object>>>>> transactionQueue = new ConcurrentLinkedDeque<>();
    private static int delayTicks = 0;

    /**
     * Called every client tick, this method handles the sending of transaction data to the server.
     * It creates a JSON object from the transaction data and sends it to the server via the WebSocket client.
     * If the WebSocket client is not open or an exception occurs during sending, it adds a delay and pushes
     * the transaction back to the front of the queue.
     */
    public static void onClientTick() {
        if (delayTicks > 0) {
            delayTicks--;
        } else {
            processTransactions();
        }
    }

    /**
     * Handles the processing of transactions. It interacts with the WebSocket client to send
     * transactions from the queue. It also handles exceptions related to the WebSocket connection
     * and unexpected errors, triggering appropriate logging and user messaging as required.
     * It monitors the number of sent transactions and stops processing when the maximum limit
     * per tick is reached or when the queue is empty.
     */
    private static void processTransactions() {
        TelemetryWebSocketClient telemetryClient = TelemetryMod.telemetryClient;

        int sentCount = 0;
        while (sentCount < MAX_SENT_COUNT_PER_TICK && !transactionQueue.isEmpty()) {
            Map.Entry<Long, Map<String, Map<String, Map<PlayerProperty, Object>>>> entry = transactionQueue.pollFirst();

            try {
                if (!telemetryClient.isOpen()) {
                    handleClientNotOpen(entry);
                    break;
                }
                sendTransactionBundle(entry, telemetryClient);
                sentCount++;
            } catch (WebsocketNotConnectedException e) {
                handleTransactionSendFailure(entry, e);

                break;
            } catch (Exception e) {
                TelemetryMod.logger.error("Unexpected error occurred", e);
                TelemetryMod.sendMessage(String.format("Unexpected error occurred: %s", e), null);
                TelemetryMod.sendMessage("Please report the above error to the developer!", null);
                TelemetryMod.sendMessage("Disabling TelemetryMod Websocket output.", null);
                TelemetryMod.sendMessage("You can re-enable it in Mod Config > TelemetryMod > Websocket.", null);
                ModConfig.websocket.enabled = false;
                break;
            }
        }

        checkAndReportQueueSize();
    }

    /**
     * Private helper method to handle the scenario where the WebSocket client is not open.
     * It adds a delay and pushes the transaction back to the front of the queue.
     *
     * @param entry The transaction data that failed to be sent.
     */
    private static void handleClientNotOpen(Map.Entry<Long, Map<String, Map<String, Map<PlayerProperty, Object>>>> entry) {
        delayTicks += DELAY_TICKS_AFTER_FAILURE;
        transactionQueue.addFirst(entry);
        TelemetryMod.logger.error("Failed to send transaction bundle: The Websocket client is not open!");
    }

    /**
     * Private helper method to send transaction bundle to the server.
     *
     * @param entry           The transaction data to be sent.
     * @param telemetryClient The WebSocket client to send the transaction data.
     */
    private static void sendTransactionBundle(Map.Entry<Long, Map<String, Map<String, Map<PlayerProperty, Object>>>> entry, TelemetryWebSocketClient telemetryClient) {
        String jsonPayload = createJsonPayload(entry);
        telemetryClient.send(jsonPayload);
        transactionCount.addAndGet(-calculateTotalTransactions(entry.getValue()));
    }

    /**
     * Private helper method to handle a WebsocketNotConnectedException.
     *
     * @param entry The transaction data that failed to be sent.
     * @param e     The exception encountered during the send attempt.
     */
    private static void handleTransactionSendFailure(Map.Entry<Long, Map<String, Map<String, Map<PlayerProperty, Object>>>> entry, WebsocketNotConnectedException e) {
        delayTicks += DELAY_TICKS_AFTER_FAILURE;
        transactionQueue.addFirst(entry);
        TelemetryMod.logger.error("Failed to send transaction bundle", e);
    }

    /**
     * Private helper method to periodically check and report on the queue size.
     */
    private static void checkAndReportQueueSize() {
        if (transactionCount.get() > MAX_TRANSACTION_COUNT_BEFORE_WARNING && TelemetryMod.calendar.getTotalWorldTicks() % WARNING_TRANSACTION_COUNT_FREQUENCY == 0) {
            TelemetryMod.sendMessage(String.format("There are more than %d transaction bundles waiting to be sent.", transactionCount.get()), null);
        }
    }

    /**
     * Private helper method to create a JSON payload from the transaction data.
     *
     * @param entry The transaction data to be sent.
     * @return A JSON string representing the transaction data.
     */
    private static String createJsonPayload(Map.Entry<Long, Map<String, Map<String, Map<PlayerProperty, Object>>>> entry) {
        Long gameTick = entry.getKey();
        Map<String, Map<String, Map<PlayerProperty, Object>>> gameTickTransactionBundles = entry.getValue();

        JsonObject payload = new JsonObject();

        for (Map.Entry<String, Map<String, Map<PlayerProperty, Object>>> transactionBundleEntry : gameTickTransactionBundles.entrySet()) {
            for (Map.Entry<String, Map<PlayerProperty, Object>> transactionEntry : transactionBundleEntry.getValue().entrySet()) {
                JsonObject gameTickObject = new JsonObject();
                gameTickObject.add("tick", new JsonPrimitive(gameTick));
                JsonObject playerTransactionObject = new JsonObject();

                for (Map.Entry<PlayerProperty, Object> property : transactionEntry.getValue().entrySet()) {
                    Object propertyValue = property.getValue();

                    if (propertyValue instanceof Number) {
                        playerTransactionObject.add(property.getKey().toString(), new JsonPrimitive((Number) propertyValue));
                    } else if (propertyValue instanceof Boolean) {
                        playerTransactionObject.add(property.getKey().toString(), new JsonPrimitive((Boolean) propertyValue));
                    } else if (propertyValue instanceof String) {
                        playerTransactionObject.add(property.getKey().toString(), new JsonPrimitive((String) propertyValue));
                    } else if (propertyValue instanceof UUID || propertyValue instanceof Collection || propertyValue instanceof ItemStack) {
                        playerTransactionObject.add(property.getKey().toString(), new JsonPrimitive(String.valueOf(propertyValue)));
                    } else
                        throw new Error(String.format("%s %s: %s cannot be converted to JSON", property.getValue().getClass(), property.getKey(), property.getValue()));
                }

                gameTickObject.add(transactionEntry.getKey(), playerTransactionObject);

                // Add the gameTickObject directly to the payload, no additional nesting
                JsonObject transactionPayload = new JsonObject();
                // Generate a unique ID
                String transactionId = UUID.randomUUID().toString();
                transactionPayload.add("id", new JsonPrimitive(transactionId));
                transactionPayload.add("data", gameTickObject);

                payload.add(transactionBundleEntry.getKey(), transactionPayload);
            }
        }

        return payload.toString();
    }

    /**
     * Private helper method to calculate the total number of transactions for a given game tick.
     *
     * @param gameTickTransactionBundles A map of transaction bundles for a specific game tick.
     * @return The total number of transactions for the given game tick.
     */
    private static int calculateTotalTransactions(Map<String, Map<String, Map<PlayerProperty, Object>>> gameTickTransactionBundles) {
        return gameTickTransactionBundles.values().stream().mapToInt(Map::size).sum();
    }

    /**
     * Returns the size of the transaction queue.
     *
     * @return The size of the transaction queue.
     */
    public static int size() {
        return transactionQueue.size();
    }


    /**
     * Adds a transaction to the appropriate bundle based on the game tick and the player name.
     * If the transaction queue already contains an entry for the given game tick, this method removes this entry,
     * adds the new transaction to the bundle, and adds the updated entry back to the queue.
     *
     * @param transaction The transaction to be added.
     */
    public static void addTransactionToBundle(ITransaction transaction) {
        String playerName = transaction.getPlayer();
        long gameTick = transaction.getGameTick();
        PlayerProperty property = transaction.getProperty();
        Object newValue = transaction.getNewValue();

        Map<String, Map<String, Map<PlayerProperty, Object>>> gameTickTransactions = transactionBundles.computeIfAbsent(gameTick, k -> new HashMap<>());

        Map<String, Map<PlayerProperty, Object>> playerTransactions = gameTickTransactions.computeIfAbsent(transaction.getClass().getSimpleName(), k -> new HashMap<>());

        Map<PlayerProperty, Object> playerTransaction = playerTransactions.get(playerName);

        if (playerTransaction == null) {
            playerTransaction = new HashMap<>();
            playerTransactions.put(playerName, playerTransaction);
            transactionCount.incrementAndGet();
        }

        playerTransaction.put(property, newValue);

        Map.Entry<Long, Map<String, Map<String, Map<PlayerProperty, Object>>>> newEntry = new AbstractMap.SimpleEntry<>(gameTick, gameTickTransactions);

        transactionQueue = new ArrayDeque<>(transactionQueue);  // Create a new deque before removing elements

        transactionQueue.removeIf(entry -> entry.getKey().equals(gameTick));

        transactionQueue.addLast(newEntry);
    }

}
