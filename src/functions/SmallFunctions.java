package functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.ActionsInput;
import fileio.CardInput;
import fileio.GameInput;
import fileio.Player;

import java.util.ArrayList;
import java.util.Objects;

public final class SmallFunctions {

    private SmallFunctions() { }

    /**
     * Function that resets the frozen and hasAttacked status of a player's cards on table (front
     * and back row) after each turn. It also resets the hero card hasAttacked status.
     */
    public static void resetCards(final ArrayList<ArrayList<CardInput>> table,
                                  final int frontRow, final int backRow, final Player player) {
        for (int i = 0; i < table.get(frontRow).size(); i++) {
            if (table.get(frontRow).get(i).isFrozen()) {
                table.get(frontRow).get(i).setFrozen(false);
            }
            if (table.get(frontRow).get(i).isHasAttacked()) {
                table.get(frontRow).get(i).setHasAttacked(false);
            }
        }
        for (int i = 0; i < table.get(backRow).size(); i++) {
            if (table.get(backRow).get(i).isFrozen()) {
                table.get(backRow).get(i).setFrozen(false);
            }
            if (table.get(backRow).get(i).isHasAttacked()) {
                table.get(backRow).get(i).setHasAttacked(false);
            }
        }
        if (player.getCardHero().isHasAttacked()) {
            player.getCardHero().setHasAttacked(false);
        }
    }

    /**
     * Taking one card from the player's deck and adding it from the cards in hand.
     * In the end, the card taken is removed from the deck.
     * @param player each player gets to take one card from their decks.
     */
    public static void takeCard(final Player player) {
        if (!player.getDeck().isEmpty()) {
            CardInput copy = CardInput.copyOneCard(player.getDeck(), 0);
            player.getHand().add(copy);
            player.getDeck().remove(0);
        }
    }

    /**
     * Places a player's card on their assigned rows. If there are errors, they will be printed,
     * using printErrorCardPlaceCard. If not, based on the row that the card is supposed to be
     * placed (front or back), I place it on the given row.
     * Thirst error: if a player can't place a card on a specific row because it is full. It
     * is tested before adding a card to the row.
     * @return the mana of the card placed on the row
     */
    public static int placeCardOnRow(final ArrayNode output, final ActionsInput action,
                 final ArrayList<CardInput> cardsHand, final ArrayList<ArrayList<CardInput>> table,
                 final int index, final int manamax, final int rowBack, final int rowFront) {
        final int maxCol = 5;
        CardInput card = CardInput.copyOneCard(cardsHand, index);
        // first two errors
        if (OutPrint.printErrorPlaceCard(output, action, card, manamax)) {
            return 0;
        }
        if (card.getPosition(card) == 1) { // front
            if (table.get(rowFront).size() >= maxCol) {
                // third error
                OutPrint.errorAddRowFullRow(output, action);
                return 0;
            }
            table.get(rowFront).add(card);
        } else { // back
            if (table.get(rowBack).size() >= maxCol) {
                // third error
                OutPrint.errorAddRowFullRow(output, action);
                return 0;
            }
            table.get(rowBack).add(card);
        }
        cardsHand.remove(index);
        return card.getMana();
    }

    /**
     * Function designed for testing the possible errors that prevents the player from using
     * an environment card (if there are errors, they will be printed using the
     * printErrorEnvironment method). If there are no errors, the method will return the mana
     * of the environment card.
     * @return mana of the environment card (or 0 if there are errors)
     */
    public static boolean testErrorEnvironment(final ArrayNode output, final GameInput game,
                                           final ActionsInput action,
                                  final ArrayList<ArrayList<CardInput>> table, final Player player,
                                  final CardInput cardenv) {
        final int case1 = 1, case2 = 2, case3 = 3, maxrows = 3,
                  row0 = 0, row1 = 1, row2 = 2, row3 = 3;
        int rowFront, rowBack;

        if (game.getPlayerTurn() == 1) {
            rowFront = row2; rowBack = row3;
        } else {
            rowFront = row1; rowBack = row0;
        }
        // error 1
        if (!Objects.equals(cardenv.getType(cardenv), "Environment")) {
            OutPrint.printErrorEnvironment(output, action, case1);
            return true;
        }
        // error 2
        if (cardenv.getMana() > player.getMana()) {
            OutPrint.printErrorEnvironment(output, action, case2);
            return true;
        }
        // error 3
        if (action.getAffectedRow() == rowFront
                || action.getAffectedRow() == rowBack) {
            OutPrint.printErrorEnvironment(output, action, case3);
            return true;
        }
        // error 4
        if (Objects.equals(cardenv.getName(), "Heart Hound")
                && !CardInput.heartHound(output, action, table)) {
            return true;
        }
        return false; // no errors = good case
    }

    /**
     * Function used to test if there are errors that can prevent the player from attacking another
     * player's card. If there are errors they will be printed using printErrorAttack.
     * @return true (there are errors) / false (there are no errors)
     */
    public static boolean testErrorCardAttack(final ObjectMapper objectMapper,
                                  final ArrayNode output, final ArrayList<ArrayList<CardInput>>
                                  table, final ActionsInput action, final GameInput game,
                                  final CardInput cardAttacker, final CardInput cardAttacked) {
        final int case1 = 1, case2 = 2, case3 = 3, case4 = 4,
                  row0 = 0, row1 = 1, row2 = 2, row3 = 3;
        int rowFront, rowBack;

        if (game.getPlayerTurn() == 1) {
            rowFront = row2; rowBack = row3;
        } else {
            rowFront = row1; rowBack = row0;
        }

        // error 1
        if (action.getCardAttacked().getX() == rowFront
                || action.getCardAttacked().getX() == rowBack) {
            OutPrint.printErrorAttack(objectMapper, output, action, case1);
            return true;
        }
        // error 2
        if (cardAttacker.isHasAttacked()) {
            OutPrint.printErrorAttack(objectMapper, output, action, case2);
            return true;
        }
        // error 3
        if (cardAttacker.isFrozen()) {
            OutPrint.printErrorAttack(objectMapper, output, action, case3);
            return true;
        }
        // error 4
        if (!cardAttacked.isTank(cardAttacked)
                && CardInput.testIfThereAreTanks(table, game)) {
            OutPrint.printErrorAttack(objectMapper, output, action, case4);
            return true;
        }
        // good test
        return false;
    }

