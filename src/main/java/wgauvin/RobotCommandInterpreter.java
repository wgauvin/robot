package wgauvin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by will on 9/03/17.
 */
public class RobotCommandInterpreter {

    private static final Pattern PLACE_REGEX = Pattern.compile("PLACE ([0-4]),([0-4]),(NORTH|EAST|SOUTH|WEST)");
    private static final int PLACE_GROUP_X = 1;
    private static final int PLACE_GROUP_Y = 2;
    private static final int PLACE_GROUP_DIRECTION = 3;

    public RobotCommand interpret(String command) {
        Matcher placeMatcher = PLACE_REGEX.matcher(command);
        if (placeMatcher.matches()) {
            return getPlaceRobotCommand(placeMatcher);
        }

        return RobotCommandEnum.getRobotCommand(command)
            .orElse(RobotCommand.ignore());
    }

    private RobotCommand getPlaceRobotCommand(Matcher matcher) {
        int x = Integer.parseInt(matcher.group(PLACE_GROUP_X));
        int y = Integer.parseInt(matcher.group(PLACE_GROUP_Y));
        Direction direction = Direction.valueOf(matcher.group(PLACE_GROUP_DIRECTION));
        return (robot) -> robot.place(x, y, direction);
    }

}
