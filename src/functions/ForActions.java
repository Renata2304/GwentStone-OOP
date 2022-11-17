package functions;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;

import java.util.ArrayList;

public final class ForActions {

    private ForActions() { }

    /**
     *
     * @param output
     * @param game
     * @param action
     * @param objectMapper
     * @param table
     * @param player1
     * @param player2
     * @param startPlayer
     */
    public static void forAc(final ArrayNode output, final GameInput game,
            final ActionsInput action, final ObjectMapper objectMapper,
            final ArrayList<ArrayList<CardInput>> table, final Player player1,
            final Player player2, final int startPlayer) {
        CardInput copy1, copy2;final int manaMax = 10, rowsmax = 4;
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
                    game.setEndTurn(game.getEndTurn() + 1);
                    if (game.getPlayerTurn() == 1) {
                        SmallFunctions.unfreezeCards(table, 2, 3);
                    } else {
                        SmallFunctions.unfreezeCards(table, 1, 0);
                    }
                    // if they have to both take cards
                    if (game.getEndTurn() % 2 == 0) {
                        // removing the card taken from the deck
                        SmallFunctions.deleteOneCard(player1);
                        // removing the card taken from the deck
                        SmallFunctions.deleteOneCard(player2);
                        game.setPlayerTurn(startPlayer);
                        // TODO
                        player1.setMana(player1.getMana()
                                + OutPrint.incMana(player1.getMana(), game.getEndTurn(), manaMax));
                        player2.setMana(player2.getMana() + OutPrint.incMana(player2.getMana(),
                                game.getEndTurn(), manaMax));
                    } else {
                        // if they don't have to take cards, we're just going to change player's turn
                        if (game.getPlayerTurn() == 1) {
                            game.setPlayerTurn(2);
                        } else {
                            game.setPlayerTurn(1);
                        }
                    }
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
                    int mana = OutPrint.addRow(output, action, player1.getHand(), player2.
                            getHand(), table, game.getPlayerTurn(), action.getHandIdx(),
                            player1.getMana(), player2.getMana());
                    if (mana != 0 && game.getPlayerTurn() == 1) {
                        player1.setMana(player1.getMana() - mana);
                    } else if (mana != 0 && game.getPlayerTurn() == 2) {
                        player2.setMana(player2.getMana() - mana);
                    }
                }
                case "getCardsOnTable" -> {
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
                case "getEnvironmentCardsInHand" -> {
                    OutPrint.printEnvironmentCard(output, objectMapper, player1.getHand(),
                            player2.getHand(), action);
                }
                case "useEnvironmentCard" -> {
                    int mana;
                    if (game.getPlayerTurn() == 1) {
                        mana = CardInput.testCardEnvironment(output, action, game.getPlayerTurn(),
                                table, player1, player1.getHand(), player2.getHand());
                    } else {
                        mana = CardInput.testCardEnvironment(output, action, game.getPlayerTurn(),
                                table, player2, player1.getHand(), player2.getHand());
                    }
                    if (mana != 0) {
                        if (game.getPlayerTurn() == 1) {
                            player1.setMana(player1.getMana() - mana);
                        } else {
                            player2.setMana(player2.getMana() - mana);
                        }
                    }
                }
                case "getCardAtPosition" -> {
                    ObjectNode jsonNodes = output.addObject();
                    jsonNodes.put("command", action.getCommand());
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
                case "getFrozenCardsOnTable" -> {
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
                case "cardUsesAttack" -> {
                    CardInput.testCardAttack(output, objectMapper, game, action, table);
                    table.get(action.getCardAttacker().getX()).
                            get(action.getCardAttacker().getY()).setHasAttacked(false);
                }
                default -> {
                }
            }
    }
}
