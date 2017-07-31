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
    public void aBusterCanMoveToAnAgent() throws Exception {
        Player.Buster buster = new Player.Buster(0, 0, 0,0,0);
        Player.Agent agent = new Player.Agent(100, 100, 0,0,0);

        Assert.assertThat(buster.moveTo(agent), is("MOVE 100 100"));
    }

    @Test
    public void aBusterCanOnlyMove800UnitsTowardsAnAgentOnYAxis(){
        Player.Buster buster = new Player.Buster(0, 0, 0,0,0);
        Player.Agent agent = new Player.Agent(0, 900, 0,0,0);

        Assert.assertThat(buster.moveTo(agent), is("MOVE 0 800"));
    }

    @Test
    public void aBusterCanOnlyMove800UnitsTowardsAnAgentOnXAxis(){
        Player.Buster buster = new Player.Buster(0, 0, 0,0,0);
        Player.Agent agent = new Player.Agent(900, 0, 0,0,0);

        Assert.assertThat(buster.moveTo(agent), is("MOVE 800 0"));
    }

    @Test
    public void aBusterCanOnlyMove800UnitsTowardsAnAgentOnYAxisBack(){
        Player.Buster buster = new Player.Buster(0, 900, 0,0,0);
        Player.Agent agent = new Player.Agent(0, 0, 0,0,0);

        Assert.assertThat(buster.moveTo(agent), is("MOVE 0 100"));
    }

    @Test
    public void aBusterCanOnlyMove800UnitsTowardsAnAgentOnXAxisBack(){
        Player.Buster buster = new Player.Buster(900, 0, 0,0,0);
        Player.Agent agent = new Player.Agent(0, 0, 0,0,0);

        Assert.assertThat(buster.moveTo(agent), is("MOVE 100 0"));
    }

    @Test
    public void aBusterCanStunAnEnemy() throws Exception {
        Player.Buster buster = new Player.Buster(900, 0, 0,0,0);
        Player.Agent agent = new Player.Agent(0, 0, 1,0,0);

        Assert.assertThat(buster.stun(agent), is("STUN 1"));
    }

    @Test
    public void aBusterCanBustAGhost() throws Exception {
        Player.Buster buster = new Player.Buster(900, 0, 0,0,0);
        Player.Agent ghost = new Player.Agent(0, 0, 1,0,0);

        Assert.assertThat(buster.bust(ghost), is("BUST 1"));
    }


}
