package wgauvin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static wgauvin.Direction.SOUTH;

/**
 * Created by will on 10/03/17.
 */
class RobotCommandInterpreterTest {

    @Mock
    private Robot robot;

    @Mock
    private Robot mockResponse;

    private RobotCommandInterpreter interpreter = new RobotCommandInterpreter();

    @BeforeEach
    void setup() {
        initMocks(this);
    }

    @Test
    void interpretShouldCreatePlaceCommand() {
        when(robot.place(anyInt(), anyInt(), any())).thenReturn(mockResponse);

        Robot response = interpreter.interpret("PLACE 3,4,SOUTH").apply(robot);

        assertAll(
            () -> assertThat(response, is(mockResponse)),
            () -> verify(robot).place(3, 4, SOUTH),
            () -> verifyNoMoreInteractions(robot)
        );
    }

    @Test
    void interpretShouldCreateLeftRobotCommand() {
        when(robot.left()).thenReturn(mockResponse);

        Robot response = interpreter.interpret("LEFT").apply(robot);

        assertAll(
            () -> assertThat(response, is(mockResponse)),
            () -> verify(robot).left(),
            () -> verifyNoMoreInteractions(robot)
        );
    }

    @Test
    void interpretShouldCreateRightRobotCommand() {
        when(robot.right()).thenReturn(mockResponse);

        Robot response = interpreter.interpret("RIGHT").apply(robot);

        assertAll(
            () -> assertThat(response, is(mockResponse)),
            () -> verify(robot).right(),
            () -> verifyNoMoreInteractions(robot)
        );
    }

    @Test
    void interpretShouldCreateMoveRobotCommand() {
        when(robot.move()).thenReturn(mockResponse);

        Robot response = interpreter.interpret("MOVE").apply(robot);

        assertAll(
            () -> assertThat(response, is(mockResponse)),
            () -> verify(robot).move(),
            () -> verifyNoMoreInteractions(robot)
        );
    }

    @Test
    void interpretShouldCreateReportRobotCommand() {
        when(robot.report()).thenReturn(mockResponse);

        Robot response = interpreter.interpret("REPORT").apply(robot);

        assertAll(
            () -> assertThat(response, is(mockResponse)),
            () -> verify(robot).report(),
            () -> verifyNoMoreInteractions(robot)
        );
    }

    @Test
    void interpretShouldCreateIgnoreRobotCommondForInvalidCommand() {
        Robot response = interpreter.interpret("invalid").apply(robot);

        assertAll(
            () -> assertThat(response, is(robot)),
            () -> verifyNoMoreInteractions(robot)
        );
    }

}

