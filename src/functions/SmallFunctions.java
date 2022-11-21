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

    static final int MAXROW = 3;
    private SmallFunctions() { }

    /**
     *
     * @param table
     * @param frontRow
     * @param backRow
     */
    public static void resetCards(final ArrayList<ArrayList<CardInput>> table,
                                  final int frontRow, final int backRow, Player player) {
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
        if (player.getCardHero().isFrozen()) {
            player.getCardHero().setFrozen(false);
        }
        if (player.getCardHero().isHasAttacked()) {
            player.getCardHero().setHasAttacked(false);
        }
    }

    /**
     *
     * @param player
     */
    public static void deleteOneCard(final Player player) {
        if (!player.getDeck().isEmpty()) {
            CardInput copy = new CardInput();
            copy = copy.copyOneCard(player.getDeck(), 0);
            player.getHand().add(copy);
            player.getDeck().remove(0);
        }
    }

    /**
     *
     * @param output
     * @param action
     * @param cardsHand
     * @param table
     * @param index
     * @param manamax
     * @return
     */
    public static int placeCardOnRow(final ArrayNode output, final ActionsInput action,
                 final ArrayList<CardInput> cardsHand, final ArrayList<ArrayList<CardInput>> table,
                 final int index, final int manamax, final int rowBack, final int rowFront) {
        final int maxCol = 5;
        CardInput card = new CardInput();
        card = card.copyOneCard(cardsHand, index);
        if (!CardInput.testErrorCardPlaceCard(output, action, card, manamax)) {
            return 0;
        }
        if (card.getPosition(card) == 1) { // front
            if (table.get(rowFront).size() >= maxCol) {
                OutPrint.errorAddRowFullRow(output, action);
                return 0;
            }
            table.get(rowFront).add(card);
        } else { // back
            if (table.get(rowBack).size() >= maxCol) {
                OutPrint.errorAddRowFullRow(output, action);
                return 0;
            }
            table.get(rowBack).add(card);
        }
        cardsHand.remove(index);
        return card.getMana();
    }

    /**
     * get mana from different Environment card types
     * @param output idk
     * @param action idk
     * @param table idk
     * @param cardsHand1 idk
     * @param cardenv idk
     * @return idk
     */
    public static int useEnvironmentType(final ArrayNode output, final ActionsInput action,
                                         final ArrayList<ArrayList<CardInput>> table,
                                         final ArrayList<CardInput> cardsHand1,
                                         final CardInput cardenv) {
        int mana = cardenv.getMana();
        if (Objects.equals(cardenv.getName(), "Heart Hound")) {
            if (CardInput.heartHound(output, action, table)) {
                cardsHand1.remove(cardenv);
                return mana;
            } else {
                return 0;
            }
        }
        if (Objects.equals(cardenv.getName(), "Firestorm")) {
            CardInput.firestorm(action, table);
            cardsHand1.remove(cardenv);
            return mana;
        }
        if (Objects.equals(cardenv.getName(), "Winterfell")) {
            CardInput.winterfell(action, table);
            cardsHand1.remove(cardenv);
            return mana;
        }
        return 0;
    }

    /**
     *
     * @param output
     * @param action
     * @param table
     * @param player
     * @param rowFront
     * @param rowBack
     * @param cardsHand
     * @return
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
     *
     * @param objectMapper
     * @param output
     * @param action
     * @param game
     * @param table
     * @return
     */
    public static boolean testErrorUseAbility(final ObjectMapper objectMapper,
                                              final ArrayNode output, final ActionsInput action,
                                              final GameInput game,
                                              final ArrayList<ArrayList<CardInput>> table) {
        final int case1 = 1, case2 = 2, case3 = 3, case4 = 4, case5 = 5;
        CardInput cardAttacker = table.get(action.getCardAttacker().getX()).
                get(action.getCardAttacker().getY());
        CardInput cardAttacked = table.get(action.getCardAttacked().getX()).
                get(action.getCardAttacked().getY());
        int rowFront, rowBack;

        if (game.getPlayerTurn() == 1) {
            rowFront = 2; rowBack = 3;
        } else {
            rowFront = 1; rowBack = 0;
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
        if (cardAttacker.getName().equals("Disciple") &&
                (action.getCardAttacked().getX() == 3 - rowFront
                || action.getCardAttacked().getX() == 3 - rowBack)) {
            OutPrint.printErrorAbility(objectMapper, output, action, case3);
            return true;
        }
        // error 4
        if (!cardAttacker.getName().equals("Disciple")
                && (action.getCardAttacked().getX() == rowFront
                || action.getCardAttacked().getX() == rowBack )) {
            OutPrint.printErrorAbility(objectMapper, output, action, case4);
            return true;
        }
        // error 5
        if (!cardAttacker.getName().equals("Disciple")

                && !cardAttacked.isTank(cardAttacked) && CardInput.testIfThereAreTanks(table, game)) {
        OutPrint.printErrorAbility(objectMapper, output, action, case5);
        return true;
    }
        return false;
}

    /**
     * I use this function to return the front row of each player
     * playerTurn = 1 -> 2, playerTurn = 2 -> 1
     * @param playerTurn
     * @return
     */
    public static int getFront(final int playerTurn) {
        if(playerTurn == 1) {
            return 2;
        }
        else {
            return 1;
        }
    }
    /**
     * I use this function to return the back row of each player
     * playerTurn = 1 -> 3, playerTurn = 2 -> 0
     * @param playerTurn
     * @return
     */
    public static int getBack(final int playerTurn) {
        if (playerTurn == 1) {
            return 3;
        } else {
            return 0;
        }
    }
}
