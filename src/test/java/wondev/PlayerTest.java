package wondev;

import org.junit.Test;

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
}
