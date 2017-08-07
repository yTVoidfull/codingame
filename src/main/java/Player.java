import sun.management.Agent;

import java.util.*;
import java.io.*;
import java.math.*;

import static java.lang.Math.atan;
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
    public static final int HERDABLE_DISTANCE = 910;

    public static Agent base;
    public static Agent enemyBase;
    public static List<Agent> checkpoints = new ArrayList<Agent>();
    public static Map<Integer, Integer> stunTimer = new HashMap<Integer, Integer>();
    public static List<Agent> catchGhosts;
    public static List<Agent> bustableGhosts;
    public static List<Agent> herdableGhosts;
    public static Agent herded;
    public static Buster catcher;
    public static List<Agent> enemies;
    public static List<Buster> busters;
    public static List<Agent> stunnedEnemies;
    public static int round;

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

        setUpStunTimer(bustersPerPlayer);
        defineCheckpoints();

        // game loop
        while (true) {
            int entities = in.nextInt(); // the number of busters and ghosts visible to you

            catchGhosts = new ArrayList<Agent>();
            bustableGhosts = new ArrayList<Agent>();
            herdableGhosts = new ArrayList<Agent>();
            enemies = new ArrayList<Agent>();
            busters = new ArrayList<Buster>();
            stunnedEnemies = new ArrayList<Agent>();

            for (int i = 0; i < entities; i++) {
                int entityId = in.nextInt(); // buster id or ghost id
                int x = in.nextInt();
                int y = in.nextInt(); // position of this buster / ghost
                int entityType = in.nextInt(); // the team id if it is a buster, -1 if it is a ghost.
                int state = in.nextInt(); // For busters: 0=idle, 1=carrying a ghost. For ghosts: remaining stamina points.
                int value = in.nextInt(); // For busters: Ghost id being carried/busted or number of turns left when stunned. For ghosts: number of busters attempting to trap this ghost.


                if (entityType == -1) {
                    Agent ghost = new Agent(x, y, entityId, state, value);
                    if (state == 0 && value == 0) {
                        catchGhosts.add(ghost);
                    } else if (state < 40 && round > 6
                            || ghost.distanceTo(base) < 1600 && round > 150
                            || ghost.distanceTo(new Agent(16000, 0, 0, 0, 0)) < 100
                            || ghost.distanceTo(new Agent(9000, 0, 0, 0, 0)) < 100) {
                        bustableGhosts.add(ghost);
                    } else if (ghost.distanceTo(base) > 400) {
                        herdableGhosts.add(ghost);
                    }
                } else if (entityType == base.getId()) {
                    busters.add(new Buster(x, y, entityId, state, value));
                } else {
                    if (state == 2) {
                        stunnedEnemies.add(new Agent(x, y, entityId, state, value));
                    } else {
                        enemies.add(new Agent(x, y, entityId, state, value));
                    }
                }

            }

            for (int i = 0; i < bustersPerPlayer; i++) {

                // Write an action using System.out.println()
                // To debug: System.err.println("Debug messages...");


                // MOVE x y | BUST id | RELEASE | STUN id | RADAR | EJECT x y
                System.out.println(decideAction(busters.get(i)));
            }
            round++;
            decreaseStunTimer();
        }
    }

    //
    // Buster actions
    //
    private static String decideAction(Buster buster) {
        getRidOfPassedCheckpointsNear(buster);
        if (buster.isScout()) {
            return decideScoutingAction(buster) + " " + stunTimer.get(buster.getInTeamId());
        } else {
            return decideFighterAction(buster) + " " + stunTimer.get(buster.getInTeamId());
        }
    }

    private static String decideFighterAction(Buster buster) {
        String carrierAction = carrierAction(buster);
        if (carrierAction != null) return carrierAction;

        String catchEjectedAction = catchGhostEjected(buster);
        if (catchEjectedAction != null) return catchEjectedAction;

        String fightAction = fightAction(buster);
        if (fightAction != null) return fightAction;

        String bustAction = bustGhostAction(buster);
        if (bustAction != null) return bustAction;

        String herdingAction = herdingPractice(buster);
        if (herdingAction != null) return herdingAction;

        return buster.scout();
    }

    private static String decideScoutingAction(Buster buster) {
        String carrierAction = carrierAction(buster);
        if (carrierAction != null) return carrierAction;

        String catchEjectedAction = catchGhostEjected(buster);
        if (catchEjectedAction != null) return catchEjectedAction;

        String fightAction = fightAction(buster);
        if (fightAction != null) return fightAction;

        String herdingAction = herdingPractice(buster);
        if (herdingAction != null) return herdingAction;

        String bustAction = bustGhostAction(buster);
        if (bustAction != null) return bustAction;

        return buster.scout();
    }

    private static String catchGhostEjected(Buster buster) {
        if (catchGhosts.size() > 0) {
            if (catcher != null && buster.isEqualTo(catcher)) {
                catcher = null;
                return buster.bust(buster.getNearestGhostOutOf(catchGhosts));
            }
        }
        return null;
    }

    private static String fightAction(Buster buster) {
        if (enemies.size() > 0) {
            System.err.println("enemies nearby");

            if (enemies.size() == busters.size() && bustableGhosts.size() > 0 && buster.getNearestGhostOutOf(bustableGhosts).state() < 8 * (bustersNearTo(buster).size() + 1)) {
                System.err.println("seeing all enemies");

                for (Agent e : enemies) {
                    if (buster.canStun(e)) {
                        System.err.println("stunning first seen enemy");
                        return buster.stun(e);
                    } else if (bustableGhosts.size() > 0) {
                        Agent nearest = buster.getNearestGhostOutOf(bustableGhosts);
                        if (nearest.state() < enemiesNearTo(buster).size() + 5 * bustersNearTo(nearest).size() && buster.canBust(nearest)) {
                            return buster.bust(buster.getNearestGhostOutOf(bustableGhosts));
                        } else {
                            return buster.moveToBustableDistance(buster.getNearestGhostOutOf(bustableGhosts));
                        }
                    }
                }
            }

            for (Agent e : enemies) {
                if (e.state() == 1 && buster.canStun(e)) {
                    return buster.stun(e);
                } else if (e.state() == 1 && stunTimer.get(buster.getInTeamId()) < 5 && round > 100) {
                    return buster.intercept(e);

                } else if (e.state() == 3 && buster.distanceTo(e) < 1760 && (e.distanceTo(enemyBase) > 1600 || round > 200)) {
                    Agent ghost = findGhost(e.value());
                    if (ghost == null) {
                        return buster.moveTo(enemyClosestToBase());
                    } else if (ghost.state() == 0 && buster.canBust(ghost)) {
                        return buster.bust(ghost);

                    } else if (ghost.state() < 5 * bustersNearTo(ghost).size() && buster.canStun(e)) {
                        return buster.stun(e);
                    }
                }
            }
        }

        return null;
    }

    private static String carrierAction(Buster buster) {
        if (buster.state() == 1) {
            Buster catcher = buster.getBusterAtPassingRange();
            if (catcher != null) {
                buster.ejectTo(catcher);
            }
            return buster.secureGhost();
        }
        return null;
    }

    private static String bustGhostAction(Buster buster) {
        if (bustableGhosts.size() > 0) {
            Agent nearest = buster.getNearestGhostOutOf(bustableGhosts);
            if (buster.canBust(nearest)) {
                return buster.bust(nearest);
            } else {
                return buster.moveToBustableDistance(nearest);
            }
        }
        return null;
    }

    private static String herdingPractice(Buster buster) {
        if (herdableGhosts.contains(herded)) {
            herdableGhosts.remove(herded);
        }
        if (herdableGhosts.size() > 0) {
            if (buster.distanceTo(enemyBase) > 3000 && buster.distanceTo(base) > 1000) {
                return buster.herd();
            }
        }
        return null;
    }


    //
    // Utility methods
    //
    private static void setUpStunTimer(int nr) {
        for (int i = 0; i < nr; i++) {
            stunTimer.put(i, 0);
        }
    }

    private static void decreaseStunTimer() {
        for (int id : stunTimer.keySet()) {
            int cooldown = stunTimer.get(id);
            if (cooldown > 0) {
                stunTimer.put(id, cooldown - 1);
            }
        }
    }

    static List<Buster> bustersNearTo(Agent point) {
        List<Buster> output = new ArrayList<Buster>();
        for (Buster b : busters) {
            if (point.distanceTo(b) < 1760 && !b.equals(point)) {
                output.add(b);
            }
        }
        return output;
    }

    static Agent findGhost(int id) {
        Agent output = null;
        for (Agent g : bustableGhosts) {
            if (g.getId() == id) output = g;
        }
        return output;
    }

    static List<Agent> enemiesNearTo(Buster buster) {
        List<Agent> output = new ArrayList<Agent>();
        for (Agent e : enemies) {
            if (buster.distanceTo(e) < 2200 && e.state() != 2) {
                output.add(e);
            }
        }
        return output;
    }

    static Agent enemyClosestToBase() {
        Agent output = enemies.get(0);
        for (Agent e : enemies) {
            if (e.distanceTo(enemyBase) < output.distanceTo(enemyBase)) {
                output = e;
            }
        }
        return output;
    }

    private static void getRidOfPassedCheckpointsNear(Buster buster) {
        List<Agent> tbd = new ArrayList<Agent>();
        for (Agent point : checkpoints) {
            if (buster.distanceTo(point) < RADAR_DISTANCE) {
                tbd.add(point);
            }
        }
        checkpoints.removeAll(tbd);
    }

    public static void defineCheckpoints() {
        if (base.getId() == 0) {
            checkpoints.add(new Agent(9000, 5000, 0, 0, 0));
            checkpoints.add(new Agent(7500, 6800, 0, 0, 0));
            checkpoints.add(new Agent(13000, 2200, 0, 0, 0));
            checkpoints.add(new Agent(6000, 7500, 0, 0, 0));
            checkpoints.add(new Agent(14500, 7500, 0, 0, 0));
        } else {
            checkpoints.add(new Agent(13000, 2200, 0, 0, 0));
            checkpoints.add(new Agent(4000, 7500, 0, 0, 0));
            checkpoints.add(new Agent(9000, 5000, 0, 0, 0));
            checkpoints.add(new Agent(5500, 6800, 0, 0, 0));
            checkpoints.add(new Agent(1500, 1500, 0, 0, 0));
        }
    }

    //
    // Classes for agents
    //
    static class Buster extends Agent {

        public Buster(double x, double y, int id, int state, int value) {
            super(x, y, id, state, value);
        }

        public String moveTo(Agent agent) {
            if (distanceTo(agent) > BUSTER_MAX_MOVE) {
                Agent point = getPointAtMovementDistanceTo(agent, BUSTER_MAX_MOVE);
                return String.format("MOVE %.0f %.0f", point.X(), point.Y());
            }
            ;
            return String.format("MOVE %.0f %.0f", agent.X(), agent.Y());
        }

        public String moveToBustableDistance(Agent agent) {

            double slope = agent.slopeWith(base);
            double endX;
            double endY;


            if (agent.X() < base.X()) {
                endX = agent.X() + HERDABLE_DISTANCE / sqrt(1 + slope * slope);
            } else {
                endX = agent.X() - HERDABLE_DISTANCE / sqrt(1 + slope * slope);
            }

            endY = agent.Y() - agent.X() * slope + slope * endX;

            if (slope == Double.POSITIVE_INFINITY || slope == Double.NEGATIVE_INFINITY) {
                if (agent.Y() < base.Y()) {
                    endY = agent.Y() + HERDABLE_DISTANCE;
                } else {
                    endY = agent.Y() - HERDABLE_DISTANCE;
                }
            }

            return String.format("MOVE %.0f %.0f herding", Math.floor(endX), Math.floor(endY));
        }

        public Agent getNearestGhostOutOf(List<Agent> ghosts) {
            Agent ghost = ghosts.get(0);
            for (Agent g : ghosts) {
                if (distanceTo(g) < distanceTo(ghost)) {
                    ghost = g;
                }
            }
            return ghost;
        }

        public String stun(Agent agent) {
            if (state() != 2) {
                stunTimer.put(getInTeamId(), 20);
                stunnedEnemies.add(agent);
            }
            return String.format("STUN %s", agent.getId());
        }

        public String bust(Agent ghost) {
            return String.format("BUST %s", ghost.getId());
        }

        int getInTeamId() {
            return getId() % 4;
        }

        boolean isScout() {
            return getInTeamId() == 0;
        }

        public String scout() {
            if (checkpoints.size() >= busters.size()) {
                return moveTo(checkpoints.get(getInTeamId()));
            } else if (checkpoints.size() > 0) {
                return moveTo(checkpoints.get(0));
            } else {
                return moveTo(new Agent(enemyBase.X() + 1500 * enemyBase.state(), enemyBase.Y() + 1500 * enemyBase.state(), 0, 0, 0));
            }
        }

        public String herd() {
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


            if (furthestGhost.X() < base.X()) {
                endX = furthestGhost.X() - HERDABLE_DISTANCE / sqrt(1 + slope * slope);
            } else {
                endX = furthestGhost.X() + HERDABLE_DISTANCE / sqrt(1 + slope * slope);
            }

            endY = furthestGhost.Y() - furthestGhost.X() * slope + slope * endX;

            if (slope == Double.POSITIVE_INFINITY || slope == Double.NEGATIVE_INFINITY) {
                if (furthestGhost.Y() < base.Y()) {
                    endY = furthestGhost.Y() - HERDABLE_DISTANCE;
                } else {
                    endY = furthestGhost.Y() + HERDABLE_DISTANCE;
                }
            }

            herded = furthestGhost;

            return String.format("MOVE %.0f %.0f herding", Math.floor(endX), Math.floor(endY));
        }

        public boolean canStun(Agent enemy) {
            return stunTimer.get(getInTeamId()) == 0 && distanceTo(enemy) < MAX_STUNABLE_DISTANCE;
        }

        public boolean canBust(Agent ghost) {
            double dist = distanceTo(ghost);
            return dist < MAX_BUSTABLE_DISTANCE && dist > MIN_BUSTABLE_DISTANCE;
        }

        public String secureGhost() {
            if (distanceTo(base) < 1600) {
                return "RELEASE score";
            } else {
                return moveTo(base) + " saving";
            }
        }

        String ejectTo(Buster buster) {
            Agent ejectPoint = buster.getPointAtMovementDistanceTo(this, 900);
            catcher = buster;
            return String.format("EJECT %.0f %.0f", ejectPoint.X(), ejectPoint.Y());
        }

        public Buster getBusterAtPassingRange() {
            List<Buster> bustersCopy = new ArrayList<Buster>();
            bustersCopy.addAll(busters);
            bustersCopy.remove(this);

            for (Buster b : bustersCopy) {
                if (b.state() == 0 || b.state() == 3) {
                    String[] fActionParts = decideAction(b).split(" ");
                    if (fActionParts[0].equals("MOVE")) {
                        Buster fBuster = new Buster(Double.parseDouble(fActionParts[1]), Double.parseDouble(fActionParts[2]), 0, 0, 0);
                        if (distanceTo(fBuster) < MAX_BUSTABLE_DISTANCE * 2
                                && fBuster.distanceTo(base) < distanceTo(base) - 1000
                                && distanceTo(fBuster) > MIN_BUSTABLE_DISTANCE * 2) {
                            return new Buster(Double.parseDouble(fActionParts[1]), Double.parseDouble(fActionParts[2]), 0, 0, 0);
                        }
                    } else if (distanceTo(b) < MAX_BUSTABLE_DISTANCE * 2
                            && b.distanceTo(base) < distanceTo(base) - 1000
                            && distanceTo(b) > MIN_BUSTABLE_DISTANCE * 2) {
                        return b;
                    }
                }
            }
            return null;
        }

        public String intercept(Agent e) {
            Agent interceptPoint = e.getPointAtMovementDistanceTo(enemyBase, 800);
            return moveTo(interceptPoint);
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

        int value() {
            return value;
        }

        int state() {
            return state;
        }

        double distanceTo(Agent agent) {
            return sqrt((X() - agent.X()) * (X() - agent.X()) + (Y() - agent.Y()) * (Y() - agent.Y()));
        }

        double slopeWith(Agent other) {
            if (this.X() == other.X()) {
                if (this.Y() < other.Y()) {
                    return Double.POSITIVE_INFINITY;
                } else {
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

        public boolean isEqualTo(Agent other) {
            return other.getId() == getId();
        }

        public Agent getPointAtMovementDistanceTo(Agent agent, int dist) {
            double slope = slopeWith(agent);
            double endX;
            double endY;


            if (X() < agent.X()) {
                endX = X() + dist / sqrt(1 + slope * slope);
            } else {
                endX = X() - dist / sqrt(1 + slope * slope);
            }

            endY = Y() - X() * slope + slope * endX;

            if (slope == Double.POSITIVE_INFINITY || slope == Double.NEGATIVE_INFINITY) {
                if (Y() < agent.Y()) {
                    endY = Y() + dist;
                } else {
                    endY = Y() - dist;
                }
            }

            return new Agent(Math.floor(endX), Math.floor(endY), 0, 0, 0);
        }

    }

}