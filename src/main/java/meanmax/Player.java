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
        List<Wreck> tankers;
        int round = 0;

        // game loop
        while (true) {
            wrecks = new ArrayList<>();
            tankers = new ArrayList<>();
            enemyReapers = new ArrayList<>();
            enemyDestroyers = new ArrayList<>();


            int myScore = in.nextInt();
            int enemyScore1 = in.nextInt();
            int enemyScore2 = in.nextInt();
            int myRage = in.nextInt();
            int enemyRage1 = in.nextInt();
            int enemyRage2 = in.nextInt();
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
                }if(unitType == 0){
                    enemyReapers.add(new Looter(x, y,vx, vy, mass, radius));
                }
                else if(unitType == 1 && player == 0){
                    destroyer = new Looter(x, y, vx, vy, mass, radius);
                }else if(unitType == 1){
                    enemyDestroyers.add(new Looter(x, y, vx,vy, mass, radius));
                }
                else if(unitType == 2 && player == 0){
                    doof = new Looter(x, y, vx, vy, mass, radius);
                }
                else if(unitType == 3){
                    tankers.add(new Wreck(x, y, extra, radius));
                }
                else if(unitType == 4){
                    wrecks.add(new Wreck(x, y, extra, radius));
                }
            }

            round ++;

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            // move to closest wreck or to fastest enemy destroyer
            System.out.println(decideReaperAction(reaper, wrecks, enemyDestroyers));

            // move to closest tanker
            System.out.println(decideDestroyerAction(destroyer, tankers));

            System.out.println(decideDoofAction(doof, enemyReapers, round, wrecks));
        }
    }

    private static String decideReaperAction(Looter reaper, List<Wreck> wrecks, List<Looter> enemyDestroyers){
        if(wrecks.size() > 0){
            Wreck closest = wrecks.get(0);
            for(Wreck w : wrecks){
                if(reaper.distanceTo(w) < reaper.distanceTo(closest)
                        && w.getQuantity() > 0){
                    closest = w;
                }
            }
            return reaper.moveTo(closest);
        }else {
            Looter d1 = enemyDestroyers.get(0);
            Looter d2 = enemyDestroyers.get(1);
            if(d1.getSpeed() > d2.getSpeed()){
                return reaper.moveTo(d1);
            }
            else{
                return reaper.moveTo(d2);
            }
        }
    }

    private static String decideDestroyerAction(Looter destroyer, List<Wreck> tankers){
        if(tankers.size() > 0){
            Wreck closest = tankers.get(0);
            for(Wreck w : tankers){
                if(destroyer.distanceTo(w) < destroyer.distanceTo(closest)){
                    closest = w;
                }
            }
            return destroyer.moveTo(closest);
        }else {
            return "WAIT";
        }
    }

    private static String decideDoofAction(Looter doof, List<Looter> enemyReapers, int round, List<Wreck> wrecks){
        Looter e1 = enemyReapers.get(0);
        Looter e2 = enemyReapers.get(1);
        if(round % 2 == 0){
            Looter en1 = enemyReapers.get(0);
            Looter en2 = enemyReapers.get(1);

            for(Wreck w : wrecks){
                if(en1.distanceTo(w) < 1000){
                    return doof.skillAt(en1.getX() + 450, en1.getY() + 450);
                }else if(en2.distanceTo(w) < 1000){
                    return doof.skillAt(en2.getX() + 450, en2.getY() + 450);
                }
            }
        }
        else if(e1.getSpeed() > e2.getSpeed()){
            return doof.moveToMaxSpeed(e1);
        }
        else{
            return doof.moveToMaxSpeed(e2);
        }
        return "WAIT";
    }

    static class Looter extends Locatable {

        private final int vx;
        private final int vy;
        private final float mass;

        public Looter(int x, int y, int vx, int vy, float mass, int radius) {
            super(x, y);
            this.vx = vx;
            this.vy = vy;
            this.mass = mass;
        }


        public int getThrottle(Locatable l) {
            int t = (int) (distanceTo(l) * mass);
            return t > 300 ? 300 : t;
        }

        public String moveTo(Locatable l) {
            return l.getX() + " " + l.getY() + " " + getThrottle(l);
        }

        public String moveToMaxSpeed(Locatable l){
            return l.getX() + " " + l.getY() + " " + 300;
        }

        public int getSpeed(){
            return (int) sqrt(vx*vx + vy*vy);
        }

        public String skillAt(int x, int y){
            return "SKILL " + x + " " + y;
        }

    }

    public static class Wreck extends Locatable{
        private int quantity;

        public Wreck(int x, int y, int quantity, int radius) {
            super(x, y);
            this.quantity = quantity;
        }

        public int getQuantity() {
            return quantity;
        }
    }

    public static abstract class Locatable{
        private int x;
        private int y;

        public Locatable(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX(){
            return x;
        }

        public int getY(){
            return y;
        }

        public double distanceTo(Locatable w) {
            return sqrt((getX() - w.getX()) * (getX() - w.getX()) + (getY()-w.getY())* (getY() - w.getY()) );
        }
    }

    public static class Sensor extends Locatable{

        public Sensor(int x, int y) {
            super(x, y);
        }

        @Override
        public int getX() {
            return 0;
        }

        @Override
        public int getY() {
            return 0;
        }

        @Override
        public boolean equals(Object other) {
            if(!(other instanceof Sensor)){
                return false;
            }
            Sensor s = (Sensor) other;
            return getX() == s.getX()
                    && getY() == s.getY();
        }
    }

    public static class CircularMatrix {

        private int unitDistance;

        private Sensor[][] sensors;

        public CircularMatrix(int radius, int unitDistance) {
            this.unitDistance = unitDistance;
            sensors = createCircularMatrixSensors(radius, unitDistance);
        }

        private Sensor[][] createCircularMatrixSensors(int radius, int unitDistance) {
            int sensorsInOneRadius = radius / unitDistance;
            Sensor[][] sensors = new Sensor[2 * sensorsInOneRadius + 1][2 * sensorsInOneRadius + 1];

            for(int x = -sensorsInOneRadius; x < sensorsInOneRadius + 1; x ++){
                for(int y = -sensorsInOneRadius; y < sensorsInOneRadius + 1; y ++){
                    Sensor s = new Sensor(x * unitDistance, y * unitDistance);
                    if(new Sensor(0,0).distanceTo(s) <= 2* radius){
                        sensors[x + sensorsInOneRadius][y + sensorsInOneRadius] = s;
                    }
                }
            }
            return sensors;
        }

        public Sensor getSensor(int row, int column){
            return sensors[row][column];
        }

        public void registerLooter(Looter l){

        }

        private void triggerSensorsInArea(int x, int y, int radius, Locatable l){
            int adjX = x - x % unitDistance;
            int adjY = y - y % unitDistance;
            
        }

    }
}