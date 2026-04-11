package org.tin.oop2_capstone;

import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class MainController {
    @FXML public SplitPane splitPaneMain;
    @FXML public AnchorPane anchorPaneSideBar;
    @FXML public ImageView imgViewCollapse;

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

}


