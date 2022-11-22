package fileio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import functions.OutPrint;
import functions.SmallFunctions;

import java.util.ArrayList;
import java.util.Objects;

public final class CardInput {
    private int mana;
    private int attackDamage;
    private int health;
    private String description;
    private ArrayList<String> colors;
    private String name;
    private boolean frozen = false;
    private boolean hasAttacked = false;
    static final int ROW0 = 0, ROW1 = 1, ROW2 = 2, ROW3 = 3;

    public CardInput() {
    }

    public int getMana() {
        return mana;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(final int health) {
        this.health = health;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(final ArrayList<String> colors) {
        this.colors = colors;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(final boolean frozen) {
        this.frozen = frozen;
    }

    public boolean isHasAttacked() {
        return hasAttacked;
    }

    public void setHasAttacked(final boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    /**
     * Function that tests if a card is Tank, based on its name.
     * @return true (the card is a Tank) / false (otherwise)
     */
    public static boolean isTank(final CardInput card) {
        if (Objects.equals(card.getName(), "Goliath")) {
            return true;
        }
        if (Objects.equals(card.getName(), "Warden")) {
            return true;
        }
        return false;
    }

    /**
     * Function that returns the type of card (given as a parameter).
     * @return Minion (if the card is a minion) / Environment (if the card is an environment card)
     * / "No type" (if it is an error)
     */
    public String getType(final CardInput card) {
        if (Objects.equals(card.name, "Sentinel") || Objects.equals(card.name, "Berserker")
            || Objects.equals(card.name, "Goliath") || Objects.equals(card.name, "Warden")
            || Objects.equals(card.name, "Miraj") || Objects.equals(card.name, "Disciple")
            || Objects.equals(card.name, "The Cursed One") || Objects.equals(card.name,
                "The Ripper")) {
            return "Minion";
        } else if (Objects.equals(card.name, "Firestorm") || Objects.equals(card.name,
                "Winterfell") || Objects.equals(card.name, "Heart Hound")) {
            return "Environment";
        }
        return "No type";
    }

    /**
     * Function used for deep copying one card found in given deck at position i.
     */
    public static CardInput copyOneCard(final ArrayList<CardInput> deck, final int i) {
        CardInput card = new CardInput();
        card.setMana(deck.get(i).getMana());
        card.setAttackDamage(deck.get(i).getAttackDamage());
        card.setHealth(deck.get(i).getHealth());
        card.setDescription(deck.get(i).getDescription());
        card.setColors(deck.get(i).getColors());
        card.setName(deck.get(i).getName());

        return card;
    }

    /**
     * Function used to get the row (front/back) on which a card should pe placed.
     * @return front (1) / back (2) / error (0)
     */
    public int getPosition(final CardInput card) {
        if (Objects.equals(card.name, "The Ripper")
                || Objects.equals(card.name, "Miraj")
                || Objects.equals(card.name, "Goliath")
                || Objects.equals(card.name, "Warden")) {
            return 1; // front
        } else if (Objects.equals(card.name, "Sentinel")
                || Objects.equals(card.name, "Disciple")
                || Objects.equals(card.name, "The Cursed One")
                || Objects.equals(card.name, "Berserker")) {
            return 2; // back
        }
        return 0;
    }

    /**
     * Function used for useEnvironmentCard case. At first, it tests if there are any errors that
     * can prevent the player from attacking a card (using testErrorEnvironment). If there are
     * no errors, there will be 3 cases, based on the 3 types of environment cards.
     * In the end, I erase the environment from the player's hand.
     * @return mana of the environment card used, so that it could be decreased from the player's
     * total mana.
     */
    public static int testCardEnvironment(final ArrayNode output, final GameInput game,
                                          final ActionsInput action,
                                          final ArrayList<ArrayList<CardInput>> table,
                                          final Player player,
                                          final ArrayList<CardInput> cardsHand) {
        CardInput cardenv = cardsHand.get(action.getHandIdx());

        // testing to see if there are any errors
        boolean error = SmallFunctions.testErrorEnvironment(output, game, action, table, player,
                cardenv);

        // if there are no errors
        if (!error) {
            int mana = cardenv.getMana();
            // Heart Hound case
            // I don't test the error because I already tested it in testErrorEnvironment (case 4)
            if (Objects.equals(cardenv.getName(), "Heart Hound")) {
                cardsHand.remove(cardenv);
                return mana;
            }
            // Firestorm case
            if (Objects.equals(cardenv.getName(), "Firestorm")) {
                CardInput.firestorm(action, table);
                cardsHand.remove(cardenv);
                return mana;
            }
            // Winterfell case
            if (Objects.equals(cardenv.getName(), "Winterfell")) {
                CardInput.winterfell(action, table);
                cardsHand.remove(cardenv);
                return mana;
            }
        }
        return 0;
    }

    /**
     * Treating the Firestorm card case. If the environment card used by the player is
     * Firestorm, I decrease all the card's health. If their health is <= 0, they will be
     * erased from the table.
     */
    public static void firestorm(final ActionsInput action,
                                 final ArrayList<ArrayList<CardInput>> table) {
        for (int i = 0; i < table.get(action.getAffectedRow()).size(); i++) {
            table.get(action.getAffectedRow()).get(i).setHealth(table.
                    get(action.getAffectedRow()).get(i).getHealth() - 1);
            if (table.get(action.getAffectedRow()).get(i).getHealth() == 0) {
                table.get(action.getAffectedRow()).remove(i);
                i--;
            }
        }
    }

    /**
     * Treating the Winterfell card case. If the environment card used by the player is
     * Winterfell, all the cards from a specific row will be frozen.
     */
    public static void winterfell(final ActionsInput action,
                                  final ArrayList<ArrayList<CardInput>> table) {
        for (int i = 0; i < table.get(action.getAffectedRow()).size(); i++) {
            table.get(action.getAffectedRow()).get(i).setFrozen(true);
        }
    }

    /**
     * Treating the Heart Hound card case. If the environment card used by the player is
     * Heart Hound, the player will steal the opponent's card that has the most health and
     * place it on their row.
     * @return true (if the action is completed succesfully) / false (if there is no more
     * space on the player's row.
     */
    public static boolean heartHound(final ArrayNode output, final ActionsInput action,
                                     final ArrayList<ArrayList<CardInput>> table) {
        final int maxRow = 4, maxCol = 5, case4 = 4;
        int currRow = maxRow - action.getAffectedRow();
        if (table.get(currRow).size() < maxCol) {
            CardInput cardStolen = table.get(action.getAffectedRow()).get(0);
            for (int i = 1; i < table.get(action.getAffectedRow()).size(); i++) {
                if (cardStolen.health < table.get(action.getAffectedRow()).get(i).health) {
                    cardStolen = table.get(action.getAffectedRow()).get(i);
                }
            }
            table.get(action.getAffectedRow()).remove(cardStolen);
            table.get(currRow).add(cardStolen);
            return true;
        } else {
            OutPrint.printErrorEnvironment(output, action, case4);
            return false;
        }
    }

    /**
     * Function used for the cardUsesAttack case. At first, it tests if there are any errors that
     * can prevent the player from attacking a card (using testErrorCardAttack). If there are
     * no errors, I decrease the health of the attacked card and if the card has health <= 0,
     * I erase it from the table. In the end, I set the hasAttacked status of the player's
     * card to true so that it won't attack twice during the same round.
     */
    public static void testCardAttack(final ArrayNode output, final ObjectMapper objectMapper,
                                      final GameInput game, final ActionsInput action,
                                      final ArrayList<ArrayList<CardInput>> table) {
        CardInput cardAttacker = table.get(action.getCardAttacker().getX()).
                get(action.getCardAttacker().getY());
        CardInput cardAttacked = table.get(action.getCardAttacked().getX()).
                get(action.getCardAttacked().getY());

        final int xAttacked = action.getCardAttacked().getX(),
                  yAttacked = action.getCardAttacked().getY();

        // testing to see if there are any errors
        boolean error = SmallFunctions.testErrorCardAttack(objectMapper, output, table, action,
                                                    game, cardAttacker, cardAttacked);
        // if there are no errors
        if (!error) {
            cardAttacked.setHealth(cardAttacked.getHealth() - cardAttacker.getAttackDamage());
            if (cardAttacked.getHealth() <= 0) {
                table.get(xAttacked).remove(yAttacked);
            }
            cardAttacker.setHasAttacked(true);
        }
    }

    /**
     * Function used for the cardUsesAbility case. At first, it tests if there are any errors that
     * can prevent the player from using a card's ability (using testErrorUseAbility). If there are
     * no errors, based on each card's ability, the player can use their card's ability. In the
     * end, the attacker's card attack status will be set to true so that it won't attack
     * twice during the same round. If the opponent's damage lvl will go under 0, it will be
     * set to 0. If the opponent's card's life will ge under 0, it will be erased from the table.
     */
    public static void testUseAbility(final ArrayNode output, final ObjectMapper objectMapper,
                                      final GameInput game, final ActionsInput action,
                                      final ArrayList<ArrayList<CardInput>> table) {

        CardInput cardAttacker = table.get(action.getCardAttacker().getX()).
                get(action.getCardAttacker().getY());
        CardInput cardAttacked = table.get(action.getCardAttacked().getX()).
                get(action.getCardAttacked().getY());

        // testing to see if there are any errors
        boolean error = SmallFunctions.testErrorUseAbility(objectMapper, output, action,
                game, table);

        // if there are no errors
        if (!error) {
            switch (cardAttacker.getName()) {
                case "The Ripper" -> {
                    cardAttacked.setAttackDamage(cardAttacked.getAttackDamage() - 2);
                }
                case "Miraj" -> {
                    final int aux = cardAttacker.getHealth();
                    cardAttacker.setHealth(cardAttacked.getHealth());
                    cardAttacked.setHealth(aux);
                }
                case "The Cursed One" -> {
                    final int aux = cardAttacked.getHealth();
                    cardAttacked.setHealth(cardAttacked.getAttackDamage());
                    cardAttacked.setAttackDamage(aux);
                }
                case "Disciple" -> {
                    cardAttacked.setHealth(cardAttacked.getHealth() + 2);
                }
                default -> {
                }
            }
            cardAttacker.setHasAttacked(true);
            if (cardAttacked.getHealth() <= 0) {
                table.get(action.getCardAttacked().getX()).remove(action.getCardAttacked().getY());
            }
            if (cardAttacked.getAttackDamage() < 0) {
                cardAttacked.setAttackDamage(0);
            }
        }
    }

    /**
     * Function used for the useAttackHero case. At first, it tests if there are any errors that
     * can prevent the player from attacking the opponent's hero (using testErrorAttackHero).
     * If there are no errors, based on the current player's turn, the active player will
     * attack the opponent's hero, decreasing its life, by the value of the player's card's
     * attack. If the life of the opponent's hero goes under 0, the game ends and the player's
     * amount of games won will be increased. The amount of games played will be increased
     * regardless of the player's turn.
     */
    public static void testAttackHero(final ArrayNode output, final ObjectMapper objectMapper,
            final GameInput game, final ActionsInput action, final ArrayList<ArrayList<CardInput>>
            table, final Player player1, final Player player2) {

        CardInput cardAttacker = table.get(action.getCardAttacker().getX()).
                get(action.getCardAttacker().getY());

        // testing to see if there are any errors
        boolean error = SmallFunctions.testErrorAttackHero(objectMapper, output, game, action,
                                                           table, cardAttacker);

        // if there are no errors
        if (!error) {
            cardAttacker.setHasAttacked(true);
            // good test
            switch (game.getPlayerTurn()) {
                case 1 -> {
                    player2.getCardHero().setHealth(player2.getCardHero().getHealth()
                            - cardAttacker.getAttackDamage());
                    if (player2.getCardHero().getHealth() <= 0) {
                        OutPrint.playerKilledHero(objectMapper, output, 1);
                        player1.setNrGamesWon(player1.getNrGamesWon() + 1);
                        OutPrint.setGamesWon(OutPrint.getGamesWon() + 1);
                    }
                }
                case 2 -> {
                    player1.getCardHero().setHealth(player1.getCardHero().getHealth()
                            - cardAttacker.getAttackDamage());
                    if (player1.getCardHero().getHealth() <= 0) {
                        OutPrint.playerKilledHero(objectMapper, output, 2);
                        player2.setNrGamesWon(player2.getNrGamesWon() + 1);
                        OutPrint.setGamesWon(OutPrint.getGamesWon() + 1);
                    }
                }
                default -> {
                }
            }
        }
    }

    /**
     * Function used for useHeroAbility case. At first, it tests if there are any errors that
     * can prevent the player from using their hero's ability (using testErrorUseHeroAbility).
     * If there are no errors, there will be 4 cases, based on the 4 hero cards types.
     * @return mana of the hero card used, so that it could be decreased from the player's
     * total mana.
     */
    public static int testUseHeroAbility(final ArrayNode output, final ObjectMapper objectMapper,
            final GameInput game, final ActionsInput action, final ArrayList<ArrayList<CardInput>>
            table, final Player player1, final Player player2) {

        final int rowFront,  rowBack; CardInput hero; boolean error;
        // get front and back row and hero for current player
        if (game.getPlayerTurn() == 1) {
            rowFront = ROW2; rowBack = ROW3;
            hero = player1.getCardHero();
        } else {
            rowFront = ROW1; rowBack = ROW0;
            hero = player2.getCardHero();
        }

        if (game.getPlayerTurn() == 1) {
            error = SmallFunctions.testErrorUseHeroAbility(objectMapper, output, action,
                    player1, hero, rowFront, rowBack);
        } else {
            error = SmallFunctions.testErrorUseHeroAbility(objectMapper, output, action,
                    player2, hero, rowFront, rowBack);
        }
        if (!error) {
            // good test
            if (hero.getName().equals("Lord Royce")) {
                int idx = 0;
                int attackMax = -1;
                for (int i = 0; i < table.get(action.getAffectedRow()).size(); i++) {
                    if (table.get(action.getAffectedRow()).get(i).getAttackDamage() > attackMax) {
                        attackMax = table.get(action.getAffectedRow()).get(i).getAttackDamage();
                        idx = i;
                    }
                }
                table.get(action.getAffectedRow()).get(idx).setFrozen(true);
                hero.setHasAttacked(true);
                return hero.getMana();
            }
            if (hero.getName().equals("Empress Thorina")) {
                int idx = 0;
                int healthMax = 0;
                for (int i = 0; i < table.get(action.getAffectedRow()).size(); i++) {
                    if (table.get(action.getAffectedRow()).get(i).getHealth() > healthMax) {
                        healthMax = table.get(action.getAffectedRow()).get(i).getHealth();
                        idx = i;
                    }
                }
                table.get(action.getAffectedRow()).remove(idx);
                hero.setHasAttacked(true);
                return hero.getMana();
            }
            if (hero.getName().equals("King Mudface")) {
                for (int i = 0; i < table.get(action.getAffectedRow()).size(); i++) {
                    table.get(action.getAffectedRow()).get(i).setHealth(
                            table.get(action.getAffectedRow()).get(i).getHealth() + 1);
                }
                hero.setHasAttacked(true);
                return hero.getMana();
            }
            if (hero.getName().equals("General Kocioraw")) {
                for (int i = 0; i < table.get(action.getAffectedRow()).size(); i++) {
                    table.get(action.getAffectedRow()).get(i).setAttackDamage(
                            table.get(action.getAffectedRow()).get(i).getAttackDamage() + 1);
                }
                hero.setHasAttacked(true);
                return hero.getMana();
            }
        }
        return 0;
    }

    /**
     * Function that tests whether there are tank cards on the opponent's rows.
     * @return true (there are tank cards on the opponent's rows) / false (otherwise)
     */
    public static boolean testIfThereAreTanks(final ArrayList<ArrayList<CardInput>> table,
                                              final GameInput game) {
        int rowFrontEnemy, rowBackEnemy;
        if (game.getPlayerTurn() == 1) {
            rowFrontEnemy = ROW1; rowBackEnemy = ROW0;
        } else {
            rowFrontEnemy = ROW2; rowBackEnemy = ROW3;
        }
        for (int i = 0; i < table.get(rowFrontEnemy).size(); i++) {
            if (CardInput.isTank(table.get(rowFrontEnemy).get(i))) {
                return true;
            }
        }
        for (int i = 0; i < table.get(rowBackEnemy).size(); i++) {
            if (CardInput.isTank(table.get(rowBackEnemy).get(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "CardInput{"
                +  "mana="
                + mana
                +  ", attackDamage="
                + attackDamage
                + ", health="
                + health
                +  ", description='"
                + description
                + '\''
                + ", colors="
                + colors
                + ", name='"
                +  ""
                + name
                + '\''
                + '}';
    }
}
