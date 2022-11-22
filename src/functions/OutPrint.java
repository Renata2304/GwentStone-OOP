package functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.CardInput;
import fileio.GameInput;

import java.util.ArrayList;
import java.util.Objects;

public final class OutPrint {
    private OutPrint() { }

    private static int gamesWon = 0;

    public static int getGamesWon() {
        return gamesWon;
    }

    public static void setGamesWon(final int gamesWon) {
        OutPrint.gamesWon = gamesWon;
    }

    /**
     * Printing every card from a given deck, based on player's turn
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
     * Printing one card from a given deck.
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
     * Printing a player's hero card.
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
     * Printing a card from the table that can be found at table[X][Y]. If there is no card at that
     * position, an error will be printed.
     */
    public static void printGetCardAtPosition(final ObjectMapper objectMapper,
                                              final ArrayNode output, final GameInput game,
                                              final ActionsInput action,
                                              final ArrayList<ArrayList<CardInput>> table) {
        ObjectNode jsonNodes = output.addObject();
        jsonNodes.put("command", action.getCommand());
        jsonNodes.put("x", action.getX());
        jsonNodes.put("y", action.getY());
        if (action.getY() >= table.get(action.getX()).size()) {
            jsonNodes.put("output", "No card available at that position.");
            return;
        }
        if (action.getY() < table.get(action.getX()).size()) {
            ObjectNode jsonNodes2 = objectMapper.createObjectNode();
            jsonNodes.set("output", jsonNodes2);
            CardInput card = table.get(action.getX()).get(action.getY());
            jsonNodes2.put("mana", card.getMana());
            jsonNodes2.put("attackDamage", card.getAttackDamage());
            jsonNodes2.put("health", card.getHealth());
            jsonNodes2.put("description", card.getDescription());

            ArrayNode colors = jsonNodes2.putArray("colors");
            for (String color : card.getColors()) {
                colors.add(color);
            }
            jsonNodes2.put("name", card.getName());
        } else {
            jsonNodes.put("output", "No card at that position.");
        }
    }


    /**
     * Prints the errors that may occur when trying to place a card on the table.
     * @return true (there was an error) / false (otherwise)
     */
    public static boolean printErrorPlaceCard(final ArrayNode output,
                                                  final ActionsInput action,
                                                  final CardInput card, final int manaHand) {
        if (Objects.equals(card.getType(card), "Environment")) {
            ObjectNode jsonNodes = output.addObject();
            jsonNodes.put("command", action.getCommand());
            jsonNodes.put("handIdx", action.getHandIdx());
            jsonNodes.put("error",
                    "Cannot place environment card on table.");
            return true;
        }
        if (card.getMana() > manaHand) {
            ObjectNode jsonNodes = output.addObject();
            jsonNodes.put("command", action.getCommand());
            jsonNodes.put("handIdx", action.getHandIdx());
            jsonNodes.put("error",
                    "Not enough mana to place card on table.");
            return true;
        }
        return false;
    }
    /**
     * Prints the error found when a player is trying to add a card on an already full row.
     */
    public static void errorAddRowFullRow(final ArrayNode output, final ActionsInput action) {
        ObjectNode jsonNodes = output.addObject();
        jsonNodes.put("command", action.getCommand());
        jsonNodes.put("handIdx", action.getHandIdx());
        jsonNodes.put("error", "Cannot place card on table since row is full.");
    }

    /**
     * Prints an environment card from one player's deck.
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
     * Prints the errors that may occur when a player is trying to use an environment card.
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
     * Prints the errors that my occur when a player is trying to attack the opponent's cards.
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
     * Prints the errors that may occur when a player is trying to use a card's ability.
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
     * Prints the frozen cards on the table.
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
     * Prints all the cards on the table.
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
     * Prints the errors that may occur when a player is trying to attack the opponent's hero.
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
     * Prints the message that one of the players killed the other's hero.
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
     * Prints the errors that may occur when a player is trying to use a hero's ability.
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
            default -> {

            }
        }
    }

}
