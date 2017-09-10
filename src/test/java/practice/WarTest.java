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
        assertThat(playerTwo.size()).isEqualTo(8);
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

}
