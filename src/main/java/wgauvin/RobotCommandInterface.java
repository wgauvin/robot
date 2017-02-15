package wgauvin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * Created by will on 15/02/17.
 */
@Component
public class RobotCommandInterface implements Runnable {

    private static final int TIMEOUT = 10;
    private static final Logger LOGGER = LoggerFactory.getLogger(RobotCommandInterface.class);

    private volatile Robot robot;
    private final BufferedReader bufferedReader;
    private final ExecutorService executorService;

    @Inject
    public RobotCommandInterface(Robot robot,
                                 @Qualifier("robotConsoleReader")BufferedReader bufferedReader,
                                 @Qualifier("robotConsoleExecutorService") ExecutorService executorService) {
        this.robot = robot;
        this.bufferedReader = bufferedReader;
        this.executorService = executorService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        executorService.submit(this);
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdownNow();
        try {
            bufferedReader.close();
        } catch (IOException e) {
            LOGGER.warn("Error in closing reader.", e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (bufferedReader.ready()) {
                    String line = bufferedReader.readLine();
                    robot = robot.respond(line);
                }
                Thread.sleep(TIMEOUT);
            } catch (IOException e) {
                LOGGER.warn("Error while reading console", e);
            } catch (InterruptedException e) {
                LOGGER.debug("Interrupt occurred.  Exiting");
                break;
            }
        }
    }

}

