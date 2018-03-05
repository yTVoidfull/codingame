package botters;

import java.util.*;

class Player {

    private static final String FIRST_HERO = "DOCTOR_STRANGE";
    private static final String SECOND_HERO = "IRONMAN";

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
        List<Item> items = new ArrayList<>();
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

            int gold = in.nextInt();
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
                if(unitType.equals("HERO") && team == myTeam){
                    Hero hero = new Hero(unitId, team, unitType, x, y, attackRange, health, maxHealth, shield, attackDamage, movementSpeed, goldValue, countDown1, countDown2, countDown3, mana, maxMana, manaRegeneration, heroType, stunDuration, isVisible, itemsOwned, gold);
                    hero.totalGold = gold;
                    heroes.add(hero);
                }else if(unitType.equals("HERO")){
                    enemyHeroes.add(new Hero(unitId, team, unitType, x, y, attackRange, health, maxHealth, shield, attackDamage, movementSpeed, goldValue, countDown1, countDown2, countDown3, mana, maxMana, manaRegeneration, heroType, stunDuration, isVisible, itemsOwned, enemyGold));
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
                    System.out.println(decideAction(hero, enemyHeroes, tower, enemyTower, units, enemyUnits, items, heroItems));
                }
            }
        }
    }

    private static String decideAction(Hero hero, List<Hero> enemyHeroes, Unit tower, Unit enemyTower, List<Unit> units, List<Unit> enemyUnits, List<Item> items, Map<String, List<Item>> heroItems) {
        // run away from the tower
        if(hero.distanceTo(enemyTower) <= enemyTower.range + 50
            || units.size() == 0){
            return(hero.moveTo(tower));
        }


        // stay behind the first unit
        Unit furthestUnit = units.get(0);
        for(int i = 1; i < units.size(); i ++){
            if(units.get(i).distanceTo(tower) > furthestUnit.distanceTo(tower)
                    && units.get(i).range < 120){
                furthestUnit = units.get(i);
            }
        }

        if(hero.distanceTo(tower) > furthestUnit.distanceTo(tower) - 50){
            return hero.moveToDistance(furthestUnit, 120);
        }

        // logic to attack enemy heroes
        for(Hero enemyHero : enemyHeroes){
            if(units.size() > enemyUnits.size()
                && hero.distanceTo(enemyHero) < hero.speed){
                return "ATTACK " + enemyHero.unitId;
            }
        }


        // logic to attach units
        for(int i = 0; i < enemyUnits.size(); i++){
            Unit g = enemyUnits.get(i);
            if(g.health < hero.damage
                    && g.distanceTo(hero) < hero.range ){
                enemyUnits.remove(g);
                return String.format("ATTACK %d", g.unitId);
            } else if(g.health < hero.damage
                    && g.distanceTo(hero) < hero.range + hero.speed
                    && g.range < 100){
                enemyUnits.remove(g);
                return String.format("MOVE_ATTACK %d %d %d", g.x-hero.range+1 + 2*hero.team*hero.range, g.y, g.unitId);
            }
        }

        // logic to deny allies
        for(int i = 0; i < units.size(); i++){
            Unit g = units.get(i);
            if(g.health < hero.damage
                    && g.distanceTo(hero) < hero.range){
                units.remove(g);
                return String.format("ATTACK %d", g.unitId);
            }
        }

        // get items
        Item i = getBestAffordableItemByDamage(hero.totalGold, items);
        if(i != null && hero.itemsOwned < 4){
            heroItems.get(hero.heroType).add(i);
            hero.totalGold -= i.cost;
            return hero.buy(i);
        }else if(i != null) {
            Item worst = getWorstItemByDamage(hero, heroItems);
            if(worst != null
                && i.damage > worst.damage){
                heroItems.get(hero.heroType).remove(i);
                return hero.sell(worst);
            }
        }

        return "ATTACK_NEAREST UNIT";
    }

    private static Item getBestAffordableItemByDamage(int gold, List<Item> items){
        Item i = items.get(0);

        for(Item item : items){
            if (item.cost/(item.damage+1) < i.cost/(i.damage+1)
                && item.cost < gold + 150){
                i = item;
            }
        }

        if(i.cost > gold || i.name.contains("potion")){
            return null;
        }

        return i;
    }

    private static Item getWorstItemByDamage(Hero hero, Map<String, List<Item>> heroItems){
        System.err.println(heroItems.get(hero.heroType).size());
        List<Item> items = heroItems.get(hero.heroType);
        Item item = null;
        if(items.size() > 0){
            item = items.get(0);
            for(int i = 1; i < items.size(); i++){
                if(items.get(i).damage < item.damage){
                    item = items.get(i);
                }
            }
        }
        System.err.println(item == null);
        return item;
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

    public static class Hero extends Unit{

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
        static int totalGold;

        public Hero(int unitId, int team, String type, int x, int y, int range, int health, int maxHealth, int shield, int damage, int speed, int goldValue, int cdQ, int cdW, int cdE, int mana, int maxMana, int manaRegen, String heroType, int stunDuration, int isVisible, int itemsOwned, int gold) {
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
            this.gold = gold;
        }

        public String buy(Item item){
            return "BUY " + item.name;
        }

        public String sell(Item item){
            return "SELL " + item.name;
        }
    }

    public static class Unit{

        int unitId;
        int team;
        String type;
        int x;
        int y;
        int range;
        int health;
        int maxHealth;
        int shield;
        int damage;
        int speed;
        int goldValue;

        public Unit(int unitId, int team, String type, int x, int y, int range, int health, int maxHealth, int shield, int damage, int speed, int goldValue) {
            this.unitId = unitId;
            this.team = team;
            this.type = type;
            this.x = x;
            this.y = y;
            this.range = range;
            this.health = health;
            this.maxHealth = maxHealth;
            this.shield = shield;
            this.damage = damage;
            this.speed = speed;
            this.goldValue = goldValue;
        }


        public int distanceTo(Unit other){
            return (int) Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y));
        }

        public String moveTo(Unit other){
            return String.format("MOVE %d %d", other.x, other.y);
        }

        public String moveToDistance(Unit other, int dist){
            return String.format("MOVE %d %d", other.x - dist + 2 * team * dist, other.y);
        }
    }

}