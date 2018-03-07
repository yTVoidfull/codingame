package botters;

import java.util.*;

class Player {

    static final String FIRST_HERO = "DOCTOR_STRANGE";
    static final String SECOND_HERO = "IRONMAN";
    static int gold;
    static List<Item> items = new ArrayList<>();

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int myTeam = in.nextInt();
        int bushAndSpawnPointCount = in.nextInt(); // usefrul from wood1, represents the number of bushes and the number of places where neutral units can spawn
        for (int i = 0; i < bushAndSpawnPointCount; i++) {
            String entityType = in.next(); // BUSH, from wood1 it can also be SPAWN
            int x = in.nextInt();
            int y = in.nextInt();
            int radius = in.nextInt();
        }

        Map<String, List<Item>> heroItems = new HashMap<>();
        int itemCount = in.nextInt(); // useful from wood2
        for (int i = 0; i < itemCount; i++) {
            String itemName = in.next(); // contains keywords such as BRONZE, SILVER and BLADE, BOOTS connected by "_" to help you sort easier
            int itemCost = in.nextInt(); // BRONZE items have lowest cost, the most expensive items are LEGENDARY
            int damage = in.nextInt(); // keyword BLADE is present if the most important item stat is damage
            int health = in.nextInt();
            int maxHealth = in.nextInt();
            int mana = in.nextInt();
            int maxMana = in.nextInt();
            int moveSpeed = in.nextInt(); // keyword BOOTS is present if the most important item stat is moveSpeed
            int manaRegeneration = in.nextInt();
            int isPotion = in.nextInt(); // 0 if it's not instantly consumed

            System.err.println(new Item(itemName, itemCost, damage, health, maxHealth, mana, maxMana, moveSpeed, manaRegeneration, isPotion));

            items.add(new Item(itemName, itemCost, damage, health, maxHealth, mana, maxMana, moveSpeed, manaRegeneration, isPotion));
        }
        int index = 0;
        String[] heroNames = {FIRST_HERO, SECOND_HERO};
        heroItems.put(FIRST_HERO, new ArrayList<>());
        heroItems.put(SECOND_HERO, new ArrayList<>());

