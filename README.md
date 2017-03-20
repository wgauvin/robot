Toy Robot Simulator
===================

# Overview

This simulator is used to control a robot that is on a 5 x 5 sized table.  Text is read in via the
console in (System.in).  The following are the rules:

* Until the robot is placed on the board it ignores all moves
* The robot has the following commands:
  - **PLACE X,Y,Direction** where x and y are positions on the board and direction is a cardinal
   point of the compass.
  - **LEFT** turns the robot left 90 degrees.
  - **RIGHT** turns the robot right 90 degrees.
  - **MOVE** moves the robot forward in its current direction.
  - **REPORT** the robot echoes out to the console it's current position and direction.
* The robot will ignore any placement that does not put it on the table.
* The robot will not move if it would be forced to move off the table.
* It safely ignores all other commands.

# Building

This project requires Java 8 installed.  Either use your machine's own package manager to install
the latest Java 8 JDK else download an installation from the
[Oracle Java Download Page](http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html).

This project runs on [Gradle](https://gradle.org/) and was developed with on version 3.3.  If you
gradle installed, then run the following command to locally install the current gradle wrapper:

```text
gradle wrapper
```
## Build Reports

The build uses [PMD](https://pmd.github.io/), [FindBugs](http://findbugs.sourceforge.net/),
[Checkstyle](http://checkstyle.sourceforge.net/) and [JaCoCo](http://www.eclemma.org/jacoco/) as part
of code quality and reporting.  All the reports can be found in the ``build/reports`` directory.

The build will break if there are violations for PMD, FindBugs and Chechstyle.

# Running

To run the application after build, run the following command:

```bash
java -jar build/libs/robot-0.0.1-SNAPSHOT.jar
```

Once the application is running you can then enter commands to move the robot around the board.

There are no other toolsets are required to build or run the application.

# Testing

The `data` directory has a few example files that can be used to test that the robot does what
it is expected.

| Filename               | Description                                                                              |
|------------------------|-------------------------------------------------------------------------------------------|
| example-1.in           | Use case 1. Should end at 0,1,NORTH                                                       |
| example-2.in           | Use case 2. Should end at 0,0,WEST                                                        |
| example-3.in           | Use case 3. Should end at 3,3,NORTH                                                       |
| example-knight-5by5.in | Making the robot do a chess knight's walk starting from 2,2,NORTH and ending at 0,4,NORTH |

These files can be feed to the simulator by:

```bash
java -jar build/libs/robot-0.0.1-SNAPSHOT.jar < data/<filename>
```

# Design Notes

The ``Robot`` and ``Point`` classes were designed to be immutable thus any action the would change their state actually
return a new object.  This allows further changes such as being about to replay the history or have asynchronous access
to have commands run on the robot.

``Direction`` was done as an enum as there were only a few values and creating a state map for turning left or right
was made more efficient by using an ``EnumMap``.  This however meant having to use a static initialiser block as enums
can't forward reference other values, not even in a lambda.  The enum ordinal could have been used to determine the next
direction for left or right like below but as can be seen it isn't easier to read and relies on the order of how the
enum values are defined and requires worrying about under or overflow dealing with the ordinal value.

```java
public Direction left() {
    int idx = this.ordinal();
    if (idx == 0) {
        // avoid underflow for later index out of bounds
        idx = WEST.ordinal();
    } else {
        idx--;
    }
    return Direction.values()[idx];
}
```

Interfacing with the robot is done via the ``Robot#respond(String)`` method which takes a command that is then sent to a
``RobotCommandInterepter`` that returns a ``RobotCommand`` that the robot then can respond to.  This allows for the
robot to encapsulate what commands it responds to and how.  The ``RobotCommandEnum`` is used to capture the commands like
MOVE, RIGHT, LEFT and REPORT as they don't need any regular expressions and using the ``Enum#valueOf`` method is safe.

The ``RobotCommandInterface`` reads from the commandline using a ``BufferedReader`` in a background thread.  The reason
for doing this is that the ``BufferedReader#ready()`` method is non-blocking but any other commands on InputStreams or
Readers are thus allowing the background thread not to block until something is ready to be read.  As this is a Spring
bean it also listens for Spring application context events, like the ``ApplicationReadyEvent`` to only start reading
from the STDIN when the whole application is up, and to gracefully shut down the ``ExecutorService`` and
``BufferedReader`` when the bean gets destroyed.

# Other Notes

I chose to use [JUnit 5](http://junit.org/junit5/), even though still in milestone (M3) as I wanted to see what it was
like.  I do think there needs to be a bit more work on how to do dynamic tests using annotations as a way of setting up
test data.  There is too much boilerplate per annotation but overall I found that the JUnit 5 easy to work with.
