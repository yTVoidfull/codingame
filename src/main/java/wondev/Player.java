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
    public static Jumper jumper;
    public static Cell enemy;
    public static final int MAX_HEIGHT = 4;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int size = in.nextInt();
        int unitsPerPlayer = in.nextInt();

        // game loop
        while (true) {
            // every round a new grid
            grid = new Grid(size);

            for (int i = 0; i < size; i++) {
                String row = in.next();
                grid.populateRow(i, row);
            }
            for (int i = 0; i < unitsPerPlayer; i++) {
                int unitX = in.nextInt();
                int unitY = in.nextInt();
                jumper = new Jumper(grid.getCell(unitX, unitY),0);
            }
            for (int i = 0; i < unitsPerPlayer; i++) {
                int otherX = in.nextInt();
                int otherY = in.nextInt();
                enemy = grid.getCell(otherX, otherY);
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

    public static String takeAction(Grid grid, Jumper jumper) {
        moveToHighestClosestCellAndBuildLowest(grid, jumper);
        return jumper.executeAction();
    }

    public static void moveToHighestClosestCellAndBuildLowest(Grid grid, Jumper jumper){
        List<Cell> accessibleCells = grid.getAccessibleCellsFor(jumper);
        System.err.println("accessible " + accessibleCells.size());
        if(accessibleCells.size() == 0) return;

        Cell highest = accessibleCells.get(0);
        Cell lowest = accessibleCells.get(0);

        for(Cell c : accessibleCells){
            if(c.getHeight() > highest.getHeight()){
                highest = c;
            }else if(c.getHeight() < lowest.getHeight()){
                lowest = c;
            }
        }

        Action action = new Action(jumper);
        action.setLandingCell(highest);
        action.setBuildCell(lowest);

        jumper.setAction(action);
    }

    public static class Grid {

        private List<Cell> cells = new ArrayList<Cell>();
        private final int size;

        public Grid(int size) {
            this.size = size;
        }

        public void populateRow(int lineNr, String rowData) {
            char[] splitData = rowData.toCharArray();

            for (int i = 0; i < size; i++) {
                cells.add(new Cell(i, lineNr, splitData[i]));
            }
        }

        public Cell getCell(int x, int y) {
            for (Cell c : cells) {
                if (c.getX() == x && c.getY() == y) {
                    return c;
                }
            }
            return null;
        }

        public List<Cell> getAccessibleCellsFor(Jumper jumper) {
            List<Cell> output = new ArrayList<Cell>();
            Cell currentCell = jumper.getCell();


            for(int i = -1; i < 2; i++){
                for(int j = -1; j < 2;j ++){
                    Cell c = this.getCell(currentCell.getX() + i, currentCell.getY() + j);
                    if(c != null && currentCell.getHeight() >= c.getHeight() -1
                            && c.getValue() != Value.NONE
                            && !currentCell.equals(c)
                            && c.getHeight() < MAX_HEIGHT
                            && !c.equals(enemy)){
                        output.add(c);
                    }
                }
            }

            return output;
        }
    }

    public static class Cell {
        private int y;
        private int x;
        private Value value;
        private int height;

        public Cell(int x, int y, char value) {
            this.y = y;
            this.x = x;
            this.value = Value.valueOf(value);

            try {
                height = Integer.parseInt(String.valueOf(value));
            } catch (Exception e) {
                height = -1;
            }
        }

        public int getY() {
            return y;
        }

        public int getX() {
            return x;
        }

        public Value getValue() {
            return value;
        }

        public int getHeight() {
            return height;
        }
    }

    public static class Action {

        public static final String MOVE_AND_BUILD = "MOVE&BUILD %s %s %s";
        public Cell landingCell;
        public Cell buildCell;

        public Jumper jumper;
        public Direction moveDirection;
        public Direction buildDirection;

        public Action (Jumper jumper){
            this.jumper = jumper;
        }

        public Cell getLandingCell() {
            return landingCell;
        }

        public Cell getBuildCell() {
            return buildCell;
        }

        public void setLandingCell(Cell landingCell) {
            this.landingCell = landingCell;
        }

        public void setBuildCell(Cell buildCell) {
            this.buildCell = buildCell;

        }

        private Direction directionBetween(Cell fromCell, Cell toCell) {
            int fromX = fromCell.getX();
            int fromY = fromCell.getY();
            int toX = toCell.getX();
            int toY = toCell.getY();

            Direction direction = null;

            if(fromX < toX){
                if(fromY < toY){
                    direction = Direction.SOUTH_EAST;
                }else if(fromY > toY){
                    direction = Direction.NORTH_EAST;
                }else {
                    direction = Direction.EAST;
                }
            }else if(fromX > toX){
                if(fromY < toY){
                    direction = Direction.SOUTH_WEST;
                }else if(fromY > toY){
                    direction = Direction.NORTH_WEST;
                }else {
                    direction = Direction.WEST;
                }
            }else {
                if(fromY < toY){
                    direction = Direction.SOUTH;
                }else if(fromY > toY){
                    direction = Direction.NORTH;
                }
            }
            return direction;
        }

        public String execute() {
            moveDirection = directionBetween(jumper.getCell(), landingCell);
            buildDirection = directionBetween(landingCell, buildCell);
            return String.format(MOVE_AND_BUILD, jumper.getId(), moveDirection.getString(), buildDirection.getString());
        }
    }

    public static class Jumper {

        private Cell cell;
        private Action action;
        private int id;

        public Jumper(Cell cell, int id) {
            this.cell = cell;
            this.id = id;
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

        public int getId() {
            return id;
        }
    }

    public static enum Value {
        NONE('.'),
        ZERO('0'),
        ONE('1'),
        TWO('2'),
        THREE('3'),
        FOUR('4');

        private char caracter;

        Value(char c) {
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

    public static enum Direction {
        NORTH("N", 0),
        SOUTH("S", 7),
        WEST("W", 1),
        EAST("E", 6),
        NORTH_EAST("NE", 2),
        NORTH_WEST("NW", 5),
        SOUTH_EAST("SE", 3),
        SOUTH_WEST("SW", 4);

        private String string;
        private int nr;

        Direction(String str, int nr) {
            string = str;
            nr = nr;
        }

        public static Direction fromString(String name) {
            for (Direction dir : values()) {
                if (dir.getString().equals(name)) {
                    return dir;
                }
            }
            throw new IllegalArgumentException("Wrong argument");
        }

        public static Direction fromNumber(int nr) {
            for (Direction dir : values()) {
                if (dir.getNr() == nr) {
                    return dir;
                }
            }
            throw new IllegalArgumentException("Wrong argument");
        }

        public static Direction getInverse(Direction direction) {
            return Direction.fromNumber(7 - direction.getNr());
        }

        public String getString() {
            return string;
        }

        public int getNr() {
            return nr;
        }
    }
}