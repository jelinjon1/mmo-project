package mmo;

import mmo.server.GameServer;
import mmo.world.GameMap;

public class Main {
    public static void main(String[] args) throws Exception {
        GameMap map = new GameMap("../maps/test.tmj");
        GameServer server = new GameServer(8080, map);
        server.start();
    }
}
