package wgauvin;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.util.stream.Stream;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static wgauvin.Direction.EAST;
import static wgauvin.Direction.NORTH;
import static wgauvin.Direction.SOUTH;
import static wgauvin.Direction.WEST;

/**
 * Created by will on 13/02/17.
 */
class DirectionTest {

    @TestParam(value = NORTH, direction = WEST)
    @TestParam(value = EAST, direction = NORTH)
    @TestParam(value = SOUTH, direction = EAST)
    @TestParam(value = WEST, direction = SOUTH)
    @ExtendWith(TestParamResolver.class)
    @TestFactory
    public Stream<DynamicTest> testDirectionLeft(TestParam[] testParams) {
        return stream(testParams)
                .map(v -> dynamicTest(testName("%s.left() should return %s", v.value(), v.direction()),
                        () -> assertEquals(v.direction(), v.value().left())));
    }

    @TestParam(value = NORTH, direction = EAST)
    @TestParam(value = EAST, direction = SOUTH)
    @TestParam(value = SOUTH, direction = WEST)
    @TestParam(value = WEST, direction = NORTH)
    @ExtendWith(TestParamResolver.class)
    @TestFactory
    public Stream<DynamicTest> testDirectionRight(TestParam[] testParams) {
        return stream(testParams)
                .map(v -> dynamicTest(testName("%s.right() should return %s", v.value(), v.direction()),
                        () -> assertEquals(v.direction(), v.value().right())));
    }

    @TestParam(value = NORTH, direction = NORTH, delta = 0)
    @TestParam(value = EAST, direction = EAST, delta = 1)
    @TestParam(value = SOUTH, direction = SOUTH, delta = 0)
    @TestParam(value = WEST, direction = WEST, delta = -1)
    @ExtendWith(TestParamResolver.class)
    @TestFactory
    public Stream<DynamicTest> testGetDeltaX(TestParam[] testParams) {
        return stream(testParams)
                .map(v -> dynamicTest(testName("%s.getDeltaX() should return %d", v.value(), v.delta()),
                        () -> assertEquals(v.delta(), v.value().getDeltaX())));
    }

    @TestParam(value = NORTH, direction = NORTH, delta = 1)
    @TestParam(value = EAST, direction = EAST, delta = 0)
    @TestParam(value = SOUTH, direction = SOUTH, delta = -1)
    @TestParam(value = WEST, direction = WEST, delta = 0)
    @ExtendWith(TestParamResolver.class)
    @TestFactory
    public Stream<DynamicTest> testGetDeltaY(TestParam[] testParams) {
        return stream(testParams)
                .map(v -> dynamicTest(testName("%s.getDeltaY() should return %d", v.value(), v.delta()),
                        () -> assertEquals(v.delta(), v.value().getDeltaY())));
    }

    private String testName(String format, Object... values) {
        return String.format(format, values);
    }

    @Retention(RUNTIME)
    @interface TestParams {
        TestParam[] value() default {};
    }

    @Repeatable(TestParams.class)
    @Retention(RUNTIME)
    @interface TestParam {
        Direction value();
        Direction direction();
        int delta() default -1;
    }

    static class TestParamResolver implements ParameterResolver {

        @Override
        public boolean supports(ParameterContext parameterContext, ExtensionContext extensionContext) {
            return parameterContext.getParameter().getType().isAssignableFrom(TestParam[].class);
        }

        @Override
        public Object resolve(ParameterContext parameterContext, ExtensionContext extensionContext) {
            return parameterContext.getDeclaringExecutable().getAnnotationsByType(TestParam.class);
        }
    }
}