        // game loop
        while (true) {
            List<Hero> heroes = new ArrayList<>();
            List<Hero> enemyHeroes = new ArrayList<>();
            Unit tower = null;
            Unit enemyTower = null;
            List<Unit> units = new ArrayList<>();
            List<Unit> enemyUnits = new ArrayList<>();
            List<Unit> groots = new ArrayList<>();

            gold = in.nextInt();
            int enemyGold = in.nextInt();
            int roundType = in.nextInt(); // a positive value will show the number of heroes that await a command
            int entityCount = in.nextInt();
            for (int i = 0; i < entityCount; i++) {
                int unitId = in.nextInt();
                int team = in.nextInt();
                String unitType = in.next(); // UNIT, HERO, TOWER, can also be GROOT from wood1
                int x = in.nextInt();
                int y = in.nextInt();
                int attackRange = in.nextInt();
                int health = in.nextInt();
                int maxHealth = in.nextInt();
                int shield = in.nextInt(); // useful in bronze
                int attackDamage = in.nextInt();
                int movementSpeed = in.nextInt();
                int stunDuration = in.nextInt(); // useful in bronze
                int goldValue = in.nextInt();
                int countDown1 = in.nextInt(); // all countDown and mana variables are useful starting in bronze
                int countDown2 = in.nextInt();
                int countDown3 = in.nextInt();
                int mana = in.nextInt();
                int maxMana = in.nextInt();
                int manaRegeneration = in.nextInt();
                String heroType = in.next(); // DEADPOOL, VALKYRIE, DOCTOR_STRANGE, HULK, IRONMAN
                int isVisible = in.nextInt(); // 0 if it isn't
                int itemsOwned = in.nextInt(); // useful from wood1

                // initialization of units
                if(unitType.equals("HERO")){
                    Hero h = null;
                    switch (heroType){
                        case "IRONMAN" : h = new IronMan(unitId, team, unitType, x, y, attackRange, health, maxHealth, shield, attackDamage, movementSpeed, goldValue, countDown1, countDown2, countDown3, mana, maxMana, manaRegeneration, heroType, stunDuration, isVisible, itemsOwned);
                        break;
                        case "HULK" : h = new Hulk(unitId, team, unitType, x, y, attackRange, health, maxHealth, shield, attackDamage, movementSpeed, goldValue, countDown1, countDown2, countDown3, mana, maxMana, manaRegeneration, heroType, stunDuration, isVisible, itemsOwned);
                        break;
                        case "DEADPOOL" : h = new Deadpool(unitId, team, unitType, x, y, attackRange, health, maxHealth, shield, attackDamage, movementSpeed, goldValue, countDown1, countDown2, countDown3, mana, maxMana, manaRegeneration, heroType, stunDuration, isVisible, itemsOwned);
                        break;
                        case "DOCTOR_STRANGE" : h = new DoctorStrange(unitId, team, unitType, x, y, attackRange, health, maxHealth, shield, attackDamage, movementSpeed, goldValue, countDown1, countDown2, countDown3, mana, maxMana, manaRegeneration, heroType, stunDuration, isVisible, itemsOwned);
                        break;
                        default:h = new Valkyrie(unitId, team, unitType, x, y, attackRange, health, maxHealth, shield, attackDamage, movementSpeed, goldValue, countDown1, countDown2, countDown3, mana, maxMana, manaRegeneration, heroType, stunDuration, isVisible, itemsOwned);
                    }

                    if(team == myTeam){
                        heroes.add(h);
                    }else{
                        enemyHeroes.add(h);
                    }

                }else if(unitType.equals("TOWER") && team == myTeam){
                    tower = new Unit(unitId, team, unitType, x, y, attackRange, health, maxHealth, shield, attackDamage, movementSpeed, goldValue);
                }else if(unitType.equals("TOWER")){
                    enemyTower = new Unit(unitId, team, unitType, x, y, attackRange, health, maxHealth, shield, attackDamage, movementSpeed, goldValue);
                }else if(unitType.equals("UNIT") && team == myTeam){
                    units.add(new Unit(unitId, team, unitType, x, y, attackRange, health, maxHealth, shield, attackDamage, movementSpeed, goldValue));
                }else if(unitType.equals("UNIT")){
                    enemyUnits.add(new Unit(unitId, team, unitType, x, y, attackRange, health, maxHealth, shield, attackDamage, movementSpeed, goldValue));
                }else {
                    groots.add(new Unit(unitId, team, unitType, x, y, attackRange, health, maxHealth, shield, attackDamage, movementSpeed, goldValue));
                }
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // If roundType has a negative value then you need to output a Hero name, such as "DEADPOOL" or "VALKYRIE".
            // Else you need to output roundType number of any valid action, such as "WAIT" or "ATTACK unitId"
            if(roundType < 0){
                System.out.println(heroNames[index++]);
            }else{
                for(Hero hero : heroes){
                    System.out.println(decideAction(hero, enemyHeroes, tower, enemyTower, units, enemyUnits,groots, heroItems));
                }
            }
        }
    }

