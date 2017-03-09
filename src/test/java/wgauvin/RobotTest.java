package wgauvin;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static wgauvin.Direction.EAST;
import static wgauvin.Direction.NORTH;
import static wgauvin.Direction.SOUTH;
import static wgauvin.Direction.WEST;

/**
 * Created by will on 14/02/17.
 */
class RobotTest {

    @Test
    void respondToPlaceActionShouldSetRobotToCorrectPlaceFacingNorth() {
        Robot robot = Robot.INITIAL_STATE;

        Robot result = robot.respond("PLACE 1,3,NORTH");

        assertAll(
            () -> assertThat(result.getDirection(), equalTo(NORTH)),
            () -> assertThat(result.getPoint(), equalTo(new Point(1, 3)))
        );
    }

    @Test
    void respondToPlaceActionShouldSetRobotToCorrectPlaceFacingEast() {
        Robot robot = Robot.INITIAL_STATE;

        Robot result = robot.respond("PLACE 2,2,EAST");

        assertAll(
            () -> assertThat(result.getDirection(), equalTo(EAST)),
            () -> assertThat(result.getPoint(), equalTo(new Point(2, 2)))
        );
    }

    @Test
    void respondToPlaceActionShouldSetRobotToCorrectPlaceFacingSouth() {
        Robot robot = Robot.INITIAL_STATE;

        Robot result = robot.respond("PLACE 3,1,SOUTH");

        assertAll(
            () -> assertThat(result.getDirection(), equalTo(SOUTH)),
            () -> assertThat(result.getPoint(), equalTo(new Point(3, 1)))
        );
    }

    @Test
    void respondToPlaceActionShouldSetRobotToCorrectPlaceFacingWest() {
        Robot robot = Robot.INITIAL_STATE;

        Robot result = robot.respond("PLACE 4,0,WEST");

        assertAll(
            () -> assertThat(result.getDirection(), equalTo(WEST)),
            () -> assertThat(result.getPoint(), equalTo(new Point(4, 0)))
        );
    }

    @Test
    void respondToNonPlaceActionForInitialStateRobotShouldIgnoreActionAndReturnInitialState() {
        Robot robot = Robot.INITIAL_STATE;

        Robot result = robot.respond("LEFT");

        assertAll(
            () -> assertThat(result, is(Robot.INITIAL_STATE)),
            // asserting we didn't change the initial state
            () -> assertThat(result.getPoint(), is(nullValue())),
            () -> assertThat(result.getDirection(), is(nullValue()))
        );
    }

    @Test
    void respondToLeftShouldTurnRobotLeft() {
        Robot robot = new Robot(new Point(1, 2), SOUTH);

        Robot result = robot.respond("LEFT");

        assertAll(
            () -> assertThat(result.getDirection(), equalTo(EAST)),
            () -> assertThat(result.getPoint(), equalTo(new Point(1, 2)))
        );

    }

    @Test
    void respondToLeftShouldTurnRobotRight() {
        Robot robot = new Robot(new Point(3, 4), EAST);

        Robot result = robot.respond("RIGHT");

        assertAll(
            () -> assertThat(result.getDirection(), equalTo(SOUTH)),
            () -> assertThat(result.getPoint(), equalTo(new Point(3, 4)))
        );

    }

    @Test
    void respondToMoveShouldMoveRobot() {
        Robot robot = new Robot(new Point(2, 2), WEST);

        Robot result = robot.respond("MOVE");

        assertAll(
            () -> assertThat(result.getDirection(), equalTo(WEST)),
            () -> assertThat(result.getPoint(), equalTo(new Point(1, 2)))
        );

    }

    @Test
    void respondToReportShouldCallReportOnRobot() {
        // since report doesn't change state and doesn't interact with anything other
        // than System.out we need to use a Mockito spy to see that Robot.report() is called.
        Robot robot = spy(new Robot(new Point(1, 1), EAST));

        Robot result = robot.respond("REPORT");

        verify(robot).report();
        // shouldn't change anything about the robot
        assertThat(result, is(robot));
    }

    @Test
    void respondToPlaceShouldWorkOnNonInitialStateRobot() {
        Robot robot = new Robot(new Point(2, 1), WEST);

        Robot result = robot.respond("PLACE 1,2,NORTH");

        assertAll(
            () -> assertThat(result.getDirection(), equalTo(NORTH)),
            () -> assertThat(result.getPoint(), equalTo(new Point(1, 2)))
        );

    }

    @Test
    void respondShouldIgnoreInvalidActions() {
        Robot robot = new Robot(new Point(2, 1), WEST);

        Robot result = robot.respond("this is invalid");

        assertThat(result, is(robot));
    }

}
