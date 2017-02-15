package wgauvin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by will on 15/02/17.
 */
class RobotCommandInterfaceTest {

    @Mock
    private Robot robot;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @BeforeEach
    public void setup() {
        initMocks(this);
    }

    @Test
    void initShouldSubmitTaskToExecutorService() {
        ExecutorService mockExecutorService = mock(ExecutorService.class);
        RobotCommandInterface robotCommandInterface = new RobotCommandInterface(robot, null, mockExecutorService);

        robotCommandInterface.init();

        verify(mockExecutorService).submit(robotCommandInterface);
    }

    @Test
    void shutdownShouldCallShutdownOnExecutorService() throws Exception {
        ExecutorService mockExecutorService = mock(ExecutorService.class);
        BufferedReader reader = mock(BufferedReader.class);
        RobotCommandInterface robotCommandInterface = new RobotCommandInterface(robot, reader, mockExecutorService);

        robotCommandInterface.shutdown();

        verify(mockExecutorService).shutdownNow();
        verify(reader).close();
    }

    @Test
    void runShouldWaitUntilLineIsReadable() throws Exception {
        try (PipedInputStream in = new PipedInputStream();
             PipedOutputStream out = new PipedOutputStream(in);
             InputStreamReader isr = new InputStreamReader(in, UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {

            when(robot.respond(anyString())).thenReturn(robot);
            RobotCommandInterface robotCommandInterface = new RobotCommandInterface(robot, reader, executorService);
            out.write("r2d2".getBytes(UTF_8));
            out.flush();

            Future<?> future = executorService.submit(robotCommandInterface);

            Thread.sleep(10);
            verify(robot, never()).respond(anyString());
            out.write("\nfoo".getBytes(UTF_8));
            out.flush();
            Thread.sleep(10);
            out.write("\n".getBytes(UTF_8));
            out.flush();
            Thread.sleep(10);
            // cancel the future else test doesn't end.
            future.cancel(true);

            verify(robot).respond("r2d2");
            verify(robot).respond("foo");
        }
    }
}
