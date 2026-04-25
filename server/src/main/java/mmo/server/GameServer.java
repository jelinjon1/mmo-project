package mmo.server;

import mmo.entity.Player;
import mmo.message.IncomingMessage;
import mmo.message.OutgoingMessage;
import mmo.pathfinding.AStar;
import mmo.world.GameMap;
import mmo.world.Tile;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import tools.jackson.databind.ObjectMapper;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameServer extends WebSocketServer {
    private final GameMap map;
    private final AStar aStar;
    private final ObjectMapper mapper;
    private final Map<WebSocket, Player> players;

    public GameServer(int port, GameMap gameMap) {
        super(new InetSocketAddress(port));
        this.map = gameMap;
        this.aStar = new AStar();
        this.mapper = new ObjectMapper();
        this.players = new HashMap<>();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        Player player = new Player(webSocket.getRemoteSocketAddress().toString(), 3, 3);
        players.put(webSocket, player);
        System.out.println("Player connected " + player.getSessionId());
    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
        Player player = players.remove(webSocket);
        if (player != null) {
            System.out.println("Player disconnected: " + player.getSessionId());
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        try {
            IncomingMessage msg = mapper.readValue(message, IncomingMessage.class);
            Player player = players.get(webSocket);

            if ("move".equals(msg.type)) {
                List<Tile> path = aStar.getPath(
                        //player.getTileX(), player.getTileY(),
                        msg.fromX, msg.fromY,
                        msg.x, msg.y,
                        map
                );

                if (!path.isEmpty()) {
                    Tile destination = path.getLast();
                    player.setPosition(destination.getX(), destination.getY());

                    List<OutgoingMessage.TileCoord> coords = path.stream()
                            .map(t -> new OutgoingMessage.TileCoord(t.getX(), t.getY()))
                            .toList();

                    String response = mapper.writeValueAsString(new OutgoingMessage("path", coords));
                    webSocket.send(response);
                }
            }

        } catch (Exception e) {
            System.err.println("Error handling message: " + e.getMessage());
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.err.println("WebSocket error: " + e.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("Server started on port " + getPort());
    }
}
