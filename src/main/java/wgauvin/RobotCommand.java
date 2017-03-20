package wgauvin;

import java.util.function.UnaryOperator;

/**
 * Created by will on 10/03/17.
 */
@FunctionalInterface
public interface RobotCommand extends UnaryOperator<Robot> {

    static RobotCommand ignore() {
        return robot -> robot;
    }

}
