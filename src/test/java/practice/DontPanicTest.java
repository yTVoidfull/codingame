package practice;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static practice.DontPanic.elevators;
import static practice.DontPanic.playGame;

public class DontPanicTest {

    @Test
    public void itWillBlockIfHeadingTheWrongWayToRight() throws Exception {
        elevators.add(new DontPanic.Locatable(0, 1));

        Assert.assertThat(playGame(0, 2, "RIGHT", 1,0), is("BLOCK"));
    }

    @Test
    public void itWillBlockIfHeadingTheWrongWayToLeft() throws Exception {
        elevators.add(new DontPanic.Locatable(0, 3));

        Assert.assertThat(playGame(0, 2, "LEFT", 1,0), is("BLOCK"));
    }

    @Test
    public void itWillBlockIfHeadingTheWrongWayToRightExit() throws Exception {
        elevators.add(new DontPanic.Locatable(2, 1));

        Assert.assertThat(playGame(0, 2, "RIGHT", 0,1), is("BLOCK"));
    }

    @Test
    public void itWillBlockIfHeadingTheWrongWayToLeftExit() throws Exception {
        elevators.add(new DontPanic.Locatable(2, 1));

        Assert.assertThat(playGame(0, 2, "LEFT", 0,3), is("BLOCK"));
    }

    @Test
    public void itWillNotBlockIfHeadingTheRightWayToLeftExit() throws Exception {
        elevators.add(new DontPanic.Locatable(2, 1));

        Assert.assertThat(playGame(0, 2, "RIGHT", 0,3), is("WAIT"));
    }

    @Test
    public void itWillNotBlockIfHeadingTheRightWayToLeft() throws Exception {
        elevators.add(new DontPanic.Locatable(2, 1));

        Assert.assertThat(playGame(0, 4, "LEFT", 0,0), is("WAIT"));
    }

}
