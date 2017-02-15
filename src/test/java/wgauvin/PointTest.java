package wgauvin;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.util.Random;
import java.util.function.IntSupplier;
import java.util.stream.Stream;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static wgauvin.Direction.EAST;
import static wgauvin.Direction.NORTH;
import static wgauvin.Direction.SOUTH;
import static wgauvin.Direction.WEST;
import static wgauvin.PointTest.TestParam.ANY;
import static wgauvin.PointTest.TestParam.SAME;

/**
 * Created by will on 14/02/17.
 */
class PointTest {

    private Random random = new Random();

    // boundary tests
    @TestParam(startX = ANY, startY = 4, direction = NORTH, endX = SAME, endY = 4)
    @TestParam(startX = 4, startY = ANY, direction = EAST, endX = 4, endY = SAME)
    @TestParam(startX = ANY, startY = 0, direction = SOUTH, endX = SAME, endY = 0)
    @TestParam(startX = 0, startY = ANY, direction = WEST, endX = 0, endY = SAME)
    @ExtendWith(TestParamResolver.class)
    @TestFactory
    Stream<DynamicTest> testBoundary(TestParam[] params) {
        return stream(params).map(this::createTest);
    }

    // non-boundary tests
    @TestParam(startX = ANY, startY = 2, direction = NORTH, endX = SAME, endY = 3)
    @TestParam(startX = 2, startY = ANY, direction = EAST, endX = 3, endY = SAME)
    @TestParam(startX = ANY, startY = 2, direction = SOUTH, endX = SAME, endY = 1)
    @TestParam(startX = 2, startY = ANY, direction = WEST, endX = 1, endY = SAME)
    @ExtendWith(TestParamResolver.class)
    @TestFactory
    Stream<DynamicTest> testNonBoundary(TestParam[] params) {
        return stream(params).map(this::createTest);
    }

    DynamicTest createTest(TestParam param) {
        int startX = getStartValue(param::startX);
        int startY = getStartValue(param::startY);
        int endX = getEndValue(param::endX, startX);
        int endY = getEndValue(param::endY, startY);

        Point start = new Point(startX, startY);
        Point result = start.move(param.direction());
        Point expected = new Point(endX, endY);
        String testName = String.format("%d,%d moving %s should result in %d,%d", startX, startY,
                param.direction(), endX, endY);

        return dynamicTest(testName, () -> assertAll(
                () -> assertEquals(expected.getX(), result.getX()),
                () -> assertEquals(expected.getY(), result.getY())
            ));
    }

    int getStartValue(IntSupplier supplier) {
        int val = supplier.getAsInt();
        if (val == ANY) {
            return random.nextInt(5);
        }
        return val;
    }

    int getEndValue(IntSupplier supplier, int start) {
        int val = supplier.getAsInt();
        if (val == SAME) {
            return start;
        }
        return val;
    }

    @Retention(RUNTIME)
    @interface TestParams {
        TestParam[] value() default {};
    }

    @Repeatable(TestParams.class)
    @Retention(RUNTIME)
    @interface TestParam {
        int ANY = -1;
        int SAME = -2;

        int startX();
        int startY();
        Direction direction();
        int endX();
        int endY();
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
