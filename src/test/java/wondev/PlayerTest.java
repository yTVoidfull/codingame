package wondev;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static wondev.Player.*;

/**
 * Created by alplesca on 8/9/2017.
 */
public class PlayerTest {

    @Test
    public void aCellHasYCoordinate() throws Exception {
        Cell cell = new Cell(1,2,'1');
        cell.getY();
    }

    @Test
    public void aCellHasXCoordinate() throws Exception {
        Cell cell = new Cell(1,1,'1');
        cell.getX();
    }

    @Test
    public void aCellHasValue() throws Exception {
        Cell cell = new Cell(1,1,'1');
        cell.getValue();
    }

    @Test
    public void aCellHasHeight() throws Exception {
        Cell cell = new Cell(1,1,'1');
        int height = cell.getHeight();
    }

    @Test
    public void thereIsAGrid() throws Exception {
        Grid grid = new Grid(3);
    }

    @Test
    public void youCanPopulateRowsInGrid() throws Exception {
        Grid grid = new Grid(3);
        grid.populateRow(0, "12.");
    }

    @Test
    public void youCanGetCellsFromGrid() throws Exception {
        Grid grid = new Grid(3);
        grid.getCell(1, 1);
    }

    @Test
    public void thereIsACell() throws Exception {
        Cell wondev = new Cell(1,1, '2');
    }

    @Test
    public void thereIsAMoveAndBuildAction() throws Exception {
        wondev1 = new Cell(1,1, '2');
        Action action = new Action(wondev1);

        action.setLandingCell(new Cell(1,2,'2'));
        action.setBuildCell(new Cell(1,1,'2'));

        Assert.assertThat(action.execute(), is("MOVE&BUILD 0 S N"));
    }

    @Test
    public void aCellHasAccessibleCellsOnTheGrid() throws Exception {
        Grid grid = new Grid(3);
        grid.populateRow(0, "11.");
        grid.populateRow(1, "11.");
        grid.populateRow(2, "11.");

        Cell wondev = grid.getCell(1,1);
        List<Cell> moveCells = grid.getAccessibleCellsFor(wondev);
    }

    @Test
    public void aCellInTheCornerHasOnlyThreeAccessibleCells() throws Exception {
        Grid grid = new Grid(3);
        grid.populateRow(0, "111");
        grid.populateRow(1, "111");
        grid.populateRow(2, "111");

        Cell wondev = grid.getCell(0,0);
        List<Cell> moveCells = grid.getAccessibleCellsFor(wondev);

        Assert.assertThat(moveCells.size(), is(3));
    }

    @Test
    public void aCellInTheMiddleHasEightAccessibleCells() throws Exception {
        Grid grid = new Grid(3);
        grid.populateRow(0, "111");
        grid.populateRow(1, "111");
        grid.populateRow(2, "111");

        Cell wondev = grid.getCell(1,1);
        List<Cell> moveCells = grid.getAccessibleCellsFor(wondev);

        Assert.assertThat(moveCells.size(), is(8));
    }

    @Test
    public void aCellWillNotSeeBannedCellsAsAccessible() throws Exception {
        Grid grid = new Grid(3);
        grid.populateRow(0, "111");
        grid.populateRow(1, "111");
        grid.populateRow(2, "11.");

        Cell wondev = grid.getCell(1,1);
        List<Cell> moveCells = grid.getAccessibleCellsFor(wondev);

        Assert.assertThat(moveCells.size(), is(7));
    }

    @Test
    public void aWondevWillJumpOnTheNearestHighestCellNW() throws Exception {
        Grid grid = new Grid(3);
        action = null;

        grid.populateRow(0, ".2.");
        grid.populateRow(1, "111");
        grid.populateRow(2, ".1.");

        wondev2 = grid.getCell(2,1);
        wondev1 = grid.getCell(0,2);


        Assert.assertThat(takeAction(grid, wondev2), is("MOVE&BUILD 1 NW SW"));
    }

    @Test
    public void directionWillBeProperlyChosenNW() throws Exception {
        wondev1 = new Cell(1,1,'1');
        Action action = new Action(wondev1);

        action.setBuildCell(new Cell(1,1,'1'));
        action.setLandingCell(new Cell(0,0,'1'));

        Assert.assertThat(action.execute(), is("MOVE&BUILD 0 NW SE"));
    }

    @Test
    public void directionWillBeProperlyChosenSE() throws Exception {
        wondev1 = new Cell(0,0,'1');
        Action action = new Action(wondev1);

        action.setBuildCell(new Cell(0,0,'1'));
        action.setLandingCell(new Cell(1,1,'1'));

        Assert.assertThat(action.execute(), is("MOVE&BUILD 0 SE NW"));
    }

    @Test
    public void directionWillBeProperlyChosenSW() throws Exception {
        wondev1 = new Cell(1,0,'1');
        Action action = new Action(wondev1);

        action.setBuildCell(new Cell(1,0,'1'));
        action.setLandingCell(new Cell(0,1,'1'));

        Assert.assertThat(action.execute(), is("MOVE&BUILD 0 SW NE"));
    }

    @Test
    public void ifOnALowerLevelIWillBuildUpCurrentCell() throws Exception {
        Grid grid = new Grid(3);
        action = null;

        grid.populateRow(0, ".1.");
        grid.populateRow(1, "111");
        grid.populateRow(2, ".2.");

        wondev2 = grid.getCell(0,1);
        wondev1 = grid.getCell(2,2);

        Assert.assertThat(takeAction(grid, wondev2), is("MOVE&BUILD 1 SE NW"));
    }

    @Test
    public void ifOnAMaxLevelItWillBuildTheLowestCell() throws Exception {
        Grid grid = new Grid(3);

        grid.populateRow(0, ".3.");
        grid.populateRow(1, "313");
        grid.populateRow(2, ".2.");

        wondev1 = grid.getCell(0,1);
        wondev2 = new Cell(4,4, '3');

        takeAction(grid, wondev1);

        Assert.assertThat(action.execute(), is("MOVE&BUILD 0 NE S"));
    }

    @Test
    public void aThreeLevelCellWillNotBeDestroyed() throws Exception {
        Grid grid = new Grid(3);
        action = null;

        grid.populateRow(0, ".3.");
        grid.populateRow(1, "020");
        grid.populateRow(2, ".2.");

        wondev2 = grid.getCell(1,2);
        wondev1 = new Cell(4,4, '3');

        takeAction(grid, wondev2);

        Assert.assertThat(action.execute(), is("MOVE&BUILD 1 N S"));
    }

    @Test
    public void aThreeLevelCellWillNotBeDestroyedOnSide() throws Exception {
        Grid grid = new Grid(3);
        action = null;

        grid.populateRow(0, "300");
        grid.populateRow(1, "200");
        grid.populateRow(2, "200");

        wondev1 = grid.getCell(0,2);
        wondev2 = new Cell(4,4, '3');

        Assert.assertThat(takeAction(grid, wondev1), is("MOVE&BUILD 0 N S"));
    }

    @Test
    public void aCellWillNotBlockItself() throws Exception {
        Grid grid = new Grid(3);
        action = null;

        grid.populateRow(0, "3.0");
        grid.populateRow(1, "3.0");
        grid.populateRow(2, "200");

        wondev1 = grid.getCell(0,1);
        wondev2 = new Cell(4,4, '3');

        Assert.assertThat(takeAction(grid, wondev1), is("MOVE&BUILD 0 S E"));
    }
}
