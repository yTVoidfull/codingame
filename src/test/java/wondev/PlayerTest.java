package wondev;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static wondev.Player.*;
import static wondev.Player.grid;

/**
 * Created by alplesca on 8/9/2017.
 */
public class PlayerTest {

    @Test
    public void aBreadthFirstGridWillBeBasedOnAWondevAndAGrid() throws Exception {
        BreadthFirstGrid bfGrid = new BreadthFirstGrid(grid, wondev1);


    }
}
