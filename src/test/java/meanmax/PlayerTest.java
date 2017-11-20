package meanmax;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PlayerTest {


    @Test
    public void thereIsAReaperAndItHasThrottle() throws Exception {
        int x = 100;
        int y = 100;
        Player.Looter reaper = new Player.Looter(x, y, 100, 200, (float)0.0, 400);
        assertThat(reaper.getThrottle(new Player.Wreck(100, 100, 2, 400))).isEqualTo(0);
    }

    @Test
    public void thereIsAWreck() throws Exception {
        int x = 100;
        int y = 100;
        Player.Wreck w = new Player.Wreck(x, y, 1, 400);
        assertThat(w.getX()).isEqualTo(100);
    }

    @Test
    public void canKnowDistanceToWreck() throws Exception {
        int x = 100;
        int y = 100;
        Player.Looter reaper = new Player.Looter(x, y, 100, 100 , (float)0.5, 400);
        Player.Wreck w = new Player.Wreck(x, y, 1, 400);
        assertThat(reaper.distanceTo(w)).isEqualTo(0.00);
    }

    @Test
    public void reaperCanMoveToWreck() throws Exception {
        int x = 100;
        int y = 100;
        Player.Looter reaper = new Player.Looter(x, y, 100, 100 , (float)0.5, 400);
        Player.Wreck w = new Player.Wreck(x, y, 1, 400);
        assertThat(reaper.moveTo(w)).isEqualTo("100 100 0");
    }

    @Test
    public void throttleWillBeBasedOnDistance() throws Exception {
        int x = 100;
        int y = 100;
        Player.Looter reaper = new Player.Looter(x, y, 100, 100 , (float)0.5, 400);
        Player.Wreck w = new Player.Wreck(x + 30, y+40, 1, 400);
        assertThat(reaper.moveTo(w)).isEqualTo("130 140 25");
    }

    @Test
    public void thereIsAMatrix() throws Exception {
        Player.CircularMatrix m = new Player.CircularMatrix(200, 200);
        assertThat(m.getSensor(0,0)).isEqualTo(new Player.Sensor(-200, 0));
        assertThat(m.getSensor(1,0)).isEqualTo(new Player.Sensor(0, -200));
        assertThat(m.getSensor(1,1)).isEqualTo(new Player.Sensor(0, 0));
        assertThat(m.getSensor(1,2)).isEqualTo(new Player.Sensor(0, 200));
        assertThat(m.getSensor(2,0)).isEqualTo(new Player.Sensor(200, 0));
    }


}