    private static String decideAction(Hero hero, List<Hero> enemyHeroes, Unit tower, Unit enemyTower, List<Unit> units, List<Unit> enemyUnits,List<Unit> groots, Map<String, List<Item>> heroItems) {

        if(hero.distanceTo(tower) < 50){
            System.err.println("defence under the tower");
            for(Hero h : enemyHeroes){
                if(h.distanceTo(tower) < tower.range
                    && enemyUnits.size() < units.size()){
                    Point p = h.getPositionTowards(tower, hero.range);
                   return hero.moveAttack(p, h);
                }
            }
            for(Unit u : enemyUnits){
                if(u.distanceTo(tower) < tower.range
                    && u.range < u.distanceTo(hero)){
                    Point p = u.getPositionTowards(tower, hero.range);
                    return hero.moveAttack(p, u);
                }
            }
        }

        // run away from the tower
        if(hero.distanceTo(enemyTower) <= enemyTower.range + 50
            || units.size() == 0){
            return(hero.moveTo(tower));
        }

        String buyDecision = hero.purchaseDecision(heroItems);
        if(buyDecision != null){
            return buyDecision;
        }

        // prioritize groots
        for(Unit g : groots){
            if(g.distanceTo(tower) < g.distanceTo(enemyTower)){
                System.err.println("attack groots");
                return hero.attack(g);
            }
        }

        // stay behind the first unit
        Unit furthestUnit = units.get(0);
        for(int i = 1; i < units.size(); i ++){
            if(units.get(i).distanceTo(tower) > furthestUnit.distanceTo(tower)
                    && units.get(i).range < 120){
                furthestUnit = units.get(i);
            }
        }

        if(hero.distanceTo(tower) > furthestUnit.distanceTo(tower) - 10){
            System.err.println("move to furthest unit");
            return hero.moveToDistance(furthestUnit, 10);
        }

        String specialAction = hero.specialMove(enemyHeroes, tower, enemyTower, units, enemyUnits);
        if(specialAction != null){
            System.err.println("special action");
            return specialAction;
        }

        // logic to last hit units
        for(Unit u : enemyUnits){
            if(u.health < hero.damage
                    && u.distanceTo(hero) < hero.range ){
                enemyUnits.remove(u);
                System.err.println("last hit unit near");
                return String.format("ATTACK %d", u.unitId);
            } else if(u.health < hero.damage
                    && u.distanceTo(hero) < hero.range + hero.speed * 0.9
                    && u.range < 100){
                enemyUnits.remove(u);
                System.err.println("last hit unit at distance");
                return String.format("MOVE_ATTACK %d %d %d", u.x-hero.range+1 + 2*hero.team*hero.range, u.y, u.unitId);
            }
        }

        // logic to deny allies
        for(int i = 0; i < units.size(); i++){
            Unit u = units.get(i);
            if(u.health < hero.damage
                    && u.distanceTo(hero) < hero.range){
                units.remove(u);
                System.err.println("deny unit");
                return hero.attack(u);
            }
        }

        // logic to attack only "strong" units
        for(Unit u : enemyUnits){
            if(u.getPotentialAttackers(units) * 27 + hero.damage < u.health){
                System.err.println("attack strong unit");
                return hero.attack(u);
            }
        }

        return "WAIT";
    }

    static Item getBestAffordableItemByDamage(List<Item> items){
        Item i = items.get(0);

        for(Item item : items){
            if (item.cost/(item.damage+1) < i.cost/(i.damage+1)
                && item.cost < gold + 100){
                i = item;
            }
        }

        if(i.cost > gold || i.name.contains("potion")){
            return null;
        }

        return i;
    }

    static Item getWorstItemByDamageOf(Hero hero, Map<String, List<Item>> heroItems){
        List<Item> items = heroItems.get(hero.heroType);
        Item item = null;
        if(items.size() > 0){
            item = items.get(0);
            for(int i = 1; i < items.size(); i++){
                if(items.get(i).cost / (items.get(i).damage+1) < item.cost/(item.damage + 1)){
                    item = items.get(i);
                }
            }
        }
        return item;
    }

    public static class IronMan extends Hero{

        Skill blink = new Skill("BLINK",16, 200, 0.05, 0);
        Skill fireball = new Skill("FIREBALL", 60, 900, 0, 50);
        Skill burning = new Skill("BURNING", 50, 250, 0,100);

        public String blinkTo(int x, int y){
            return String.format("BLINK %d %d", x , y);
        }

        public String sendFireballTo(int x, int y){
            return String.format("FIREBALL %d %d", x, y);
        }

        public String castFireOn(int x, int y){
            return String.format("BURNING %d %d", x, y);
        }

        public IronMan(int unitId, int team, String type, int x, int y, int range, int health, int maxHealth, int shield, int damage, int speed, int goldValue, int cdQ, int cdW, int cdE, int mana, int maxMana, int manaRegen, String heroType, int stunDuration, int isVisible, int itemsOwned) {
            super(unitId, team, type, x, y, range, health, maxHealth, shield, damage, speed, goldValue, cdQ, cdW, cdE, mana, maxMana, manaRegen, heroType, stunDuration, isVisible, itemsOwned);
        }

        @Override
        public String specialMove(List<Hero> enemyHeroes, Unit tower, Unit enemyTower, List<Unit> units, List<Unit> enemyUnits) {
            // logic to attack enemy heroes
            for(Hero enemyHero : enemyHeroes){
                if(enemyHero.health/this.damage > this.health/enemyHero.damage
                    && getPotentialAttackers(enemyUnits) <= enemyHero.getPotentialAttackers(units)){
                    Point newP = enemyHero.getPositionTowards(tower, (int)(this.speed*0.9));
                    return moveAttack(newP, enemyHero);
                }

                else if(units.size() > enemyUnits.size()
                    && this.distanceTo(enemyHero) < this.speed * 0.9){
                    return attack(enemyHero);
                }
            }

            return null;
        }

