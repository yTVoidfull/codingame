package wondev;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;

/**
 * Created by alplesca on 8/9/2017.
 */
public class PlayerTest {

    @Test
    public void thereIsACell() throws Exception {
        Player.Cell cell = new Player.Cell(1,1,'1');
    }

    @Test
    public void aCellHasYCoordinate() throws Exception {
        Player.Cell cell = new Player.Cell(1,2,'1');
        cell.getY();
    }

    @Test
    public void aCellHasXCoordinate() throws Exception {
        Player.Cell cell = new Player.Cell(1,1,'1');
        cell.getX();
    }

    @Test
    public void aCellHasValue() throws Exception {
        Player.Cell cell = new Player.Cell(1,1,'1');
        cell.getValue();
    }

    @Test
    public void aCellHasHeight() throws Exception {
        Player.Cell cell = new Player.Cell(1,1,'1');
        int height = cell.getHeight();
    }

    @Test
    public void thereIsAGrid() throws Exception {
        Player.Grid grid = new Player.Grid(3);
    }

    @Test
    public void youCanPopulateRowsInGrid() throws Exception {
        Player.Grid grid = new Player.Grid(3);
        grid.populateRow(0, "12.");
    }

    @Test
    public void youCanGetCellsFromGrid() throws Exception {
        Player.Grid grid = new Player.Grid(3);
        grid.getCell(1, 1);
    }

    @Test
    public void thereIsAJumper() throws Exception {
        Player.Jumper jumper = new Player.Jumper(new Player.Cell(1,1, '2'),0);
    }

    @Test
    public void aJumperIsOnACell() throws Exception {
        Player.Jumper jumper = new Player.Jumper(new Player.Cell(1,1, '2'),0);
        Player.Cell currentCell = jumper.getCell();
    }

    @Test
    public void eachJumperHasAnAction() throws Exception {
        Player.Jumper jumper = new Player.Jumper(new Player.Cell(1,1, '2'),0);
        jumper.setAction(new Player.Action(jumper.getCell(), jumper.getId()));
    }

    @Test
    public void aJumperCanMoveAndBuild() throws Exception {
        Player.Jumper jumper = new Player.Jumper(new Player.Cell(1,1, '2'),0);
        Player.Action action = new Player.Action(jumper.getCell(), jumper.getId());
        action.setLandingCell(new Player.Cell(1,2,'2'));
        action.setBuildCell(new Player.Cell(1,1,'2'));
        jumper.setAction(action);
        Assert.assertThat(jumper.executeAction(), is("MOVE&BUILD 0 S N"));
    }

    @Test
    public void aCellHasAccessibleCellsOnTheGrid() throws Exception {
        Player.Grid grid = new Player.Grid(3);
        grid.populateRow(0, "11.");
        grid.populateRow(1, "11.");
        grid.populateRow(2, "11.");

        Player.Jumper jumper = new Player.Jumper(grid.getCell(1,1),0);
        List<Player.Cell> moveCells = grid.getAccessibleCellsFor(jumper.getCell());
    }

    @Test
    public void aJumperInTheCornerHasOnlyThreeAccessibleCells() throws Exception {
        Player.Grid grid = new Player.Grid(3);
        grid.populateRow(0, "111");
        grid.populateRow(1, "111");
        grid.populateRow(2, "111");

        Player.Jumper jumper = new Player.Jumper(grid.getCell(0,0),0);
        List<Player.Cell> moveCells = grid.getAccessibleCellsFor(jumper.getCell());

        Assert.assertThat(moveCells.size(), is(3));
    }

    @Test
    public void aJumperInTheMiddleHasEightAccessibleCells() throws Exception {
        Player.Grid grid = new Player.Grid(3);
        grid.populateRow(0, "111");
        grid.populateRow(1, "111");
        grid.populateRow(2, "111");

        Player.Jumper jumper = new Player.Jumper(grid.getCell(1,1),0);
        List<Player.Cell> moveCells = grid.getAccessibleCellsFor(jumper.getCell());

        Assert.assertThat(moveCells.size(), is(8));
    }

    @Test
    public void aJumperWillNotSeeBannedCellsAsAccessible() throws Exception {
        Player.Grid grid = new Player.Grid(3);
        grid.populateRow(0, "111");
        grid.populateRow(1, "111");
        grid.populateRow(2, "11.");

        Player.Jumper jumper = new Player.Jumper(grid.getCell(1,1),0);
        List<Player.Cell> moveCells = grid.getAccessibleCellsFor(jumper.getCell());

        Assert.assertThat(moveCells.size(), is(7));
    }

    @Test
    public void aJumperWillJumpOnTheNearestHighestCellNW() throws Exception {
        Player.Grid grid = new Player.Grid(3);
        grid.populateRow(0, ".2.");
        grid.populateRow(1, "111");
        grid.populateRow(2, ".1.");

        Player.Jumper jumper = new Player.Jumper(grid.getCell(2,1),0);
        List<Player.Cell> moveCells = grid.getAccessibleCellsFor(jumper.getCell());
        Player.takeAction(grid, jumper);

        Assert.assertThat(jumper.executeAction(), is("MOVE&BUILD 0 NW SW"));
    }

