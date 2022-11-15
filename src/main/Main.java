package main;

import checker.Checker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import checker.CheckerConstants;
//import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.CardInput;
import fileio.GameInput;
import fileio.Input;
import other.OutPrint;

import java.util.ArrayList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

/**
 * The entry point to this homework. It runs the checker that tests your implementation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Input inputData = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH + filePath1),
                Input.class);

        ArrayNode output = objectMapper.createArrayNode();
        //TODO add here the entry point to your implementation
        //GameInput game = inputData.getGames().get(0);

        for (GameInput game : inputData.getGames()) {
            int playerTurn = game.getStartGame().getStartingPlayer();
            int startPlayer = playerTurn;
            int shuff = game.getStartGame().getShuffleSeed();

            int idx1 = game.getStartGame().getPlayerOneDeckIdx();
            int idx2 = game.getStartGame().getPlayerTwoDeckIdx();

            int mana1 = 1, mana2 = 1;

            int endCont = 0;

            int nrCards1 = inputData.getPlayerOneDecks().getNrCardsInDeck();
            int nrCards2 = inputData.getPlayerTwoDecks().getNrCardsInDeck();

            int nrCardsHand1 = 0; int nrCardsHand2 = 0;
            final int rowsmax = 4, manaMax = 10;

            ArrayList<ArrayList<CardInput>> table = new ArrayList<>();
            table.add(new ArrayList<>()); table.add(new ArrayList<>());
            table.add(new ArrayList<>()); table.add(new ArrayList<>());

            ArrayList<CardInput> deck1 = new ArrayList<>();
            ArrayList<CardInput> deck2 = new ArrayList<>();

            // generating player1's deck
            for (int i = 0; i < nrCards1; i++) {
                CardInput copy = new CardInput();
                copy = copy.copyOneCard(inputData.getPlayerOneDecks().getDecks().get(idx1), i);
                deck1.add(copy);
            }

            // generation player2's deck
            for (int i = 0; i < nrCards2; i++) {
                CardInput copy = new CardInput();
                copy = copy.copyOneCard(inputData.getPlayerTwoDecks().getDecks().get(idx2), i);
                deck2.add(copy);
            }

            // shuffle the two decks
            Collections.shuffle(deck1, new Random(shuff));
            Collections.shuffle(deck2, new Random(shuff));

            // getting the hero for each player
            CardInput hero1 = game.getStartGame().getPlayerOneHero();
            CardInput hero2 = game.getStartGame().getPlayerTwoHero();

            // setting health for each player's hero
            final int nr = 30;
            hero1.setHealth(nr);
            hero2.setHealth(nr);

            // creating each player's hands
            ArrayList<CardInput> cardsHand1 = new ArrayList<>();
            ArrayList<CardInput> cardsHand2 = new ArrayList<>();

            CardInput copy1 = new CardInput();
            copy1 = copy1.copyOneCard(deck1, 0);
            cardsHand1.add(copy1);
            // removing the card taken from the deck
            deck1.remove(0);
            nrCards1--;
            nrCardsHand1++;

            CardInput copy2 = new CardInput();
            copy2 = copy2.copyOneCard(deck2, 0);
            cardsHand2.add(copy2);
            // removing the card taken from the deck
            deck2.remove(0);
            nrCards2--;
            nrCardsHand2++;

            // going through actions
            for (ActionsInput action : game.getActions()) {
                switch (action.getCommand()) {
                    case "getPlayerDeck" -> {
                        OutPrint.printPlayerDeck(output, objectMapper, deck1, deck2, action,
                                nrCards1, nrCards2);
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
                                copy1 = new CardInput();
                                copy1 = copy1.copyOneCard(deck1, 0);
                                cardsHand1.add(copy1);
                                deck1.remove(0);
                                nrCards1--;
                                nrCardsHand1++;
                            }


                            // removing the card taken from the deck
                            if (!deck2.isEmpty()) {
                                copy2 = new CardInput();
                                copy2 = copy2.copyOneCard(deck2, 0);
                                cardsHand2.add(copy2);
                                deck2.remove(0);
                                nrCards2--;
                                nrCardsHand2++;
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
                                    action, nrCardsHand1, nrCardsHand2);
                    }
                    case "placeCard" -> {
                        int mana = OutPrint.addRow(cardsHand1, cardsHand2, table, playerTurn,
                                action.getHandIdx());
                        if (mana != 0 && playerTurn == 1) {
                            nrCardsHand1--;
                            mana1 -= mana;
                        } else if (mana != 0 && playerTurn == 2) {
                            nrCardsHand2--;
                            mana2 -= mana;
                        }
                    }
                    case "getCardsOnTable" -> {
                        ObjectNode jsonNodes = output.addObject();
                        jsonNodes.put("command", action.getCommand());
                        ArrayNode arrayNode1 = jsonNodes.putArray("output");
                        for (int i = 0; i < rowsmax; i++) {
                            if (!table.get(i).isEmpty()) {
                                ArrayNode arrayNode2 = arrayNode1.addArray();
                                for (int cardId = 0; cardId < table.get(i).size(); cardId++) {
                                    OutPrint.printCard(objectMapper, arrayNode2, table.get(i),
                                            cardId);
                                }
                            }
                        }
                    }
                    default -> {
                    }
                }
                ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
                objectWriter.writeValue(new File(filePath2), output);
            }
        }
    }
}