        @Override
        public String purchaseDecision(Map<String, List<Item>> heroItems){
            // get items
            Item i = getBestAffordableItemByDamage(items);
            if(i != null && this.itemsOwned < 4){
                heroItems.get(this.heroType).add(i);
                gold -= i.cost;
                return this.buy(i);
            }else if(i != null) {
                Item worst = getWorstItemByDamageOf(this, heroItems);
                if(worst != null
                    && i.damage > worst.damage){
                    heroItems.get(this.heroType).remove(i);
                    return this.sell(worst);
                }
            }

            return null;
        }

    }

    public static class Hulk extends Hero{

        public Hulk(int unitId, int team, String type, int x, int y, int range, int health, int maxHealth, int shield, int damage, int speed, int goldValue, int cdQ, int cdW, int cdE, int mana, int maxMana, int manaRegen, String heroType, int stunDuration, int isVisible, int itemsOwned) {
            super(unitId, team, type, x, y, range, health, maxHealth, shield, damage, speed, goldValue, cdQ, cdW, cdE, mana, maxMana, manaRegen, heroType, stunDuration, isVisible, itemsOwned);
        }

        @Override
        public String specialMove(List<Hero> enemyHeroes, Unit tower, Unit enemyTower, List<Unit> units, List<Unit> enemyUnits) {
            for(Hero enemyHero : enemyHeroes){
                if(units.size() > enemyUnits.size()
                    && this.distanceTo(enemyHero) < this.speed * 0.9){
                    return "ATTACK " + enemyHero.unitId;
                }
            }

            return null;
        }

        @Override
        public String purchaseDecision(Map<String, List<Item>> heroItems) {
            Item i = getBestAffordableItemByDamage(items);
            if(i != null && this.itemsOwned < 4){
                heroItems.get(this.heroType).add(i);
                gold -= i.cost;
                return this.buy(i);
            }else if(i != null) {
                Item worst = getWorstItemByDamageOf(this, heroItems);
                if(worst != null
                    && i.damage > worst.damage){
                    heroItems.get(this.heroType).remove(i);
                    return this.sell(worst);
                }
            }

            return null;
        }
    }

    public static class DoctorStrange extends Hero{

        public DoctorStrange(int unitId, int team, String type, int x, int y, int range, int health, int maxHealth, int shield, int damage, int speed, int goldValue, int cdQ, int cdW, int cdE, int mana, int maxMana, int manaRegen, String heroType, int stunDuration, int isVisible, int itemsOwned) {
            super(unitId, team, type, x, y, range, health, maxHealth, shield, damage, speed, goldValue, cdQ, cdW, cdE, mana, maxMana, manaRegen, heroType, stunDuration, isVisible, itemsOwned);
        }

        @Override
        public String specialMove(List<Hero> enemyHeroes, Unit tower, Unit enemyTower, List<Unit> units, List<Unit> enemyUnits) {
            // logic to attack enemy heroes
            for(Hero enemyHero : enemyHeroes) {
                if (enemyHero.health / this.damage > this.health / enemyHero.damage
                    && getPotentialAttackers(enemyUnits) <= enemyHero.getPotentialAttackers(units)) {
                    Point newP = enemyHero.getPositionTowards(tower, (int) (this.speed * 0.9));
                    return moveAttack(newP, enemyHero);
                } else if (units.size() > enemyUnits.size()
                    && this.distanceTo(enemyHero) < this.speed * 0.9) {
                    return attack(enemyHero);
                }
            }

            return null;
        }

        @Override
        public String purchaseDecision(Map<String, List<Item>> heroItems) {
            Item i = getBestAffordableItemByDamage(items);
            if(i != null && this.itemsOwned < 4){
                heroItems.get(this.heroType).add(i);
                gold -= i.cost;
                return this.buy(i);
            }else if(i != null) {
                Item worst = getWorstItemByDamageOf(this, heroItems);
                if(worst != null
                    && i.damage > worst.damage){
                    heroItems.get(this.heroType).remove(i);
                    gold += worst.cost;
                    return this.sell(worst);
                }
            }

            return null;
        }
    }

    public static class Deadpool extends Hero{

