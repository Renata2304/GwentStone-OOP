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
        if (!OutPrint.printErrorCardPlaceCard(output, action, card, manamax)) {
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
     * Recieves as parameter cardenv, the environment card that needs to be tested.
     * Get mana from different Environment card types: heartHound, firestorm, winterfell.
     * Each of the three types uses a specific method.
     * @return mana from specific environment card type
     */
    public static int useEnvironmentType(final ArrayNode output, final ActionsInput action,
                                         final ArrayList<ArrayList<CardInput>> table,
                                         final ArrayList<CardInput> cardsHand1,
                                         final CardInput cardenv) {
        int mana = cardenv.getMana();
        // Heart Hound case
        if (Objects.equals(cardenv.getName(), "Heart Hound")) {
            if (CardInput.heartHound(output, action, table)) {
                cardsHand1.remove(cardenv);
                return mana;
            } else {
                return 0;
            }
        }
        // Firestorm case
        if (Objects.equals(cardenv.getName(), "Firestorm")) {
            CardInput.firestorm(action, table);
            cardsHand1.remove(cardenv);
            return mana;
        }
        // Winterfell case
        if (Objects.equals(cardenv.getName(), "Winterfell")) {
            CardInput.winterfell(action, table);
            cardsHand1.remove(cardenv);
            return mana;
        }
        return 0;
    }

    /**
     * Function designed for testing the possible errors that prevents the player from using
     * an environment card (if there are errors, they will be printed using the
     * printErrorEnvironment method). If there are no errors, the method will return the mana
     * of the environment card.
     * @return mana of the environment card (or 0 if there are errors)
     */
    public static int testErrorEnvironment(final ArrayNode output, final ActionsInput action,
                                  final ArrayList<ArrayList<CardInput>> table, final Player player,
                                  final int rowFront, final int rowBack, final ArrayList<CardInput>
                                  cardsHand) {
        final int case1 = 1, case2 = 2, case3 = 3, maxrows = 3;
        CardInput cardenv = cardsHand.get(action.getHandIdx());
        if (!Objects.equals(cardenv.getType(cardenv), "Environment")) {
            OutPrint.printErrorEnvironment(output, action, case1);
            return 0;
        }
        if (cardenv.getMana() > player.getMana()) {
            OutPrint.printErrorEnvironment(output, action, case2);
            return 0;
        }
        if (action.getAffectedRow() == maxrows - rowFront
                || action.getAffectedRow() == maxrows - rowBack) {
            OutPrint.printErrorEnvironment(output, action, case3);
            return 0;
        }
        return SmallFunctions.useEnvironmentType(output, action, table, cardsHand, cardenv);
    }

    /**
     * Function used for testing the erros that can prevent a player from using an
     * @return
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

}