    /**
     * Function used for testing the possible erros that can prevent a player from using a card's
     * ability. If there are errors they will be printed using printErrorAbility. Otherwise, the
     * function will return false, meaning there were no errors.
     * @return true (if there were errors), false (if there weren't any errors)
     */
    public static boolean testErrorUseAbility(final ObjectMapper objectMapper,
                                              final ArrayNode output, final ActionsInput action,
                                              final GameInput game,
                                              final ArrayList<ArrayList<CardInput>> table) {
        final int case1 = 1, case2 = 2, case3 = 3, case4 = 4, case5 = 5, maxRow = 3,
                  row0 = 0, row1 = 1, row2 = 2, row3 = 3;

        CardInput cardAttacker = table.get(action.getCardAttacker().getX()).
                get(action.getCardAttacker().getY());
        CardInput cardAttacked = table.get(action.getCardAttacked().getX()).
                get(action.getCardAttacked().getY());

        int rowFront, rowBack;

        if (game.getPlayerTurn() == 1) {
            rowFront = row2; rowBack = row3;
        } else {
            rowFront = row1; rowBack = row0;
        }

        // error 1
        if (cardAttacker.isFrozen()) {
            OutPrint.printErrorAbility(objectMapper, output, action, case1);
            return true;
        }
        // error 2
        if (cardAttacker.isHasAttacked()) {
            OutPrint.printErrorAbility(objectMapper, output, action, case2);
            return true;
        }
        // error 3
        if (cardAttacker.getName().equals("Disciple")
                && (action.getCardAttacked().getX() == maxRow - rowFront
                || action.getCardAttacked().getX() == maxRow - rowBack)) {
            OutPrint.printErrorAbility(objectMapper, output, action, case3);
            return true;
        }
        // error 4
        if (!cardAttacker.getName().equals("Disciple")
                && (action.getCardAttacked().getX() == rowFront
                || action.getCardAttacked().getX() == rowBack)) {
            OutPrint.printErrorAbility(objectMapper, output, action, case4);
            return true;
        }
        // error 5
        if (!cardAttacker.getName().equals("Disciple")
                && !cardAttacked.isTank(cardAttacked)
                && CardInput.testIfThereAreTanks(table, game)) {
        OutPrint.printErrorAbility(objectMapper, output, action, case5);
        return true;
    }
        return false;
}

    /**
     * Function used for testing the possible erros that can prevent a player from attacking the
     * opponent's hero. If there are errors they will be printed using printErrorAttackHero.
     * Otherwise, the function will return false, meaning there were no errors.
     * @return true (if there were errors), false (if there weren't any errors)
     */
    public static boolean testErrorAttackHero(final ObjectMapper objectMapper,
                                              final ArrayNode output, final GameInput game,
                                              final ActionsInput action,
                                              final ArrayList<ArrayList<CardInput>> table,
                                              final CardInput cardAttacker) {
        final int case1 = 1, case2 = 2, case3 = 3;
        // error 1
        if (cardAttacker.isFrozen()) {
            OutPrint.printErrorAttackHero(objectMapper, output, action, case1);
            return true;
        }
        // error 2
        if (cardAttacker.isHasAttacked()) {
            OutPrint.printErrorAttackHero(objectMapper, output, action, case2);
            return true;
        }
        // error 3
        if (CardInput.testIfThereAreTanks(table, game)) {
            OutPrint.printErrorAttackHero(objectMapper, output, action, case3);
            return true;
        }
        return false;
    }

    /**
     * Function used for testing the possible erros that can prevent a player from using their
     * hero's ability. If there are errors they will be printed using printErrorUseHeroAbility.
     * Otherwise, the function will return false, meaning there were no errors.
     * @return true (if there were errors), false (if there weren't any errors)
     */
    public static boolean testErrorUseHeroAbility(final ObjectMapper objectMapper,
                                                  final ArrayNode output,
                                                  final ActionsInput action, final Player player,
                                                  final CardInput hero,
                                                  final int rowFront, final int rowBack) {
        final int case1 = 1, case2 = 2, case3 = 3, case4 = 4, maxRow = 3;
        // error 1
        if (player.getMana() < player.getCardHero().getMana()) {
            OutPrint.printErrorUseHeroAbility(objectMapper, output, action, case1);
            return true;
        }
        // error 2
        if (hero.isHasAttacked()) {
            OutPrint.printErrorUseHeroAbility(objectMapper, output, action, case2);
            return true;
        }
        // error 3
        if ((hero.getName().equals("Lord Royce") || hero.getName().equals("Empress Thorina"))
                && (action.getAffectedRow() == rowFront
                || action.getAffectedRow() == rowBack)) {
            OutPrint.printErrorUseHeroAbility(objectMapper, output, action, case3);
            return true;
        }
        // error 4
        if ((hero.getName().equals("General Kocioraw") || hero.getName().equals("King Mudface"))
                && (action.getAffectedRow() == maxRow - rowFront
                || action.getAffectedRow() == maxRow - rowBack)) {
            OutPrint.printErrorUseHeroAbility(objectMapper, output, action, case4);
            return true;
        }
        return false;
    }

}
