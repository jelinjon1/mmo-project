package mmo.world;

public class Tile {
    private final int x;
    private final int y;
    private final String type;
    private final boolean walkable;

    public Tile(int x, int y, String type, boolean walkable) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.walkable = walkable;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public String getType() {
        return type;
    }
    public boolean isWalkable() {
        return walkable;
    }
}
