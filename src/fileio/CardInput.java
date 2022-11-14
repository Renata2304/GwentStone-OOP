package fileio;

import java.util.ArrayList;

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

    public CardInput copyPlayer(final DecksInput deck, final int idx, final int i) {
        CardInput card = new CardInput();
        card.setMana(deck.getDecks().get(idx).get(i).getMana());
        card.setAttackDamage(deck.getDecks().get(idx).get(i).getAttackDamage());
        card.setHealth(deck.getDecks().get(idx).get(i).getHealth());
        card.setDescription(deck.getDecks().get(idx).get(i).getDescription());
        card.setColors(deck.getDecks().get(idx).get(i).getColors());
        card.setName(deck.getDecks().get(idx).get(i).getName());

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
