package functions;

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
            case 1 -> {
                if (nrCards1 != 0) {
                    for (int i = 0; i < nrCards1; i++) {
                        printCard(objectMapper, arrayNode, deck1, i);
                    }
                }
            }
            case 2 -> {
                if (nrCards2 != 0) {
                    for (int i = 0; i < nrCards2; i++) {
                        printCard(objectMapper, arrayNode, deck2, i);
                    }
                }
            }
            default -> {
            }
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

    public static void printCardOnTable(final ObjectMapper objectMapper, final ArrayNode arrayNode,
                                        final ArrayList<CardInput> row, final int i) {

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
     * @param cardsHand1
     * @param cardsHand2
     * @param playerTurn
     */
    public static int addRow(final ArrayNode output, final ActionsInput action,
            final ArrayList<CardInput> cardsHand1, final ArrayList<CardInput> cardsHand2,
            final ArrayList<ArrayList<CardInput>> table, final int playerTurn, final int index,
            final int manamax1, final int manamax2) {
        final int row0 = 0, row1 = 1, row2 = 2, row3 = 3;
        switch (playerTurn) {
            case 1 -> {
                return SmallFunctions.placeCardOnRow(output, action, cardsHand1, table, index,
                        manamax1, row3, row2);
            }
            case 2 -> {
                return SmallFunctions.placeCardOnRow(output, action, cardsHand2, table, index,
                        manamax2, row0, row1);
            }
            default -> {
                return 0;
            }
        }
    }

    /**
     *
     * @param output
     * @param action
     */
    public static void errorAddRowFullRow(final ArrayNode output, final ActionsInput action) {
        ObjectNode jsonNodes = output.addObject();
        jsonNodes.put("command", action.getCommand());
        jsonNodes.put("handIdx", action.getHandIdx());
        jsonNodes.put("error", "Cannot place card on table since row is full.");
    }

    /**
     *
     * @param mana
     * @param endCont
     * @return
     */
    public static int incMana(final int mana, final int endCont, final int maxMana) {
        if (endCont / 2 + 1 < maxMana) {
            return endCont / 2 + 1;
        }
        return maxMana;
    }

    /**
     *
     * @param output
     * @param objectMapper
     * @param deck1
     * @param deck2
     * @param action
     */
    public static void printEnvironmentCard(final ArrayNode output, final ObjectMapper objectMapper,
                           final ArrayList<CardInput> deck1, final ArrayList<CardInput> deck2,
                           final ActionsInput action) {
        ObjectNode jsonNodes = output.addObject();
        jsonNodes.put("command", action.getCommand());
        jsonNodes.put("playerIdx", action.getPlayerIdx());
        ArrayNode arrayNode = jsonNodes.putArray("output");
        switch (action.getPlayerIdx()) {
            case 1 :
                if (deck1.size() != 0) {
                    for (int i = 0; i < deck1.size(); i++) {
                        if (deck1.get(i).getType(deck1.get(i)).equals("Environment")) {
                            printCard(objectMapper, arrayNode, deck1, i);
                        }
                    }
                }
                break;
            case 2 :
                if (deck2.size() != 0) {
                    for (int i = 0; i < deck2.size(); i++) {
                        if (deck2.get(i).getType(deck2.get(i)).equals("Environment")) {
                            printCard(objectMapper, arrayNode, deck2, i);
                        }
                    }
                }
                break;
            default :
                break;
        }
    }

    /**
     *
     * @param output
     * @param action
     * @param nrCase
     */
    public static void printErrorEnvironment(final ArrayNode output, final ActionsInput action,
                                             final int nrCase) {
        final int case1 = 1, case2 = 2, case3 = 3, case4 = 4;

        ObjectNode jsonNodes = output.addObject();
        jsonNodes.put("command", action.getCommand());
        jsonNodes.put("handIdx", action.getHandIdx());
        jsonNodes.put("affectedRow", action.getAffectedRow());

        switch (nrCase) {
            case case1 -> {
                jsonNodes.put("error", "Chosen card is not of type environment.");
            }
            case case2 -> {
                jsonNodes.put("error", "Not enough mana to use environment card.");
            }
            case case3 -> {
                jsonNodes.put("error", "Chosen row does not belong to the enemy.");
            }
            case case4 -> {
                jsonNodes.put("error", "Cannot steal enemy card since the player's row is full.");
            }
            default -> {
            }
        }

    }

    /**
     *
     * @param objectMapper
     * @param output
     * @param action
     * @param nrCase
     */
    public static void printErrorAttack(final ObjectMapper objectMapper, final ArrayNode output,
                                        final ActionsInput action, final int nrCase) {
        final int case1 = 1, case2 = 2, case3 = 3, case4 = 4, case5 = 5;
        ObjectNode jsonNodes = output.addObject();
        jsonNodes.put("command", action.getCommand());
        ObjectNode jsonNodes2 = objectMapper.createObjectNode();
        jsonNodes.set("cardAttacker", jsonNodes2);
        jsonNodes2.put("x", action.getCardAttacker().getX());
        jsonNodes2.put("y", action.getCardAttacker().getY());
        jsonNodes2 = objectMapper.createObjectNode();
        jsonNodes.set("cardAttacked", jsonNodes2);
        jsonNodes2.put("x", action.getCardAttacked().getX());
        jsonNodes2.put("y", action.getCardAttacked().getY());

        switch (nrCase) {
            case case1 -> {
                jsonNodes.put("error", "Attacked card does not belong to the enemy.");
            }
            case case2 -> {
                jsonNodes.put("error", "Attacker card has already attacked this turn.");
            }
            case case3 -> {
                jsonNodes.put("error", "Attacker card is frozen.");
            }
            case case4 -> {
                jsonNodes.put("error", "Attacked card is not of type 'Tank'.");
            }
            default -> {
            }
        }
    }

    /**
     *
     * @param output
     * @param action
     * @param nrCase
     */
    public static void printErrorAbility(final ObjectMapper objectMapper, final ArrayNode output,
                                         final ActionsInput action, final int nrCase) {
        final int case1 = 1, case2 = 2, case3 = 3, case4 = 4, case5 = 5;

        ObjectNode jsonNodes = output.addObject();
        jsonNodes.put("command", action.getCommand());
        ObjectNode jsonNodes2 = objectMapper.createObjectNode();
        jsonNodes.set("cardAttacker", jsonNodes2);
        jsonNodes2.put("x", action.getCardAttacker().getX());
        jsonNodes2.put("y", action.getCardAttacker().getY());
        jsonNodes2 = objectMapper.createObjectNode();
        jsonNodes.set("cardAttacked", jsonNodes2);
        jsonNodes2.put("x", action.getCardAttacked().getX());
        jsonNodes2.put("y", action.getCardAttacked().getY());

        switch (nrCase) {
            case case1 -> {
                jsonNodes.put("error", "Attacker card is frozen.");
            }
            case case2 -> {
                jsonNodes.put("error", "Attacker card has already attacked this turn.");
            }
            case case3 -> {
                jsonNodes.put("error", "Attacked card does not belong to the current player.");
            }
            case case4 -> {
                jsonNodes.put("error", "Attacked card does not belong to the enemy.");
            }
            case case5 -> {
                jsonNodes.put("error", "Attacked card is not of type 'Tank'.");
            }
            default -> {
            }
        }

    }

    /**
     *
     * @param objectMapper
     * @param output
     * @param action
     * @param table
     */
    public static void printFrozenCardsOnTable(final ObjectMapper objectMapper, final ArrayNode
            output, final ActionsInput action, final ArrayList<ArrayList<CardInput>> table) {
        final int rowsmax = 4;
        ObjectNode jsonNodes = output.addObject();
        jsonNodes.put("command", action.getCommand());
        ArrayNode arrayNode = jsonNodes.putArray("output");
        for (int row = 0; row < rowsmax; row++) {
            for (int poz = 0; poz < table.get(row).size(); poz++) {
                if (table.get(row).get(poz).isFrozen()) {
                    OutPrint.printCard(objectMapper, arrayNode, table.get(row), poz);
                }
            }
        }
    }

    /**
     *
     * @param objectMapper
     * @param output
     * @param action
     * @param table
     */
    public static void printCardsOnTable(final ObjectMapper objectMapper, final ArrayNode
            output, final ActionsInput action, final ArrayList<ArrayList<CardInput>> table) {
        final int rowsmax = 4;
        ObjectNode jsonNodes = output.addObject();
        jsonNodes.put("command", action.getCommand());
        ArrayNode arrayNode1 = jsonNodes.putArray("output");
        for (int i = 0; i < rowsmax; i++) {
            ArrayNode arrayNode2 = arrayNode1.addArray();
            for (int cardId = 0; cardId < table.get(i).size(); cardId++) {
                OutPrint.printCard(objectMapper, arrayNode2, table.get(i),
                        cardId);
            }
        }
    }

    /**
     *
     * @param objectMapper
     * @param output
     * @param action
     * @param nrCase
     */
    public static void printErrorAttackHero(final ObjectMapper objectMapper, final ArrayNode output,
                                         final ActionsInput action, final int nrCase) {
        final int case1 = 1, case2 = 2, case3 = 3;

        ObjectNode jsonNodes = output.addObject();
        jsonNodes.put("command", action.getCommand());
        ObjectNode jsonNodes2 = objectMapper.createObjectNode();
        jsonNodes.set("cardAttacker", jsonNodes2);
        jsonNodes2.put("x", action.getCardAttacker().getX());
        jsonNodes2.put("y", action.getCardAttacker().getY());

        switch (nrCase) {
            case case1 -> {
                jsonNodes.put("error", "Attacker card is frozen.");
            }
            case case2 -> {
                jsonNodes.put("error", "Attacker card has already attacked this turn.");
            }
            case case3 -> {
                jsonNodes.put("error", "Attacked card is not of type 'Tank'.");
            }
            default -> {
            }
        }
    }

    /**
     *
     * @param objectMapper
     * @param output
     * @param playerTurn
     */
    public static void playerKilledHero(final ObjectMapper objectMapper, final ArrayNode
            output, final int playerTurn) {
        ObjectNode jsonNodes = output.addObject();
        if (playerTurn == 1) {
            jsonNodes.put("gameEnded", "Player one killed the enemy hero.");
        } else {
            jsonNodes.put("gameEnded", "Player two killed the enemy hero.");
        }
    }

    /**
     *
     * @param objectMapper
     * @param output
     * @param action
     * @param nrCase
     */
    public static void printErrorUseHeroAbility(final ObjectMapper objectMapper,
                                                final ArrayNode output,
                                                final ActionsInput action, final int nrCase) {
        final int case1 = 1, case2 = 2, case3 = 3, case4 = 4;

        ObjectNode jsonNodes = output.addObject();
        jsonNodes.put("command", action.getCommand());
        jsonNodes.put("affectedRow", action.getAffectedRow());

        switch (nrCase) {
            case case1 -> {
                jsonNodes.put("error", "Not enough mana to use hero's ability.");
            }
            case case2 -> {
                jsonNodes.put("error", "Hero has already attacked this turn.");
            }
            case case3 -> {
                jsonNodes.put("error", "Selected row does not belong to the enemy.");
            }
            case case4 -> {
                jsonNodes.put("error", "Selected row does not belong to the current player.");
            }
        }
    }

}
