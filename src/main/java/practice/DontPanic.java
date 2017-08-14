package practice;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DontPanic {

    static List<Locatable> elevators = new ArrayList<Locatable>();

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int nbFloors = in.nextInt(); // number of floors
        int width = in.nextInt(); // width of the area
        int nbRounds = in.nextInt(); // maximum number of rounds
        int exitFloor = in.nextInt(); // floor on which the exit is found
        int exitPos = in.nextInt(); // position of the exit on its floor
        int nbTotalClones = in.nextInt(); // number of generated clones
        int nbAdditionalElevators = in.nextInt(); // ignore (always zero)
        int nbElevators = in.nextInt(); // number of elevators
        for (int i = 0; i < nbElevators; i++) {
            int elevatorFloor = in.nextInt(); // floor on which this elevator is found
            int elevatorPos = in.nextInt(); // position of the elevator on its floor
            elevators.add(new Locatable(elevatorFloor, elevatorPos));
        }

        // game loop
        while (true) {
            int cloneFloor = in.nextInt(); // floor of the leading clone
            int clonePos = in.nextInt(); // position of the leading clone on its floor
            String direction = in.next(); // direction of the leading clone: LEFT or RIGHT

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            System.out.println(playGame(cloneFloor, clonePos, direction, exitFloor, exitPos));
        }
    }

    public static String playGame(int cloneFloor, int clonePos, String direction, int exitFloor, int exitPos) {
        String decision = null;
        System.err.println("cloneFloor " + cloneFloor);
        System.err.println("clonePos " + clonePos);
        System.err.println("direction " + direction);
        System.err.println("exitFloor " + exitFloor);
        System.err.println("exitPos " + exitPos);

        if (cloneFloor == exitFloor) {
            if (clonePos > exitPos && direction.equals("RIGHT")
                    || clonePos < exitPos && direction.equals("LEFT")) {
                return "BLOCK";
            }
        } else {
            for (Locatable elevator : elevators) {
                if (elevator.getFloor() == cloneFloor) {
                    System.err.println("elevatorFloor " + elevator.getFloor());
                    System.err.println("elevatorPos " + elevator.getPos());
                    if (clonePos > elevator.getPos() && direction.equals("RIGHT")
                            || clonePos < elevator.getPos() && direction.equals("LEFT")) {
                        return "BLOCK";
                    }
                }
            }
        }
        return "WAIT"; // action: WAIT or BLOCK
    }


    public static class Locatable {

        private int floor;
        private int pos;

        public Locatable(int floor, int pos) {
            this.floor = floor;
            this.pos = pos;
        }

        public int getFloor() {
            return floor;
        }

        public int getPos() {
            return pos;
        }
    }

}
