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

            CardInput hero1 = game.getStartGame().getPlayerOneHero();
            CardInput hero2 = game.getStartGame().getPlayerTwoHero();
            final int nr = 30;
            hero1.setHealth(nr);
            hero2.setHealth(nr);

            for (ActionsInput action : game.getActions()) {
                switch (action.getCommand()) {
                    case "getPlayerDeck" -> {
                        //tsk1
                        OutPrint.printPlayerDeck(output, objectMapper, inputData.
                                getPlayerTwoDecks().getNrCardsInDeck(), deck1, deck2, action);
                    }
                    case "endPlayerTurn" -> {
                        //idk
                        if (playerTurn == 1) {
                            playerTurn = 2;
                        } else {
                            playerTurn = 1;
                        }
                    }
                    case "getPlayerHero" -> {
                        if (action.getPlayerIdx() == 1) {
                            OutPrint.printPlayerHero(output, action, objectMapper, hero1);
                        } else {
                            OutPrint.printPlayerHero(output, action, objectMapper, hero2);
                        }
                    }
                    case "getPlayerTurn" -> {
                        ObjectNode jsonNodes = output.addObject();
                        jsonNodes.put("command", action.getCommand());
                        jsonNodes.put("output", playerTurn);
                    }
                    default -> {
                    }
                }
                //System.out.printf(filePath2, deck1);
                ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
                objectWriter.writeValue(new File(filePath2), output);
            }
        }
    }
}
