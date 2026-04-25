package mmo.message;

import java.util.List;

public class OutgoingMessage {
    public String type;
    public List<TileCoord> tiles;

    public OutgoingMessage(String type, List<TileCoord> tiles) {
        this.type = type;
        this.tiles = tiles;
    }

    public record TileCoord(int x, int y) {}
}
