package practice;

import java.util.*;

public class War {

    static Deque<Card> playerTwo = new ArrayDeque<>();
    static Deque<Card> playerOne = new ArrayDeque<>();
    static List<Card> playerOneInGameCards = new ArrayList<>();
    static List<Card> playerTwoInGameCards = new ArrayList<>();
    static Card playerOneUpperCard;
    static Card playerTwoUpperCard;
    static int round = 0;
    static String winner;

    static void play() {
        while (thereIsNoWinner()){
            playOneRound();
            checkIfPlayerOneWonTheGame();
            checkIfPlayerTwoWonTheGame();
        }
        if(winner.length() > 1){
            System.out.println(winner);
        }else {
            System.out.println(winner + " " + round);
        }
    }

    private static void checkIfPlayerOneWonTheGame(){
        if(thereIsNoWinner() && playerTwo.size() == 0){
            winner = "1";
        }
    }

    private static void checkIfPlayerTwoWonTheGame(){
        if(thereIsNoWinner() && playerOne.size() == 0){
            winner = "2";
        }
    }

    private static void playOneRound() {
        resetInGameCards();
        performWarActions();
        if(thereIsNoWinner()){
            checkIfFirstPlayerWonAndFinishRound();
            checkIfSecondPlayerWonAndFinishRound();
        }
    }

    static void resetInGameCards(){
        playerOneInGameCards = new ArrayList<>();
        playerTwoInGameCards = new ArrayList<>();
    }

    static boolean thereIsNoWinner(){
        return winner == null;
    }

    public static void checkIfItsADraw(){
        if(playerOneUpperCard == null || playerTwoUpperCard == null){
            winner = "PAT";
        }
    }

    public static void performWarActions(){
        playerOneUpperCard = playerOnePutsCardInGame();
        playerTwoUpperCard = playerTwoPutsCardInGame();
        while(playerOneUpperCard == playerTwoUpperCard && playerOneUpperCard != null){
            playerOneUpperCard = playerOnePutsThreeCardsForWarAndPutsAnotherOneInGame();
            playerTwoUpperCard = playerTwoPutsThreeCardsForWarAndPutsAnotherOneIgGame();
        }
        checkIfItsADraw();
    }

    public static void checkIfFirstPlayerWonAndFinishRound(){
        if(playerOneUpperCard.isBiggerThan(playerTwoUpperCard)){
            playerOne.addAll(playerOneInGameCards);
            playerOne.addAll(playerTwoInGameCards);
            round ++;
        }
    }

    public static void checkIfSecondPlayerWonAndFinishRound(){
        if(playerTwoUpperCard.isBiggerThan(playerOneUpperCard)){
            playerTwo.addAll(playerOneInGameCards);
            playerTwo.addAll(playerTwoInGameCards);
            round ++;
        }
    }

    static Card playerOnePutsCardInGame(){
        Card c = playerOne.poll();
        playerOneInGameCards.add(c);
        return c;
    }

    static Card playerOnePutsThreeCardsForWarAndPutsAnotherOneInGame(){
        for(int i = 0; i < 3; i++){
            playerOneInGameCards.add(playerOne.poll());
        }
        return playerOnePutsCardInGame();
    }

    static Card playerTwoPutsCardInGame(){
        Card c = playerTwo.poll();
        playerTwoInGameCards.add(c);
        return c;
    }

    static Card playerTwoPutsThreeCardsForWarAndPutsAnotherOneIgGame(){
        for(int i = 0; i < 3; i++){
            playerTwoInGameCards.add(playerTwo.poll());
        }
        return playerTwoPutsCardInGame();
    }

    public enum Card {

        TWO("2", 2),
        THREE("3", 3),
        FOUR("4", 4),
        FIVE("5", 5),
        SIX("6", 6),
        SEVEN("7", 7),
        EIGHT("8", 8),
        NINE("9", 9),
        TEN("10", 10),
        JACK("J", 11),
        QUEEN("Q", 12),
        KING("K", 13),
        ACE("A", 14);

        private String name;
        private int value;

        Card(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public boolean isBiggerThan(Card other) {
            return this.value > other.value;
        }

        public static Card fromFullName(String fullName) {
            int length = fullName.length();
            for(Card c : Card.values()){
                if(fullName.substring(0, length - 1).equals(c.getName())){
                    return c;
                }
            }
            return null;
        }
    }
}
