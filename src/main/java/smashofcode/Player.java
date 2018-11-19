package smashofcode;

import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            for (int i = 0; i < 8; i++) {
                int colorA = in.nextInt(); // color of the first block
                int colorB = in.nextInt(); // color of the attached block
            }
            int score1 = in.nextInt();
            for (int i = 0; i < 12; i++) {
                String row = in.next(); // One line of the map ('.' = empty, '0' = skull block, '1' to '5' = colored block)
            }
            int score2 = in.nextInt();
            for (int i = 0; i < 12; i++) {
                String row = in.next();
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println("0"); // "x": the column in which to drop your blocks
        }
    }

    public class Grid{

        GridNode[][] nodes;

        public Grid() {
            nodes = new GridNode[12][6];
        }

        public void setRow(int rowNr, String str){
            for(int i = 0; i < str.length(); i++){
                if(str.charAt(i) != '.'){
                    nodes[rowNr][i] = new GridNode((int)str.charAt(i) - 47);
                }
            }
        }

        public GridNode[] getRow(int i){
            return nodes[i];
        }

        public int getBestColumnFor(int colorOne, int colorTwo){
            return 1;
        }
    }

    public class GridNode {
        int color;

        public GridNode(int c) {
            color = c;
        }
    }
}
