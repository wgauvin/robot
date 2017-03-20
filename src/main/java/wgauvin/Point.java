package wgauvin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * Created by will on 13/02/17.
 */
public class Point {

    private static final Logger LOGGER = LoggerFactory.getLogger(Point.class);
    private static final int TABLE_SIZE = 5;
    private static final int MAX_TABLE_INDEX = TABLE_SIZE - 1; // since we're using offset from 0.

    private final int x;
    private final int y;

    public Point(int x, int y) {
        Assert.isTrue(x >= 0 && x <= MAX_TABLE_INDEX, "x must be between 0 and " + MAX_TABLE_INDEX);
        Assert.isTrue(y >= 0 && y <= MAX_TABLE_INDEX, "y must be between 0 and " + MAX_TABLE_INDEX);
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point move(Direction direction) {
        int newX = applyBoundary(x, direction, direction.getDeltaX());
        int newY = applyBoundary(y, direction, direction.getDeltaY());

        return new Point(newX, newY);
    }

    private int applyBoundary(int value, Direction direction, int delta) {
        if (delta == 0) {
            return value;
        }
        int newValue = value + delta;
        if (newValue < 0) {
            // underflow. Stay put
            LOGGER.debug("Moving {} will cause underflow. Staying put.", direction);
            return value;
        }
        if (newValue == TABLE_SIZE) {
            // overflow.  Stay put
            LOGGER.debug("Moving {} will cause overflow. Staying put.", direction);
            return value;
        }
        return newValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Point point = (Point) other;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
