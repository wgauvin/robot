package wgauvin;

import java.util.Optional;

/**
 * Created by will on 9/03/17.
 */
public enum RobotCommandEnum implements RobotCommand {
    LEFT(Robot::left),
    RIGHT(Robot::right),
    MOVE(Robot::move),
    REPORT(Robot::report);

    private RobotCommand command;

    RobotCommandEnum(RobotCommand command) {
        this.command = command;
    }

    @Override
    public Robot apply(Robot robot) {
        return command.apply(robot);
    }

    public static Optional<RobotCommand> getRobotCommand(String command) {
        try {
            return Optional.of(RobotCommandEnum.valueOf(command));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
