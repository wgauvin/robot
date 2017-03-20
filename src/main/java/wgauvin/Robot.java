package wgauvin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Created by will on 13/02/17.
 */
public class Robot {

    public static final Robot INITIAL_STATE = new Robot();

    private static final Logger LOGGER = LoggerFactory.getLogger(Robot.class);
    private static final Logger REPORT_LOGGER = LoggerFactory.getLogger("wgauvin.Robot.REPORT_LOGGER");

    private final Point point;
    private final Direction direction;
    private final RobotCommandInterpreter commandInterpreter;

    private Robot() {
        point = null;
        direction = null;
        commandInterpreter = new RobotCommandInterpreter();
    }

    private Robot(int x, int y, Direction direction, RobotCommandInterpreter commandInterpreter) {
        this.point = new Point(x, y);
        this.direction = direction;
        this.commandInterpreter = commandInterpreter;
    }

    public Robot(Point point, Direction direction, RobotCommandInterpreter commandInterpreter) {
        this.point = point;
        this.direction = direction;
        this.commandInterpreter = commandInterpreter;
    }

    public Point getPoint() {
        return point;
    }

    public Direction getDirection() {
        return direction;
    }

    public Robot respond(String action) {
        LOGGER.debug("Responding to {}", action);
        try {
            return commandInterpreter.interpret(action).apply(this);
        } catch (IllegalStateException | IllegalArgumentException e) {
            LOGGER.error("Invalid action '{}'. Robot ignore command.", action, e);
            return this;
        }
    }

    public Robot place(int x, int y, Direction direction) {
        LOGGER.debug("Placing robot at {},{},{}.", x, y, direction);
        return new Robot(x, y, direction, commandInterpreter);
    }

    public Robot report() {
        assertRobotHasBeenInitialised("report");
        REPORT_LOGGER.info("Output: {},{},{}", point.getX(), point.getY(), direction);
        return this;
    }

    public Robot move() {
        LOGGER.debug("Moving robot forward");
        assertRobotHasBeenInitialised("move");
        return new Robot(point.move(direction), direction, commandInterpreter);
    }

    public Robot left() {
        LOGGER.debug("Turning robot left");
        assertRobotHasBeenInitialised("left");
        return new Robot(point, direction.left(), commandInterpreter);
    }

    public Robot right() {
        LOGGER.debug("Turning robot right");
        assertRobotHasBeenInitialised("right");
        return new Robot(point, direction.right(), commandInterpreter);
    }

    private void assertRobotHasBeenInitialised(String command) {
        if (this == INITIAL_STATE) {
            String msg = String.format("Robot hasn't been initialised, can't respond to '%s' command", command);
            throw new IllegalStateException(msg);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Robot robot = (Robot) other;
        return Objects.equals(point, robot.point) && direction == robot.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, direction);
    }

}
