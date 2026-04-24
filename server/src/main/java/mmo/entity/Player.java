package mmo.entity;

public class Player {
    private final String sessionId;
    private int tileX;
    private int tileY;

    public Player(String sessionId, int tileX, int tileY) {
        this.sessionId = sessionId;
        this.tileX = tileX;
        this.tileY = tileY;
    }
    public String getSessionId() {
        return sessionId;
    }
    public int getTileX() {
        return tileX;
    }
    public int getTileY() {
        return tileY;
    }
    public void setPosition(int x, int y) {
        this.tileX = x;
        this.tileY = y;
    }
}