    @Test
    public void directionWillBeProperlyChosenNW() throws Exception {
        Player.Jumper jumper = new Player.Jumper(new Player.Cell(1,1,'1'), 0);
        Player.Action action = new Player.Action(jumper.getCell(), jumper.getId());

        action.setBuildCell(new Player.Cell(1,1,'1'));
        action.setLandingCell(new Player.Cell(0,0,'1'));
        jumper.setAction(action);

        Assert.assertThat(jumper.executeAction(), is("MOVE&BUILD 0 NW SE"));
    }

    @Test
    public void directionWillBeProperlyChosenSE() throws Exception {
        Player.Jumper jumper = new Player.Jumper(new Player.Cell(0,0,'1'), 0);
        Player.Action action = new Player.Action(jumper.getCell(), jumper.getId());

        action.setBuildCell(new Player.Cell(0,0,'1'));
        action.setLandingCell(new Player.Cell(1,1,'1'));
        jumper.setAction(action);

        Assert.assertThat(jumper.executeAction(), is("MOVE&BUILD 0 SE NW"));
    }

    @Test
    public void directionWillBeProperlyChosenSW() throws Exception {
        Player.Jumper jumper = new Player.Jumper(new Player.Cell(1,0,'1'), 0);
        Player.Action action = new Player.Action(jumper.getCell(), jumper.getId());

        action.setBuildCell(new Player.Cell(1,0,'1'));
        action.setLandingCell(new Player.Cell(0,1,'1'));
        jumper.setAction(action);

        Assert.assertThat(jumper.executeAction(), is("MOVE&BUILD 0 SW NE"));
    }

    @Test
    public void ifOnALowerLevelIWillBuildUpCurrentCell() throws Exception {
        Player.Grid grid = new Player.Grid(3);
        grid.populateRow(0, ".1.");
        grid.populateRow(1, "111");
        grid.populateRow(2, ".2.");

        Player.Jumper jumper = new Player.Jumper(grid.getCell(0,1),0);
        Player.takeAction(grid, jumper);

        Assert.assertThat(jumper.executeAction(), is("MOVE&BUILD 0 SE NW"));
    }

    @Test
    public void ifOnAMaxLevelItWillBuildTheLowestCell() throws Exception {
        Player.jumper1 = new Player.Jumper(new Player.Cell(4,4, '3'), 0);
        Player.jumper2 = new Player.Jumper(new Player.Cell(4,4, '3'), 1);
        Player.Grid grid = new Player.Grid(3);
        grid.populateRow(0, ".3.");
        grid.populateRow(1, "313");
        grid.populateRow(2, ".2.");

        Player.Jumper jumper = new Player.Jumper(grid.getCell(0,1),0);
        Player.takeAction(grid, jumper);

        Assert.assertThat(jumper.executeAction(), is("MOVE&BUILD 0 NE S"));
    }

    @Test
    public void aThreeLevelCellWillNotBeDestroyed() throws Exception {
        Player.jumper1 = new Player.Jumper(new Player.Cell(4,4, '3'), 0);
        Player.jumper2 = new Player.Jumper(new Player.Cell(4,4, '3'), 1);
        Player.Grid grid = new Player.Grid(3);
        grid.populateRow(0, ".3.");
        grid.populateRow(1, "020");
        grid.populateRow(2, ".2.");

        Player.Jumper jumper = new Player.Jumper(grid.getCell(1,2),0);
        Player.takeAction(grid, jumper);

        Assert.assertThat(jumper.executeAction(), is("MOVE&BUILD 0 N S"));
    }

    @Test
    public void aThreeLevelCellWillNotBeDestroyedOnSide() throws Exception {
        Player.jumper1 = new Player.Jumper(new Player.Cell(4,4, '3'), 0);
        Player.jumper2 = new Player.Jumper(new Player.Cell(4,4, '3'), 1);
        Player.Grid grid = new Player.Grid(3);
        grid.populateRow(0, "300");
        grid.populateRow(1, "200");
        grid.populateRow(2, "200");

        Player.Jumper jumper = new Player.Jumper(grid.getCell(0,2),0);
        Player.takeAction(grid, jumper);

        Assert.assertThat(jumper.executeAction(), is("MOVE&BUILD 0 N S"));
    }

    @Test
    public void aJumperWillNotBlockItself() throws Exception {
        Player.jumper1 = new Player.Jumper(new Player.Cell(4,4, '3'), 0);
        Player.jumper2 = new Player.Jumper(new Player.Cell(4,4, '3'), 1);
        Player.Grid grid = new Player.Grid(3);
        grid.populateRow(0, "3.0");
        grid.populateRow(1, "3.0");
        grid.populateRow(2, "200");

        Player.Jumper jumper = new Player.Jumper(grid.getCell(0,1),0);
        Player.takeAction(grid, jumper);

        Assert.assertThat(jumper.executeAction(), is("MOVE&BUILD 0 S E"));
    }
}
