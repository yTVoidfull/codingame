import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;


/**
 * Created by alplesca on 7/24/2017.
 */
public class PlayerTest {

    @Test
    public void aBusterCanMoveToAnLocatable() throws Exception {
        Player.Buster buster = new Player.Buster(0, 0, 0,0,0);
        Player.Locatable Locatable = new Player.Locatable(100, 100, 0,0,0);

        Assert.assertThat(buster.moveTo(Locatable), is("MOVE 100 100"));
    }

    @Test
    public void aBusterCanOnlyMove800UnitsTowardsAnLocatableOnYAxis(){
        Player.Buster buster = new Player.Buster(0, 0, 0,0,0);
        Player.Locatable Locatable = new Player.Locatable(0, 900, 0,0,0);

        Assert.assertThat(buster.moveTo(Locatable), is("MOVE 0 800"));
    }

    @Test
    public void aBusterCanOnlyMove800UnitsTowardsAnLocatableOnXAxis(){
        Player.Buster buster = new Player.Buster(0, 0, 0,0,0);
        Player.Locatable Locatable = new Player.Locatable(900, 0, 0,0,0);

        Assert.assertThat(buster.moveTo(Locatable), is("MOVE 800 0"));
    }

    @Test
    public void aBusterCanOnlyMove800UnitsTowardsAnLocatableOnYAxisBack(){
        Player.Buster buster = new Player.Buster(0, 900, 0,0,0);
        Player.Locatable Locatable = new Player.Locatable(0, 0, 0,0,0);

        Assert.assertThat(buster.moveTo(Locatable), is("MOVE 0 100"));
    }

    @Test
    public void aBusterCanOnlyMove800UnitsTowardsAnLocatableOnXAxisBack(){
        Player.Buster buster = new Player.Buster(900, 0, 0,0,0);
        Player.Locatable Locatable = new Player.Locatable(0, 0, 0,0,0);

        Assert.assertThat(buster.moveTo(Locatable), is("MOVE 100 0"));
    }

    @Test
    public void aBusterCanStunAnEnemy() throws Exception {
        Player.Buster buster = new Player.Buster(900, 0, 0,0,0);
        Player.Locatable Locatable = new Player.Locatable(0, 0, 1,0,0);

        Assert.assertThat(buster.stun(Locatable), is("STUN 1"));
    }

    @Test
    public void aBusterCanBustAGhost() throws Exception {
        Player.Buster buster = new Player.Buster(900, 0, 0,0,0);
        Player.Locatable ghost = new Player.Locatable(0, 0, 1,0,0);

        Assert.assertThat(buster.bust(ghost), is("BUST 1"));
    }

    @Test
    public void aBusterCanHerdGhosts() throws Exception {

    }
}
