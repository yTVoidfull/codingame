package strikeback;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

/**
 * Created by alplesca on 7/26/2017.
 */
public class PlayerTest {


    @Test
    public void testingTwoLocatables() throws Exception {

        Player.Locatable first = new Player.Locatable(100, 100);
        Player.Locatable second = new Player.Locatable(100, 100);

        Assert.assertThat(first.equals(second), is(true));

    }
}
