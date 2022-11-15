package functions;
/*
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.CardInput;

import java.util.ArrayList;
*/
public final class ForActions {

    private ForActions() { }
    /*
    public static void forAc(final ArrayNode output, final ActionsInput action, final ObjectMapper
            objectMapper, final ArrayList<CardInput> deck1, final ArrayList<CardInput> deck2,
            final ArrayList<CardInput> cardsHand1, final ArrayList<CardInput> cardsHand2,
            final CardInput hero1, final CardInput hero2, int endCont, final int startPlayer,
            int playerTurn, final ArrayList<ArrayList<CardInput>> table) {
        CardInput copy1, copy2;
        int mana1 = 1, mana2 = 1;
        final int manaMax = 10, rowsmax = 3;
        switch (action.getCommand()) {
            case "getPlayerDeck" -> {
                OutPrint.printPlayerDeck(output, objectMapper, deck1, deck2, action,
                        deck1.size(), deck2.size());
            }
            case "getPlayerHero" -> {
                if (action.getPlayerIdx() == 1) {
                    OutPrint.printPlayerHero(output, action, objectMapper, hero1);
                } else {
                    OutPrint.printPlayerHero(output, action, objectMapper, hero2);
                }
            }
            case "endPlayerTurn" -> {
                endCont++;
                // if they have to both take cards
                if (endCont % 2 == 0) {
                    // copy the first card from deck1 to player1's hand
                    // removing the card taken from the deck
                    if (!deck1.isEmpty()) {
                        copy1 = new CardInput(); copy1 = copy1.copyOneCard(deck1, 0);
                        cardsHand1.add(copy1); deck1.remove(0);
                    }
                    // removing the card taken from the deck
                    if (!deck2.isEmpty()) {
                        copy2 = new CardInput(); copy2 = copy2.copyOneCard(deck2, 0);
                        cardsHand2.add(copy2); deck2.remove(0);
                    }
                    playerTurn = startPlayer;
                    mana1 += OutPrint.incMana(mana1, endCont, manaMax);
                    mana2 += OutPrint.incMana(mana2, endCont, manaMax);
                } else {
                    // if they don't have to take cards, we're just going to
                    // change player's turn
                    if (playerTurn == 1) {
                        playerTurn = 2;
                    } else {
                        playerTurn = 1;
                    }
                }
            }
            case "getPlayerTurn" -> {
                ObjectNode jsonNodes = output.addObject();
                jsonNodes.put("command", action.getCommand());
                jsonNodes.put("output", playerTurn);
            }
            case "getPlayerMana" -> {
                ObjectNode jsonNodes = output.addObject();
                jsonNodes.put("command", action.getCommand());
                jsonNodes.put("playerIdx", action.getPlayerIdx());
                if (action.getPlayerIdx() == 1) {
                    jsonNodes.put("output", mana1);
                } else {
                    jsonNodes.put("output", mana2);
                }
            }
            case "getCardsInHand" -> {
                OutPrint.printPlayerDeck(output, objectMapper, cardsHand1, cardsHand2,
                        action, cardsHand1.size(), cardsHand2.size());
            }
            case "placeCard" -> {
                int mana = OutPrint.addRow(output, action, cardsHand1, cardsHand2, table,
                        playerTurn, action.getHandIdx(), mana1, mana2);
                if (mana != 0 && playerTurn == 1) {
                    mana1 -= mana;
                } else if (mana != 0 && playerTurn == 2) {
                    mana2 -= mana;
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
                OutPrint.printEnvironmentCard(output, objectMapper, cardsHand1, cardsHand2,
                        action, cardsHand1.size(), cardsHand2.size());
            }
            case "useEnvironmentCard" -> {
                int mana = CardInput.testCardEnvironment(output, action, playerTurn, table,
                        cardsHand1, cardsHand2, mana1, mana2);
                if (mana != 0) {
                    if (playerTurn == 1) {
                        mana1 -= mana;
                    } else {
                        mana2 -= mana;
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
            default -> {
            }
        }
    }
    */
}
