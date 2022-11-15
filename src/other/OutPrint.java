package other;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.CardInput;

import java.util.ArrayList;

public final class OutPrint {
    private OutPrint() { }

    /**
     * i dunno 1
     */
    public static void printPlayerDeck(final ArrayNode output, final ObjectMapper objectMapper,
                     final ArrayList<CardInput> deck1, final ArrayList<CardInput> deck2,
                     final ActionsInput action, final int nrCards1, final int nrCards2) {
        ObjectNode jsonNodes = output.addObject();
        jsonNodes.put("command", action.getCommand());
        jsonNodes.put("playerIdx", action.getPlayerIdx());
        ArrayNode arrayNode = jsonNodes.putArray("output");
        switch (action.getPlayerIdx()) {
            case 1 :
                if (nrCards1 != 0) {
                    for (int i = 0; i < nrCards1; i++) {
                        printCard(objectMapper, arrayNode, deck1, i);
                    }
                }
                break;
            case 2 :
                if (nrCards2 != 0) {
                    for (int i = 0; i < nrCards2; i++) {
                        printCard(objectMapper, arrayNode, deck2, i);
                    }
                }
                break;
            default :
                break;
        }
    }

    /**
     * i dunno 2
     */
    public static void printCard(final ObjectMapper objectMapper, final ArrayNode arrayNode,
                                 final ArrayList<CardInput> deck, final int i) {
        CardInput card = deck.get(i);
        ObjectNode arrayNode2 = objectMapper.createObjectNode();
        arrayNode2.put("mana", card.getMana());
        if (card.getHealth() != 0) {
            arrayNode2.put("attackDamage", card.getAttackDamage());
            arrayNode2.put("health", card.getHealth());
        }
        arrayNode2.put("description", card.getDescription());

        ArrayNode colors = arrayNode2.putArray("colors");
        for (String color : card.getColors()) {
            colors.add(color);
        }
        arrayNode2.put("name", card.getName());
        arrayNode.add(arrayNode2);
    }

    /**
     * i dunno 3
     */
    public static void printPlayerHero(final ArrayNode output, final ActionsInput action,
                       final ObjectMapper objectMapper, final CardInput card) {
        ObjectNode jsonNodes = output.addObject();
        jsonNodes.put("command", action.getCommand());
        jsonNodes.put("playerIdx", action.getPlayerIdx());
        ObjectNode arrayNode2 = objectMapper.createObjectNode();
        arrayNode2.put("mana", card.getMana());
        arrayNode2.put("health", card.getHealth());
        arrayNode2.put("description", card.getDescription());
        ArrayNode colors = arrayNode2.putArray("colors");
        for (String color : card.getColors()) {
            colors.add(color);
        }
        arrayNode2.put("name", card.getName());
        jsonNodes.set("output", arrayNode2);
    }

    /**
     *
     * @param output
     * @param objectMapper
     * @param action
     * @param card
     */
    public static void testCard(final ArrayNode output, final ObjectMapper objectMapper,
                                final ActionsInput action, final CardInput card) {
        ObjectNode jsonNodes = output.addObject();
        jsonNodes.put("command", action.getCommand());
        jsonNodes.put("handIdx", action.getHandIdx());

        if (card.getType(card).equals("Environment")) {
            jsonNodes.put("playerIdx", action.getPlayerIdx());
            jsonNodes.put("error", "Cannot place environment card on table.");
        }
    }


    /**
     *
     * @param cardsHand1
     * @param cardsHand2
     * @param playerTurn
     */
    public static int addRow(final ArrayList<CardInput> cardsHand1,
            final ArrayList<CardInput> cardsHand2, final ArrayList<ArrayList<CardInput>> table,
            final int playerTurn, final int index) {
        switch (playerTurn) {
            case 1 -> {
                if (index >= cardsHand1.size()) {
                    return 0;
                }
                CardInput card = new CardInput();
                card = card.copyOneCard(cardsHand1, index);
                cardsHand1.remove(index);
                if (card.getPosition(card) == 1) {
                    table.get(2).add(card);
                } else {
                    table.get(3).add(card);
                }
                return card.getMana();
            }
            case 2 -> {
                if (index >= cardsHand2.size()) {
                    return 0;
                }
                CardInput card = new CardInput();
                card = card.copyOneCard(cardsHand2, index);
                cardsHand2.remove(index);
                if (card.getPosition(card) == 1) {
                    table.get(1).add(card);
                } else {
                    table.get(0).add(card);
                }
                return card.getMana();
            }
            default -> {
                return 0;
            }
        }
    }

    /**
     *
     * @param mana
     * @param endCont
     * @return
     */
    public static int incMana(final int mana, final int endCont, final int maxMana) {
        if (mana != maxMana) {
            return endCont / 2 + 1;
        }
        return maxMana;
    }

}
