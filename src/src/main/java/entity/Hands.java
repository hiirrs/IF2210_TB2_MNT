package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hands {
    private ArrayList<Card> cards;
    private int active;
    private static final Map<String, Integer> locationIndexMap = new HashMap<>();

    static {
        locationIndexMap.put("A01", 0);
        locationIndexMap.put("A02", 1);
        locationIndexMap.put("A03", 2);
        locationIndexMap.put("A04", 3);
        locationIndexMap.put("A05", 4);
        locationIndexMap.put("A06", 5);
    }

    public Hands() {
        cards = new ArrayList<>();
        active = 0;
    }

    public ArrayList<Card> getCards() {
        return this.cards;
    }

    public int length(){
        return this.active;
    }

    public void addCard(Card card) {
        if (active < 6) {
            cards.add(card);
            active++;
        } else {
            System.out.println("Hand is full, cannot add more cards.");
        }
    }

    public void addCardByLocation(Card card, String location) {
        Integer index = locationIndexMap.get(location);
        if (index == null) {
            throw new IndexOutOfBoundsException("Invalid location: " + location);
        }
        this.cards.set(index, card);
    }
    
    public void deleteCard(int index) {
        index ++;
        if (index >= 0 && index < cards.size()) {
            cards.set(index, null);
            active--;
        } else {
            System.out.println("Index out of bounds.");
        }
    }

    public void moveCard(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex >= cards.size() || toIndex < 0 || toIndex >= cards.size() || fromIndex == toIndex) {
            return; 
        }
    
        Card cardToMove = cards.get(fromIndex);
        if (cardToMove != null) {
            cards.remove(fromIndex); 
            cards.add(toIndex, cardToMove);  
        }
    }
    
    public Card getCard(int index) {
        if (index >= 0 && index < cards.size()) {
            return cards.get(index);
        } else {
            System.out.println("Index out of bounds.");
            return null;
        }
    }
    
    public String findCardLocation(Card card) {
        for (int i = 0; i < this.getCardCount(); i++) {
            if (this.cards.get(i) == card) {
                return String.format("A%02d", i + 1);
            }
        }
        throw new IllegalArgumentException("Card not found in the player's deck");
    }

    public int getCardCount() {
        int count = 0;
        for (Card card : cards) {
            if (card != null) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Card card : cards) {
            if (card != null) {
                sb.append(card.toString()).append("\n");
            } else {
                sb.append("Empty Slot\n");
            }
        }
        return sb.toString();
    }
    
    public void printHand() {
        for (Card card : cards) {
            if (card != null) {
                System.out.println(card.getName());
            }
        }
    }
    public void addSet(List<Card> cardSet) {
        for (Card card : cardSet) {
            if (active < 6) {
                cards.add(card);
                active++;
            } else {
                System.out.println("Hand is full, cannot add more cards.");
                break;
            }
        }
    }
}