        public Deadpool(int unitId, int team, String type, int x, int y, int range, int health, int maxHealth, int shield, int damage, int speed, int goldValue, int cdQ, int cdW, int cdE, int mana, int maxMana, int manaRegen, String heroType, int stunDuration, int isVisible, int itemsOwned) {
            super(unitId, team, type, x, y, range, health, maxHealth, shield, damage, speed, goldValue, cdQ, cdW, cdE, mana, maxMana, manaRegen, heroType, stunDuration, isVisible, itemsOwned);
        }

        @Override
        public String specialMove(List<Hero> enemyHeroes, Unit tower, Unit enemyTower, List<Unit> units, List<Unit> enemyUnits) {
            for(Hero enemyHero : enemyHeroes){
                if(units.size() > enemyUnits.size()
                    && this.distanceTo(enemyHero) < this.speed * 0.9){
                    return "ATTACK " + enemyHero.unitId;
                }
            }

            return null;
        }

        @Override
        public String purchaseDecision(Map<String, List<Item>> heroItems) {
            Item i = getBestAffordableItemByDamage(items);
            if(i != null && this.itemsOwned < 4){
                heroItems.get(this.heroType).add(i);
                gold -= i.cost;
                return this.buy(i);
            }else if(i != null) {
                Item worst = getWorstItemByDamageOf(this, heroItems);
                if(worst != null
                    && i.damage > worst.damage){
                    heroItems.get(this.heroType).remove(i);
                    return this.sell(worst);
                }
            }

            return null;
        }
    }

    public static class Valkyrie extends Hero{

        public Valkyrie(int unitId, int team, String type, int x, int y, int range, int health, int maxHealth, int shield, int damage, int speed, int goldValue, int cdQ, int cdW, int cdE, int mana, int maxMana, int manaRegen, String heroType, int stunDuration, int isVisible, int itemsOwned) {
            super(unitId, team, type, x, y, range, health, maxHealth, shield, damage, speed, goldValue, cdQ, cdW, cdE, mana, maxMana, manaRegen, heroType, stunDuration, isVisible, itemsOwned);
        }

        @Override
        public String specialMove(List<Hero> enemyHeroes, Unit tower, Unit enemyTower, List<Unit> units, List<Unit> enemyUnits) {
            for(Hero enemyHero : enemyHeroes){
                if(units.size() > enemyUnits.size()
                    && this.distanceTo(enemyHero) < this.speed * 0.9){
                    return "ATTACK " + enemyHero.unitId;
                }
            }

            return null;
        }

        @Override
        public String purchaseDecision(Map<String, List<Item>> heroItems) {
            Item i = getBestAffordableItemByDamage(items);
            if(i != null && this.itemsOwned < 4){
                heroItems.get(this.heroType).add(i);
                gold -= i.cost;
                return this.buy(i);
            }else if(i != null) {
                Item worst = getWorstItemByDamageOf(this, heroItems);
                if(worst != null
                    && i.damage > worst.damage){
                    heroItems.get(this.heroType).remove(i);
                    return this.sell(worst);
                }
            }

            return null;
        }
    }

    public static class Item {
        String name;
        int cost;
        int damage;
        int health;
        int maxHealth;
        int mana;
        int maxMana;
        int moveSpeed;
        int manaRegeneration;
        int isPotion;

        public Item(String name, int cost, int damage, int health, int maxHealth, int mana, int maxMana, int moveSpeed, int manaRegeneration, int isPotion) {
            this.name = name;
            this.cost = cost;
            this.damage = damage;
            this.health = health;
            this.maxHealth = maxHealth;
            this.mana = mana;
            this.maxMana = maxMana;
            this.moveSpeed = moveSpeed;
            this.manaRegeneration = manaRegeneration;
            this.isPotion = isPotion;
        }

        @Override
        public String toString() {
            return "Item{" +
                "name='" + name + '\'' +
                ", cost=" + cost +
                ", damage=" + damage +
                ", health=" + health +
                ", maxHealth=" + maxHealth +
                ", mana=" + mana +
                ", maxMana=" + maxMana +
                ", moveSpeed=" + moveSpeed +
                ", manaRegeneration=" + manaRegeneration +
                ", isPotion=" + isPotion +
                '}';
        }
    }

    public static abstract class Hero extends Unit{

