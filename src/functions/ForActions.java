package functions;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import com.fasterxml.jackson.databind.ObjectMapper;
import fileio.ActionsInput;
import fileio.CardInput;
import fileio.GameInput;
import fileio.Player;

import java.util.ArrayList;

public final class ForActions {

    private ForActions() {
    }

    /**
     * Function used for the game loop from main.Main
     */
    public static void forAc(final ArrayNode output, final GameInput game,
                             final ActionsInput action, final ObjectMapper objectMapper,
                             final ArrayList<ArrayList<CardInput>> table, final Player player1,
                             final Player player2, final int startPlayer) {
        final int manaMax = 10;
        switch (action.getCommand()) {
            case "getPlayerDeck" -> {
                OutPrint.printPlayerDeck(output, objectMapper, player1.getDeck(),
                        player2.getDeck(), action, player1.getDeck().size(),
                        player2.getDeck().size());
            }
            case "getPlayerHero" -> {
                if (action.getPlayerIdx() == 1) {
                    OutPrint.printPlayerHero(output, action, objectMapper,
                            player1.getCardHero());
                } else {
                    OutPrint.printPlayerHero(output, action, objectMapper,
                            player2.getCardHero());
                }
            }
            case "endPlayerTurn" -> {
                caseEndPLayerTurn(game, table, player1, player2, startPlayer, manaMax);
            }
            case "getPlayerTurn" -> {
                ObjectNode jsonNodes = output.addObject();
                jsonNodes.put("command", action.getCommand());
                jsonNodes.put("output", game.getPlayerTurn());
            }
            case "getPlayerMana" -> {
                ObjectNode jsonNodes = output.addObject();
                jsonNodes.put("command", action.getCommand());
                jsonNodes.put("playerIdx", action.getPlayerIdx());
                if (action.getPlayerIdx() == 1) {
                    jsonNodes.put("output", player1.getMana());
                } else {
                    jsonNodes.put("output", player2.getMana());
                }
            }
            case "getCardsInHand" -> {
                OutPrint.printPlayerDeck(output, objectMapper, player1.getHand(),
                        player2.getHand(), action, player1.getHand().size(),
                        player2.getHand().size());
            }
            case "placeCard" -> {
                casePlaceCard(output, action, game, player1, player2, table);
            }
            case "getCardsOnTable" -> {
                OutPrint.printCardsOnTable(objectMapper, output, action, table);
            }
            case "getEnvironmentCardsInHand" -> {
                OutPrint.printEnvironmentCard(output, objectMapper, player1.getHand(),
                        player2.getHand(), action);
            }
            case "useEnvironmentCard" -> {
                caseUseEnvironmentCard(output, game, action, player1, player2, table);
            }
            case "getCardAtPosition" -> {
                OutPrint.printGetCardAtPosition(objectMapper, output, game, action, table);
            }
            case "getFrozenCardsOnTable" -> {
                OutPrint.printFrozenCardsOnTable(objectMapper, output, action, table);
            }
            case "cardUsesAttack" -> {
                CardInput.testCardAttack(output, objectMapper, game, action, table);
            }
            case "cardUsesAbility" -> {
                if (game.getPlayerTurn() == 1) {
                    CardInput.testUseAbility(output, objectMapper, game, action,
                            table);
                } else {
                    CardInput.testUseAbility(output, objectMapper, game, action,
                            table);
                }
            }
            case "useAttackHero" -> {
                CardInput.testAttackHero(output, objectMapper, game, action, table,
                        player1, player2);
            }
            case "useHeroAbility" -> {
                caseUseHeroAbility(output, game, action, player1, player2, objectMapper, table);
            }
            case "getPlayerOneWins" -> {
                ObjectNode jsonNodes = output.addObject();
                jsonNodes.put("command", "getPlayerOneWins");
                jsonNodes.put("output", player1.getNrGamesWon());
            }
            case "getPlayerTwoWins" -> {
                ObjectNode jsonNodes = output.addObject();
                jsonNodes.put("command", "getPlayerTwoWins");
                jsonNodes.put("output", player2.getNrGamesWon());
            }
            case "getTotalGamesPlayed" -> {
                ObjectNode jsonNodes = output.addObject();
                jsonNodes.put("command", "getTotalGamesPlayed");
                jsonNodes.put("output", player1.getNrGamesWon()
                        + player2.getNrGamesWon());
            }
            default -> {
            }
        }
    }

