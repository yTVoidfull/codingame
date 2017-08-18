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
    public static final int MAX_HEIGHT = 4;

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
                if(i == 0) wondev1 = grid.getCell(unitX, unitY);
                else wondev2 = grid.getCell(unitX,unitY);
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

        private List<List<Cell>> cells = new ArrayList<>();
        private final int size;

        public Grid(int size) {
            this.size = size;
            for(int i = 0; i < size; i++){
                cells.add(new ArrayList<>());
            }
        }

        public void populateRow(int lineNr, String rowData) {
            char[] splitData = rowData.toCharArray();

            for (int i = 0; i < size; i++) {
                cells.get(lineNr).add(new Cell(i, lineNr, splitData[i]));
            }
        }

        public Cell getCell(int x, int y) {
            try{
                return cells.get(y).get(x);
            }catch (Exception e){
                return null;
            }
        }

        public Cell getCellWith(Direction direction, Cell fromCell){
            return getCell(fromCell.getX() + direction.getdX(), fromCell.getY() + direction.getdY());
        }

        public List<Cell> getCellsToMoveFrom(Cell cell) {
            List<Cell> output = new ArrayList<Cell>();

            for(Direction direction : Direction.values()){
                Cell c = this.getCell(cell.getX() + direction.getdX(), cell.getY() + direction.getdY());
                if (cell.canMoveTo(c)) {
                    output.add(c);
                }
            }

            return output;
        }

        public List<Cell> getCellsToBuildFrom(Cell cell) {
            List<Cell> output = new ArrayList<Cell>();

            for(Direction direction : Direction.values()){
                Cell c = this.getCell(cell.getX() + direction.getdX(), cell.getY() + direction.getdY());
                if (cell.canBuildTo(c)) {
                    output.add(c);
                }
            }

            return output;
        }

        public Cell getHighestCell(List<Cell> accessibleCells){
            Cell highest = accessibleCells.get(0);
            for(Cell c : accessibleCells){
                if(c.getHeight() > highest.getHeight()){
                    highest = c;
                }
            }
            return highest;
        }

        public Cell getLowestCell(List<Cell> accessibleCells){
            Cell lowest = accessibleCells.get(0);
            for(Cell c : accessibleCells){
                if(c.getHeight() < lowest.getHeight()){
                    lowest = c;
                }
            }
            return lowest;
        }

        public Cell getSameValueCellFor(Cell cell, List<Cell> accessibleCells){
            for(Cell c : accessibleCells){
                if(c.getHeight() == cell.getHeight()){
                    return c;
                }
            }
            return null;
        }

        public Cell getHighestCellWithValueUpTo(List<Cell> accessibleCells, Value value) {
            Cell h = getLowestCell(accessibleCells);

            for (Cell c : accessibleCells) {
                if (c.getHeight() > h.getHeight()
                        && c.getValue().getNr() <= value.getNr()) {
                    h = c;
                }
            }
            return h;
        }

        public boolean enemyHasAccessTo(Cell cell) {
            for(int i = -1; i < 2; i++){
                for(int j = -1; j < 2;j ++){
                    Cell c = this.getCell(cell.getX() + i, cell.getY() + j);
                    if(c != null
                            && cell.getHeight() -1 <= c.getHeight()
                            && (c.equals(enemy1) || c.equals(enemy2))){
                        return true;
                    }
                }
            }
            return false;
        }

        public List<Cell> getCellsToPushFrom(Cell wondev, Cell enemy) {
            List<Cell> output = new ArrayList<>();
            Direction direction = wondev.directionTo(enemy);
            Cell potential = grid.getCellWith(direction, enemy);
            if(potential != null && enemy.canMoveTo(potential) && !potential.equals(otherWondev(wondev))){
                output.add(potential);
            }

            potential = grid.getCellWith(direction.getLeftDirection(), enemy);
            if(potential != null && enemy.canMoveTo(potential) && !potential.equals(otherWondev(wondev))){
                output.add(potential);
            }

            potential = grid.getCellWith(direction.getRightDirection(), enemy);
            if(potential != null && enemy.canMoveTo(potential) && !potential.equals(otherWondev(wondev))){
                output.add(potential);
            }

            return output;
        }

        public List<Cell> getEnemiesNearTo(Cell cell) {
            List<Cell> enemiesNearby = new ArrayList<>();
            for(Direction direction : Direction.values()){
                Cell c = grid.getCellWith(direction, cell);
                if(c != null &&(c.equals(enemy1) || c.equals(enemy2))){
                    enemiesNearby.add(c);
                }
            }
            return enemiesNearby;
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

        public boolean canMoveTo(Cell other) {
            return  this.getHeight() >= other.getHeight() - 1
                    && other.getValue() != Value.NONE
                    && other.getHeight() < MAX_HEIGHT
                    && !other.equals(enemy1)
                    && !other.equals(enemy2)
                    && !(this.equals(wondev1) && other.equals(wondev2))
                    && !(this.equals(wondev2) && other.equals(wondev1));
        }

        public boolean canBuildTo(Cell other){
            return  other.getValue() != Value.NONE
                    && other.getHeight() < MAX_HEIGHT
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

        public List<Cell> getAdjacentCells(){
            List<Cell> output = new ArrayList<Cell>();

            for(Direction direction : Direction.values()){
                Cell c = grid.getCell(getX() + direction.getdX(), getY() + direction.getdY());
                if (c != null) {
                    output.add(c);
                }
            }

            return output;
        }
    }

    public class Wondev{
        private Grid grid;
        private Cell baseCell;
        private int id;
        public static final String MOVE_AND_BUILD = "MOVE&BUILD %s %s %s";
        public static final String PUSH_AND_BUILD = "PUSH&BUILD %s %s %s";

        private Cell moveCell;
        private Cell buildCell;

        public Wondev(Cell baseCell, Grid grid){
            this.baseCell = baseCell;
            this.grid = grid;
        }

        public Wondev setMoveCell(Cell moveCell) {
            this.moveCell = moveCell;
            return this;
        }

        public Wondev setBuildCell(Cell buildCell) {
            this.buildCell = buildCell;
            return this;
        }

        public String executeAs(Action action){
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

        private void processGrid(){

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
        private int dX;
        private int dY;
        private int id;

        Direction(String str, int dX, int dY, int id) {
            string = str;
            this.dX = dX;
            this.dY = dY;
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

        public int getdX(){
            return dX;
        }

        public int getdY() {
            return dY;
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