        int cdQ;
        int cdW;
        int cdE;
        int mana;
        int maxMana;
        int manaRegen;
        String heroType;
        int stunDuration;
        int isVisible;
        int itemsOwned;
        int gold;

        public Hero(int unitId, int team, String type, int x, int y, int range, int health, int maxHealth, int shield, int damage, int speed, int goldValue, int cdQ, int cdW, int cdE, int mana, int maxMana, int manaRegen, String heroType, int stunDuration, int isVisible, int itemsOwned) {
            super(unitId, team, type, x, y, range, health, maxHealth, shield, damage, speed, goldValue);
            this.cdQ = cdQ;
            this.cdW = cdW;
            this.cdE = cdE;
            this.mana = mana;
            this.maxMana = maxMana;
            this.manaRegen = manaRegen;
            this.heroType = heroType;
            this.stunDuration = stunDuration;
            this.isVisible = isVisible;
            this.itemsOwned = itemsOwned;
            this.gold = goldValue;
        }

        public String buy(Item item){
            return "BUY " + item.name;
        }

        public String sell(Item item){
            return "SELL " + item.name;
        }

        public String moveTo(Unit other){
            return String.format("MOVE %d %d", other.x, other.y);
        }

        public String moveToDistance(Unit other, int dist){
            return String.format("MOVE %d %d", other.x - dist + 2 * team * dist, other.y);
        }


        public String attack(Unit g) {
            return String.format("ATTACK %d", g.unitId);
        }

        public abstract String specialMove(List<Hero> enemyHeroes, Unit tower, Unit enemyTower, List<Unit> units, List<Unit> enemyUnits);

        public abstract String purchaseDecision(Map<String, List<Item>> heroItems);

        public String moveAttack(Point p, Unit unit){
            return String.format("MOVE_ATTACK %d %d %d", p.x, p.y, unit.unitId);
        }
    }

    public static class Unit extends Point{

        int unitId;
        int team;
        String type;
        int range;
        int health;
        int maxHealth;
        int shield;
        int damage;
        int speed;
        int goldValue;

        public Unit(int unitId, int team, String type, int x, int y, int range, int health, int maxHealth, int shield, int damage, int speed, int goldValue) {
            super(x, y);
            this.unitId = unitId;
            this.team = team;
            this.type = type;
            this.range = range;
            this.health = health;
            this.maxHealth = maxHealth;
            this.shield = shield;
            this.damage = damage;
            this.speed = speed;
            this.goldValue = goldValue;
        }


        public int getPotentialAttackers(List<Unit> units){
            int i = 0;
            for(Unit u : units){
                if(u.range >= u.distanceTo(this)){
                    i++;
                }
            }
            return i;
        }

        @Override
        public String toString() {
            return "Unit{" +
                "unitId=" + unitId +
                ", team=" + team +
                ", type='" + type + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", range=" + range +
                ", health=" + health +
                ", maxHealth=" + maxHealth +
                ", shield=" + shield +
                ", damage=" + damage +
                ", speed=" + speed +
                ", goldValue=" + goldValue +
                '}';
        }
    }

    public static class Skill{

        String name;
        int mana;
        int range;
        double duration;
        int radius;

        public Skill(String name, int mana, int range, double duration, int radius) {
            this.name = name;
            this.mana = mana;
            this.range = range;
            this.duration = duration;
            this.radius = radius;
        }

        public boolean canCast(Hero hero, Unit unit){
            return hero.distanceTo(unit) < range;
        }
    }

    public static class Point{
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point getPositionTowards(Point other, int dist){
            double dx = x - other.x;
            double dy = y - other.y;

            double alpha = dy / dx;

            double nx = x;
            double ny = y;

            if(x == other.x){
                ny = y + dist * (other.y - y) / Math.abs(other.y - y);
            }else if(y == other.y){
                nx = x + dist * (other.x - x) / Math.abs(other.x - x);
            }else {
                if(x > other.x){
                    nx = x - dist/Math.sqrt(1 + alpha*alpha);
                }else {
                    nx = x + dist/Math.sqrt(1 + alpha * alpha);
                }

                ny = y + (nx - x) * alpha;
            }

            return new Point((int) nx, (int)ny);
        }

        public boolean isOnMap(){
            return x > 0 && y > 0;
        }

        public int distanceTo(Unit other){
            return (int) Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y));
        }
    }

}