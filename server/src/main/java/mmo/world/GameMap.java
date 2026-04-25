package mmo.world;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameMap {
    private final Tile[][] grid;
    private final int width;
    private final int height;

    public GameMap(String tmjPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(tmjPath));

        this.width = root.get("width").asInt();
        this.height = root.get("height").asInt();
        this.grid = new Tile[height][width];

        JsonNode tileset = root.get("tilesets").get(0);
        int firstGid = tileset.get("firstgid").asInt();
        Map<Integer, TileMeta> metaMap = new HashMap<>();

        JsonNode tilesNode = tileset.get("tiles");
        if (tilesNode != null) {
            for (JsonNode tileNode : tilesNode) {
                int localId = tileNode.get("id").asInt();
                String type = tileNode.has("type") ? tileNode.get("type").asText() : "unknown";
                boolean walkable = false;

                JsonNode props = tileNode.get("properties");
                if (props != null) {
                    for (JsonNode prop : props) {
                        if (prop.get("name").asText().equals("walkable")) {
                            walkable = prop.get("value").asBoolean();
                        }
                    }
                }
                metaMap.put(localId, new TileMeta(type, walkable));
            }
        }

        JsonNode layer = root.get("layers").get(0);
        JsonNode data = layer.get("data");

        for (int i = 0; i < data.size(); i++) {
            int gid = data.get(i).asInt();
            int tileX = i % width;
            int tileY = i / width;

            if (gid == 0) {
                grid[tileY][tileX] = new Tile(tileX, tileY, "empty", false);
            } else {
                int localId = gid - firstGid;
                TileMeta meta = metaMap.getOrDefault(localId, new TileMeta("unknown", false));
                grid[tileY][tileX] = new Tile(tileX, tileY, meta.type, meta.walkable);
            }
        }
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return null;
        }
        return grid[y][x];
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    private record TileMeta(String type, boolean walkable) {}

}
