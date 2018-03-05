package meanmax;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.sqrt;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        Looter reaper = null;
        Looter destroyer = null;
        Looter doof = null;
        List<Wreck> wrecks;
        List<Looter> enemyReapers;
        List<Looter> enemyDestroyers;
        List<Looter> enemyDoofs;
        List<Tanker> tankers;
        int betterPlayerId;

        // game loop
        while (true) {
            wrecks = new ArrayList<>();
            tankers = new ArrayList<>();
            enemyReapers = new ArrayList<>();
            enemyDestroyers = new ArrayList<>();
            enemyDoofs = new ArrayList<>();


            int myScore = in.nextInt();
            int enemyScore1 = in.nextInt();
            int enemyScore2 = in.nextInt();
            int myRage = in.nextInt();
            int enemyRage1 = in.nextInt();
            int enemyRage2 = in.nextInt();

            betterPlayerId = enemyScore1 > enemyScore2 ? 1 : 2;

            int unitCount = in.nextInt();
            for (int i = 0; i < unitCount; i++) {
                int unitId = in.nextInt();
                int unitType = in.nextInt();
                int player = in.nextInt();
                float mass = in.nextFloat();
                int radius = in.nextInt();
                int x = in.nextInt();
                int y = in.nextInt();
                int vx = in.nextInt();
                int vy = in.nextInt();
                int extra = in.nextInt();
                int extra2 = in.nextInt();

                if(unitType == 0 && player == 0){
                    reaper = new Looter(x, y, vx, vy, mass, radius);
                }else if(unitType == 0){
                    enemyReapers.add(new Looter(x, y,vx, vy, mass, radius));
                } else if(unitType == 1 && player == 0){
                    destroyer = new Looter(x, y, vx, vy, mass, radius);
                }else if(unitType == 1){
                    enemyDestroyers.add(new Looter(x, y, vx,vy, mass, radius));
                } else if(unitType == 2 && player == 0){
                    doof = new Looter(x, y, vx, vy, mass, radius);
                }else if(unitType == 2){
                    enemyDoofs.add(new Looter(x, y, vx, vy, mass, radius));
                } else if(unitType == 3){
                    tankers.add(new Tanker(x, y, radius, extra, extra2));
                } else if(unitType == 4){
                    wrecks.add(new Wreck(x, y, extra, radius));
                }
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            // move to closest wreck or to fastest enemy destroyer
            System.out.println(decideReaperAction(reaper, wrecks, enemyDestroyers, destroyer, tankers, enemyDoofs));

            // move to closest tanker
            System.out.println(decideDestroyerAction(destroyer, tankers, wrecks, reaper, enemyReapers, myRage, betterPlayerId));

            System.out.println(decideDoofAction(doof, enemyReapers, reaper, wrecks, myRage, betterPlayerId));
        }
    }

    private static String decideReaperAction(Looter reaper,
                                             List<Wreck> wrecks,
                                             List<Looter> enemyDestroyers,
                                             Looter destroyer,
                                             List<Tanker> tankers,
                                             List<Looter> enemyDoofs){
        if(wrecks.size() > 0){
            Wreck best = wrecks.get(0);
            double maxCoefficient = 0;
            for(Wreck w : wrecks){
                double c = w.getQuantity() / (reaper.distanceTo(w) / 200);
                if(c > maxCoefficient){
                    best = w;
                    maxCoefficient = c;
                }
            }
            return reaper.moveTo(best);
        }
        if(tankers.size() > 0){
            Tanker closest = tankers.get(0);
            for(Tanker w : tankers){
                if(reaper.distanceTo(w) < reaper.distanceTo(closest)){
                    closest = w;
                }
            }
            return destroyer.moveTo(closest);
        }
        return "WAIT";
    }

    private static String decideDestroyerAction(Looter destroyer,
                                                List<Tanker> tankers,
                                                List<Wreck> wrecks,
                                                Looter reaper,
                                                List<Looter> enemyReapers,
                                                int rage,
                                                int betterPlayerId){
        if(rage > 60){
            for(Wreck w : wrecks){
                Looter enemy = enemyReapers.get(betterPlayerId -1);
                if(w.distanceTo(enemy) < 600
                    && destroyer.distanceTo(enemy) < 2000){
                    return destroyer.skillAt(enemy.getX() + 400, enemy.getY() + 400);
                }
            }
        }
        if(tankers.size() > 0){
            Tanker closest = tankers.get(0);
            for(Tanker w : tankers){
                if(destroyer.distanceTo(w) < destroyer.distanceTo(closest)
                    && w.distanceTo(new Wreck(0,0,0,0)) < 3000
                    && w.getQuantity() > 1){
                    closest = w;
                }
            }
            return destroyer.moveTo(closest);
        }
        return "WAIT";
    }

    private static String decideDoofAction(Looter doof,
                                           List<Looter> enemyReapers,
                                           Looter reaper,
                                           List<Wreck> wrecks,
                                           int rage,
                                           int betterPlayerId){
        if(rage > 60){
            for(Wreck w : wrecks){
                Looter enemy = enemyReapers.get(betterPlayerId -1);
                if(w.distanceTo(enemy) < 800
                    && doof.distanceTo(w) < 2000
                    && reaper.distanceTo(w) > 2000){
                    return doof.skillAt(w.getX(), w.getY());
                }
            }
        }

        Looter e1 = enemyReapers.get(0);
        Looter e2 = enemyReapers.get(1);

        if(e1.distanceTo(doof) > e2.distanceTo(doof)){
            return doof.moveToMaxSpeed(e2);
        }
        else{
            return doof.moveToMaxSpeed(e1);
        }
    }

    static class Looter extends Agent {

        private final int vx;
        private final int vy;
        private final float mass;

        public Looter(int x, int y, int vx, int vy, float mass, int radius) {
            super(x, y, radius);
            this.vx = vx;
            this.vy = vy;
            this.mass = mass;
        }


        public int getThrottle(Agent l) {
            int t = (int) (distanceTo(l) * mass);
            return t > 300 ? 300 : t;
        }

        public String moveTo(Agent l) {
            return l.getX() + " " + l.getY() + " " + getThrottle(l);
        }

        public String moveAwayFrom(Agent a){
            int x;
            int y;
            if(a.getX() > getX()){
                x = getX() - 400;
            }else {
                x = getX() + 400;
            }

            if(a.getY() > getY()){
                y = getY() - 400;
            }else {
                y = getY() + 400;
            }
            return x + " " + y + getThrottle(new Wreck(x, y, 0,0));
        }

        public String moveToMaxSpeed(Agent l){
            return l.getX() + " " + l.getY() + " " + 300;
        }

        public int getSpeed(){
            return (int) sqrt(vx*vx + vy*vy);
        }

        public String skillAt(int x, int y){
            return "SKILL " + x + " " + y;
        }

    }

    public static class Wreck extends Agent {
        private int quantity;

        public Wreck(int x, int y, int quantity, int radius) {
            super(x, y, radius);
            this.quantity = quantity;
        }

        public int getQuantity() {
            return quantity;
        }
    }

    public static class Tanker extends Agent {
        private int quantity;
        private int capacity;


        public Tanker(int x, int y, int radius, int quantity, int capacity) {
            super(x, y, radius);
            this.quantity = quantity;
            this.capacity = capacity;
        }

        public int getQuantity() {
            return quantity;
        }

        public int getCapacity() {
            return capacity;
        }
    }

    public static abstract class Agent {
        private int x;
        private int y;
        private int radius;

        public Agent(int x, int y, int radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        public int getX(){
            return x;
        }

        public int getY(){
            return y;
        }

        public double distanceTo(Agent w) {
            return sqrt((getX() - w.getX()) * (getX() - w.getX()) + (getY()-w.getY())* (getY() - w.getY()) );
        }

        public boolean intersectsWithAnyWreck(List<Wreck> others){
            for(Agent a : others){
                if(a.distanceTo(this) < a.getRadius()){
                    return true;
                }
            }
            return false;
        }

        public boolean intersectsWithAnyLooter(List<Looter> others){
            for(Agent a : others){
                if(a.distanceTo(this) < a.getRadius()){
                    return true;
                }
            }
            return false;
        }

        public int getRadius() {
            return radius;
        }
    }
}