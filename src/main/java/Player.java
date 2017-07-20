

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
    static Locatable[] checkpoints = new Locatable[8];
    static int current = 0;
    static int[] scoutingPath;
    static Map<Integer, Integer> stunTimer = new HashMap<Integer, Integer>();
    static int round;
    static List<Locatable> stunnedEnemies;
    static int ROUND_LIMIT = 100;
    static List<Buster> busters;
    static List<Locatable> ghosts;
    static List<Locatable> enemies;
    static Locatable potential;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int bustersPerPlayer = in.nextInt(); // the amount of busters you control
        int ghostCount = in.nextInt(); // the amount of ghosts on the map
        int myTeamId = in.nextInt(); // if this is 0, your base is on the top left of the map, if it is one, on the bottom right
        defineCheckpoints();

        if(ghostCount < 15){
            ROUND_LIMIT = 30;
        }

        if (myTeamId == 0) {
            base = new Locatable(0.0, 0.0, 0, -1, 0);
            enemyBase = new Locatable(16000.0, 9000.0, 1, 1, 0);
            scoutingPath = new int[]{0, 2, 1, 3};
        } else {
            base = new Locatable(16000.0, 9000.0, 1, 1, 0);
            enemyBase = new Locatable(0.0, 0.0, 0, -1, 0);
            scoutingPath = new int[]{0, 4, 3, 1};
        }

        setUpStunTimer(myTeamId, bustersPerPlayer);

        // game loop
        while (true) {

            busters = new ArrayList<Buster>();
            ghosts = new ArrayList<Locatable>();
            enemies = new ArrayList<Locatable>();
            stunnedEnemies = new ArrayList<Locatable>();
            round++;

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

            for (Locatable enemy : enemies) {
                if (enemy.getState() == 2) {
                    stunnedEnemies.add(enemy);
                }
            }


            for (int i = 0; i < bustersPerPlayer; i++) {

                // Write an action using System.out.println()
                // To debug: System.err.println("Debug messages...");
                System.out.println(decideAction(busters.get(i), ghosts, enemies));
            }
            decreaseStunTimer();
        }
    }

    static String decideAction(Buster buster, List<Locatable> ghosts, List<Locatable> enemies) {

        // strategy for buster with ghost
        //System.err.println("stun cooldown for " +buster.getId() + " is" + stunTimer.get(buster.getId()));
        //System.err.println("enemies : " + enemies.size());

        if(round < 6) return buster.scout();

        if (buster.getState() == 1) {
            if (enemies.size() > 0) {
                for (Locatable e : enemies) {
                    if (buster.canStun(e)) {
                        return buster.stun(e);
                    }
                }
            }
            Buster closestToBase = busterClosestToBaseFromBuster(buster);
            if(closestToBase != null) return buster.eject(closestToBase);

            List<Locatable> ghostsNearby = ghostsNearTo(buster);
            if (ghostsNearby.size() > 0) {
                String herd = buster.herd(ghostsNearby);
                if (herd != null) return herd;
            }

            List<Buster> allies = bustersNearTo(buster);
            for(Buster ally : allies){
                if(ally.getState() == 3 && enemies.size() < allies.size() + 1 && findGhost(ally.getValue()).getState() == 0){
                   return buster.moveTo(ally);
                }
            }

            return buster.saveGhost();

        } else {

            ghosts = filterGhosts(ghosts);

            // when enemies are near

            if (enemies.size() > 0) {
                if(enemies.size() == busters.size() && ghosts.size() > 0 && buster.getNearestGhost().getState() < 8){
                    for(Locatable e : enemies){
                        if(buster.distanceTo(e) < 1760 && stunTimer.get(buster.getId()) == 0 && !stunnedEnemies.contains(e) ){
                            return buster.stun(e);
                        }
                        else if(ghosts.size() > 0){
                            Locatable nearest = buster.getNearestGhost();
                            if(nearest.getState() < enemiesNearTo(buster).size() && buster.canBust(nearest)){
                                return buster.bust(buster.getNearestGhost());
                            }
                        }
                    }
                }

                for (Locatable e : enemies) {
                    if (e.getState() == 1 && buster.canStun(e)) {
                        return buster.stun(e);
                    } else if (e.getState() == 1 && stunTimer.get(buster.getId()) < 10 && round >100) {
                        return buster.intercept(e);
                    } else if (e.getState() == 3 && buster.distanceTo(e) < 1760 && e.distanceTo(enemyBase) > 2000) {
                        Locatable ghost = findGhost(e.getValue());
                        if(ghost == null){
                            return buster.moveTo(enemyClosestToBase());
                        }
                        else if (ghost.getState() < 8 && buster.canStun(e)) {
                            return buster.stun(e);
                        }
                    } else if(ghostsNearTo(buster).size() > 0){
                        for(Locatable ghost : ghostsNearTo(buster)){
                            if(ghost.getState() <=5 && buster.canStun(e)){
                                return buster.stun(e);
                            }
                        }
                    }
                }


                List<Buster> allies = new ArrayList<Buster>();
                allies.addAll(busters);
                allies.remove(buster);
                for (Buster ally : allies) {
                    if (enemiesNearTo(ally).size() > bustersNearTo(ally).size() && buster.distanceTo(ally) < 5000) {
                        return buster.moveTo(ally);
                    }
                }

                // handle ghosts when enemies are near

                if (ghosts.size() > 0) {
                    Locatable nearest = buster.getNearestGhost();
                    List<Buster> bustersNearby = bustersNearTo(nearest);

                    for(Locatable e : enemies){
                        Locatable nearestToEnemy = getNearestGhostTo(e);
                        if(buster.canBust(nearestToEnemy)){
                            return buster.bust(nearestToEnemy);
                        }
                    }

                    if (buster.canBust(nearest)) {
                        if(nearest.getState() == 0){
                            for(Locatable e : enemies){
                                if(buster.canStun(e)){
                                    return buster.stun(e);
                                }
                            }
                        }
                        if (nearest.getValue() > bustersNearby.size()) {
                            return buster.moveTo(nearest);
                        }
                        if(nearest.getState() == 0 && buster.equals(busterClosestToBaseFromGhost(nearest))){
                            return buster.bust(nearest);
                        }
                        else if(nearest.getState() != 0){
                            return buster.bust(nearest);
                        }
                    } else {
                        if (buster.distanceTo(nearest) < 900 && nearest.distanceTo(base) < 800) {
                            return buster.moveTo(new Locatable(nearest.getX() - 700 * base.getValue(), nearest.getY() - 700 * base.getValue(), 0, 0, 0));
                        }else if(buster.distanceTo(nearest) < 900 && nearest.distanceTo(base) > 800){
                            return buster.moveTo(new Locatable(nearest.getX() + 700 * base.getValue(), nearest.getY() + 700 * base.getValue(), 0, 0, 0));
                        }
                        return buster.moveTo(nearest);
                    }

                    allies = new ArrayList<Buster>();
                    allies.addAll(busters);
                    allies.remove(buster);
                    for (Buster ally : allies) {
                        if (enemiesNearTo(ally).size() > bustersNearTo(ally).size()) {
                            return buster.moveTo(ally);
                        }
                    }

                }


            } else {

                // no enemies around

                if (ghosts.size() > 0) {
                    Locatable nearest = buster.getNearestGhost();
                    if (buster.canBust(nearest)) {
                        if(nearest.getState() == 0 && buster.equals(busterClosestToBaseFromGhost(nearest))){
                            return buster.bust(nearest);
                        }
                        else if(nearest.getState() != 0){
                            return buster.bust(nearest);
                        }
                    } else {
                        if (buster.distanceTo(nearest) < 900) {
                            return buster.moveTo(new Locatable(nearest.getX() - 700 * base.getValue(), nearest.getY() - 700 * base.getValue(), 0, 0, 0));
                        }
                        return buster.moveTo(nearest);
                    }
                }
            }
        }
        return buster.scout();
    }

    static void defineCheckpoints() {
        checkpoints[0] = new Locatable(8000, 4500, 0, 0, 0);
        checkpoints[1] = new Locatable(14500, 1500, 0, 0, 0);
        checkpoints[2] = new Locatable(14500, 7500, 0, 0, 0);
        checkpoints[3] = new Locatable(1500, 7500, 0, 0, 0);
        checkpoints[4] = new Locatable(1500, 1500, 0, 0, 0);
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
            }
        }
        return output;
    }

    static List<Buster> bustersNearTo(Locatable point) {
        List<Buster> output = new ArrayList<Buster>();
        for (Buster b : busters) {
            if (point.distanceTo(b) < 1760) {
                output.add(b);
            }
        }
        return output;
    }

    static Buster busterClosestToBaseFromGhost(Locatable ghost){
        List<Buster> allies = bustersNearTo(ghost);
        Buster output = allies.get(0);
        for(Buster b : allies){
            if(b.distanceTo(base) < output.distanceTo(base) && b.getState() == 0){
                output = b;
            }
        }
        return output;
    }

    static Buster busterClosestToBaseFromBuster(Buster buster){
        Buster output = buster;
        List<Buster> allies = new ArrayList<Buster>();
        allies.addAll(busters);
        allies.remove(buster);
        for(Buster b : allies){
            if( output.distanceTo(base) - b.distanceTo(base) > 1000 && b.distanceTo(output) < 4000 && b.distanceTo(buster) > 1600 && b.getState() == 0){
                output = b;
            }
        }
        if(output.equals(buster)) return null;
        return output;
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

    static List<Locatable> enemiesNearTo(Buster buster) {
        List<Locatable> output = new ArrayList<Locatable>();
        for (Locatable e : enemies) {
            if (buster.distanceTo(e) < 2200) {
                output.add(e);
            }
        }
        return output;
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

    static List<Locatable> getWeakGhosts(){
        List<Locatable> output = new ArrayList<Locatable>();
        Locatable weakest = ghosts.get(0);
        for(Locatable ghost:ghosts){
            if(ghost.getState() < weakest.getState()){
                weakest = ghost;
            }
        }
        for(Locatable ghost:ghosts){
            if(ghost.getState() <= weakest.getState()){
                output.add(ghost);
            }
        }
        return output;
    }

    static Locatable enemyClosestToBase(){
        Locatable output = enemies.get(0);
        for(Locatable e : enemies){
            if(e.distanceTo(enemyBase) < output.distanceTo(enemyBase)){
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

        String saveGhost() {
            if (distanceTo(base) < 1600) return release();
            else if (round > 230){
                return moveTo(checkpoints[scoutingPath[1]]);
            }
            else return moveTo(base);
        }

        String scout() {
            if(round < 30){
                current = scoutingPath[(getId() + 1) % 4];
            }
            if (current == scoutingPath.length) current = 0;
            Locatable checkpoint = checkpoints[scoutingPath[current]];
            if(round > 200){
                checkpoint = checkpoints[scoutingPath[2]];
            }
            if (distanceTo(checkpoint) < 400) {
                current++;
            }
            return moveTo(checkpoint);
        }

        String herd(List<Locatable> ghosts) {
            Locatable furthest = base;

            for (Locatable ghost : ghosts) {
                if (ghost.distanceTo(base) > furthest.distanceTo(base)
                        && ((ghost.getX() <= getX() && ghost.getY() <= getY() && base.getId() == 0 && ghost.getX() > 0 && ghost.getY() >= 0)
                        || (ghost.getX() >= getX() && ghost.getY() >= getY() && base.getId() == 1 && ghost.getX() < 16000 && ghost.getY() <= 9000))
                        && ghost.getValue() == 0) {
                    furthest = ghost;
                }
            }

            if (furthest.equals(base) || enemiesNearTo(this).size() > 0) return null;
            else return String.format("MOVE %.0f %.0f", furthest.getX() - 100*base.getValue(), furthest.getY() - 100*base.getValue());
        }

        String bust(Locatable locatable) {
            return "BUST " + locatable.getId();
        }

        boolean canBust(Locatable ghost){
            return distanceTo(ghost) <= 1760 && distanceTo(ghost) >= 900;
        }

        boolean canStun(Locatable enemy){
            return distanceTo(enemy) <= 1760 && stunTimer.get(getId()) == 0 && !stunnedEnemies.contains(enemy);
        }

        String intercept(Locatable enemy) {
            double xDist = enemyBase.getX() - getX();
            double yDist = enemyBase.getY() - getY();

            double alpha;
            double endX;
            double endY;

            if (xDist == 0) xDist = 0.00001;
            alpha = abs(atan(yDist / xDist));

            if (xDist > 0)
                endX = enemy.getX() - cos(alpha) * 800;
            else
                endX = enemy.getX() + cos(alpha) * 800;
            if (yDist > 0)
                endY = enemy.getY() - sin(alpha) * 800;
            else
                endY = enemy.getY() + sin(alpha) * 800;

            return String.format("MOVE %.0f %.0f", endX, endY);
        }

        String eject(Buster buster){
            String futureDecision = decideAction(buster, ghosts, enemies);
            String [] parts = futureDecision.split(" ");
            if(parts.length == 3){
                System.err.println("ejecting to future position");
                return String.format("EJECT %.0f %.0f", Double.parseDouble(parts[1]) - 700*base.getValue(), Double.parseDouble(parts[2]) - 700*base.getValue());
            }
            return String.format("EJECT %.0f %.0f", buster.getX() - 700*base.getValue(), buster.getY() - 700*base.getValue());
        }

        String stun(Locatable locatable) {
            if(getState() != 2)stunTimer.put(getId(), 20);
            System.err.println("drop set to " + locatable.getX() + locatable.getY() );
            stunnedEnemies.add(locatable);
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

        String release() {
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
    }

}