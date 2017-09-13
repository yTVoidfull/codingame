package wondev;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.core.Is.is;
import static wondev.Player.*;
import static wondev.Player.grid;

/**
 * Created by alplesca on 8/9/2017.
 */
public class PlayerTest {

    @Test
    public void gridProcessorValidatingThatNextLevelToACellIsProperlyAssigned() throws Exception {
        grid = new Grid(3);
        grid.populateRow(0, "012");
        grid.populateRow(1, "021");
        grid.populateRow(2, "210");
        wondev1 = new Wondev(grid.getCell(1,1), 0);

        Cell levelOneCell = wondev1.getProcessedGrid().getCell(0,0);
        assertThat(levelOneCell.getLevel()).isEqualTo(1);
    }
}
