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
    public void aBusterWillStunTheEnemyTrappingTheGhostWith7Stamina() throws Exception {

        Player.base = new Player.Locatable(0.0, 0.0, 0, 1, 0);
        Player.enemyBase = new Player.Locatable(16000.0, 9000.0, 1, -1, 0);
        Player.stunTimer = new HashMap<Integer, Integer>();
        Player.stunnedEnemies = new ArrayList<Player.Locatable>();
        Player.setUpStunTimer(0, 1);

        Player.Buster buster = new Player.Buster(7000, 900, 0, 0,0);
        Player.busters = new ArrayList<Player.Buster>();

        Player.Locatable enemy = new Player.Locatable(7900, 0, 2,3,3);
        Player.enemies = new ArrayList<Player.Locatable>();

        Player.Locatable ghost = new Player.Locatable(7000, 0, 3,1,7);
        Player.ghosts = new ArrayList<Player.Locatable>();

        Player.busters.add(buster);
        Player.enemies.add(enemy);
        Player.ghosts.add(ghost);

        Assert.assertThat(Player.decideAction(buster), is("STUN 2"));

    }

    @Test
    public void aBusterWillBustAGhostWithZeroStaminaWhenAnEqualNumberOfAlliesAndEnemiesTryToBustIt() throws Exception {

        Player.base = new Player.Locatable(0.0, 0.0, 0, 1, 0);
        Player.enemyBase = new Player.Locatable(16000.0, 9000.0, 1, -1, 0);
        Player.stunTimer = new HashMap<Integer, Integer>();
        Player.stunnedEnemies = new ArrayList<Player.Locatable>();
        Player.setUpStunTimer(0, 2);
        Player.busters = new ArrayList<Player.Buster>();
        Player.enemies = new ArrayList<Player.Locatable>();
        Player.ghosts = new ArrayList<Player.Locatable>();

        Player.Buster buster = new Player.Buster(7000, 900, 0, 3,3);
        Player.busters.add(buster);

        Player.Buster buster1 = new Player.Buster(6100, 900, 1, 0,0);
        Player.busters.add(buster1);

        Player.Locatable enemy = new Player.Locatable(7900, 0, 2,3,3);
        Player.enemies.add(enemy);

        Player.Locatable ghost = new Player.Locatable(7000, 0, 3,2,0);
        Player.ghosts.add(ghost);

        Assert.assertThat(Player.decideAction(buster), is("BUST 3"));
    }

    @Test
    public void twoBustersWillStunTwoEnemiesToStealAGhostWith10Stamina() throws Exception {
        Player.base = new Player.Locatable(0.0, 0.0, 0, 1, 0);
        Player.enemyBase = new Player.Locatable(16000.0, 9000.0, 1, -1, 0);
        Player.stunTimer = new HashMap<Integer, Integer>();
        Player.stunnedEnemies = new ArrayList<Player.Locatable>();
        Player.setUpStunTimer(0, 2);
        Player.busters = new ArrayList<Player.Buster>();
        Player.enemies = new ArrayList<Player.Locatable>();
        Player.ghosts = new ArrayList<Player.Locatable>();

        Player.Buster buster = new Player.Buster(7000, 900, 0, 3,3);
        Player.busters.add(buster);

        Player.Buster buster1 = new Player.Buster(7000, 900, 1, 3,3);
        Player.busters.add(buster1);

        Player.Locatable enemy = new Player.Locatable(7900, 0, 2,3,3);
        Player.enemies.add(enemy);

        Player.Locatable enemy1 = new Player.Locatable(7900, 0, 3,3,3);
        Player.enemies.add(enemy1);

        Player.Locatable ghost = new Player.Locatable(7000, 0, 3,2,10);
        Player.ghosts.add(ghost);

        Assert.assertThat(Player.decideAction(buster), is("STUN 2"));
        Assert.assertThat(Player.decideAction(buster1), is("STUN 3"));
    }

    @Test
    public void twoBustersNextToAGhostWithMoreThan0StaminaWillBustIt() throws Exception {
        Player.base = new Player.Locatable(0.0, 0.0, 0, 1, 0);
        Player.enemyBase = new Player.Locatable(16000.0, 9000.0, 1, -1, 0);
        Player.stunTimer = new HashMap<Integer, Integer>();
        Player.stunnedEnemies = new ArrayList<Player.Locatable>();
        Player.setUpStunTimer(0, 2);
        Player.busters = new ArrayList<Player.Buster>();
        Player.enemies = new ArrayList<Player.Locatable>();
        Player.ghosts = new ArrayList<Player.Locatable>();

        Player.Buster buster = new Player.Buster(7000, 900, 0, 0,0);
        Player.busters.add(buster);

        Player.Buster buster1 = new Player.Buster(7000, 900, 1, 0,0);
        Player.busters.add(buster1);

        Player.Locatable ghost = new Player.Locatable(7000, 0, 3,2,10);
        Player.ghosts.add(ghost);

        Assert.assertThat(Player.decideAction(buster), is("BUST 3"));
        Assert.assertThat(Player.decideAction(buster1), is("BUST 3"));
    }

}
