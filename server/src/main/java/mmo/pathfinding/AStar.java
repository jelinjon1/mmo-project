package mmo.pathfinding;

import mmo.world.GameMap;
import mmo.world.Tile;
import java.util.*;

public class AStar {
    int[][] moves = {{-1, 0}, {0, -1}, {1, 0},  {0, 1} };

    public List<Tile> getPath(int startX, int startY, int endX, int endY, GameMap map) {
        Map<Tile, Tile> cameFrom = new HashMap<>();
        Map<Tile, Integer> gScore = new HashMap<>();

        Tile start = map.getTile(startX, startY);
        Tile goal = map.getTile(endX, endY);

        if (start == null || goal == null || !goal.isWalkable()) {
            return List.of();
        }

        PriorityQueue<Tile> open = new PriorityQueue<>(
                Comparator.comparingInt(t -> gScore.getOrDefault(t, Integer.MAX_VALUE) + heuristic(t, goal))
        );

        gScore.put(start, 0);
        open.add(start);

        while (!open.isEmpty()) {
            Tile current = open.poll();

            if (current == goal) {
                return reconstructPath(cameFrom, current);
            }

            for (int[] move : moves) {
                int nextX = current.getX() + move[0];
                int nextY = current.getY() + move[1];

                Tile neighbour = map.getTile(nextX, nextY);

                if (neighbour == null || !neighbour.isWalkable()){
                    continue;
                }
                // TODO replace +1 with tile crossing cost
                int tentativeG = gScore.getOrDefault(current, Integer.MAX_VALUE) + 1;

                if (tentativeG < gScore.getOrDefault(neighbour, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbour, current);
                    gScore.put(neighbour, tentativeG);
                    open.add(neighbour);
                }
            }
        }
        return List.of();
    }

    private int heuristic(Tile a, Tile b) {
        // Manhattan distance
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    private List<Tile> reconstructPath(Map<Tile, Tile> cameFrom, Tile current) {
        LinkedList<Tile> path = new LinkedList<>();
        while (cameFrom.containsKey(current)) {
            path.addFirst(current);
            current = cameFrom.get(current);
        }
        return path;
    }
}
