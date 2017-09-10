package wondev;

import javafx.scene.control.CellBuilder;

import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static Grid grid;
    public static Wondev wondev1;
    public static Wondev wondev2;
    public static Cell enemy1;
    public static Cell enemy2;
    public static Action action;
    public static final Value MAX_VALUE = Value.FOUR;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int size = in.nextInt();
        int unitsPerPlayer = in.nextInt();

        // game loop
        while (true) {
            // every round a new grid
            grid = new Grid(size);
            action = null;

            for (int i = 0; i < size; i++) {
                String row = in.next();
                grid.populateRow(i, row);
            }
            for (int i = 0; i < unitsPerPlayer; i++) {
                int unitX = in.nextInt();
                int unitY = in.nextInt();
                if(i == 0) wondev1 = new Wondev(grid.getCell(unitX, unitY), 0);
                else wondev2 = new Wondev(grid.getCell(unitX,unitY),1);
            }
            for (int i = 0; i < unitsPerPlayer; i++) {
                int otherX = in.nextInt();
                int otherY = in.nextInt();
                if(i == 0) enemy1 = grid.getCell(otherX, otherY);
                else enemy2 = grid.getCell(otherX, otherY);
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

            // to be defined
        }
    }

    public static class Grid {

        private List<List<Cell>> rows = new ArrayList<>();
        private final int size;

        public Grid(int size) {
            this.size = size;
            for(int i = 0; i < size; i++){
                rows.add(new ArrayList<>());
            }
        }

        public void populateRow(int lineNr, String rowData) {
            char[] splitData = rowData.toCharArray();

            for (int i = 0; i < size; i++) {
                rows.get(lineNr).add(new Cell(i, lineNr, splitData[i]));
            }
        }

        public Cell getCell(int x, int y) {
            try{
                return rows.get(y).get(x);
            }catch (Exception e){
                return null;
            }
        }

        public Grid(List<List<Cell>> rows) {
            this.rows = rows;
            this.size = rows.size();
        }

        public List<Cell> getAdjacentCellsFor(Cell cell){
            List<Cell> output = new ArrayList<Cell>();

            for(Direction direction : Direction.values()){
                Cell c = this.getCell(cell.getX() + direction.getDeltaX(), cell.getY() + direction.getDeltaY());
                if (c != null && c.getValue().getNr() < MAX_VALUE.getNr()) {
                    output.add(c);
                }
            }

            return output;
        }
    }

    public static class BreadthFirstGrid extends Grid {

        public BreadthFirstGrid(Grid grid, Wondev wondev1) {
            super(grid.rows);

        }

        private void processGrid(){




        }
    }

    public static class Cell {
        private int y;
        private int x;
        private Value value;
        private int height;
        private int minimumDistance;
        private int connections;
        private int connectionPoints;

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

        public int getConnections() {
            return connections;
        }

        public Value getValue() {
            return value;
        }

        public int getHeight() {
            return height;
        }

        public int getMinimumDistance() {
            return minimumDistance;
        }

        public int getConnectionPoints() {
            return connectionPoints;
        }

        public void setMinimumDistance(int minimumDistance) {
            this.minimumDistance = minimumDistance;
        }

        public void setConnections(int connections) {
            this.connections = connections;
        }

        public void setConnectionPoints(int connectionPoints) {
            this.connectionPoints = connectionPoints;
        }

        public boolean canMoveTo(Cell other) {
            return  this.getHeight() >= other.getHeight() - 1
                    && other.getValue() != Value.NONE
                    && !other.equals(enemy1)
                    && !other.equals(enemy2)
                    && !(this.equals(wondev1) && other.equals(wondev2))
                    && !(this.equals(wondev2) && other.equals(wondev1));
        }

        public boolean canBuildTo(Cell other){
            return  other.getValue() != Value.NONE
                    && !other.equals(enemy1)
                    && !other.equals(enemy2);
        }

        public Direction directionTo(Cell toCell) {
            int fromX = this.getX();
            int fromY = this.getY();
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
    }

    public static class Wondev{

        private BreadthFirstGrid breadthFirstGrid;
        private Cell baseCell;
        private int id;
        public final String MOVE_AND_BUILD = "MOVE&BUILD %s %s %s";
        public final String PUSH_AND_BUILD = "PUSH&BUILD %s %s %s";

        private Cell moveCell;
        private Cell buildCell;

        public  Wondev(Cell baseCell, int id){
            this.baseCell = baseCell;
            this.id = id;
        }

        public Wondev setMoveCell(Cell moveCell) {
            this.moveCell = moveCell;
            return this;
        }

        public Wondev setBuildCell(Cell buildCell) {
            this.buildCell = buildCell;
            return this;
        }

        public void setBreadthFirstGrid(BreadthFirstGrid breadthFirstProcessedGrid) {
            this.breadthFirstGrid = breadthFirstProcessedGrid;
        }

        public String execute(Action action){
            if(action == Action.MOVE_ACTION){
                Direction moveDirection = baseCell.directionTo(moveCell);
                Direction buildDirection = moveCell.directionTo(buildCell);
                return String.format(MOVE_AND_BUILD, id, moveDirection.getString(), buildDirection.getString());
            }else {
                Direction pushDirection = baseCell.directionTo(buildCell);
                Direction landingDirection = buildCell.directionTo(moveCell);
                return String.format(PUSH_AND_BUILD, id, pushDirection.getString(), landingDirection.getString());
            }
        }
    }

    public enum Action{
        MOVE_ACTION,
        PUSH_ACTION;
    }

    public enum Value {
        NONE('.', 0),
        ZERO('0', 0),
        ONE('1', 1),
        TWO('2', 2),
        THREE('3', 3),
        FOUR('4', 4);

        private char caracter;
        private int nr;

        Value(char c, int nr) {
            caracter = c;
            this.nr = nr;
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

        public int getNr() {
            return nr;
        }
    }

    public enum Direction {
        NORTH("N", 0, -1, 0),
        NORTH_EAST("NE", 1, -1, 1),
        EAST("E", 1, 0, 2),
        SOUTH_EAST("SE", 1, 1, 3),
        SOUTH("S", 0, 1, 4),
        SOUTH_WEST("SW", -1, 1, 5),
        WEST("W", -1, 0, 6),
        NORTH_WEST("NW", -1, -1, 7)
        ;

        private String string;
        private int deltaX;
        private int deltaY;
        private int id;

        Direction(String str, int dX, int dY, int id) {
            string = str;
            this.deltaX = dX;
            this.deltaY = dY;
            this.id = id;
        }

        public static Direction fromString(String name) {
            for (Direction dir : values()) {
                if (dir.getString().equals(name)) {
                    return dir;
                }
            }
            throw new IllegalArgumentException("Wrong argument");
        }

        public static Direction fromId(int id) {
            for (Direction dir : values()) {
                if (dir.getId() == id) {
                    return dir;
                }
            }
            throw new IllegalArgumentException("Wrong argument");
        }

        public int getDeltaX(){
            return deltaX;
        }

        public int getDeltaY() {
            return deltaY;
        }

        public Direction getLeftDirection(){
            int leftId = getId() - 1;
            if(leftId == -1) {
                return fromId(7);
            }else {
                return fromId(leftId);
            }
        }

        public Direction getRightDirection(){
            int rightId = getId() + 1;
            if(rightId == 8) {
                return fromId(0);
            }else {
                return fromId(rightId);
            }
        }

        public String getString() {
            return string;
        }

        public int getId() {
            return id;
        }
    }
}