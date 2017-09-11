package practice;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import static practice.War.*;

import java.util.ArrayDeque;
import java.util.ArrayList;


public class WarTest {

    static void resetWinnerRoundPlayersAndInGameCards(){
        playerOne = new ArrayDeque<>();
        playerTwo = new ArrayDeque<>();
        playerOneInGameCards = new ArrayList<>();
        playerTwoInGameCards = new ArrayList<>();
        round = 0;
        winner = null;
    }

    @Test
    public void canReadAStringAndGenerateCard() throws Exception {
        Card card = Card.fromFullName("AD");
        assertThat(card.getName()).isEqualTo("A");
    }

    @Test
    public void canCreateTwoDifferentCards() throws Exception {
        Card card = Card.valueOf("ACE");
        Card card1 = Card.KING;
        assertThat(card.getName()).isEqualTo("A");
        assertThat(card1.getName()).isEqualTo("K");
    }

    @Test
    public void aceIsBiggerThanKing() throws Exception {
        Card ace = Card.valueOf("ACE");
        Card king = Card.KING;
        assertThat(ace.isBiggerThan(king)).isEqualTo(true);
    }

    @Test
    public void queenIsNotBiggerThanQueen() throws Exception {
        Card queen = Card.QUEEN;
        Card queen1 = Card.QUEEN;
        assertThat(queen.isBiggerThan(queen1)).isEqualTo(false);
    }

    @Test
    public void eachPlayerCanTakeCards() throws Exception {
        resetWinnerRoundPlayersAndInGameCards();
        playerOne.add(Card.QUEEN);
        playerTwo.add(Card.KING);
    }

    @Test
    public void playerOneWithKingTakesCardWithQueen() throws Exception {
        resetWinnerRoundPlayersAndInGameCards();
        playerOne.add(Card.KING);
        playerTwo.add(Card.QUEEN);
        play();
        assertThat(playerOne.poll()).isEqualTo(Card.KING);
        assertThat(playerOne.poll()).isEqualTo(Card.QUEEN);
    }

    @Test
    public void playerTwoWithAceTakesEight() throws Exception {
        resetWinnerRoundPlayersAndInGameCards();
        playerOne.add(Card.EIGHT);
        playerTwo.add(Card.ACE);
        play();
        assertThat(playerTwo.poll()).isEqualTo(Card.EIGHT);
        assertThat(playerTwo.poll()).isEqualTo(Card.ACE);
    }

    @Test
    public void equalCardsMeansAWarIsStartedAndTheWinnerTakesAll() throws Exception {
        resetWinnerRoundPlayersAndInGameCards();
        playerOne.add(Card.QUEEN);
        playerOne.add(Card.QUEEN);
        playerOne.add(Card.QUEEN);
        playerOne.add(Card.QUEEN);
        playerOne.add(Card.QUEEN);
        playerTwo.add(Card.QUEEN);
        playerTwo.add(Card.QUEEN);
        playerTwo.add(Card.QUEEN);
        playerTwo.add(Card.QUEEN);
        playerTwo.add(Card.KING);
        play();
        assertThat(playerOne.size()).isEqualTo(0);
        assertThat(playerTwo.size()).isEqualTo(10);
    }

    @Test
    public void whenSomebodyWinsTheRoundIncreases() throws Exception {
        resetWinnerRoundPlayersAndInGameCards();
        playerOne.add(Card.EIGHT);
        playerTwo.add(Card.ACE);
        play();
        assertThat(round).isEqualTo(1);
    }

    @Test
    public void aWarIsPartOfASingleRound() throws Exception {
        resetWinnerRoundPlayersAndInGameCards();
        playerOne.add(Card.QUEEN);
        playerOne.add(Card.QUEEN);
        playerOne.add(Card.QUEEN);
        playerOne.add(Card.QUEEN);
        playerOne.add(Card.QUEEN);
        playerTwo.add(Card.QUEEN);
        playerTwo.add(Card.QUEEN);
        playerTwo.add(Card.QUEEN);
        playerTwo.add(Card.QUEEN);
        playerTwo.add(Card.KING);
        play();
        assertThat(round).isEqualTo(1);
    }

    @Test
    public void WhenAPlayerDoesNotHaveEnoughCardsInAWarItsADraw() throws Exception {
        resetWinnerRoundPlayersAndInGameCards();
        playerOne.add(Card.QUEEN);
        playerOne.add(Card.QUEEN);
        playerOne.add(Card.QUEEN);
        playerOne.add(Card.QUEEN);
        playerTwo.add(Card.QUEEN);
        playerTwo.add(Card.QUEEN);
        playerTwo.add(Card.QUEEN);
        playerTwo.add(Card.QUEEN);
        playerTwo.add(Card.KING);
        play();
        assertThat(winner).isEqualTo("PAT");
    }

    @Test
    public void WhenAPlayerHasNoCardsTheOtherWins() throws Exception {
        resetWinnerRoundPlayersAndInGameCards();
        playerOne.add(Card.QUEEN);
        playerTwo.add(Card.KING);
        play();
        assertThat(winner).isEqualTo("2");
    }

    @Test
    public void testARealBattle() throws Exception {
        resetWinnerRoundPlayersAndInGameCards();
        String playerOneHand = "10H KD 6C 10S 8S AD QS 3D 7H KH 9D 2D JC KS 3S 2S QC AC JH 7D KC 10D 4C AS 5D 5S";
        String playerTwoHand = "2H 9C 8C 4S 5C AH JD QH 7C 5H 4H 6H 6S QD 9H 10C 4D JS 6D 3H 8H 3C 7S 9S 8D 2C";
        for(String cardS : playerOneHand.split(" ")){
            playerOne.add(Card.fromFullName(cardS));
        }
        for(String cardS : playerTwoHand.split(" ")){
            playerTwo.add(Card.fromFullName(cardS));
        }
        play();
        assertThat(winner).isEqualTo("1");
        assertThat(round).isEqualTo(52);
    }
}
