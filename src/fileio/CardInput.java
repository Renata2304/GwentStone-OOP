package fileio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import functions.OutPrint;

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
    public static boolean testCardPlaceCard(final ArrayNode output, final ActionsInput action,
                            final CardInput card, final int manaHand) {
        if (Objects.equals(card.getType(card), "Environment")) {
            ObjectNode jsonNodes = output.addObject();
            jsonNodes.put("command", action.getCommand());
            jsonNodes.put("handIdx", action.getHandIdx());
            jsonNodes.put("error",
                    "Cannot place environment card on table.");
            return false;
        } else if (card.getMana() > manaHand) {
            ObjectNode jsonNodes = output.addObject();
            jsonNodes.put("command", action.getCommand());
            jsonNodes.put("handIdx", action.getHandIdx());
            jsonNodes.put("error",
                    "Not enough mana to place card on table.");
            return false;
        }
        return true;
    }

    public static int testCardEnvironment(final ArrayNode output, final ActionsInput
            action, final int playerTurn, final ArrayList<ArrayList<CardInput>> table,
            final ArrayList<CardInput> cardsHand1, final ArrayList<CardInput> cardsHand2,
            final int mana1, final int mana2) {
        switch (playerTurn) {
            case 1 -> {
                if (action.getHandIdx() > cardsHand1.size()) {
                    return 0;
                }
                CardInput cardenv = cardsHand1.get(action.getHandIdx());
                if (!Objects.equals(cardenv.getType(cardenv),"Environment")) {
                    OutPrint.printErrorEnvironment(output, action, 1);
                    return 0;
                }
                if (cardenv.getMana() > mana1) {
                    OutPrint.printErrorEnvironment(output, action, 2);
                    return 0;
                }
                if (action.getAffectedRow() == 2 || action.getAffectedRow() == 3) {
                    OutPrint.printErrorEnvironment(output, action, 3);
                    return 0;
                }
                if (Objects.equals(cardenv.getName(),"Heart Hound")) {
                    if (heartHound(output, action, table)) {
                        cardsHand1.remove(cardenv);
                        return cardenv.getMana();
                    }
                    return 0;
                }
                if (Objects.equals(cardenv.getName(),"Firestorm")) {
                    firestorm(action, table);
                    cardsHand1.remove(cardenv);
                    return cardenv.getMana();
                }
                if (Objects.equals(cardenv.getName(),"Winterfell")) {
                    winterfell(action, table);
                    cardsHand1.remove(cardenv);
                    return cardenv.getMana();
                }
                return 0;
            }
            case 2 -> {
                if (action.getHandIdx() > cardsHand2.size()) {
                    return 0;
                }
                CardInput cardenv = cardsHand2.get(action.getHandIdx());
                if (!Objects.equals(cardenv.getType(cardenv),"Environment")) {
                    OutPrint.printErrorEnvironment(output, action, 1);
                    return 0;
                }
                if (cardenv.getMana() > mana2) {
                    OutPrint.printErrorEnvironment(output, action, 2);
                    return 0;
                }
                if (action.getAffectedRow() == 0 || action.getAffectedRow() == 1) {
                    OutPrint.printErrorEnvironment(output, action, 3);
                    return 0;
                }
                if (Objects.equals(cardenv.getName(),"Heart Hound")) {
                    if (heartHound(output, action, table)) {
                        cardsHand2.remove(cardenv);
                        return cardenv.getMana();
                    }
                    return 0;
                }
                if (Objects.equals(cardenv.getName(),"Firestorm")) {
                    firestorm(action, table);
                    cardsHand2.remove(cardenv);
                    return cardenv.getMana();
                }
                if (Objects.equals(cardenv.getName(),"Winterfell")) {
                    winterfell(action, table);
                    cardsHand2.remove(cardenv);
                    return cardenv.getMana();
                }
                return 0;
            }
            default -> {
                return 0;
            }
        }
    }

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
    public static void winterfell(final ActionsInput action,
                                  final ArrayList<ArrayList<CardInput>> table) {
        for (int i = 0; i < table.get(action.getAffectedRow()).size(); i++) {
            table.get(action.getAffectedRow()).get(i).setFrozen(true);
        }
    }
    public static boolean heartHound(final ArrayNode output, final ActionsInput
            action, final ArrayList<ArrayList<CardInput>> table) {
        int currRow = 3 - action.getAffectedRow(); final int maxRow = 5;
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
            OutPrint.printErrorEnvironment(output, action, 4);
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
     * Function that deep copies every field of a card from a deck into a new card
     * @param deck
     * @param i
     * @return
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
