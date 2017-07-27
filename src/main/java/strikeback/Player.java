package strikeback;

import java.util.*;

import static java.lang.Math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    // constants for controlling the movement of first lap
    public static final int MIN_THRUST = 50;
    public static final int DIST_TO_SLOWDOWN = 3000;
    public static final int DIST_TO_ACCELERATE = 1500;
    public static final int DIST_TO_BOOST = 6000;

    // constants for controlling the movement of consecutive laps
    public static final double MIN_CURB_ANGLE = Math.PI / 6;
    public static final double MAX_CURB_ANGLE = Math.PI / 4;
    public static final int CURB_DISTANCE = 2000;
    public static final int MIN_DIST_BETWEEN_CHECKPOINTS = 2000;
    public static final int MAX_DIST_BETWEEN_CHECKPOINTS = 6000;

    // additional agents for consecutive laps
    public static List<Locatable> checkpoints = new ArrayList<Locatable>();
    public static int checkpointsPassed = 0;
    public static boolean isFirstLap = true;

    // basic agents
    public static Locatable nextCheckpoint;
    public static Pod pod;
    public static int thrust = 100;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            int x = in.nextInt();
            int y = in.nextInt();
            int nextCheckpointX = in.nextInt(); // x position of the next check point
            int nextCheckpointY = in.nextInt(); // y position of the next check point
            int nextCheckpointDist = in.nextInt(); // distance to the next checkpoint
            int nextCheckpointAngle = in.nextInt(); // angle between your pod orientation and the direction of the next checkpoint
            int opponentX = in.nextInt();
            int opponentY = in.nextInt();

            nextCheckpoint = new Locatable(nextCheckpointX, nextCheckpointY);
            pod = new Pod(x, y);

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // You have to output the target position
            // followed by the power (0 <= thrust <= 100)
            // i.e.: "x y thrust"

            // adding any new checkpoint
            if (!elementIsPresentInList(checkpoints, nextCheckpoint)) {
                checkpoints.add(nextCheckpoint);
            } else if (checkpoints.size() > 1 && nextCheckpoint.equals(checkpoints.get(0))) {
                isFirstLap = false;
            }

            System.out.println(decideAction(nextCheckpointAngle, nextCheckpointDist));
        }
    }

    private static boolean elementIsPresentInList(List<Locatable> list, Locatable element) {
        for (Locatable el : list) {
            if (el.equals(element)) return true;
        }
        return false;
    }

    private static String decideAction(int nextCheckpointAngle, int nextCheckpointDist) {
        thrust = getThrustBasedOnAngleAndDistance(nextCheckpointAngle, nextCheckpointDist);
        String boost = decideIfBoostIsNeeded(nextCheckpointAngle, nextCheckpointDist);
        if (boost != null) {
            return pod.boostToNextCheckPoint();
        } else if (isFirstLap || pod.distanceTo(nextCheckpoint) < MIN_DIST_BETWEEN_CHECKPOINTS) {
            return pod.moveCarefullyToNextCheckPoint();
        } else {
            return pod.moveLikeAProToNextCheckpoint();
        }
    }

    private static int getThrustBasedOnAngleAndDistance(int angle, int dist) {
        int absoluteAngle = abs(angle);
        int distProportion = 100 - MIN_THRUST;
        int angleProportion = 100 - MIN_THRUST;
        if (dist < DIST_TO_SLOWDOWN && dist > DIST_TO_ACCELERATE) {
            distProportion = (DIST_TO_SLOWDOWN - dist) * (100 - MIN_THRUST) / DIST_TO_SLOWDOWN;
            angleProportion = absoluteAngle * (100 - MIN_THRUST) / 180;
        }
        return abs(angleProportion) * distProportion / (100 - MIN_THRUST) + MIN_THRUST;
    }

    private static String decideIfBoostIsNeeded(int angle, int dist) {
        if (angle == 0 && dist > DIST_TO_BOOST) return "BOOST";
        else return null;
    }

    static class Pod extends Locatable {

        public Pod(double x, double y) {
            super(x, y);
        }

        String moveCarefullyToNextCheckPoint() {
            return String.format("%.0f %.0f %s safe", nextCheckpoint.getX(), nextCheckpoint.getY(), thrust);
        }

        String moveLikeAProToNextCheckpoint() {
            Locatable furtherNextCheckpoint;
            int index = checkpoints.indexOf(nextCheckpoint);
            if (index == checkpoints.size() - 1) {
                furtherNextCheckpoint = checkpoints.get(0);
            } else {
                furtherNextCheckpoint = checkpoints.get(index + 1);
            }

            double xDist = furtherNextCheckpoint.xDistance(nextCheckpoint);
            double yDist = furtherNextCheckpoint.yDistance(nextCheckpoint);

            double m = nextCheckpoint.slopeWith(furtherNextCheckpoint);
            boolean isAboveCheckpointLine = false;

            if(this.getY() - m*this.getX() > nextCheckpoint.getY() - m * nextCheckpoint.getX()){
                isAboveCheckpointLine = true;
            }




            double endX = 0;
            double endY = 0;

            return String.format("%.0f %.0f %s pro", endX, endY, 100);
        }

        String boostToNextCheckPoint() {
            return String.format("%.0f %.0f BOOST", nextCheckpoint.getX(), nextCheckpoint.getY());
        }
    }

    static class Locatable {

        private double x;
        private double y;

        public Locatable(double x, double y) {
            this.x = x;
            this.y = y;
        }

        double getX() {
            return x;
        }

        double getY() {
            return y;
        }

        double distanceTo(Locatable locatable) {
            return sqrt((getX() - locatable.getX()) * (getX() - locatable.getX()) + (getY() - locatable.getY()) * (getY() - locatable.getY()));
        }

        double slopeWith(Locatable other){
            if(this.getX() == other.getX()) {
                return 0;
            }
            return (this.getY() - other.getY()) / (this.getX() - other.getX());
        }

        int getQuadrantFrom(Locatable centre){
            if(xDistance(centre) > 0 && yDistance(centre) > 0){
                return 1;
            }else if(xDistance(centre) < 0 && yDistance(centre) > 0){
                return 2;
            }
            else if(xDistance(centre) < 0 && yDistance(centre) < 0){
                return 3;
            }else {
                return 4;
            }
        }

        double xDistance(Locatable loc) {
            return getX() - loc.getX();
        }

        double yDistance(Locatable loc) {
            return getY() - loc.getY();
        }

        public boolean equals(Locatable other) {
            return this.getX() == other.getX() && this.getY() == other.getY();
        }
    }

}