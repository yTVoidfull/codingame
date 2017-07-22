package refactor;

/**
 * Created by alplesca on 7/10/2017.
 */

import java.util.*;
import java.io.*;
import java.math.*;

import static java.lang.Math.*;

/**
 * Send your busters out into the fog to trap ghosts and bring them home!
 **/
class Player {

    static Locatable base;
    static Locatable enemyBase;
    static List<Buster> busters;
    static List<Locatable> ghosts;
    static List<Locatable> enemies;
    static final Locatable UPPER_ZERO = new Locatable(12000, 0, 0,0,0);
    static final Locatable LEFT_ZERO = new Locatable(0, 6000, 0,0,0);
    static final Locatable MIDDLE = new Locatable(8000, 4500, 0, 0,0);
    static boolean heardStatus[] = new boolean[]{false, false, false, false};
    static Locatable checkpoints[] = new Locatable[]{UPPER_ZERO, LEFT_ZERO, MIDDLE, MIDDLE};


    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int bustersPerPlayer = in.nextInt(); // the amount of busters you control
        int ghostCount = in.nextInt(); // the amount of ghosts on the map
        int myTeamId = in.nextInt(); // if this is 0, your base is on the top left of the map, if it is one, on the bottom right

        if (myTeamId == 0) {
            base = new Locatable(0.0, 0.0, 0, 1, 0);
            enemyBase = new Locatable(16000.0, 9000.0, 1, 1, 0);
        } else {
            base = new Locatable(16000.0, 9000.0, 1, 1, 0);
            enemyBase = new Locatable(0.0, 0.0, 0, -1, 0);
        }

        // game loop
        while (true) {

            busters = new ArrayList<Buster>();
            ghosts = new ArrayList<Locatable>();
            enemies = new ArrayList<Locatable>();

            int entities = in.nextInt(); // the number of busters and ghosts visible to you
            for (int i = 0; i < entities; i++) {
                int entityId = in.nextInt(); // buster id or ghost id
                int x = in.nextInt();
                int y = in.nextInt(); // position of this buster / ghost
                int entityType = in.nextInt(); // the team id if it is a buster, -1 if it is a ghost.
                int state = in.nextInt(); // For busters: 0=idle, 1=carrying a ghost. For ghosts: remaining stamina points.
                int value = in.nextInt(); // For busters: Ghost id being carried/busted or number of turns left when stunned. For ghosts: number of busters attempting to trap this ghost.

                if (entityType == -1) ghosts.add(new Locatable(x, y, entityId, value, state));
                else if (entityType == base.getId()) busters.add(new Buster(x, y, entityId, value, state));
                else enemies.add(new Locatable(x, y, entityId, value, state));
            }

            for (int i = 0; i < bustersPerPlayer; i++) {

                // Write an action using System.out.println()
                // To debug: System.err.println("Debug messages...");
                System.out.println(decideAction(busters.get(i)));
            }

        }
    }

    static String decideAction(Buster buster) {
        if(!buster.isReadyToHerd() || ghosts.size() == 0){
            return buster.scout();
        }
        return buster.herd();
    }

    static class Buster extends Locatable {

        Buster(double x, double y, int id, int val, int state) {
            super(x, y, id, val, state);
            setId(getId() % 4);
        }

        String moveTo(Locatable locatable) {
            double xDist = locatable.getX() - getX();
            double yDist = locatable.getY() - getY();

            double alpha;
            double endX;
            double endY;

            if (xDist == 0) xDist = 0.00001;
            alpha = abs(atan(yDist / xDist));

            if (xDist > 0)
                endX = getX() + cos(alpha) * 800;
            else
                endX = getX() - cos(alpha) * 800;
            if (yDist > 0)
                endY = getY() + sin(alpha) * 800;
            else
                endY = getY() - sin(alpha) * 800;

            return String.format("MOVE %.0f %.0f", endX, endY);
        }

        String scout() {
            Locatable checkpoint = getCheckpoint();
            if (distanceTo(checkpoint) < 400) {
                heardStatus[getId()] = true;
                checkpoints[getId()] = MIDDLE;
            }
            return moveTo(checkpoint) + " Scouting";
        }

        boolean isReadyToHerd(){
            return heardStatus[getId()];
        }

        String herd() {
            Locatable furthest = base;

            List<Locatable> ghostsNear = ghostsNearTo(this);

            for (Locatable ghost : ghostsNear) {
                if (ghost.distanceTo(base) > furthest.distanceTo(base)
                        && (ghost.getX() != 0 || ghost.getY() != 9000 || ghost.getX() == 16000 || ghost.getY() == 0)
                        && ghost.getValue() == 0) {
                    furthest = ghost;
                }
            }

            if(furthest.equals(base)) return scout();
            return String.format("MOVE %.0f %.0f Herding " + furthest.getId(), furthest.getX() + 160*base.getValue(), furthest.getY() + 90*base.getValue());
        }


        String release() {
            return "RELEASE";
        }

        public Locatable getCheckpoint() {
            return checkpoints[getId()];
        }
    }

    static class Locatable {

        private int state;
        private double x;
        private double y;
        private int id;
        private int value;

        public Locatable(double x, double y, int id, int value, int state) {
            this.x = x;
            this.y = y;
            this.id = id;
            this.state = state;
            this.value = value;
        }

        int getId() {
            return id;
        }

        void setId(int id){
            this.id = id;
        }

        double getX() {
            return x;
        }

        double getY() {
            return y;
        }

        int getValue() {
            return value;
        }

        int getState() {
            return state;
        }

        double distanceTo(Locatable locatable) {
            return sqrt((getX() - locatable.getX()) * (getX() - locatable.getX()) + (getY() - locatable.getY()) * (getY() - locatable.getY()));
        }
    }

    static List<Locatable> ghostsNearTo(Buster buster) {
        List<Locatable> output = new ArrayList<Locatable>();
        for (Locatable g : ghosts) {
            if (buster.distanceTo(g) < 2200) {
                output.add(g);
            }
        }
        return output;
    }

}