package botters;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static botters.Player.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class PlayerTest {

    Hero doctorStrange;
    Hero ironMan;
    Hero hulk;
    Hero valkyrie;
    Hero deadpool;
    Map<String, List<Player.Item>> heroItems = new HashMap<>();

    @Before
    public void initialize(){
        items = new ArrayList<>();
        Player.gold = 500;

        doctorStrange = new DoctorStrange(0, 0, "HERO", 100, 100, 245, 955, 955, 0,50, 200,300,0,0,0,300,300, 2, "DOCTOR_STRANGE",1,1,0);
        heroItems.put(doctorStrange.heroType, new ArrayList<>());
    }

    @Test
    public void validateThatBestItemIsChosen() throws Exception {
        items.add(new Player.Item("bronze_blade", 127,18, 0, 0,0,0,0,0,0));
        items.add(new Player.Item("silver_blade", 450,61, 0, 0,0,0,0,0,0));

        assertThat(doctorStrange.purchaseDecision(heroItems)).isEqualTo("BUY bronze_blade");
    }

    @Test
    public void validateThatYouCanApproximateFuturePosition(){
        Point x = new Point(100, 600);
        Point tower = new Point(100, 100);
        Point newP = x.getPositionTowards(tower, 100);
        assertThat(newP.x).isEqualTo(100);
        assertThat(newP.y).isEqualTo(500);

        x = new Point(100, 100);
        tower = new Point(600, 100);
        newP = x.getPositionTowards(tower, 100);
        assertThat(newP.x).isEqualTo(200);
        assertThat(newP.y).isEqualTo(100);

        x = new Point(0,0);
        tower = new Point(300, 300);
        newP = x.getPositionTowards(tower, 141);

        assertThat(newP.x).isEqualTo(99);
        assertThat(newP.y).isEqualTo(99);

        x = new Point(300,300);
        tower = new Point(0, 0);
        newP = x.getPositionTowards(tower, 141);
        assertThat(newP.x).isEqualTo(200);
        assertThat(newP.y).isEqualTo(200);

        x = new Point(300,300);
        tower = new Point(0, 600);
        newP = x.getPositionTowards(tower, 141);
        assertThat(newP.x).isEqualTo(200);
        assertThat(newP.y).isEqualTo(399);
    }

}