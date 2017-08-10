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

            System.out.println("MOVE&BUILD 0 N S");
        }
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

        public void setCaracter(char caracter) {
            this.caracter = caracter;
        }
    }
}