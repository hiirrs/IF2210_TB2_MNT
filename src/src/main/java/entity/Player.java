package entity;

import java.util.*;

public class Player {
    private int cash;
    private String name;
    private Deck deck;
    private ArrayList<?> field;
    private Hands hands;
    private int cardInHandCount;

    public Player() {
        this.cash = 0;
        this.name = "default";
        this.deck = new Deck(this);
        // Inisialisasi field
        this.hands = new Hands();
        this.cardInHandCount = 0;
    }


    public int getCash() {
        return this.cash;
    }

    public Deck getDeck() {
        return this.deck;
    }

    public Hands Hand() {
        return this.hands;
    }

    public int getCardInHandCount() {
        return this.cardInHandCount;
    }

    public String getName() {
        return this.name;
    }

    public void AddHand(Card card){
        this.hands.addCard(card);
    }
    public void ShowHand(){
        this.hands.printHand();;
    }
    public void useCard(int position){
        this.hands.deleteCard(position);
    }
    public void DeckInfo(){
        this.deck.printCardNames();
    }
    public List<Card> draw4(){
        return this.deck.deal();
    }
    public void save(List<Card> draws){
        this.hands.addSet(draws);
    }
}


