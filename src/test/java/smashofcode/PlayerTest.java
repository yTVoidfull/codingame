package smashofcode;

import org.junit.Before;
import org.junit.Test;
import smashofcode.Player.Grid;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class PlayerTest {

    @Test
    public void theBlockWillFallOnSameColor() {
        Player player  = new Player();
        Grid grid = player.new Grid();

        grid.setRow(11, "012345");
        assertThat(grid.getBestColumnFor(1, 2)).isEqualTo(1);
    }
}
