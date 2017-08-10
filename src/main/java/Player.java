

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

    public static final int HERDABLE_DISTANCE = 900;

    static Locatable base;
    static Locatable enemyBase;
    static ArrayList<Locatable> checkpoints = new ArrayList<Locatable>();
    static Locatable[] scoutPoints = new Locatable[4];
    static boolean[] checkedScoutPoints = new boolean[]{false, false, false, false};
    static Map<Integer, Integer> stunTimer = new HashMap<Integer, Integer>();
    static int round;
    static List<Locatable> stunnedEnemies;
    static int ROUND_LIMIT = 60;
    static List<Buster> busters;
    static List<Locatable> ghosts;
    static List<Locatable> enemies;
    static Set<Locatable> potential = new HashSet<Locatable>();
    static List<Locatable> herded;
    static List<Locatable> herdableGhosts;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int bustersPerPlayer = in.nextInt(); // the amount of busters you control
        int ghostCount = in.nextInt(); // the amount of ghosts on the map
        int myTeamId = in.nextInt(); // if this is 0, your base is on the top left of the map, if it is one, on the bottom right

        if (ghostCount < 15) {
            ROUND_LIMIT = 30;
        }

        if (myTeamId == 0) {
            base = new Locatable(0.0, 0.0, 0, 1, 0);
            enemyBase = new Locatable(16000.0, 9000.0, 1, -1, 0);
        } else {
            base = new Locatable(16000.0, 9000.0, 1, -1, 0);
            enemyBase = new Locatable(0.0, 0.0, 0, 1, 0);
        }

        defineCheckpoints(bustersPerPlayer);

        setUpStunTimer(myTeamId, bustersPerPlayer);

        // game loop
        while (true) {

            busters = new ArrayList<Buster>();
            ghosts = new ArrayList<Locatable>();
            enemies = new ArrayList<Locatable>();
            stunnedEnemies = new ArrayList<Locatable>();
            herded = new ArrayList<Locatable>();
            herdableGhosts = new ArrayList<Locatable>();
            round++;

            int entities = in.nextInt(); // the number of busters and ghosts visible to you
            for (int i = 0; i < entities; i++) {
                int entityId = in.nextInt(); // buster id or ghost id
                int x = in.nextInt();
                int y = in.nextInt(); // position of this buster / ghost
                int entityType = in.nextInt(); // the team id if it is a buster, -1 if it is a ghost.
                int state = in.nextInt(); // For busters: 0=idle, 1=carrying a ghost. For ghosts: remaining stamina points.
                int value = in.nextInt(); // For busters: Ghost id being carried/busted or number of turns left when stunned. For ghosts: number of busters attempting to trap this ghost.

                if (entityType == -1) {
                    Locatable ghost = new Locatable(x, y, entityId, value, state);
                    potential.add(ghost);
                    ghosts.add(ghost);
                    if (round < 5) {
                        potential.add(createSymmetrical(ghost));
                    }
                } else if (entityType == base.getId()) busters.add(new Buster(x, y, entityId, value, state));
                else enemies.add(new Locatable(x, y, entityId, value, state));
            }

            for (Locatable enemy : enemies) {
                if (enemy.getState() == 2) {
                    stunnedEnemies.add(enemy);
                }
            }


            for (int i = 0; i < bustersPerPlayer; i++) {

                // Write an action using System.out.println()
                // To debug: System.err.println("Debug messages...");
                System.out.println(decideAction(busters.get(i)));
            }
            decreaseStunTimer();
        }
    }

    static String decideAction(Buster buster) {

        // strategy for buster with ghost
        //System.err.println("stun cooldown for " +buster.getId() + " is" + stunTimer.get(buster.getId()));
        //System.err.println("enemies : " + enemies.size());
        if (buster.getState() == 0) buster.cleanUpPotentialGhostsNearby();
        buster.clearCheckpointsNear();
        ghosts = filterGhosts(ghosts);

        if (buster.getState() == 1) {
            if (enemies.size() > 0) {
                for (Locatable e : enemies) {
                    if (buster.canStun(e)) {
                        return buster.stun(e);
                    }
                }
            }
            Buster closestToBase = busterClosestToBaseFromBuster(buster);
            if (closestToBase != null) return buster.eject(closestToBase);

            if (ghostsNearTo(herdableGhosts, buster).size() > 0 && !anyEnemyNear(buster)) {
                String safeHerd = buster.safeHerd(herdableGhosts);
                if (safeHerd != null) return safeHerd;
            }

            if (ghostsNearTo(ghosts, buster).size() > 0 && !anyEnemyNear(buster)) {
                String safeHerd = buster.safeHerd(ghosts);
                if (safeHerd != null) return safeHerd;
            }

            List<Buster> allies = bustersNearTo(buster);
            for (Buster ally : allies) {
                if (ally.getState() == 3 && enemies.size() < allies.size() + 1 && findGhost(ally.getValue()).getState() == 0 && round < 100) {
                    return buster.moveToBustableDistance(ally, false);
                }
            }

            return buster.saveGhost();

        } else {

            // when enemies are near

            if (enemies.size() > 0) {

                if (enemies.size() == busters.size() && ghosts.size() > 0 && buster.getNearestGhost().getState() < 8 * (bustersNearTo(buster).size() + 1)) {
                    System.err.println("seeing all enemies");

                    for (Locatable e : enemies) {
                        if (buster.canStun(e)) {
                            System.err.println("stunning first seen enemy");
                            return buster.stun(e);
                        }
                    }

                    Locatable nearest = buster.getNearestGhost();
                    if (nearest.getState() < enemiesNearTo(buster).size() + 8 * bustersNearTo(nearest).size() && buster.canBust(nearest)) {
                        System.err.println("busting ghost to steal");
                        return buster.bust(buster.getNearestGhost());
                    } else {
                        System.err.println("moving next to ghost to steal");
                        return buster.moveToBustableDistance(buster.getNearestGhost(), true);
                    }
                }

                for (Locatable e : enemies) {
                    if (e.getState() == 1 && buster.canStun(e)) {
                        return buster.stun(e);
                    } else if (e.getState() == 1 && stunTimer.get(buster.getId()) < 5 && round > 100) {
                        return buster.intercept(e);
                    } else if (e.getState() == 3 && buster.distanceTo(e) < 1760 && (e.distanceTo(enemyBase) > 2000 || round > 200)) {
                        Locatable ghost = findGhost(e.getValue());
                        if (ghost == null) {
                            return buster.moveTo(enemyClosestToBase());
                        } else if (ghost.getState() == 0 && buster.canBust(ghost)) {
                            return buster.bust(ghost);
                        } else if (ghost.getState() < 5 * bustersNearTo(ghost).size() && buster.canStun(e)) {
                            return buster.stun(e);
                        }
                    } else if (e.getState() == 3 && buster.distanceTo(e) > 1760 && buster.distanceTo(e) < 2200) {
                        return buster.moveTo(e);
                    } else if (ghostsNearTo(ghosts, buster).size() > 0) {
                        for (Locatable ghost : ghostsNearTo(ghosts, buster)) {
                            if (ghost.getState() <= 5 * bustersNearTo(ghost).size() && buster.canStun(e)) {
                                return buster.stun(e);
                            }
                        }
                    }
                }

                List<Buster> allies = new ArrayList<Buster>();
                allies.addAll(busters);
                allies.remove(buster);
                for (Buster ally : allies) {
                    if (ally.getState() == 1 && enemiesNearTo(ally).size() > 0) {
                        return buster.moveToBustableDistance(ally, false);
                    }
                }

                // handle ghosts when enemies are near

                if (ghosts.size() > 0) {
                    Locatable nearest = buster.getNearestGhost();
                    List<Buster> bustersNearby = bustersNearTo(nearest);

                    if (nearest.getState() == 0 && buster.canBust(nearest)) {
                        return buster.bust(nearest);
                    }

                    for (Locatable e : enemies) {
                        Locatable nearestToEnemy = getNearestGhostTo(e);
                        if (buster.canBust(nearestToEnemy)) {
                            return buster.bust(nearestToEnemy);
                        }
                    }

                    if (buster.canBust(nearest)) {
                        if (nearest.getState() == 0) {
                            for (Locatable e : enemies) {
                                if (buster.canStun(e)) {
                                    return buster.stun(e);
                                }
                            }
                        }
                        if (nearest.getValue() > bustersNearby.size()) {
                            return buster.bust(nearest);
                        }
                        if (nearest.getState() == 0 && buster.equals(busterClosestToBaseFromGhost(nearest))) {
                            return buster.bust(nearest);
                        } else if (nearest.getState() != 0) {
                            return buster.bust(nearest);
                        }
                    } else {
                        if (buster.distanceTo(nearest) < 900 && nearest.distanceTo(base) < 800) {
                            return buster.moveToBustableDistance(nearest, true);
                        }
                        System.err.println("no attack - move to bust");
                        return buster.moveToBustableDistance(nearest, true);
                    }
                }

                allies = new ArrayList<Buster>();
                allies.addAll(busters);
                allies.remove(buster);
                for (Buster ally : allies) {
                    if (enemiesNearTo(ally).size() > bustersNearTo(ally).size() + 1
                            && buster.distanceTo(ally) < 5000
                            && ghostsNearTo(ghosts, ally).size() > ghostsNearTo(ghosts, buster).size()) {
                        return buster.moveToBustableDistance(ally, false);
                    }
                }

            } else {

                // no enemies around
                if (ghosts.size() > 0) {
                    System.err.println("seeing " + ghosts.size());
                    Locatable nearest = buster.getNearestGhost();

                    if (!buster.isCheckedScoutPoint() && !(nearest.getState() <= 3))
                        return buster.scout();

                    if (potential.size() > 0) {
                        Locatable potential = buster.getNearestPotentialGhost();
                        if (potential.getState() <= 3 && nearest.getState() > 3 && potential.distanceTo(enemyBase) > 8000 && bustersNearTo(potential).size() == 0) {
                            return buster.moveToBustableDistance(potential, true) + " potential 3";
                        }
                    }

                    if (buster.canBust(nearest)) {
                        System.err.println("bustable range to nearest");
                        Buster nearestBuster = busterClosestToBaseFromGhost(nearest);
                        if (nearest.getState() == 0 && buster.equals(nearestBuster)) {
                            return buster.bust(nearest);
                        } else if (nearest.getState() == 0 && nearestBuster.getState() != 0 || !nearestBuster.canBust(nearest)) {
                            return buster.bust(nearest);
                        }
                        return buster.bust(nearest);
                    } else if (buster.stepsTo(nearest) < nearest.getState()) {
                        if (buster.distanceTo(nearest) < 900 && nearest.distanceTo(base) < 800) {
                            return buster.moveToBustableDistance(nearest, false) + " help";
                        }
                    }else if(buster.distanceTo(nearest) < 2200){
                        return buster.moveToBustableDistance(nearest, true) + " nearest";
                    }

                }
            }
        }

        if (ghostsNearTo(herdableGhosts, buster).size() > 1) {
            String herd = buster.herd(herdableGhosts);
            if (herd != null) return herd;
        }

        if (ghostsNearTo(ghosts, buster).size() > 1) {
            String herd = buster.herd(ghosts);
            if (herd != null) return herd;
        }

        return buster.scout() + " scout";
    }

    static void defineCheckpoints(int bustersPerPlayer) {
        if (base.getValue() == 1) {
            switch (bustersPerPlayer) {
                case 4:
                    scoutPoints[2] = new Locatable(3000, 7000, 0, 0, 0);
                    scoutPoints[1] = new Locatable(9000, 3500, 0, 0, 0);
                    scoutPoints[0] = new Locatable(8000, 5000, 0, 0, 0);
                    scoutPoints[3] = new Locatable(14500, 1500, 0, 0, 0);
                    break;
                case 3:
                    scoutPoints[2] = new Locatable(5000, 7000, 0, 0, 0);
                    scoutPoints[1] = new Locatable(10000, 2500, 0, 0, 0);
                    scoutPoints[0] = new Locatable(8500, 5000, 0, 0, 0);
                    break;
                case 2:
                    scoutPoints[1] = new Locatable(8000, 5000, 0, 0, 0);
                    scoutPoints[0] = new Locatable(4000, 7000, 0, 0, 0);
                    break;
            }

        } else {
            switch (bustersPerPlayer) {
                case 4:
                    scoutPoints[3] = new Locatable(13000, 2000, 0, 0, 0);
                    scoutPoints[1] = new Locatable(10000, 4000, 0, 0, 0);
                    scoutPoints[0] = new Locatable(7000, 5500, 0, 0, 0);
                    scoutPoints[2] = new Locatable(14000, 2000, 0, 0, 0);
                    break;
                case 3:
                    scoutPoints[2] = new Locatable(7000, 2000, 0, 0, 0);
                    scoutPoints[1] = new Locatable(6000, 6500, 0, 0, 0);
                    scoutPoints[0] = new Locatable(7500, 4000, 0, 0, 0);
                    break;
                case 2:
                    scoutPoints[1] = new Locatable(8000, 5000, 0, 0, 0);
                    scoutPoints[0] = new Locatable(12000, 2000, 0, 0, 0);
                    break;
            }

        }

        for (int x = 0; x < 16000; x += 1000) {
            for (int y = 0; y < 9000; y += 1000) {
                checkpoints.add(new Locatable(x, y, 0, 0, 0));
            }
        }

    }

    static void setUpStunTimer(int teamId, int nr) {
        int offset = 0;
        if (teamId == 1) {
            offset = nr;
        }
        for (int i = 0; i < nr; i++) {
            stunTimer.put(offset + i, 0);
        }
    }

    static List<Locatable> filterGhosts(List<Locatable> ghosts) {
        List<Locatable> output = new ArrayList<Locatable>();
        for (Locatable g : ghosts) {
            if (g.getState() < 40 && g.distanceTo(enemyBase) > 1600 || (g.getState() == 40 && round > ROUND_LIMIT)) {
                output.add(g);
            } else {
                if (!herdableGhosts.contains(g)) {
                    herdableGhosts.add(g);
                }
            }
        }
        return output;
    }

    static Locatable createSymmetrical(Locatable loc) {
        return new Locatable(16000 - loc.getX(), 9000 - loc.getY(), 0, loc.getValue(), loc.getState());
    }

    static List<Buster> bustersNearTo(Locatable point) {
        List<Buster> output = new ArrayList<Buster>();
        for (Buster b : busters) {
            if (point.distanceTo(b) < 1760 && !b.equals(point)) {
                output.add(b);
            }
        }
        return output;
    }

    static Buster busterClosestToBaseFromGhost(Locatable ghost) {
        List<Buster> allies = bustersNearTo(ghost);
        Buster output = allies.get(0);
        for (Buster b : allies) {
            if (b.distanceTo(base) < output.distanceTo(base) && b.getState() == 0) {
                output = b;
            }
        }
        return output;
    }

    static Buster busterClosestToBaseFromBuster(Buster buster) {
        Buster output = buster;
        List<Buster> allies = new ArrayList<Buster>();
        allies.addAll(busters);
        allies.remove(buster);
        for (Buster b : allies) {
            if (output.distanceTo(base) - b.distanceTo(base) > 1500 && b.distanceTo(output) < 3560 && b.distanceTo(buster) > 1800 && b.getState() == 0) {
                output = b;
            }
        }
        if (output.equals(buster)) return null;
        return output;
    }

    static List<Locatable> ghostsNearTo(List<Locatable> ghosts, Buster buster) {
        List<Locatable> output = new ArrayList<Locatable>();
        for (Locatable g : ghosts) {
            if (buster.distanceTo(g) < 2200) {
                output.add(g);
            }
        }
        return output;
    }

    static List<Locatable> enemiesNearTo(Locatable buster) {
        List<Locatable> output = new ArrayList<Locatable>();
        for (Locatable e : enemies) {
            if (buster.distanceTo(e) < 2200 && e.getState() != 2) {
                output.add(e);
            }
        }
        return output;
    }

    static boolean anyEnemyNear(Buster buster) {
        List<Locatable> output = new ArrayList<Locatable>();
        for (Locatable e : enemies) {
            if (buster.distanceTo(e) < 2200) {
                return true;
            }
        }
        return false;
    }

    static Locatable getNearestGhostTo(Locatable l) {
        Locatable nearby = ghosts.get(0);
        for (Locatable g : ghosts) {
            if (l.distanceTo(g) < l.distanceTo(nearby))
                nearby = g;
            if (g.getState() <= 3)
                nearby = g;
        }
        return nearby;
    }

    static List<Locatable> getWeakGhosts() {
        List<Locatable> output = new ArrayList<Locatable>();
        Locatable weakest = ghosts.get(0);
        for (Locatable ghost : ghosts) {
            if (ghost.getState() < weakest.getState()) {
                weakest = ghost;
            }
        }
        for (Locatable ghost : ghosts) {
            if (ghost.getState() <= weakest.getState()) {
                output.add(ghost);
            }
        }
        return output;
    }

    static List<Locatable> getPotentiallyWeakGhosts() {
        List<Locatable> output = new ArrayList<Locatable>();
        Locatable weakest = potential.iterator().next();
        for (Locatable ghost : potential) {
            if (ghost.getState() < weakest.getState()) {
                weakest = ghost;
            }
        }
        for (Locatable ghost : potential) {
            if (ghost.getState() == weakest.getState()) {
                output.add(ghost);
            }
        }
        return output;
    }

    static Locatable enemyClosestToBase() {
        Locatable output = enemies.get(0);
        for (Locatable e : enemies) {
            if (e.distanceTo(enemyBase) < output.distanceTo(enemyBase)) {
                output = e;
            }
        }
        return output;
    }

    static Locatable findGhost(int id) {
        Locatable output = null;
        for (Locatable g : ghosts) {
            if (g.getId() == id) output = g;
        }
        return output;
    }

    static void decreaseStunTimer() {
        for (Integer key : stunTimer.keySet()) {
            Integer timer = stunTimer.get(key);
            if (timer > 0) stunTimer.put(key, timer - 1);
        }
    }

    static class Buster extends Locatable {

        Buster(double x, double y, int id, int val, int state) {
            super(x, y, id, val, state);
        }

        String moveTo(Locatable locatable) {

            if (distanceTo(locatable) > 800) {
                Locatable point = getPointAtDistanceTo(locatable, 800);
                return String.format("MOVE %.0f %.0f", point.getX(), point.getY());
            }

            return String.format("MOVE %.0f %.0f", locatable.getX(), locatable.getY());
        }

        private void cleanUpPotentialGhostsNearby() {
            List<Locatable> checked = new ArrayList<Locatable>();

            for (Locatable p : potential) {
                if (distanceTo(p) < 2200) checked.add(p);
            }

            potential.removeAll(checked);
        }

        String moveToBustableDistance(Locatable loc, boolean towardsBase) {
            int direction;
            if (towardsBase) direction = -1;
            else direction = 1;
            Locatable newLoc = loc.getPointAtDistanceTo(base, direction * 1000);
            return moveTo(newLoc);
        }

        String saveGhost() {
            if (distanceTo(base) < 1600) return release();
            else return moveTo(base);
        }

        private void clearCheckpointsNear() {
            List<Locatable> tbd = new ArrayList<Locatable>();

            for (Locatable checkpoint : checkpoints) {
                if (distanceTo(checkpoint) < 1500) {
                    tbd.add(checkpoint);
                }
            }

            checkpoints.removeAll(tbd);
        }

        boolean isCheckedScoutPoint() {
            return checkedScoutPoints[getInTeamId()];
        }

        int getInTeamId() {
            return getId() - busters.size() * base.getId();
        }

        String scout() {
            if (distanceTo(scoutPoints[getInTeamId()]) < 400) {
                checkedScoutPoints[getInTeamId()] = true;
            }
            if (!isCheckedScoutPoint()) {
                return moveTo(scoutPoints[getInTeamId()]);
            } else if (potential.size() > 1) {
                return moveTo(getNearestPotentialGhost()) + " potential ";
            } else {
                if (checkpoints.size() > 0) {
                    return moveTo(getClosestCheckpoint());
                } else {
                    defineCheckpoints(busters.size());
                    return moveTo(getClosestCheckpoint());
                }
            }
        }

        private Locatable getClosestCheckpoint() {
            Locatable closest = checkpoints.get(0);

            for (Locatable c : checkpoints) {
                if (distanceTo(c) < distanceTo(closest)) {
                    closest = c;
                }
            }

            return closest;
        }

        String herd(List<Locatable> ghosts) {
            ghosts.removeAll(herded);
            if (ghosts.size() == 0) {
                return null;
            }
            Locatable furthestGhost = ghosts.get(0);
            for (Locatable ghost : ghosts) {
                if (!(ghost.getX() == 16000 && ghost.getY() == 0)
                        && !(ghost.getX() == 0 && ghost.getY() == 9000)
                        && ghost.distanceTo(base) > furthestGhost.distanceTo(base)) {
                    furthestGhost = ghost;
                }
            }

            double slope = furthestGhost.slopeWith(base);
            double endX;
            double endY;


            if (furthestGhost.getX() < base.getX()) {
                endX = furthestGhost.getX() - HERDABLE_DISTANCE / sqrt(1 + slope * slope);
            } else {
                endX = furthestGhost.getX() + HERDABLE_DISTANCE / sqrt(1 + slope * slope);
            }

            endY = furthestGhost.getY() - furthestGhost.getX() * slope + slope * endX;

            if (slope == Double.POSITIVE_INFINITY || slope == Double.NEGATIVE_INFINITY) {
                if (furthestGhost.getY() < base.getY()) {
                    endY = furthestGhost.getY() - HERDABLE_DISTANCE;
                } else {
                    endY = furthestGhost.getY() + HERDABLE_DISTANCE;
                }
            }

            if (furthestGhost.distanceTo(base) < 400 || furthestGhost.value != 0) {
                return null;
            }

            herded.add(furthestGhost);

            return String.format("MOVE %.0f %.0f herding", Math.floor(endX), Math.floor(endY));
        }

        String safeHerd(List<Locatable> ghosts) {
            ghosts.removeAll(herded);
            if (ghosts.size() == 0) {
                return null;
            }
            System.err.println("safe herding" + ghosts.size());
            Locatable furthestGhost = ghosts.get(0);
            for (Locatable ghost : ghosts) {
                if (!(ghost.getX() == 16000 && ghost.getY() == 0)
                        && !(ghost.getX() == 0 && ghost.getY() == 9000)
                        && ghost.distanceTo(base) > furthestGhost.distanceTo(base)
                        && ghost.distanceTo(base) < distanceTo(base)) {
                    furthestGhost = ghost;
                }
            }

            if (furthestGhost.distanceTo(base) < 400 || furthestGhost.value != 0 || furthestGhost.distanceTo(base) > distanceTo(base)) {
                return null;
            }

            double slope = furthestGhost.slopeWith(base);
            double endX;
            double endY;


            if (furthestGhost.getX() < base.getX()) {
                endX = furthestGhost.getX() - HERDABLE_DISTANCE / sqrt(1 + slope * slope);
            } else {
                endX = furthestGhost.getX() + HERDABLE_DISTANCE / sqrt(1 + slope * slope);
            }

            endY = furthestGhost.getY() - furthestGhost.getX() * slope + slope * endX;

            if (slope == Double.POSITIVE_INFINITY || slope == Double.NEGATIVE_INFINITY) {
                if (furthestGhost.getY() < base.getY()) {
                    endY = furthestGhost.getY() - HERDABLE_DISTANCE;
                } else {
                    endY = furthestGhost.getY() + HERDABLE_DISTANCE;
                }
            }

            herded.add(furthestGhost);

            return String.format("MOVE %.0f %.0f herding", Math.floor(endX), Math.floor(endY));
        }

        String bust(Locatable locatable) {
            return "BUST " + locatable.getId();
        }

        boolean canBust(Locatable ghost) {
            return distanceTo(ghost) <= 1760 && distanceTo(ghost) >= 900;
        }

        boolean canStun(Locatable enemy) {
            return distanceTo(enemy) <= 1760 && stunTimer.get(getId()) == 0 && !stunnedEnemies.contains(enemy);
        }

        public String intercept(Locatable e) {
            Locatable interceptPoint = e.getPointAtDistanceTo(enemyBase, 800);
            return moveTo(interceptPoint);
        }

        String eject(Buster buster) {
            String futureDecision = decideAction(buster);
            String[] parts = futureDecision.split(" ");

            if (parts.length >= 3) {
                Locatable fPos = new Locatable(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), 0, 0, 0);
                Locatable passPoint = fPos.getPointAtDistanceTo(this, 910);

                return String.format("EJECT %.0f %.0f", passPoint.getX(), passPoint.getY());
            }

            Locatable passPoint = buster.getPointAtDistanceTo(this, 910);

            return String.format("EJECT %.0f %.0f", passPoint.getX(), passPoint.getY());
        }

        String stun(Locatable locatable) {
            if (getState() != 2) {
                stunTimer.put(getId(), 20);
                stunnedEnemies.add(locatable);
            }
            return "STUN " + locatable.getId();
        }

        Locatable getNearestGhost() {
            List<Locatable> ghosts = getWeakGhosts();
            Locatable nearby = ghosts.get(0);
            for (Locatable g : ghosts) {
                if (distanceTo(g) < distanceTo(nearby))
                    nearby = g;
            }
            ghosts.remove(nearby);
            return nearby;
        }

        Locatable getNearestPotentialGhost() {
            List<Locatable> ghosts = getPotentiallyWeakGhosts();
            Locatable nearby = ghosts.get(0);
            for (Locatable g : ghosts) {
                if (distanceTo(g) < distanceTo(nearby))
                    nearby = g;
            }
            ghosts.remove(nearby);
            return nearby;
        }

        String release() {
            return "RELEASE";
        }

        int stepsTo(Locatable loc) {
            return (int) Math.floor(distanceTo(loc) / 800);
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

        double xDistance(Locatable loc) {
            return getX() - loc.getX();
        }

        double yDistance(Locatable loc) {
            return getY() - loc.getY();
        }

        double slopeWith(Locatable other) {
            if (this.getX() == other.getX()) {
                if (this.getY() < other.getY()) {
                    return Double.POSITIVE_INFINITY;
                } else {
                    return Double.NEGATIVE_INFINITY;
                }
            }
            return (other.getY() - this.getY()) / (other.getX() - this.getX());
        }

        public Locatable getPointAtDistanceTo(Locatable locatable, int dist) {
            double slope = slopeWith(locatable);
            double endX;
            double endY;

            if (getX() < locatable.getX()) {
                endX = getX() + dist / sqrt(1 + slope * slope);
            } else {
                endX = getX() - dist / sqrt(1 + slope * slope);
            }

            endY = getY() - getX() * slope + slope * endX;

            if (slope == Double.POSITIVE_INFINITY || slope == Double.NEGATIVE_INFINITY) {
                if (getY() < locatable.getY()) {
                    endY = getY() + dist;
                } else {
                    endY = getY() - dist;
                }
            }

            return new Locatable(Math.floor(endX), Math.floor(endY), 0, 0, 0);
        }
    }

}