    /**
     * Increases the mana of a player, at the end of a round.
     * @return the value of mana that needs to be added to the player's mana.
     */
    public static int incMana(final int mana, final int endCont, final int maxMana) {
        if (endCont / 2 + 1 < maxMana) {
            return endCont / 2 + 1;
        }
        return maxMana;
    }

    /**
     * Function for the case endPlayerTurn that resets cards frozen and hasAttacked status after
     * each turn, based on the player's turn. After each round (two turns) I also increase the mana
     * of each player. Each time, I change the playerTurn.
     */
    public static void caseEndPLayerTurn(final GameInput game,
                                         final ArrayList<ArrayList<CardInput>> table,
                                         final Player player1, final Player player2, final int
                                         startPlayer, final int manaMax) {
        final int row0 = 0, row1 = 1, row2 = 2, row3 = 3;
        game.setEndTurn(game.getEndTurn() + 1);
        // after each turn I reset player's cards (reseting frozen status, attacked status)
        if (game.getPlayerTurn() == 1) {
            SmallFunctions.resetCards(table, row2, row3, player1);
        } else {
            SmallFunctions.resetCards(table, row1, row0, player2);
        }
        // if they have to both take cards
        if (game.getEndTurn() % 2 == 0) {
            // removing the card taken from the deck
            SmallFunctions.takeCard(player1);
            // removing the card taken from the deck
            SmallFunctions.takeCard(player2);
            game.setPlayerTurn(startPlayer);
            // adding to each player's mana
            player1.setMana(player1.getMana()
                    + incMana(player1.getMana(), game.getEndTurn(), manaMax));
            player2.setMana(player2.getMana()
                    + incMana(player2.getMana(), game.getEndTurn(), manaMax));
        } else {
            // if they don't have to take cards, we're just going to change player's turn
            if (game.getPlayerTurn() == 1) {
                game.setPlayerTurn(2);
            } else {
                game.setPlayerTurn(1);
            }
        }
    }

    /**
     * Function for the case placeCard that decreases the mana from the card placed on the table
     * from the player's mana
     */
    public static void casePlaceCard(final ArrayNode output, final ActionsInput action,
                                     final GameInput game,
                                     final Player player1, final Player player2,
                                     final ArrayList<ArrayList<CardInput>> table) {
        int mana = addOnRow(output, action, player1.getHand(), player2.getHand(), table,
                game.getPlayerTurn(), action.getHandIdx(), player1.getMana(), player2.getMana());

        if (game.getPlayerTurn() == 1) {
            player1.setMana(player1.getMana() - mana);
        } else {
            player2.setMana(player2.getMana() - mana);
        }
    }

    /**
     * Taking cards from the current player's cards in hand and adding a card on a row,
     * based on the player's turn and on card's type.
     * It is used on casePlaceCard, as an easier way to read the code.
     * @return the value of mana that needs to be decreased from player's mana
     */
    public static int addOnRow(final ArrayNode output, final ActionsInput action,
                               final ArrayList<CardInput> cardsHand1,
                               final ArrayList<CardInput> cardsHand2,
                               final ArrayList<ArrayList<CardInput>> table,
                               final int playerTurn, final int index,
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
     * Function for useCardEnvironment case that decreases the mana from the Environment card
     * (that is used in combat) from the player's mana
     */
    public static void caseUseEnvironmentCard(final ArrayNode output, final GameInput game,
                                              final ActionsInput action, final Player player1,
                                              final Player player2,
                                              final ArrayList<ArrayList<CardInput>> table) {
        int mana;
        if (game.getPlayerTurn() == 1) {
            mana = CardInput.testCardEnvironment(output, game, action, table, player1,
                    player1.getHand());
        } else {
            mana = CardInput.testCardEnvironment(output, game, action, table, player2,
                    player2.getHand());
        }
        if (game.getPlayerTurn() == 1) {
            player1.setMana(player1.getMana() - mana);
        } else {
            player2.setMana(player2.getMana() - mana);
        }
    }

    /**
     * Function for useCardEnvironment case that decreases the mana from the hero card
     * (that is used in combat) from the player's mana
     */
    public static void caseUseHeroAbility(final ArrayNode output, final GameInput game,
                                          final ActionsInput action, final Player player1,
                                          final Player player2, final ObjectMapper objectMapper,
                                          final ArrayList<ArrayList<CardInput>> table) {
        int mana = CardInput.testUseHeroAbility(output, objectMapper, game,
                action, table, player1, player2);
        if (game.getPlayerTurn() == 1) {
            player1.setMana(player1.getMana() - mana);
        } else {
            player2.setMana(player2.getMana() - mana);
        }
    }

}
