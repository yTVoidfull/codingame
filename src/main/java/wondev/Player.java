package wondev;

import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static Grid grid;
    public static Cell wondev1;
    public static Cell wondev2;
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

            if(grid.getCellsToMoveFrom(wondev1).size() == 1
                    || grid.getCellsToMoveFrom(wondev2).size() == 0)System.out.println(takeAction(grid, wondev1));
            else System.out.println(takeAction(grid, wondev2));
        }
    }

    public static String takeAction(Grid grid, Cell wondev) {
        fightAction(grid, wondev);

        if(action == null){
            buildAction(grid, wondev);
        }

        return action.execute();
    }

    public static void fightAction(Grid grid, Cell wondev){
        List<Cell> enemiesNearby = grid.getEnemiesNearTo(wondev);
        if(enemiesNearby.size() > 0){
            for(Cell enemy : enemiesNearby){
                trapAction(grid, wondev, enemy);
            }
        }
        else if(grid.enemyHasAccessTo(wondev) && wondev.getValue() == Value.THREE){
            moveToHighestAndBuildPrevious(grid, wondev);
        }
    }

    public static void trapAction(Grid grid, Cell wondev, Cell enemy){
        List<Cell> enemyLandingCells = grid.getCellsToPushFrom(wondev, enemy);
        for(Cell landingCell : enemyLandingCells){
            if(grid.getCellsToMoveFrom(landingCell).size() == 0){
                PushAction pushAction = new PushAction(wondev);
                pushAction.setLandingCell(landingCell);
                pushAction.setPushCell(enemy);
                action = pushAction;
            }
        }
    }

    public static void moveToHighestAndBuildPrevious(Grid grid, Cell wondev){
        BuildAction buildAction = new BuildAction(wondev);

        Cell landingCell = grid.getHighestCell(grid.getCellsToMoveFrom(wondev));

        buildAction.setLandingCell(landingCell);
        buildAction.setBuildCell(grid.getCell(wondev.getX(), wondev.getY()));
        action = buildAction;
    }

    public static void buildAction(Grid grid, Cell wondev){
        if(wondev.getValue() == Value.THREE){
            moveToHighestClosestCellAndBuildLowest(grid, wondev);
        }else {
            moveToHighestAndBuildHighest(grid, wondev);
        }
    }

    public static void moveToHighestAndBuildHighest(Grid grid, Cell wondev) {
        BuildAction buildAction = new BuildAction(wondev);

        Cell landingCell = grid.getHighestCell(grid.getCellsToMoveFrom(wondev));

        setWondev(wondev, landingCell);

        Cell buildCell = grid.getHighestCellWithValueUpTo(grid.getCellsToBuildFrom(landingCell), landingCell.getValue());

        if(buildCell.getValue() == Value.THREE && landingCell.getValue() != Value.THREE){
            buildCell = grid.getSameValueCellFor(landingCell, grid.getCellsToBuildFrom(landingCell));
        }
        if(buildCell == null){
            buildCell = grid.getLowestCell(grid.getCellsToBuildFrom(landingCell));
        }

        buildAction.setLandingCell(landingCell);
        buildAction.setBuildCell(buildCell);

        action = buildAction;
    }

    public static void moveToHighestClosestCellAndBuildLowest(Grid grid, Cell wondev){
        BuildAction buildAction = new BuildAction(wondev);
        Cell landingCell = grid.getHighestCell(grid.getCellsToMoveFrom(wondev));

        List<Cell> accessibleToHighest = grid.getCellsToBuildFrom(landingCell);
        if(accessibleToHighest.size() == 1
                && accessibleToHighest.get(0).getValue() == Value.THREE
                || accessibleToHighest.size() == 0){
            landingCell = grid.getHighestCellWithValueUpTo(grid.getCellsToMoveFrom(wondev), Value.TWO);
        }

        setWondev(wondev, landingCell);

        Cell buildCell = grid.getLowestCell(grid.getCellsToBuildFrom(landingCell));

        buildAction.setLandingCell(landingCell);
        buildAction.setBuildCell(buildCell);

        action = buildAction;
    }

    private static void setWondev(Cell wondev, Cell landingCell) {
        if(wondev.equals(wondev1)){
            wondev1 = landingCell;
        }else {
            wondev2 = landingCell;
        }
    }

    // helper methods for dealing with two wondev cells

    private static Cell otherWondev(Cell wondev) {
        if(wondev.equals(wondev1)){
            return wondev2;
        }else {
            return wondev1;
        }
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
            return other != null
                    && this.getHeight() >= other.getHeight() - 1
                    && other.getValue() != Value.NONE
                    && other.getHeight() < MAX_HEIGHT
                    && !other.equals(enemy1)
                    && !other.equals(enemy2)
                    && !(this.equals(wondev1) && other.equals(wondev2))
                    && !(this.equals(wondev2) && other.equals(wondev1))
                    && !other.equals(otherWondev(this));
        }

        public boolean canBuildTo(Cell other){
            return other != null
                    && other.getValue() != Value.NONE
                    && other.getHeight() < MAX_HEIGHT
                    && !other.equals(enemy1)
                    && !other.equals(enemy2)
                    && !other.equals(otherWondev(this));
        }

        private Direction directionTo(Cell toCell) {
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

    public static class BuildAction implements   Action {

        public static final String MOVE_AND_BUILD = "MOVE&BUILD %s %s %s";
        private Cell landingCell;
        private Cell buildCell;
        private Cell baseCell;
        private int id;

        public Direction moveDirection;
        public Direction buildDirection;

        public BuildAction(Cell cell) {
            baseCell = cell;

            if(cell.equals(wondev1))
            {
                this.id = 0;
            }else {
                id = 1;
            }
        }

        public void setLandingCell(Cell landingCell) {
            this.landingCell = landingCell;
        }

        public void setBuildCell(Cell buildCell) {
            this.buildCell = buildCell;

        }

        public String execute() {
            moveDirection = baseCell.directionTo(landingCell);
            buildDirection = landingCell.directionTo(buildCell);
            return String.format(MOVE_AND_BUILD, id, moveDirection.getString(), buildDirection.getString());
        }

        public Cell getBaseCell() {
            return baseCell;
        }
    }

    public static class PushAction implements   Action {

        public static final String PUSH_AND_BUILD = "PUSH&BUILD %s %s %s";
        private Cell pushCell;
        private Cell landingCell;
        private Cell baseCell;
        private int id;

        public Direction pushDirection;
        public Direction landingDirection;

        public PushAction(Cell cell) {
            baseCell = cell;

            if(cell.equals(wondev1))
            {
                this.id = 0;
            }else {
                id = 1;
            }
        }

        public void setLandingCell(Cell landingCell) {
            this.landingCell = landingCell;
        }

        public void setPushCell(Cell pushCell) {
            this.pushCell = pushCell;

        }

        public String execute() {
            pushDirection = baseCell.directionTo(pushCell);
            landingDirection = pushCell.directionTo(landingCell);
            return String.format(PUSH_AND_BUILD, id, pushDirection.getString(), landingDirection.getString());
        }

        public Cell getBaseCell() {
            return baseCell;
        }
    }

    public interface Action{
        String execute();
        Cell getBaseCell();
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