package fileio;

import java.util.ArrayList;

public final class Player {
    private int mana;
    ArrayList<CardInput> deck;
    ArrayList<CardInput> hand;
    CardInput cardHero;

    public int getMana() {
        return mana;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }

    public ArrayList<CardInput> getDeck() {
        return deck;
    }

    public void setDeck(final ArrayList<CardInput> deck) {
        this.deck = deck;
    }

    public ArrayList<CardInput> getHand() {
        return hand;
    }

    public void setHand(final ArrayList<CardInput> hand) {
        this.hand = hand;
    }

    public CardInput getCardHero() {
        return cardHero;
    }

    public void setCardHero(final CardInput cardHero) {
        this.cardHero = cardHero;
    }
}
