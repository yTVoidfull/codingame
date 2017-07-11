

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

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int bustersPerPlayer = in.nextInt(); // the amount of busters you control
        int ghostCount = in.nextInt(); // the amount of ghosts on the map
        int myTeamId = in.nextInt(); // if this is 0, your base is on the top left of the map, if it is one, on the bottom right


        if(myTeamId == 0) base = new Locatable(0.0, 0.0, 1,0);
        else base = new Locatable(16000.0, 9000.0, -1, 0);

        // game loop
        while (true) {

            List<Buster> busters = new ArrayList<Buster>();
            List<Ghost> ghosts = new ArrayList<Ghost>();
            List<Locatable> enemies = new ArrayList<Locatable>();

            int entities = in.nextInt(); // the number of busters and ghosts visible to you
            for (int i = 0; i < entities; i++) {
                int entityId = in.nextInt(); // buster id or ghost id
                int x = in.nextInt();
                int y = in.nextInt(); // position of this buster / ghost
                int entityType = in.nextInt(); // the team id if it is a buster, -1 if it is a ghost.
                int state = in.nextInt(); // For busters: 0=idle, 1=carrying a ghost. For ghosts: remaining stamina points.
                int value = in.nextInt(); // For busters: Ghost id being carried/busted or number of turns left when stunned. For ghosts: number of busters attempting to trap this ghost.

                if(entityType == -1) ghosts.add(new Ghost(x,y,entityId,value));
                else if(entityType == base.getValue()) busters.add(new Buster(x,y,entityId,value));
                else enemies.add(new Locatable(x,y,entityId,value));
            }


            for (int i = 0; i < bustersPerPlayer; i++) {

                // Write an action using System.out.println()
                // To debug: System.err.println("Debug messages...");
                System.out.println( busters.get(i).scout());
            }
        }
    }

    static String moveBusterToCheckpoint(Buster buster, Locatable checkpoint){
        return buster.moveTo(checkpoint);
    }

    static String decideToBustGhost(Buster buster, Ghost ghost){
        return buster.bust(ghost);
    }

    static String decideToStunEnemy(Buster buster, Locatable enemy){
        return buster.stun(enemy);
    }

    static String deciceToPushGhost(Buster buster, Ghost ghost){
        return buster.moveTo(ghost);
    }

    static String decideToReleaseGhost(Buster buster, Locatable base){
        if(buster.distanceTo(base) < 1600) return buster.release();
        return buster.moveTo(base);
    }

    static class Buster extends Locatable {

        Buster(double x, double y, int id, int val){
            super(x, y, id, val);
        }

        String moveTo(Locatable locatable) {

            if(distanceTo(locatable) < 800) return String.format("MOVE %.0f %.0f", locatable.getX(), locatable.getY());

            double xDist = getX() -locatable.getX();
            double yDist = getY() - locatable.getY();

            double alpha;
            double endX;
            double endY;

            if(xDist == 0) xDist = 0.00001;
            alpha = abs(atan(yDist / xDist));

            if(xDist > 0)
                endX = getX() - cos(alpha) * 800;
            else
                endX = getX() + cos(alpha) * 800;
            if(yDist > 0)
                endY = getY() - sin(alpha) * 800;
            else
                endY = getY() + sin(alpha) * 800;

            return String.format("MOVE %.0f %.0f", endX, endY);
        }

        String scout(){
            double endX = getX();
            double endY = getY();
            int outerY = 7445;
            int outerX = 14445;
            int innerX = 1555;
            int innerY = 1555;

            if(getId() % 2 == 1){

                if(getY() >= outerY && getX() <= innerX){
                    endX = innerX;
                    endY = getY() - 800;
                }
                else if(getY() <= innerY && getX() < outerX){
                    endX = getX() + 800 * base.getId();
                    endY = innerY;
                }else if(getY() < outerY && getX() >= outerX){
                    endX = outerX;
                    endY = getY() + 800;
                }else if(getY() >= outerY && getX() <= outerX){
                    endY = outerY;
                    endX = getX() - 800 * base.getId();
                }else{
                    endX = innerX;
                    endY = innerY;
                }
            }

            return moveTo(new Locatable(endX, endY, 1,1));
        }

        String push(Locatable locatable){
            double xDist = base.getX() -locatable.getX();
            double yDist = base.getY() - locatable.getY();

            double alpha;
            double endX;
            double endY;

            if(xDist == 0) xDist = 0.00001;
            alpha = abs(atan(yDist / xDist));

            if(xDist > 0)
                endX = locatable.getX() - cos(alpha) * 1000;
            else
                endX = locatable.getX() + cos(alpha) * 1000;
            if(yDist > 0)
                endY = locatable.getY() - sin(alpha) * 1000;
            else
                endY = locatable.getY() + sin(alpha) * 1000;

            if (endX < 0) endX = 0;
            else if(endX > 16000) endX = 16000;

            if (endY < 0) endY = 0;
            else if(endY > 9000) endY = 9000;

            return String.format("MOVE %.0f %.0f", endX, endY);
        }

        String bust(Locatable locatable){
            return "BUST " + locatable.getId();
        }

        String stun(Locatable locatable){
            return "STUN " + locatable.getId();
        }

        String release(){
            return "RELEASE";
        }

    }

    static class Ghost extends Locatable {

        public Ghost(double x, double y, int id, int val) {
            super(x,y,id, val);
        }

    }

    static class Locatable {

        private double x;
        private double y;
        private int id;
        private int value;

        public Locatable(double x, double y, int id, int value) {
            this.x = x;
            this.y = y;
            this.id = id;
        }

        int getId(){
            return id;
        }

        double getX(){
            return x;
        }

        double getY(){
            return y;
        }

        double getValue(){return value;}

        double distanceTo(Locatable locatable){
            return sqrt((getX()-locatable.getX()) * (getX()-locatable.getX()) + (getX()-locatable.getY()) * (getX()-locatable.getY()));
        }
    }

}


