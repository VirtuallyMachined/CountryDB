package com.virtuallymachined.countrydb;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.util.WaitForAsyncUtils;

public class MainJavaFXTest extends BaseJavaFXTest {

    @Test
    public void sideMenuExists() {
        FxAssert.verifyThat("#sideMenuContainer", NodeMatchers.hasChild("#sideMenu"));
    }

    @Test
    public void listViewExists() {
        FxAssert.verifyThat("#viewContainer", NodeMatchers.hasChild("#listView"));
    }

    @Test
    public void countryInfoExists() {
        WaitForAsyncUtils.waitForFxEvents();

        TreeView<String> treeView = lookup("#sideMenu").query();
        ObservableList<TreeItem<String>> sideMenuItems = treeView.getRoot().getChildren();

        Assert.assertEquals(2, sideMenuItems.size());

        TreeItem<String> regions = sideMenuItems.get(1);
        Assert.assertEquals(6, regions.getChildren().size());
    }

    @Test
    public void verifyLabel() {

        FxAssert.verifyThat("#refreshButton", LabeledMatchers.hasText("Refresh"));
    }


    @Test
    public void clickOnRefreshButton() {

        clickOn("#refreshButton");

        // flushes out all fx events from the queue
        WaitForAsyncUtils.waitForFxEvents();

        TreeView<String> treeView = lookup("#sideMenu").query();
        ObservableList<TreeItem<String>> sideMenuItems = treeView.getRoot().getChildren();

        Assert.assertEquals(2, sideMenuItems.size());

        TreeItem<String> regions = sideMenuItems.get(1);
        Assert.assertEquals(6, regions.getChildren().size());
    }
}
