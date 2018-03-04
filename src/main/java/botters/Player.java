package botters;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Player {

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

            items.add(new Item(itemName, itemCost, damage, health, maxHealth, mana, maxMana, moveSpeed, manaRegeneration, isPotion));
        }
        int index = 0;
        String[] heroNames = {"DOCTOR_STRANGE", "IRONMAN"};

        // game loop
        while (true) {
            List<Hero> heroes = new ArrayList<>();
            List<Hero> enemyHeroes = new ArrayList<>();
            Unit tower = null;
            Unit enemyTower = null;
            List<Unit> groots = new ArrayList<>();
            List<Unit> enemyGroots = new ArrayList<>();

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

                if(unitType.equals("HERO") && team == myTeam){
                    heroes.add(new Hero(unitId, team, UnitType.valueOf(unitType), x, y, attackRange, health, maxHealth, shield, attackDamage, movementSpeed, goldValue, countDown1, countDown2, countDown3, mana, maxMana, manaRegeneration, heroType, stunDuration, isVisible, itemsOwned, gold));
                }else if(unitType.equals("HERO")){
                    enemyHeroes.add(new Hero(unitId, team, UnitType.valueOf(unitType), x, y, attackRange, health, maxHealth, shield, attackDamage, movementSpeed, goldValue, countDown1, countDown2, countDown3, mana, maxMana, manaRegeneration, heroType, stunDuration, isVisible, itemsOwned, enemyGold));
                }else if(unitType.equals("TOWER") && team == myTeam){
                    tower = new Unit(unitId, team, UnitType.valueOf(unitType), x, y, attackRange, health, maxHealth, shield, attackDamage, movementSpeed, goldValue);
                }else if(unitType.equals("TOWER")){
                    enemyTower = new Unit(unitId, team, UnitType.valueOf(unitType), x, y, attackRange, health, maxHealth, shield, attackDamage, movementSpeed, goldValue);
                }else if(team == myTeam){
                    groots.add(new Unit(unitId, team, UnitType.valueOf(unitType), x, y, attackRange, health, maxHealth, shield, attackDamage, movementSpeed, goldValue));
                }else{
                    enemyGroots.add(new Unit(unitId, team, UnitType.valueOf(unitType), x, y, attackRange, health, maxHealth, shield, attackDamage, movementSpeed, goldValue));
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
                    if(enemyHeroes.size() > 0){
                        System.out.println(decideAction(hero, enemyHeroes.get(0), tower, enemyTower, groots, enemyGroots, items));
                    }else{
                        System.out.println(decideAction(hero, null, tower, enemyTower, groots, enemyGroots, items));
                    }
                }
            }
        }
    }

    private static String decideAction(Hero hero, Hero enemyHero, Unit tower, Unit enemyTower, List<Unit> groots, List<Unit> enemyGroots, List<Item> items) {
        if(enemyHero !=null
                && hero.distanceTo(enemyTower) <= enemyTower.range + 50
                || groots.size() == 0){
            return(hero.moveTo(tower));
        }


        Unit furthestGroot = groots.get(0);
        for(int i = 1; i < groots.size(); i ++){
            if(groots.get(i).distanceTo(tower) > furthestGroot.distanceTo(tower)
                    && groots.get(i).range < 150){
                furthestGroot = groots.get(i);
            }
        }

        if(hero.distanceTo(tower) > furthestGroot.distanceTo(tower) - 50){
            return hero.moveToDistance(furthestGroot, hero.range);
        }

        if(enemyHero != null
                && hero.distanceTo(enemyHero) < hero.range &&
                groots.size() > enemyGroots.size()){
            return "ATTACK_NEAREST HERO";
        }


        for(int i = 0; i < enemyGroots.size(); i++){
            Unit g = enemyGroots.get(i);
            if(g.health < hero.damage
                    && g.distanceTo(hero) < hero.range ){
                enemyGroots.remove(g);
                return String.format("ATTACK %d", g.unitId);
            } else if(g.health < hero.damage
                    && g.distanceTo(hero) < hero.range + hero.speed
                    && g.range < 100){
                enemyGroots.remove(g);
                return String.format("MOVE_ATTACK %d %d %d", g.x-hero.range+1 + 2*hero.team*hero.range, g.y, g.unitId);
            }
        }

        for(int i = 0; i < groots.size(); i++){
            Unit g = groots.get(i);
            if(g.health < hero.damage
                    && g.distanceTo(hero) < hero.range){
                groots.remove(g);
                return String.format("ATTACK %d", g.unitId);
            }
        }

        Item i = getAnyItemByGoldAndDamage(hero.gold, "Blade", items);
        if(i != null && hero.itemsOwned < 4){
            return hero.buy(i);
        }

        return "ATTACK_NEAREST UNIT";
    }

    private static Item getAnyItemByGoldAndDamage(int gold, String type,List<Item> items){
        Item i = null;
        for(Item item : items){
            if(item.name.contains(type)
                    && item.cost < gold){
                i =  item;
                break;
            }
        }

        for(Item item : items){
            if(item.name.contains(type)
                    && i != null
                    && item.damage > i.damage
                    && item.cost < gold){
                i = item;
                break;
            }
        }

        return i;
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

        public Hero(int unitId, int team, UnitType type, int x, int y, int range, int health, int maxHealth, int shield, int damage, int speed, int goldValue, int cdQ, int cdW, int cdE, int mana, int maxMana, int manaRegen, String heroType, int stunDuration, int isVisible, int itemsOwned, int gold) {
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
    }

    public static class Unit{

        int unitId;
        int team;
        UnitType type;
        int x;
        int y;
        int range;
        int health;
        int maxHealth;
        int shield;
        int damage;
        int speed;
        int goldValue;

        public Unit(int unitId, int team, UnitType type, int x, int y, int range, int health, int maxHealth, int shield, int damage, int speed, int goldValue) {
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

    public enum UnitType{
        HERO,
        UNIT,
        TOWER,
        GROOT;
    }


}