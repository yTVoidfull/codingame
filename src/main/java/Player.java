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
    public static final int HERDABLE_DISTANCE = 800;
    public static final int GHOST_MAX_MOVE = 400;
    public static final int MIN_DISTANCE_FROM_SIDE = 0;
    public static final int MAX_X = 16000;
    public static final int MAX_Y = 9000;

    public static Agent base;
    public static Agent enemyBase;
    public static List<Agent> checkpoints = new ArrayList<Agent>();
    public static Map<Integer, Integer> stunTimer = new HashMap<Integer, Integer>();
    public static List<Agent> catchGhosts;
    public static List<Agent> bustableGhosts;
    public static List<Agent> herdableGhosts;
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

            catchGhosts = new ArrayList<Agent>();
            bustableGhosts = new ArrayList<Agent>();
            herdableGhosts = new ArrayList<Agent>();
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
                    if(state == 0){
                        catchGhosts.add(ghost);
                    }else if(state < 16){
                        bustableGhosts.add(ghost);
                    }else {
                        herdableGhosts.add(ghost);
                    }
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

            setUpStunTimer(bustersPerPlayer);

            for (int i = 0; i < bustersPerPlayer; i++) {

                // Write an action using System.out.println()
                // To debug: System.err.println("Debug messages...");


                // MOVE x y | BUST id | RELEASE | STUN id | RADAR | EJECT x y
                System.out.println(decideAction(busters.get(i)));
            }
            decreaseStunTimer();
        }
    }

    //
    // Buster actions
    //
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
        if(herdableGhosts.size() > 0){
            return buster.herd();
        }
        return buster.scout();
    }

    private static String decideScoutingAction(Buster buster) {
        if(herdableGhosts.size() > 0){
            return buster.herd();
        }
        return buster.scout();
    }


    //
    // Utility methods
    //
    private static void setUpStunTimer(int nr){
        for(int i = 0; i < nr; i++){
            stunTimer.put(i, 0);
        }
    }

    private static void decreaseStunTimer(){
        for(int id : stunTimer.keySet()){
            int cooldown = stunTimer.get(id);
            if(cooldown > 0){
                stunTimer.put(id, cooldown-1);
            }
        }
    }

    private static void getRidOfPassedCheckpointsNear(Buster buster){
        List<Agent> tbd = new ArrayList<Agent>();
        for(Agent point : checkpoints){
            if(buster.distanceTo(point) < RADAR_DISTANCE){
                tbd.add(point);
            }
        }
        checkpoints.removeAll(tbd);
    }

    public static void defineCheckpoints(){
        if(base.getId() == 0){
            checkpoints.add(new Agent(9000, 5000, 0,0,0));
            checkpoints.add(new Agent(5500, 6800, 0, 0,0));
            checkpoints.add(new Agent(13000, 2200,0,0,0));
            checkpoints.add(new Agent(4000, 7500,0,0,0));
            checkpoints.add(new Agent(14500, 7500,0,0,0));
        }else {
            checkpoints.add(new Agent(13000, 2200, 0,0,0));
            checkpoints.add(new Agent(4000, 7500, 0, 0,0));
            checkpoints.add(new Agent(9000, 5000,0,0,0));
            checkpoints.add(new Agent(5500, 6800,0,0,0));
            checkpoints.add(new Agent(1500, 1500,0,0,0));
        }
    }

    //
    // Classes for agents
    //
    static class Buster extends Agent{

        public Buster(double x, double y, int id, int state, int value) {
            super(x, y, id, state, value);
        }

        public String moveTo(Agent agent) {
            if(distanceTo(agent) > BUSTER_MAX_MOVE){
                return moveTo(getPointAtMovementDistanceTo(agent, BUSTER_MAX_MOVE));
            };
            return String.format("MOVE %.0f %.0f", agent.X(), agent.Y());
        }

        private Agent getPointAtMovementDistanceTo(Agent agent, int dist) {
            double slope = slopeWith(agent);
            double endX;
            double endY;


            if(X() < agent.X()){
                endX = X() + dist / sqrt(1 + slope * slope);
            }else {
                endX = X() - dist / sqrt(1 + slope * slope);
            }

            endY = Y() - X() * slope + slope * endX;

            if (slope == Double.POSITIVE_INFINITY || slope == Double.NEGATIVE_INFINITY){
                if(Y() < agent.Y()){
                    endY = Y() - dist;
                }
                else{
                    endY = Y() + dist;
                }
            }

            return new Agent(Math.floor(endX), Math.floor(endY), 0,0,0);
        }

        public String stun(Agent agent) {
            if(state() != 2){
                stunTimer.put(getInTeamId(), 20);
                stunnedEnemies.add(agent);
            }
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
            if(checkpoints.size() >= busters.size()){
                return moveTo(checkpoints.get(getInTeamId()));
            }
            else if(checkpoints.size() > 0){
                return moveTo(checkpoints.get(0));
            }
            else {
                return moveTo(new Agent(enemyBase.X() + 1500 * enemyBase.state(), enemyBase.Y() + 1500 * enemyBase.state(), 0,0,0));
            }
        }

        public String herd(){
            Agent furthestGhost = herdableGhosts.get(0);
            for (Agent ghost : herdableGhosts) {
                if (!(ghost.X() == 16000 && ghost.Y() == 0)
                        && !(ghost.X() == 0 && ghost.Y() == 9000)
                        && ghost.distanceTo(base) > furthestGhost.distanceTo(base)) {
                    furthestGhost = ghost;
                }
            }

            double slope = furthestGhost.slopeWith(base);
            double endX;
            double endY;


            if(furthestGhost.X() < base.X()){
                endX = furthestGhost.X() - HERDABLE_DISTANCE / sqrt(1 + slope * slope);
            }else {
                endX = furthestGhost.X() + HERDABLE_DISTANCE / sqrt(1 + slope * slope);
            }

            endY = furthestGhost.Y() - furthestGhost.X() * slope + slope * endX;

            if (slope == Double.POSITIVE_INFINITY || slope == Double.NEGATIVE_INFINITY){
                if(furthestGhost.Y() < base.Y()){
                    endY = furthestGhost.Y() - HERDABLE_DISTANCE;
                }
                else{
                    endY = furthestGhost.Y() + HERDABLE_DISTANCE;
                }
            }
            System.err.println("gx " + furthestGhost.X());
            System.err.println("gy " + furthestGhost.Y());
            System.err.println("endX " + endX);
            System.err.println("endY " + endY);
            return String.format("MOVE %.0f %.0f herding", endX, endY);
        }

        public boolean canStun(Agent enemy){
            return stunTimer.get(getInTeamId()) == 0 && distanceTo(enemy) < MAX_STUNABLE_DISTANCE;
        }

        public boolean canBust(Agent ghost){
            double dist = distanceTo(ghost);
            return dist < MAX_BUSTABLE_DISTANCE && dist > MIN_BUSTABLE_DISTANCE;
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

        double value(){
            return value;
        }

        double state(){
            return state;
        }

        double distanceTo(Agent agent) {
            return sqrt((X() - agent.X()) * (X() - agent.X()) + (Y() - agent.Y()) * (Y() - agent.Y()));
        }

        double slopeWith(Agent other){
            if(this.X() == other.X()) {
                if(this.Y() < other.Y()){
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