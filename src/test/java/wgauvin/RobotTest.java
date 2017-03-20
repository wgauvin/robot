package wgauvin;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static wgauvin.Direction.EAST;
import static wgauvin.Direction.NORTH;
import static wgauvin.Direction.SOUTH;
import static wgauvin.Direction.WEST;

/**
 * Created by will on 14/02/17.
 */
class RobotTest {

    @Mock
    private RobotCommandInterpreter commandInterpreter;

    @BeforeEach
    public void setup() {
        initMocks(this);
    }

    @Test
    void respondShouldCallInterpreter() {
        Robot robot = new Robot(new Point(1, 2), SOUTH, commandInterpreter);
        RobotCommand robotCommand = mock(RobotCommand.class);
        Robot mockResponse = mock(Robot.class);
        when(commandInterpreter.interpret("foo")).thenReturn(robotCommand);
        when(robotCommand.apply(robot)).thenReturn(mockResponse);

        Robot response = robot.respond("foo");

        assertAll(
            () -> assertThat(response, is(mockResponse)),
            () -> verify(commandInterpreter).interpret("foo"),
            () -> verify(robotCommand).apply(robot) // assert that the current robot is sent to the command
        );
    }

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
    void placeShouldReturnRobotInCorrectState() {
        Robot robot = Robot.INITIAL_STATE;

        Robot result = robot.place(1, 3, NORTH);

        assertAll(
            () -> assertThat(result, is(robotDescribedBy(1, 3, NORTH)))
        );
    }

    @Test
    void leftShouldThrowIllegalStateExceptionOnInitialStateRobot() {
        Robot robot = Robot.INITIAL_STATE;

        IllegalStateException result = assertThrows(IllegalStateException.class, () -> robot.left());
        assertThat(result.getMessage(), is("Robot hasn't been initialised, can't respond to 'left' command"));
    }

    @Test
    void respondToLeftButRobotInInitialStateShouldIgnoreCommand() {
        Robot result = Robot.INITIAL_STATE.respond("LEFT");

        assertAll(
            () -> assertThat(result, is(Robot.INITIAL_STATE)),
            // asserting we didn't change the initial state
            () -> assertThat(result.getPoint(), is(nullValue())),
            () -> assertThat(result.getDirection(), is(nullValue()))
        );
    }

    @Test
    void leftShouldTurnRobotLeft() {
        Robot robot = new Robot(new Point(1, 2), SOUTH, commandInterpreter);

        Robot result = robot.left();

        assertAll(
            () -> assertThat(result.getDirection(), equalTo(EAST)),
            () -> assertThat(result.getPoint(), equalTo(new Point(1, 2)))
        );
    }

    @Test
    void rightShouldThrowIllegalStateExceptionOnInitialStateRobot() {
        Robot robot = Robot.INITIAL_STATE;

        IllegalStateException result = assertThrows(IllegalStateException.class, () -> robot.right());
        assertThat(result.getMessage(), is("Robot hasn't been initialised, can't respond to 'right' command"));
    }

    @Test
    void respondToRightButRobotInInitialStateShouldIgnoreCommand() {
        Robot result = Robot.INITIAL_STATE.respond("RIGHT");

        assertAll(
            () -> assertThat(result, is(Robot.INITIAL_STATE)),
            // asserting we didn't change the initial state
            () -> assertThat(result.getPoint(), is(nullValue())),
            () -> assertThat(result.getDirection(), is(nullValue()))
        );
    }

    @Test
    void rightShouldTurnRobotRight() {
        Robot robot = new Robot(new Point(3, 4), EAST, commandInterpreter);

        Robot result = robot.right();

        assertAll(
            () -> assertThat(result.getDirection(), equalTo(SOUTH)),
            () -> assertThat(result.getPoint(), equalTo(new Point(3, 4)))
        );

    }

    @Test
    void moveShouldThrowIllegalStateExceptionOnInitialStateRobot() {
        Robot robot = Robot.INITIAL_STATE;

        IllegalStateException result = assertThrows(IllegalStateException.class, () -> robot.move());
        assertThat(result.getMessage(), is("Robot hasn't been initialised, can't respond to 'move' command"));
    }

    @Test
    void respondToMoveButRobotInInitialStateShouldIgnoreCommand() {
        Robot result = Robot.INITIAL_STATE.respond("MOVE");

        assertAll(
            () -> assertThat(result, is(Robot.INITIAL_STATE)),
            // asserting we didn't change the initial state
            () -> assertThat(result.getPoint(), is(nullValue())),
            () -> assertThat(result.getDirection(), is(nullValue()))
        );
    }

    @Test
    void moveShouldMoveRobot() {
        Robot robot = new Robot(new Point(2, 2), WEST, commandInterpreter);

        Robot result = robot.move();

        assertAll(
            () -> assertThat(result.getDirection(), equalTo(WEST)),
            () -> assertThat(result.getPoint(), equalTo(new Point(1, 2)))
        );

    }

    @Test
    void reportShouldThrowIllegalStateExceptionOnInitialStateRobot() {
        Robot robot = Robot.INITIAL_STATE;

        IllegalStateException result = assertThrows(IllegalStateException.class, () -> robot.report());
        assertThat(result.getMessage(), is("Robot hasn't been initialised, can't respond to 'report' command"));
    }

    @Test
    void respondToReportButRobotInInitialStateShouldIgnoreCommand() {
        Robot result = Robot.INITIAL_STATE.respond("MOVE");

        assertAll(
            () -> assertThat(result, is(Robot.INITIAL_STATE)),
            // asserting we didn't change the initial state
            () -> assertThat(result.getPoint(), is(nullValue())),
            () -> assertThat(result.getDirection(), is(nullValue()))
        );
    }

    @Test
    void reportShouldReturnSameRobot() {
        Robot robot = new Robot(new Point(1, 1), EAST, commandInterpreter);

        Robot result = robot.report();

        assertThat(result, is(robot));
    }


    @Test
    void respondToPlaceShouldWorkOnNonInitialStateRobot() {
        Robot robot = new Robot(new Point(2, 1), WEST, commandInterpreter);

        Robot result = robot.place(1, 2, NORTH);

        assertAll(
            () -> assertThat(result.getDirection(), equalTo(NORTH)),
            () -> assertThat(result.getPoint(), equalTo(new Point(1, 2)))
        );

    }

    Matcher<Robot> robotDescribedBy(int x, int y, Direction direction) {
        Robot expected = new Robot(new Point(x, y), direction, commandInterpreter);

        return new TypeSafeMatcher<Robot>() {
            @Override
            protected boolean matchesSafely(Robot item) {
                return expected.equals(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("should equal ").appendValue(expected);
            }
        };

    }
}
