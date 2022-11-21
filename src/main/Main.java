package main;

import checker.Checker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import checker.CheckerConstants;

import fileio.ActionsInput;
import fileio.CardInput;
import fileio.DecksInput;
import fileio.GameInput;
import fileio.Input;
import fileio.Player;
import functions.ForActions;

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
        Player player1 = new Player();
        Player player2 = new Player();
        for (GameInput game : inputData.getGames()) {
            int startPlayer = game.getStartGame().getStartingPlayer();
            int shuff = game.getStartGame().getShuffleSeed();
            int idx1 = game.getStartGame().getPlayerOneDeckIdx();
            int idx2 = game.getStartGame().getPlayerTwoDeckIdx();
            int endCont = 0; game.setEndTurn(endCont);
            game.setPlayerTurn(startPlayer);
            player1.setMana(1); player2.setMana(1);
            ArrayList<ArrayList<CardInput>> table = new ArrayList<>();
            table.add(new ArrayList<>()); table.add(new ArrayList<>());
            table.add(new ArrayList<>()); table.add(new ArrayList<>());
            // generating player1's deck
            player1.setDeck(DecksInput.getNewDeck(inputData.getPlayerOneDecks().
                    getDecks().get(idx1), inputData.getPlayerOneDecks().getDecks().
                    get(idx1).size()));
            // generation player2's deck
            player2.setDeck(DecksInput.getNewDeck(inputData.getPlayerTwoDecks().
                    getDecks().get(idx2), inputData.getPlayerTwoDecks().getDecks().
                    get(idx2).size()));
            // shuffle the two decks
            Collections.shuffle(player1.getDeck(), new Random(shuff));
            Collections.shuffle(player2.getDeck(), new Random(shuff));
            // getting the hero for each player
            player1.setCardHero(game.getStartGame().getPlayerOneHero());
            player2.setCardHero(game.getStartGame().getPlayerTwoHero());
            // setting health for each player's hero
            final int nr = 30; player1.getCardHero().setHealth(nr); player2.getCardHero().
                    setHealth(nr);
            player1.setHand(new ArrayList<>()); player2.setHand(new ArrayList<>());
            // taking the first card
            CardInput copy1 = new CardInput(); copy1 = copy1.copyOneCard(player1.getDeck(), 0);
            player1.getHand().add(copy1);
            // removing the card taken from the deck
            player1.getDeck().remove(0);
            // taking the first card
            CardInput copy2 = new CardInput(); copy2 = copy2.copyOneCard(player2.getDeck(), 0);
            player2.getHand().add(copy2);
            // removing the card taken from the deck
            player2.getDeck().remove(0);
            // going through actions
            for (ActionsInput action : game.getActions()) {
                ForActions.forAc(output, game, action, objectMapper, table, player1, player2,
                                 startPlayer);
            }
        }
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }
}
