package org.example.src;

import entity.GameData;
import entity.*;
import entity.Grid;
import javafx.application.Platform;
import java.util.List;

public class UIUpdateService {
    private static UIUpdateService instance = new UIUpdateService();
    private ActiveHandsController handsController;
    private DrawsController drawsController;
    private GridController gridController;
    private StoreController storeController;
    private UIUpdateService() {}

    public static UIUpdateService getInstance() {
        return instance;
    }

    public void setHandsController(ActiveHandsController controller) {
        this.handsController = controller;
    }

    public void setStoreController(StoreController controller) {
        this.storeController = controller;
    }

//    public void getStoreController(StoreController controller) {
//        return this.storeController = controller;
//    }

    public void setDrawsController(DrawsController controller) {
        this.drawsController = controller;
    }

    public void setGridsController(GridController controller) {
        this.gridController = controller;
    }


    public void updateHandsGrid() {
        System.out.println("Wanted to Save: "+PlayerManager.getInstance().getCurrentPlayer().getHands());
        // System.out.println("Tes hertz:");
        // PlayerManager.getInstance().getCurrentPlayer().ShowHand();
        if (handsController != null) {
            Platform.runLater(() -> {
                handsController.updateGrid(PlayerManager.getInstance().getCurrentPlayer().getHands());
            });
        }
        else{
            System.out.println("Hands is null");
        }
    }

    public void UpdateStockProduct(){
        Store store = GameController.getInstance().getStore();
        storeController.updateUI(store);
    }


    public void updateStoreHandsGrid() {
        if (storeController != null) {
            Platform.runLater(() -> {
                storeController.updateStoreGrid(PlayerManager.getInstance().getCurrentPlayer().getHands());
            });
        } else {
            System.out.println("Store hands is null");
        }
    }



    public void updateRealGrid() {
        System.out.println("updating grid: ");
        PlayerManager.getInstance().getCurrentPlayer().getField().printInformation();
        System.out.println("row ui" + PlayerManager.getInstance().getCurrentPlayer().getField().getHeight());
        System.out.println("col ui" + PlayerManager.getInstance().getCurrentPlayer().getField().getWidth());
        if (gridController != null) {
            Platform.runLater(() -> {
                gridController.toggleGridDisplay(false);
            });
        }
        else{
            System.out.println("grid is null");
        }
    }
    public void updateEnemyGrid() {
        if (gridController != null) {
            Platform.runLater(() -> {
                gridController.toggleGridDisplay(true);
            });
        }
        else{
            System.out.println("grid is null");
        }
    }
    
    public void updateDrawsGrid() {
        int handCardCount = PlayerManager.getInstance().getCurrentPlayer().getHands().getCardCount();
        int initialSize = 6 - handCardCount;
        final int size = Math.min(initialSize, 4);
        if (drawsController != null) {
            Platform.runLater(() -> {
                drawsController.updateCardGrid(PlayerManager.getInstance().getCurrentPlayer().draw4(size));
            });
        }
    }
    

    public void updateGridColorAttack(List<List<Integer>> subgridLocationAttack) {
        Grid currentGrid = PlayerManager.getInstance().getCurrentPlayer().getField();
        gridController.updateGrids(currentGrid, subgridLocationAttack);
    }
}
