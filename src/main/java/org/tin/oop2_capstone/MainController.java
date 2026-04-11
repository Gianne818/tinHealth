package org.tin.oop2_capstone;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import javafx.scene.input.MouseEvent;
import java.io.IOException;

public class MainController {
    @FXML public SplitPane splitPaneMain;
    @FXML public AnchorPane anchorPaneSideBar;
    @FXML public AnchorPane anchorPaneContent;
    @FXML public ImageView imgViewCollapse;

    @FXML public HBox dashboardNav;
    @FXML public HBox foodLogNav;
    @FXML public HBox activityLogNav;
    @FXML public HBox settingsNav;

    private boolean isSideBarCollapsed = false;

    public void initialize(){

    }

    public void toggleSideBar(){
        double defaultPosition = 0.3;
        if(isSideBarCollapsed){
            splitPaneMain.setDividerPosition(0, defaultPosition);
            anchorPaneSideBar.setMinWidth(300);
        } else {
            anchorPaneSideBar.setMinWidth(0);
            anchorPaneSideBar.setMaxWidth(0);
            splitPaneMain.setDividerPosition(0, 0);
        }
        isSideBarCollapsed = !isSideBarCollapsed;
    }

    @FXML public void onNavElementClicked(MouseEvent event){
        HBox clickedBox = (HBox) event.getSource();
        char id = clickedBox.getId().charAt(0);
        switch(id){
            case 'd':
                navigateToView("dashboard-view");
                break;

            case'f':
                navigateToView("food-log-view");
                break;

            case 'a':
                navigateToView("activity-log-view");
                break;

            case 's':
                navigateToView("settings-view");
                break;

            case 'p':
                navigateToView("profile-view");
                break;
        }
    }

    private void navigateToView(String filename){
        ScrollPane view = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/tin/oop2_capstone/views/" + filename + ".fxml"));
            view = fxmlLoader.load();

            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            anchorPaneContent.getChildren().setAll(view);
        } catch (IOException e) {
            System.out.println("File not found!");
        }
    }

}


