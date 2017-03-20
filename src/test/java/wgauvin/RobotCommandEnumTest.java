package wgauvin;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.mockito.stubbing.Answer;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Arrays.stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static wgauvin.RobotCommandEnum.LEFT;
import static wgauvin.RobotCommandEnum.MOVE;
import static wgauvin.RobotCommandEnum.REPORT;
import static wgauvin.RobotCommandEnum.RIGHT;

/**
 * Created by will on 9/03/17.
 */

class RobotCommandEnumTest {

    private Robot updatedRobot = mock(Robot.class);

    private Robot robot = mock(Robot.class, (Answer) invocation -> updatedRobot);

    @Test
    public void applyOnLeftShouldCallRobotLeft() {
        Robot response = RobotCommandEnum.LEFT.apply(robot);

        assertAll(() -> {
            verify(robot).left();
            verifyNoMoreInteractions(robot);
            assertThat(response, is(updatedRobot));
        });
    }

    @Test
    public void applyOnRightShouldCallRobotRight() {
        Robot response = RIGHT.apply(robot);

        assertAll(() -> {
            verify(robot).right();
            verifyNoMoreInteractions(robot);
            assertThat(response, is(updatedRobot));
        });
    }

    @Test
    public void applyOnMoveShouldCallRobotMove() {
        Robot response = RobotCommandEnum.MOVE.apply(robot);

        assertAll(() -> {
            verify(robot).move();
            verifyNoMoreInteractions(robot);
            assertThat(response, is(updatedRobot));
        });
    }

    @Test
    public void applyOnReportShouldCallRobotReport() {
        Robot response = RobotCommandEnum.REPORT.apply(robot);

        assertAll(() -> {
            verify(robot).report();
            verifyNoMoreInteractions(robot);
            assertThat(response, is(updatedRobot));
        });
    }

    @Test
    public void getActionWithInvalidValueReturnsEmptyOptional() {
        Optional<RobotCommand> result = RobotCommandEnum.getRobotCommand("invalid");

        assertAll(() -> {
            assertThat(result, is(notNullValue()));
            assertThat(result.isPresent(), is(false));
        });
    }

    @TestParam(value = "RIGHT", action = RIGHT)
    @TestParam(value = "LEFT", action = LEFT)
    @TestParam(value = "MOVE", action = MOVE)
    @TestParam(value = "REPORT", action = REPORT)
    @ExtendWith(TestParamResolver.class)
    @TestFactory
    Stream<DynamicTest> getActionShouldReturnCorrectValue(RobotCommandEnumTest.TestParam[] params) {
        return stream(params).map(p -> {
            String value = p.value();
            RobotCommandEnum action = p.action();
            String testName = String.format("'%s' should return RobotCommandEnum.%s", value, action);
            Optional<RobotCommand> result = RobotCommandEnum.getRobotCommand(value);

            return dynamicTest(testName, () -> assertAll(
                () -> assertThat(result.isPresent(), is(true)),
                () -> assertThat(result.get(), is(action)))
            );
        });
    }


    @Retention(RUNTIME)
    @interface TestParams {
        RobotCommandEnumTest.TestParam[] value() default {};
    }

    @Repeatable(RobotCommandEnumTest.TestParams.class)
    @Retention(RUNTIME)
    @interface TestParam {
        String value();
        RobotCommandEnum action();
    }

    static class TestParamResolver implements ParameterResolver {

        @Override
        public boolean supports(ParameterContext parameterContext, ExtensionContext extensionContext) {
            return parameterContext.getParameter().getType().isAssignableFrom(RobotCommandEnumTest.TestParam[].class);
        }

        @Override
        public Object resolve(ParameterContext parameterContext, ExtensionContext extensionContext) {
            return parameterContext.getDeclaringExecutable().getAnnotationsByType(RobotCommandEnumTest.TestParam.class);
        }
    }
}
