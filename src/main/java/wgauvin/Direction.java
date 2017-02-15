package wgauvin;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by will on 13/02/17.
 */
public enum Direction {
    NORTH(0, 1),
    EAST(1, 0),
    SOUTH(0, -1),
    WEST(-1, 0);

    private static final Map<Direction, Direction> LEFT_MAP = new EnumMap<>(Direction.class);
    private static final Map<Direction, Direction> RIGHT_MAP = new EnumMap<>(Direction.class);

    static {
        LEFT_MAP.put(NORTH, WEST);
        LEFT_MAP.put(EAST, NORTH);
        LEFT_MAP.put(SOUTH, EAST);
        LEFT_MAP.put(WEST, SOUTH);

        RIGHT_MAP.put(NORTH, EAST);
        RIGHT_MAP.put(EAST, SOUTH);
        RIGHT_MAP.put(SOUTH, WEST);
        RIGHT_MAP.put(WEST, NORTH);
    }

    private final int deltaX;
    private final int deltaY;

    Direction(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public Direction left() {
        return LEFT_MAP.get(this);
    }

    public Direction right() {
        return RIGHT_MAP.get(this);
    }

}
