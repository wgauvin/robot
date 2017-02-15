package wgauvin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.nio.charset.StandardCharsets.UTF_8;

@SpringBootApplication
public class RobotApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(RobotApplication.class);

    public static void main(String... args) throws Exception {
        SpringApplication.run(RobotApplication.class, args);

    }

    @Bean
    public ScheduledExecutorService robotConsoleExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Bean
    public Robot initialRobot() {
        return Robot.INITIAL_STATE;
    }

    @Bean
    public BufferedReader robotConsoleReader() {
        return new BufferedReader(new InputStreamReader(System.in, UTF_8));
    }

}
