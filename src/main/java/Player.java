import java.util.*;
import java.io.*;
import java.math.*;

import static java.lang.Math.sqrt;

/**
 * Send your busters out into the fog to trap ghosts and bring them home!
 **/
class Player {

    public static final int RADAR_DISTANCE = 2200;
    public static final int MAX_BUSTABLE_DISTANCE = 1760;
    public static final int MIN_BUSTABLE_DISTANCE = 900;
    public static final int MAX_STUNABLE_DISTANCE = 1760;
    public static final int BUSTER_MAX_MOVE = 800;
    public static final int GHOST_MAX_MOVE = 400;
    public static final int CHECKPOINT_DISTANCE = 500;
    public static final int MIN_DISTANCE_FROM_SIDE = 0;
    public static final int MAX_X = 16000;
    public static final int MAX_Y = 9000;
    public static final Agent MIDDLE = new Agent(8000, 4500, 0, 0,0);
    public static final Agent SW_MID = new Agent(1500, 7500, 0,0,0);
    public static final Agent NE_MID = new Agent(14500, 1500, 0,0,0);

    public static Agent base;
    public static Agent enemyBase;
    public static List<Agent> checkpoints = new ArrayList<Agent>();
    public static Map<Integer, Integer> stunTimer = new HashMap<Integer, Integer>();
    public static List<Agent> ghosts;
    public static List<Agent> enemies;
    public static List<Buster> busters;
    public static List<Agent> stunnedEnemies;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int bustersPerPlayer = in.nextInt(); // the amount of busters you control
        int ghostCount = in.nextInt(); // the amount of ghosts on the map
        int myTeamId = in.nextInt(); // if this is 0, your base is on the top left of the map, if it is one, on the bottom right

        if (myTeamId == 0) {
            base = new Agent(0.0, 0.0, 0, 1, 0);
            enemyBase = new Agent(16000.0, 9000.0, 1, -1, 0);
        } else {
            base = new Agent(16000.0, 9000.0, 1, -1, 0);
            enemyBase = new Agent(0.0, 0.0, 0, 1, 0);
        }

        defineCheckpoints();

