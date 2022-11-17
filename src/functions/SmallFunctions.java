package functions;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.CardInput;
import fileio.Player;

import java.util.ArrayList;
import java.util.Objects;

public final class SmallFunctions {

    private SmallFunctions() { }

    public static void unfreezeCards(ArrayList<ArrayList<CardInput>> table,
                                     final int frontRow, final int backRow) {
        for( int i = 0; i < table.get(frontRow).size(); i++) {
            if(table.get(frontRow).get(i).isFrozen()) {
                table.get(frontRow).get(i).setFrozen(false);
            }
        }
        for( int i = 0; i < table.get(backRow).size(); i++) {
            if(table.get(backRow).get(i).isFrozen()) {
                table.get(backRow).get(i).setFrozen(false);
            }
        }
    }

    /**
     *
     * @param player
     */
    public static void deleteOneCard(Player player) {
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
     * @param maxCol
     * @param rowBack
     * @param rowFront
     * @return
     */
    public static int placeCardOnRow(final ArrayNode output, final ActionsInput action,
                 final ArrayList<CardInput> cardsHand, final ArrayList<ArrayList<CardInput>> table,
                 final int index, final int manamax, final int maxCol, final int rowBack,
                 final int rowFront) {
        if (index >= cardsHand.size()) {
            return 0;
        }
        CardInput card = new CardInput();
        card = card.copyOneCard(cardsHand, index);
        if (!CardInput.testErrorCardPlaceCard(output, action, card, manamax)) {
            return 0;
        }
        if (card.getPosition(card) == 1) {
            if (table.get(rowFront).size() == maxCol) {
                SmallFunctions.afisAddRowFullRow(output, action);
                return 0;
            }
            table.get(rowFront).add(card);
        } else {
            if (table.get(rowBack).size() == maxCol) {
                SmallFunctions.afisAddRowFullRow(output, action);
                return 0;
            }
            table.get(rowBack).add(card);
        }
        cardsHand.remove(index);
        return card.getMana();
    }

    /**
     *
     * @param output
     * @param action
     */
    public static void afisAddRowFullRow(final ArrayNode output, final ActionsInput action) {
        ObjectNode jsonNodes = output.addObject();
        jsonNodes.put("command", action.getCommand());
        jsonNodes.put("handIdx", action.getHandIdx());
        jsonNodes.put("error", "Cannot place card on table since row is full.");
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
        if (Objects.equals(cardenv.getName(), "Heart Hound")) {
            if (CardInput.heartHound(output, action, table)) {
                cardsHand1.remove(cardenv);
                return cardenv.getMana();
            }
            return 0;
        }
        if (Objects.equals(cardenv.getName(), "Firestorm")) {
            CardInput.firestorm(action, table);
            cardsHand1.remove(cardenv);
            return cardenv.getMana();
        }
        if (Objects.equals(cardenv.getName(), "Winterfell")) {
            CardInput.winterfell(action, table);
            cardsHand1.remove(cardenv);
            return cardenv.getMana();
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
                                           final ArrayList<ArrayList<CardInput>> table,
                                           final Player player, final int rowFront, final int rowBack,
                                           final ArrayList<CardInput> cardsHand) {
        final int case1 = 1, case2 = 2, case3 = 3, maxrows = 3;
        if (action.getHandIdx() >= cardsHand.size()) {
            return 0;
        }
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

}
