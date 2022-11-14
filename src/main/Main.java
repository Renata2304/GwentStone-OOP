package main;

import checker.Checker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.CardInput;
import fileio.GameInput;
import fileio.Input;

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
            int shuff = game.getStartGame().getShuffleSeed();
            int idx1 = game.getStartGame().getPlayerOneDeckIdx();
            int idx2 = game.getStartGame().getPlayerTwoDeckIdx();

            ArrayList<CardInput> deck1 = new ArrayList<>();
            ArrayList<CardInput> deck2 = new ArrayList<>();
            // generating player1's deck
            for (int i = 0; i < inputData.getPlayerOneDecks().getNrCardsInDeck(); i++) {
                CardInput copy = new CardInput();
                copy = copy.copyPlayer(inputData.getPlayerOneDecks(), idx1, i);
                deck1.add(copy);
            }
            // generation player2's deck
            for (int i = 0; i < inputData.getPlayerTwoDecks().getNrCardsInDeck(); i++) {
                CardInput copy = new CardInput();
                copy = copy.copyPlayer(inputData.getPlayerTwoDecks(), idx2, i);
                deck2.add(copy);
            }

            Collections.shuffle(deck1, new Random(shuff));
            Collections.shuffle(deck2, new Random(shuff));

            for (ActionsInput action : game.getActions()) {
                switch (inputData.getGames().get(0).getActions().get(0).getCommand()) {
                    case "getPlayerDeck" -> {
                        //tsk1
                        switch (action.getPlayerIdx()) {
                            case 2 -> {
                                // output player two
                                ObjectNode jsonNodes = output.addObject();
                                jsonNodes.put("command", action.getCommand());
                                jsonNodes.put("playerIdx", action.getPlayerIdx());
                                ArrayNode arrayNode = jsonNodes.putArray("output");
                                for (int i = 1; i < inputData.getPlayerTwoDecks().getNrCardsInDeck(); i++) {
                                    CardInput card = deck2.get(i);
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
                            }
                            case 1 -> { // output player one
                                ObjectNode jsonNodes = output.addObject();
                                jsonNodes.put("command", action.getCommand());
                                jsonNodes.put("playerIdx", action.getPlayerIdx());
                                ArrayNode arrayNode = jsonNodes.putArray("output");
                                for (int i = 1; i < inputData.getPlayerOneDecks().getNrCardsInDeck(); i++) {
                                    CardInput card = deck1.get(i);
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
                            }
                        }
                    }
                    case "endPlayerTurn" -> {
                        //idk
                    }
                    case "getPlayerHero" -> {
                        ObjectNode jsonNodes = output.addObject();
                        jsonNodes.put("command", action.getCommand());
                        jsonNodes.put("playerIdx", action.getPlayerIdx());
                        ArrayNode arrayNode = jsonNodes.putArray("output");
                    }
                }
                //System.out.printf(filePath2, deck1);
                ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
                objectWriter.writeValue(new File(filePath2), output);
            }
        }
    }
}
