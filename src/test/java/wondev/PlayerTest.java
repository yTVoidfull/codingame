package wondev;

import org.junit.Test;

/**
 * Created by alplesca on 8/9/2017.
 */
public class PlayerTest {

    @Test
    public void thereIsACell() throws Exception {
        Player.Cell cell = new Player.Cell();
    }

    @Test
    public void aCellHasRow() throws Exception {
        Player.Cell cell = new Player.Cell();
        cell.getRow();
    }

    @Test
    public void aCellHasColumn() throws Exception {
        Player.Cell cell = new Player.Cell();
        cell.getColumn();
    }

    @Test
    public void aCellHasValue() throws Exception {
        Player.Cell cell = new Player.Cell();
        cell.getValue();
    }

    @Test
    public void name() throws Exception {

    }
}
