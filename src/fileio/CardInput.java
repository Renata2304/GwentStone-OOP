package fileio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
     *
     * @param card
     * @return
     */
    public boolean isTank(CardInput card) {
        if(Objects.equals(card.getName(), "Goliath"))
            return true;
        if(Objects.equals(card.getName(), "Warden"))
            return true;
        return false;
    }

    /**
     *
     * @param card
     * @return
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
     *
     * @param card
     * @param manaHand
     * @return
     */
    public static boolean testErrorCardPlaceCard(final ArrayNode output, final ActionsInput action,
                            final CardInput card, final int manaHand) {
        if (Objects.equals(card.getType(card), "Environment")) {
            ObjectNode jsonNodes = output.addObject();
            jsonNodes.put("command", action.getCommand());
            jsonNodes.put("handIdx", action.getHandIdx());
            jsonNodes.put("error",
                    "Cannot place environment card on table.");
            return false;
        }
        if (card.getMana() > manaHand) {
            ObjectNode jsonNodes = output.addObject();
            jsonNodes.put("command", action.getCommand());
            jsonNodes.put("handIdx", action.getHandIdx());
            jsonNodes.put("error",
                    "Not enough mana to place card on table.");
            return false;
        }
        return true;
    }

    /**
     *
     * @param output
     * @param action
     * @param playerTurn
     * @param table
     * @param cardsHand1
     * @param cardsHand2
     * @return
     */
    public static int testCardEnvironment(final ArrayNode output, final ActionsInput action,
                        final int playerTurn, final ArrayList<ArrayList<CardInput>> table,
                        final Player player, final ArrayList<CardInput> cardsHand1,
                        final ArrayList<CardInput> cardsHand2) {
        switch (playerTurn) {
            case 1 -> {
                return SmallFunctions.testErrorEnvironment(output, action, table, player, 0, 1,
                        cardsHand1);
            }
            case 2 -> {
                return SmallFunctions.testErrorEnvironment(output, action, table, player, 2, 3,
                        cardsHand2);
            }
            default -> {
                return 0;
            }
        }
    }

    /**
     *
     * @param output
     * @param objectMapper
     * @param game
     * @param action
     * @param table
     */
    public static void testCardAttack(final ArrayNode output, final ObjectMapper objectMapper,
            final GameInput game, final ActionsInput action, final ArrayList<ArrayList<CardInput>>
            table) {
        final int case1 = 1, case2 = 2, case3 = 3, case4 = 4;
        CardInput cardAttacker = table.get(action.getCardAttacker().getX()).
                get(action.getCardAttacker().getY());
        CardInput cardAttacked = table.get(action.getCardAttacked().getX()).
                get(action.getCardAttacked().getY());
        final int xAttacked = action.getCardAttacked().getX(),
                  yAttacked = action.getCardAttacked().getY();
        if (game.getPlayerTurn() == 1) {
            // error 1
            if (xAttacked == 2 || xAttacked == 3) {
                OutPrint.printErrorAttack(objectMapper, output, action, case1);
                return;
            }
            // error 2
            if(cardAttacker.isHasAttacked()) {
                OutPrint.printErrorAttack(objectMapper, output, action, case2);
                return;
            }
            // error 3
            if (cardAttacker.isFrozen()) {
                OutPrint.printErrorAttack(objectMapper, output, action, case3);
                return;
            }
            // error 4
            if (!cardAttacked.isTank(cardAttacked)
                    && testIfThereAreTanks(table, game)) {
                OutPrint.printErrorAttack(objectMapper, output, action, case4);
                return;
            }
            // good test
            cardAttacked.setHealth(cardAttacked.getHealth() - cardAttacker.getAttackDamage());
            if (cardAttacked.getHealth() <= 0) {
                table.get(xAttacked).remove(yAttacked);
            }
            cardAttacker.setHasAttacked(true);
            return;
        } else {
            // error 1
            if (xAttacked == 0 || xAttacked == 1) {
                OutPrint.printErrorAttack(objectMapper, output, action, case1);
                return;
            }
            // error 2
            if(cardAttacker.isHasAttacked()) {
                OutPrint.printErrorAttack(objectMapper, output, action, case2);
                return;
            }
            // error 3
            if (cardAttacker.isFrozen()) {
                OutPrint.printErrorAttack(objectMapper, output, action, case3);
                return;
            }
            // error 4
            if (!cardAttacked.isTank(cardAttacked)
                    && testIfThereAreTanks(table, game)) {
                OutPrint.printErrorAttack(objectMapper, output, action, case4);
                return;
            }
            // good test
            cardAttacked.setHealth(cardAttacked.getHealth() - cardAttacker.getAttackDamage());
            if (cardAttacked.getHealth() <= 0) {
                table.get(xAttacked).remove(yAttacked);
            }
            cardAttacker.setHasAttacked(true);
            return;
        }
    }

    public static boolean testIfThereAreTanks(final ArrayList<ArrayList<CardInput>> table,
                                              final GameInput game) {
        if (game.getPlayerTurn() == 1) {
            for (int y = 0; y < table.get(0).size(); y++) {
                if (table.get(0).get(y).isTank(table.get(0).get(y))) {
                    return true;
                }
            }
            for (int y = 0; y < table.get(1).size(); y++) {
                if (table.get(1).get(y).isTank(table.get(1).get(y))) {
                    return true;
                }
            }
            return false;
        } else {
            for (int y = 0; y < table.get(2).size(); y++) {
                if (table.get(2).get(y).isTank(table.get(2).get(y))) {
                    return true;
                }
            }
            for (int y = 0; y < table.get(3).size(); y++) {
                if (table.get(3).get(y).isTank(table.get(3).get(y))) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     *
     * @param action
     * @param table
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
     *
     * @param action
     * @param table
     */
    public static void winterfell(final ActionsInput action,
                                  final ArrayList<ArrayList<CardInput>> table) {
        for (int i = 0; i < table.get(action.getAffectedRow()).size(); i++) {
            table.get(action.getAffectedRow()).get(i).setFrozen(true);
        }
    }

    /**
     *
     * @param output
     * @param action
     * @param table
     * @return
     */
    public static boolean heartHound(final ArrayNode output, final ActionsInput
            action, final ArrayList<ArrayList<CardInput>> table) {
        final int maxRow = 5, maxCol = 3, case4 = 4;
        int currRow = maxCol - action.getAffectedRow();
        if (table.get(currRow).size() < maxRow) {
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
     *
     * @param card
     * @return
     */
    public int getPosition(final CardInput card) {
        if (Objects.equals(card.name, "The Ripper") || Objects.equals(card.name, "Miraj")
                || Objects.equals(card.name, "Goliath") || Objects.equals(card.name, "Warden")) {
            return 1; // front
        } else if (Objects.equals(card.name, "Sentinel") || Objects.equals(card.name, "Disciple")
                || Objects.equals(card.name, "The Cursed One") || Objects.equals(card.name,
                "Berserker")) {
            return 2; // back
        }
        return 0;
    }

    /**
     *
     */
    public CardInput copyOneCard(final ArrayList<CardInput> deck, final int i) {
        CardInput card = new CardInput();
        card.setMana(deck.get(i).getMana());
        card.setAttackDamage(deck.get(i).getAttackDamage());
        card.setHealth(deck.get(i).getHealth());
        card.setDescription(deck.get(i).getDescription());
        card.setColors(deck.get(i).getColors());
        card.setName(deck.get(i).getName());

        return card;
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
