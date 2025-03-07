package com.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import entity.plugin.*;
import entity.*;

public class JsonConfigLoader extends BasePlugin implements PluginInterface {
    public JsonConfigLoader() {
        super();
    }

    @Override
    public String getName() {
        return "com.plugin.JsonConfigLoader";
    }

    @Override
    public boolean verifyDirectory(String directoryPath) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new FileNotFoundException("Directory not found: " + directoryPath);
        }

        File gameStateFile = new File(directoryPath, "gamestate.json");
        File player1File = new File(directoryPath, "player1.json");
        File player2File = new File(directoryPath, "player2.json");

        return gameStateFile.exists() && player1File.exists() && player2File.exists();
    }

    @Override
    public GameState loadGameState(String directoryPath) throws IOException {
        Store tempStore = new Store();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File(directoryPath + "/gamestate.json"));
        if (jsonNode == null) {
            throw new FileNotFoundException();
        }

        JsonNode array = jsonNode.get("shop_item");
        for (int i = 0; i < array.size(); i++) {
            JsonNode parts = array.get(i);
            String itemName = parts.get(0).textValue();
            String parsedItemName = this.keyToValueMap.get(itemName);
            Integer itemQuantity = parts.get(1).intValue();
            tempStore.addItem((ProductCard) CardFactory.createCard(parsedItemName, new Player()), itemQuantity);
        }

        List<Player> listPlayers = new ArrayList<>();
        Player player1 = this.loadPlayer(directoryPath + "/player1.json");
        Player player2 = this.loadPlayer(directoryPath + "/player2.json");
        listPlayers.add(player1);
        listPlayers.add(player2);

        return new GameState(jsonNode.get("current_turn").intValue(), tempStore, listPlayers);
    }

    @Override
    public void saveGameState(GameState gameState, String directory) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("current_turn", gameState.getCurrentTurn());

        // Create an ArrayNode for shop items
        ArrayNode shopItemsArray = objectMapper.createArrayNode();
        Map<ProductCard, Integer> itemList = gameState.getStore().getStoreInformation();

        for (Map.Entry<ProductCard, Integer> entry : itemList.entrySet()) {
            ArrayNode itemArray = objectMapper.createArrayNode();
            itemArray.add(this.keyToValueMap.get(entry.getKey().getName()));
            itemArray.add(entry.getValue());
            shopItemsArray.add(itemArray);
        }

        // Add the shop items array to the main JSON node
        jsonNode.set("shop_item", shopItemsArray);

        // Serialize and write the main JSON node to the file
        objectMapper.writeValue(new File(directory + "/gamestate.json"), jsonNode);

        // Save player states
        this.savePlayer(gameState.getPlayers().get(0), directory + "/player1.json");
        this.savePlayer(gameState.getPlayers().get(1), directory + "/player2.json");
    }

    @Override
    public Player loadPlayer(String filePath) throws IOException {
        Player player = new Player();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File(filePath));
        if (jsonNode == null) {
            throw new FileNotFoundException();
        }

        int gulden = jsonNode.get("gulden").intValue();
        player.setCash(gulden);
        int deckSize = jsonNode.get("active_deck_count").intValue();
        player.reshuffleDeck(deckSize);

        JsonNode array = jsonNode.get("active_hand");
        for (int i = 0; i < array.size(); i++) {
            JsonNode parts = array.get(i);
            String cardName = this.keyToValueMap.get(parts.get(1).textValue());
            Card card = CardFactory.createCard(cardName, player);
            player.getHands().addCardByLocation(card, parts.get(0).textValue());
        }

        JsonNode farmCard = jsonNode.get("in_field_card");
        for (int i = 0; i < farmCard.size(); i++) {
            JsonNode parts = farmCard.get(i);
            String location = parts.get(0).textValue();
            String cardName = this.keyToValueMap.get(parts.get(1).textValue());
            int weightOrAge = parts.get(2).intValue();
            Card card = CardFactory.createCard(cardName, player);

            if (card instanceof AnimalCard) {
                AnimalCard animalCard = (AnimalCard) card;
                animalCard.setWeight(weightOrAge); // Assuming setCurrentWeight method exists
            } else if (card instanceof PlantCard) {
                PlantCard plantCard = (PlantCard) card;
                plantCard.setAge(weightOrAge); // Assuming setAge method exists
            }

            JsonNode skills = objectMapper.readTree(parts.get(3).traverse());
            for (int j = 0; j < skills.get("skills").size(); j++) {
                String itemName = skills.get("skills").get(j).textValue();
                card.addEffect(this.keyToValueMap.get(itemName));
            }
            player.addCardToField(location, card);
        }

        return player;
    }

    @Override
    public void savePlayer(Player player, String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("gulden", player.getCash());
        jsonNode.put("active_deck_count", player.getDeck().getCurrentDeckCardCount());

        ArrayNode activeHandsArray = objectMapper.createArrayNode();
        for (Card card : player.getHands().getCards()) {
            if (card != null) {
                ArrayNode activeArray = objectMapper.createArrayNode();
                activeArray.add(player.getHands().findCardLocation(card));
                activeArray.add(this.keyToValueMap.get(card.getName()));
                activeHandsArray.add(activeArray);
            }
        }
        jsonNode.put("active_hand", activeHandsArray);

        Grid field = player.getField();
        ArrayNode globalFieldCard = objectMapper.createArrayNode();

        for (int i = 0; i < field.getWidth(); i++) {
            for (int j = 0; j < field.getHeight(); j++) {
                ArrayNode inFieldCard = objectMapper.createArrayNode();
                Card currentCard = field.getCard(i, j);
                if (currentCard != null) {
                    inFieldCard.add(Grid.convertIndicesToLocation(j, i));
                    inFieldCard.add(this.keyToValueMap.get(currentCard.getName()));
                    if (currentCard instanceof AnimalCard) {
                        inFieldCard.add(((AnimalCard) currentCard).getCurrentWeight());
                    } else if (currentCard instanceof PlantCard) {
                        inFieldCard.add(((PlantCard) currentCard).getCurrentAge());
                    } else {
                        inFieldCard.add(0);
                    }

                    ObjectNode skillObject = objectMapper.createObjectNode();
                    ArrayNode skills = objectMapper.createArrayNode();
                    for (int k = 0; k < currentCard.getActiveEffect().size(); k++) {
                        skills.add(this.keyToValueMap.get(currentCard.getActiveEffect().get(k)));
                    }
                    skillObject.put("skills", skills);
                    inFieldCard.add(skillObject);
                    globalFieldCard.add(inFieldCard);
                }
            }
        }
        jsonNode.put("in_field_card", globalFieldCard);
        objectMapper.writeValue(new File(filePath), jsonNode);
    }
}
