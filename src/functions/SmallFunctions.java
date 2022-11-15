package functions;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.CardInput;

import java.util.ArrayList;

public final class SmallFunctions {

    private SmallFunctions() { }

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
    public static int idk(final ArrayNode output, final ActionsInput action,
                 final ArrayList<CardInput> cardsHand, final ArrayList<ArrayList<CardInput>> table,
                 final int index, final int manamax, final int maxCol, final int rowBack,
                 final int rowFront) {
        if (index >= cardsHand.size()) {
            return 0;
        }
        CardInput card = new CardInput();
        card = card.copyOneCard(cardsHand, index);
        if (!CardInput.testCardPlaceCard(output, action, card, manamax)) {
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

}
