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
    public void aCellHasRow() throws Exception {
        Player.Cell cell = new Player.Cell(1,2,'1');
        cell.getRow();
    }

    @Test
    public void aCellHasColumn() throws Exception {
        Player.Cell cell = new Player.Cell(1,1,'1');
        cell.getColumn();
    }

    @Test
    public void aCellHasValue() throws Exception {
        Player.Cell cell = new Player.Cell(1,1,'1');
        cell.getValue();
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
        Player.Jumper jumper = new Player.Jumper(new Player.Cell(1,1, '2'));
    }

    @Test
    public void aJumperIsOnACell() throws Exception {
        Player.Jumper jumper = new Player.Jumper(new Player.Cell(1,1, '2'));
        Player.Cell currentCell = jumper.getCell();
    }

    @Test
    public void eachJumperHasAnAction() throws Exception {
        Player.Jumper jumper = new Player.Jumper(new Player.Cell(1,1, '2'));
        jumper.setAction(new Player.Action());
    }

    @Test
    public void aJumperCanMoveAndBuild() throws Exception {
        Player.Jumper jumper = new Player.Jumper(new Player.Cell(1,1, '2'));
        Player.Action action = new Player.Action();
        action.setBuildDirection(Player.Direction.SOUTH);
        action.setJumpDirection(Player.Direction.NORTH);
        jumper.setAction(action);
        Assert.assertThat(jumper.executeAction(), is("MOVE&BUILD N S"));
    }

    @Test
    public void aJumperHasAccessibleCellsOnTheGrid() throws Exception {
        Player.Grid grid = new Player.Grid(3);
        grid.populateRow(0, "11.");
        grid.populateRow(1, "11.");
        grid.populateRow(2, "11.");

        Player.Jumper jumper = new Player.Jumper(grid.getCell(1,1));
        List<Player.Cell> moveCells = grid.getAccessibleCellsFor(jumper);
    }
}
