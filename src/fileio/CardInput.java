package fileio;

import java.util.ArrayList;
import java.util.Objects;

public final class CardInput {
    private int mana;
    private int attackDamage;
    private int health;
    private String description;
    private ArrayList<String> colors;
    private String name;

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

    /**
     * Function that gets the type of certain card
     * @param card
     * @return
     */
    public String getType(final CardInput card) {
        if (Objects.equals(card.name, "Sentinel") || Objects.equals(card.name, "Berserker")
                || Objects.equals(card.name, "Goliath") || Objects.equals(card.name, "Warden")) {
            return "Minion";
        } else if (Objects.equals(card.name, "Miraj") || Objects.equals(card.name, "Disciple")
                || Objects.equals(card.name, "The Cursed One") || Objects.equals(card.name,
                "The Ripper")) {
            return "Special";
        } else if (Objects.equals(card.name, "Firestorm") || Objects.equals(card.name,
                "Winterfell") || Objects.equals(card.name, "Heart Hound")) {
            return "Environment";
        }
        return "No type";
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
