package org.example.src;

import entity.Card;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.util.List;

public class GameController {

    @FXML
    private StackPane arrowImageView; 
    @FXML
    private Label counterLabel;
    private int counter = 1; 

    @FXML
    public void initialize() {
        PlayerManager manager = PlayerManager.getInstance();
        manager.switchPlayer();
        counterLabel.setText(String.valueOf(counter));
        arrowImageView.setOnMouseClicked(event -> nextTurn());
        GameApp.openNewWindow("None", "Draws.fxml");


    }

    private void nextTurn() {
        PlayerManager manager = PlayerManager.getInstance();
        PlayerManager.getInstance().getCurrentPlayer().nextDay();
        manager.switchPlayer(); // Switch to the next player
        counter++;
        counterLabel.setText(String.valueOf(counter));
        UIUpdateService.getInstance().updateRealGrid();
        UIUpdateService.getInstance().updateHandsGrid();
        // List<Card> draws = manager.getCurrentPlayer().draw4();
        // manager.getCurrentPlayer().save(draws);
        GameApp.openNewWindow("None", "Draws.fxml");
    }
    @FXML
    public void ShowEnemy(MouseEvent event){
        UIUpdateService.getInstance().updateEnemyGrid();
    }
    @FXML
    public void ShowCurrent(MouseEvent event){
        UIUpdateService.getInstance().updateRealGrid();
    }
}