        // game loop
        while (true) {
            int entities = in.nextInt(); // the number of busters and ghosts visible to you

            ghosts = new ArrayList<Agent>();
            enemies = new ArrayList<Agent>();
            busters = new ArrayList<Buster>();

            for (int i = 0; i < entities; i++) {
                int entityId = in.nextInt(); // buster id or ghost id
                int x = in.nextInt();
                int y = in.nextInt(); // position of this buster / ghost
                int entityType = in.nextInt(); // the team id if it is a buster, -1 if it is a ghost.
                int state = in.nextInt(); // For busters: 0=idle, 1=carrying a ghost. For ghosts: remaining stamina points.
                int value = in.nextInt(); // For busters: Ghost id being carried/busted or number of turns left when stunned. For ghosts: number of busters attempting to trap this ghost.


                if (entityType == -1) {
                    Agent ghost = new Agent(x, y, entityId, value, state);
                    ghosts.add(ghost);
                }
                else if (entityType == base.getId()) {
                    busters.add(new Buster(x, y, entityId, value, state));
                }
                else {
                    if(state == 2){
                        stunnedEnemies.add(new Agent(x, y, entityId, value, state));
                    }else {
                        enemies.add(new Agent(x, y, entityId, value, state));
                    }
                }

            }

            for (int i = 0; i < bustersPerPlayer; i++) {

                // Write an action using System.out.println()
                // To debug: System.err.println("Debug messages...");


                // MOVE x y | BUST id | RELEASE | STUN id | RADAR | EJECT x y
                System.out.println(decideAction(busters.get(i)));
            }
        }
    }

    private static String decideAction(Buster buster) {
        getRidOfPassedCheckpointsNear(buster);
        if(buster.isScout()){
            return decideScoutingAction(buster);
        }
        else{
            return decideFighterAction(buster);
        }
    }

    private static String decideFighterAction(Buster buster) {
        return buster.scout();
    }

    private static String decideScoutingAction(Buster buster) {
        return buster.scout();
    }

    private static void getRidOfPassedCheckpointsNear(Buster buster){
        List<Agent> tbd = new ArrayList<Agent>();
        for(Agent point : checkpoints){
            if(buster.distanceTo(point) < RADAR_DISTANCE){
                tbd.add(point);
            }
        }
        checkpoints.removeAll(tbd);
        System.err.println("checkpoints remaining " + checkpoints.size());
    }

    public static void defineCheckpoints(){
        checkpoints.add(MIDDLE);
        checkpoints.add(NE_MID);
        checkpoints.add(SW_MID);
        for(int dx = MIN_DISTANCE_FROM_SIDE; dx < MAX_X + 1; dx += CHECKPOINT_DISTANCE){
            for(int dy = MIN_DISTANCE_FROM_SIDE; dy < MAX_Y + 1; dy += CHECKPOINT_DISTANCE){
                checkpoints.add(new Agent(dx, dy, 0,0,0));
            }
        }
    }

    static class Buster extends Agent{

        public Buster(double x, double y, int id, int state, int value) {
            super(x, y, id, state, value);
        }

        public String moveTo(Agent agent) {
            if(distanceTo(agent) > 800){
                return moveTo(getPointAtMaxMovementDistanceTo(agent));
            }
            return String.format("MOVE %.0f %.0f", agent.X(), agent.Y());
        }

        private Agent getPointAtMaxMovementDistanceTo(Agent agent) {
            double slope = slopeWith(agent);
            double endX;
            double endY;

            if(X() < agent.X()){
                endX = X() + BUSTER_MAX_MOVE / (1 + slope * slope);
            }else {
                endX = X() - BUSTER_MAX_MOVE / (1 + slope * slope);
            }

            endY = Y() - X() * slope + slope * endX;

            if (slope == Double.POSITIVE_INFINITY || slope == Double.NEGATIVE_INFINITY){
                if(Y() < agent.Y()){
                    endY = Y() + BUSTER_MAX_MOVE;
                }
                else{
                    endY = Y() - BUSTER_MAX_MOVE;
                }
            }

            return new Agent(endX, endY, 0,0,0);
        }

        public String stun(Agent agent) {
            return String.format("STUN %s", agent.getId());
        }

        public String bust(Agent ghost) {
            return String.format("BUST %s", ghost.getId());
        }

        int getInTeamId(){
            return getId() % 4;
        }   

        boolean isScout(){
            return getInTeamId() == 0;
        }

        public String scout() {
            if(checkpoints.size() > busters.size()){
                return moveTo(checkpoints.get(getInTeamId()));
            }
            else if(checkpoints.size() > 0){
                return moveTo(checkpoints.get(0));
            }
            else {
                return moveTo(enemyBase);
            }
        }
    }

    static class Agent {

        private int value;
        private double x;
        private double y;
        private int id;
        private int state;

        public Agent(double x, double y, int id, int state, int value) {
            this.x = x;
            this.y = y;
            this.id = id;
            this.state = state;
            this.value = value;
        }

        double X() {
            return x;
        }

        double Y() {
            return y;
        }

        double distanceTo(Agent agent) {
            return sqrt((X() - agent.X()) * (X() - agent.X()) + (Y() - agent.Y()) * (Y() - agent.Y()));
        }

        double slopeWith(Agent other){
            if(this.X() == other.X()) {
                if(this.X() < other.X()){
                    return Double.POSITIVE_INFINITY;
                }
                else {
                    return Double.NEGATIVE_INFINITY;
                }
            }
            return (other.Y() - this.Y()) / (other.X() - this.X());
        }

        double xDistance(Agent loc) {
            return loc.X() - X();
        }

        double yDistance(Agent loc) {
            return loc.Y() - Y();
        }

        public int getId() {
            return id;
        }
    }

}