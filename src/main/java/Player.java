

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
    static Locatable[] checkpoints = new Locatable[8];
    static int current = 0;
    static int[] scoutingPath = new int[] {0,1,2,3};

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int bustersPerPlayer = in.nextInt(); // the amount of busters you control
        int ghostCount = in.nextInt(); // the amount of ghosts on the map
        int myTeamId = in.nextInt(); // if this is 0, your base is on the top left of the map, if it is one, on the bottom right


        if(myTeamId == 0) base = new Locatable(0.0, 0.0, 1,0,0);
        else base = new Locatable(16000.0, 9000.0, -1, 0,0);

        // game loop
        while (true) {

            List<Buster> busters = new ArrayList<Buster>();
            List<Locatable> ghosts = new ArrayList<Locatable>();
            List<Locatable> enemies = new ArrayList<Locatable>();
            defineCheckpoints();

            int entities = in.nextInt(); // the number of busters and ghosts visible to you
            for (int i = 0; i < entities; i++) {
                int entityId = in.nextInt(); // buster id or ghost id
                int x = in.nextInt();
                int y = in.nextInt(); // position of this buster / ghost
                int entityType = in.nextInt(); // the team id if it is a buster, -1 if it is a ghost.
                int state = in.nextInt(); // For busters: 0=idle, 1=carrying a ghost. For ghosts: remaining stamina points.
                int value = in.nextInt(); // For busters: Ghost id being carried/busted or number of turns left when stunned. For ghosts: number of busters attempting to trap this ghost.

                if(entityType == -1) ghosts.add(new Locatable(x,y,entityId,value, state));
                else if(entityType == base.getValue()) busters.add(new Buster(x,y,entityId,value, state));
                else enemies.add(new Locatable(x,y,entityId,value, state));
            }


            for (int i = 0; i < bustersPerPlayer; i++) {

                // Write an action using System.out.println()
                // To debug: System.err.println("Debug messages...");
                System.out.println(decideAction(busters.get(i),ghosts,enemies));
            }
        }
    }

    static String decideAction(Buster buster, List<Locatable> ghosts, List<Locatable> enemies){
        if(buster.getState() == 1) return buster.saveGhost();

        if(ghosts.size() > 0){
            for(Locatable g : ghosts){
                if(buster.distanceTo(g) > 900 && buster.distanceTo(g) < 1760) return buster.bust(g);
                else return buster.moveTo(new Locatable(g.getX() + 600, g.getY() + 600, 0, 0,0));
            }
        }else if(enemies.size() > 0){
            for(Locatable e : enemies){
                if(buster.distanceTo(e) < 1760) return buster.stun(e);
            }
        }
        return buster.scout();
    }

    static void defineCheckpoints(){
        checkpoints[0] = new Locatable(8000, 4500, 0, 0, 0);
        checkpoints[1] = new Locatable(14500, 1500, 0, 0, 0);
        checkpoints[2] = new Locatable(14500, 7500, 0, 0, 0);
        checkpoints[3] = new Locatable(1500, 7500, 0 ,0, 0);
    }

    static class Buster extends Locatable {

        Buster(double x, double y, int id, int val, int state){
            super(x, y, id, val, state);
        }

        String moveTo(Locatable locatable) {
            return String.format("MOVE %.0f %.0f", locatable.getX(), locatable.getY());
        }

        String saveGhost(){
            if(distanceTo(base) < 1600) return release();
            else return moveTo(base);
        }

        String scout(){
            if(current == scoutingPath.length) current = 0;
            Locatable checkpoint = checkpoints[scoutingPath[current]];
            if(distanceTo(checkpoint) < 400) {
                current ++;
            }
            System.err.println(getId() + " is moving to checkpoint " + checkpoint.getX() + " " + checkpoint.getY());
            return moveTo(checkpoint);
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

        int getState(){return state;}

        double distanceTo(Locatable locatable){
            return sqrt((getX()-locatable.getX()) * (getX()-locatable.getX()) + (getY()-locatable.getY()) * (getY()-locatable.getY()));
        }
    }

}