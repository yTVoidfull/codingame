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
                grid.fillOutRow(i, row);
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

        private Map<Integer, Character[]> data = new HashMap<Integer, Character[]>();
        private final int size;

        public Grid(int size){
            this.size = size;
            for (int i = 0; i < size; i++){
                Character[] chars = new Character[size];
                data.put(i,chars);
            }
        }

        public void fillOutRow(int lineNr, String lineData){
            char[] splitData = lineData.toCharArray();
            Character[] row = data.get(lineNr);

            for(int i = 0; i < size; i++){
                row[i] = splitData[i];
            }
        }
    }

    public static class Cell{
        private int row;
        private int column;
        private Value value;

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


        public char getCaracter() {
            return caracter;
        }

        public void setCaracter(char caracter) {
            this.caracter = caracter;
        }
    }
}