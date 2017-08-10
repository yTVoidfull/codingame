package wondev;

import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static Grid grid;
    public static Action prevAction;
    public static Jumper jumper;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int size = in.nextInt();
        int unitsPerPlayer = in.nextInt();
        grid = new Grid(size);

        // game loop
        while (true) {
            for (int i = 0; i < size; i++) {
                String row = in.next();
                grid.populateRow(i, row);
            }
            for (int i = 0; i < unitsPerPlayer; i++) {
                int unitX = in.nextInt();
                int unitY = in.nextInt();
                jumper = new Jumper(grid.getCell(unitY, unitX));
            }
            for (int i = 0; i < unitsPerPlayer; i++) {
                int otherX = in.nextInt();
                int otherY = in.nextInt();
            }
            int legalActions = in.nextInt();
            for (int i = 0; i < legalActions; i++) {
                String atype = in.next();
                int index = in.nextInt();
                String dir1 = in.next();
                String dir2 = in.next();
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println(takeAction(grid, jumper));
        }
    }

    private static String takeAction(Grid grid, Jumper jumper) {
        return null;
    }

    public static class Grid{

        private List<Cell> cells = new ArrayList<Cell>();
        private final int size;

        public Grid(int size){
            this.size = size;
        }

        public void populateRow(int lineNr, String rowData) {
            char[] splitData = rowData.toCharArray();

            for(int i = 0; i < size; i++){
                cells.add(new Cell(lineNr, i, splitData[i]));
            }
        }

        public Cell getCell(int row, int column) {
            for(Cell c : cells){
                if(c.getRow() == row && c.getColumn() == column){
                    return c;
                }
            }
            return null;
        }

        public List<Cell> getAccessibleCellsFor(Jumper jumper) {
            List<Cell> output = new ArrayList<Cell>();
            Cell currentCell = jumper.getCell();



            return output;
        }
    }

    public static class Cell{
        private int row;
        private int column;
        private Value value;

        public Cell(int row, int column, char value) {
            this.row = row;
            this.column = column;
            this.value = Value.valueOf(value);
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }

        public Value getValue() {
            return value;
        }
    }

    public static class Action{

        public static final String MOVE_AND_BUILD = "MOVE&BUILD %s %s";
        public Direction moveDirection;
        public Direction buildDirection;

        public void setJumpDirection(Direction jumpDirection) {
            this.moveDirection = moveDirection;
        }

        public void setBuildDirection(Direction buildDirection) {
            this.buildDirection = buildDirection;
        }

        public String execute() {
            return String.format(MOVE_AND_BUILD, moveDirection.getCaracter(), buildDirection.getCaracter());
        }
    }

    public static class Jumper {

        private Cell cell;
        private Action action;

        public Jumper(Cell cell) {
            this.cell = cell;
        }

        public Cell getCell() {
            return cell;
        }

        public void setAction(Action action) {
            this.action = action;
        }

        public String executeAction() {
            return action.execute();
        }
    }

    public static enum Value{
        NONE('.'),
        ONE('1'),
        TWO('2'),
        THREE('3');

        private char caracter;

        Value(char c){
            caracter = c;
        }

        public static Value valueOf(char name) {
            for (Value val : values()) {
                if (val.getCaracter() == name) {
                    return val;
                }
            }
            throw new IllegalArgumentException("Wrong argument");
        }

        public char getCaracter() {
            return caracter;
        }

    }

    public static enum Direction{
        NORTH('N',0),
        SOUTH('S',4),
        WEST('W',1),
        EAST('E',3);

        private char caracter;
        private int nr;

        Direction(char c, int nr){
            caracter = c;
            nr = nr;
        }

        public static Direction valueOf(char name) {
            for (Direction dir : values()) {
                if (dir.getCaracter() == name) {
                    return dir;
                }
            }
            throw new IllegalArgumentException("Wrong argument");
        }

        public static Direction valueOf(int nr) {
            for (Direction dir : values()) {
                if (dir.getNr() == nr) {
                    return dir;
                }
            }
            throw new IllegalArgumentException("Wrong argument");
        }

        public static Direction getInverse(Direction direction){
            return Direction.valueOf(4 - direction.getNr());
        }

        public char getCaracter() {
            return caracter;
        }

        public int getNr() {
            return nr;
        }
    }
}