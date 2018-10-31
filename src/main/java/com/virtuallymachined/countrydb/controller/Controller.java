package com.virtuallymachined.countrydb.controller;

import com.virtuallymachined.countrydb.model.Country;
import com.virtuallymachined.countrydb.service.DownloadFlagTask;
import com.virtuallymachined.countrydb.service.GetCountriesService;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller extends BaseController {

    @FXML
    private URL location;

    @FXML
    private ResourceBundle resources;

    @FXML
    private ScrollPane sideMenuContainer;

    @FXML
    private TreeView<String> sideMenu;

    @FXML
    private ListView<Country> listView;

    @FXML
    private Button refreshButton;

    @FXML
    private ProgressIndicator progressIndicator;

    private TreeItem<String> countriesItem;
    private TreeItem<String> regionsItem;

    private GetCountriesService getCountriesService;
    private ObservableList<Country> countries = FXCollections.observableArrayList();
    private FilteredList<Country> filteredCountries = new FilteredList<>(countries, s -> true);

    public Controller() {
        getCountriesService = new GetCountriesService();
    }

    public Controller(GetCountriesService getCountriesService) {
        this.getCountriesService = getCountriesService;
    }

    @FXML
    private void initialize() {

        initRefreshButton();

        initSideMenu();

        initListView();

        initCountries();
    }

    private void initCountries() {

        getCountriesService.setOnSucceeded(event -> {

            countries.setAll((List<Country>) event.getSource().getValue());

            // set the regions in the side menu
            List<TreeItem<String>> regions = countries.stream()
                    .map(Country::getRegion)
                    .distinct()
                    .filter(s -> !s.isEmpty())
                    .map(TreeItem::new)
                    .collect(Collectors.toList());
            regionsItem.getChildren().setAll(regions);
        });

        progressIndicator.visibleProperty().bind(getCountriesService.runningProperty());
        progressIndicator.managedProperty().bind(getCountriesService.runningProperty());

        if (countries.isEmpty()) {
            if (getCountriesService.isRunning()) {
                getCountriesService.cancel();
            }

            getCountriesService.restart();
        }
    }

    private void initRefreshButton() {
        refreshButton.disableProperty().bind(getCountriesService.runningProperty());
        refreshButton.setOnAction(event -> getCountriesService.restart());
    }

    private void initSideMenu() {

        SplitPane.setResizableWithParent(sideMenuContainer, false);

        sideMenu.setShowRoot(false);
        sideMenu.disableProperty().bind(Bindings.isEmpty(countries));

        TreeItem<String> root = new TreeItem<>("Side Menu");
        sideMenu.setRoot(root);

        countriesItem = new TreeItem<>("Countries");
        regionsItem = new TreeItem<>("Regions");
        regionsItem.setExpanded(true);
        root.getChildren().addAll(countriesItem, regionsItem);

        sideMenu.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String currentValue = newValue.getValue();
                switch (currentValue) {
                    case "Countries":
                    case "Regions":
                    case "":
                        filteredCountries.setPredicate(country -> true);
                        break;
                    default:
                        filteredCountries.setPredicate(country -> country.getRegion().equals(currentValue));
                        break;
                }
            }
        });
    }

    private void initListView() {
        listView.setItems(filteredCountries);
        listView.setCellFactory(new Callback<ListView<Country>, ListCell<Country>>() {

            @Override
            public ListCell<Country> call(ListView<Country> param) {
                return new ListCell<Country>() {

                    @Override
                    protected void updateItem(Country item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(item.getName());

                            Task<File> flagTask = new DownloadFlagTask(item.getAlpha2Code());
                            new Thread(flagTask).start();

                            flagTask.setOnSucceeded(event -> {
                                if (isVisible()) {
                                    setGraphic(getFlagImage((File) event.getSource().getValue()));
                                }
                            });
                        }
                    }
                };
            }
        });
    }

    private ImageView getFlagImage(File file) {
        try {
            FileInputStream input = new FileInputStream(file);
            Image image = new Image(input);
            return new ImageView(image);
        } catch (FileNotFoundException ex) {
            System.out.println("Failed to load flag image file " + file.getAbsolutePath());
        }

        return null;
    }
}
