package wgauvin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by will on 13/02/17.
 */
public class Robot {

    public static final Robot INITIAL_STATE = new Robot();

    private static final Logger LOGGER = LoggerFactory.getLogger(Robot.class);

    private static final Pattern PLACE_REGEX = Pattern.compile("PLACE ([0-4]),([0-4]),(NORTH|EAST|SOUTH|WEST)");
    private static final int PLACE_GROUP_X = 1;
    private static final int PLACE_GROUP_Y = 2;
    private static final int PLACE_GROUP_DIRECTION = 3;

    private final Point point;
    private final Direction direction;

    private Robot() {
        point = null;
        direction = null;
    }

    private Robot(int x, int y, Direction direction) {
        this.point = new Point(x, y);
        this.direction = direction;
    }

    public Robot(Point point, Direction direction) {
        this.point = point;
        this.direction = direction;
    }

    public Point getPoint() {
        return point;
    }

    public Direction getDirection() {
        return direction;
    }

    public Robot respond(String action) {
        LOGGER.debug("Responding to {}", action);
        Matcher placeMatcher = PLACE_REGEX.matcher(action);
        if (placeMatcher.matches()) {
            return place(placeMatcher);
        }
        // only respond to valid place actions, else return robot to
        if (this == INITIAL_STATE) {
            return this;
        }
        return RobotAction.getAction(action)
                .map(a -> a.apply(this))
                .orElse(this);
    }

    // action methods
    private Robot place(Matcher matcher) {
        int x = Integer.parseInt(matcher.group(PLACE_GROUP_X));
        int y = Integer.parseInt(matcher.group(PLACE_GROUP_Y));
        Direction direction = Direction.valueOf(matcher.group(PLACE_GROUP_DIRECTION));
        LOGGER.debug("Placing robot at {},{},{}.", x, y, direction);
        return new Robot(x, y, direction);
    }

    Robot report() {
        System.out.format("Output: %d,%d,%s%n", point.getX(), point.getY(), direction);
        return this;
    }

    private Robot move() {
        LOGGER.debug("Moving robot forward");
        return new Robot(point.move(direction), direction);
    }

    private Robot left() {
        LOGGER.debug("Turning robot left");
        return new Robot(point, direction.left());
    }

    private Robot right() {
        LOGGER.debug("Turning robot left");
        return new Robot(point, direction.right());
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

    private enum RobotAction implements Function<Robot, Robot> {
        LEFT(Robot::left),
        RIGHT(Robot::right),
        MOVE(Robot::move),
        REPORT(Robot::report);

        private Function<Robot, Robot> action;

        RobotAction(Function<Robot, Robot> action) {
            this.action = action;
        }

        @Override
        public Robot apply(Robot robot) {
            return action.apply(robot);
        }

        public static Optional<RobotAction> getAction(String action) {
            try {
                return Optional.of(RobotAction.valueOf(action));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }
    }
}
