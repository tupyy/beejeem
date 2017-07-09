package stes.isami.bjm.components.materialExplorer.presenter;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.textfield.TextFields;
import stes.isami.bjm.components.hub.presenter.HubController;
import stes.isami.bjm.components.materialExplorer.business.Material;
import stes.isami.bjm.components.materialExplorer.business.MaterialExplorerHandler;
import stes.isami.bjm.components.materialExplorer.presenter.actions.ExportToXmlAction;
import stes.isami.bjm.components.materialExplorer.presenter.actions.ImportAction;
import stes.isami.bjm.components.materialExplorer.presenter.actions.LoadAction;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by cosmin2 on 02/07/2017.
 */
public class MaterialExplorerController implements Initializable {

    @FXML private TableView materialTable;
    @FXML private HBox hbox;
    @FXML private ToolBar toolBar;
    @FXML private Button loadMaterialsButtton;
    @FXML private Button importButton;
    @FXML private Button export2XMLButton;
    @FXML private Button closeButton;
    @FXML private StackPane mainPane;
    @FXML private HBox statusHBox;
    @FXML private ComboBox isamiVersionCombo;

    private TextField filterTextBox = TextFields.createClearableTextField();

    ObservableList<Material> data = FXCollections.observableArrayList();
    SortedList<Material> sortedList;
    private SimpleBooleanProperty canSelectProperty = new SimpleBooleanProperty(true);

    private EventHandler loadMaterialsActions;
    private EventHandler importAction;
    private EventHandler export2XMLAction;

    private MaterialExplorerHandler handler;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTable();
        HBox.setHgrow(hbox, Priority.ALWAYS);
        HBox.setHgrow(statusHBox,Priority.ALWAYS);

        HBox filterHBox = new HBox();
        filterHBox.getChildren().add(filterTextBox);
        filterHBox.setAlignment(Pos.CENTER_RIGHT);
        filterTextBox.setPrefWidth(200);
        filterTextBox.setPromptText("Filter");
        toolBar.getItems().add(filterHBox);

        isamiVersionCombo.setItems(FXCollections.observableArrayList(
                "v7.2.1_a350",
                "v8.1.0_a350-1000",
                "v8.2.0_a350-1000",
                "v8.2.1_a350-1000",
                "v9.2.0",
                "v9.4.0"));
        isamiVersionCombo.getSelectionModel().select(5);
    }

    /**
     * Set handler
     * @param handler
     */
    public void setHandler(MaterialExplorerHandler handler) {
        this.handler = handler;
        createActions(handler);

        decorateButton(loadMaterialsButtton,"images/loadIcon.png");
        decorateButton(importButton,"images/importIcon.png");
        decorateButton(export2XMLButton,"images/export-icon.png");
    }

    /**
     * Return data
     * @return
     */
    public ObservableList<Material> getData() {
        return data;
    }

    /**
     * Disable toolbar
     * @param disable
     */
    public void setDisableToolbar(boolean disable) {
       toolBar.setDisable(disable);
    }
    public HBox getStatusPane() {
        return statusHBox;
    }

    public void clearData() {
        data.clear();
    }

    /**
     * Return the isami version choose by the user
     * @return
     */
    public String getIsamiVersion() {
        return (String) isamiVersionCombo.getSelectionModel().getSelectedItem();
    }
    /********************************************************************
     *
     *                              PRIVATE
     *
     ********************************************************************/


    /**
     * Create the actions for the buttons
     * @param handler
     */
    private void createActions(MaterialExplorerHandler handler) {
        loadMaterialsActions = new LoadAction(handler,this);
        loadMaterialsButtton.setOnAction(loadMaterialsActions);

        importAction = new ImportAction(handler,this);
        importButton.setOnAction(importAction);

        export2XMLAction = new ExportToXmlAction(handler,this);
        export2XMLButton.setOnAction(export2XMLAction);

        closeButton.setOnAction(event -> {
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();

            if (handler.countJobs() > 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete the job created?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {
                    handler.close(true);
                } else {
                    handler.close(false);
                }
            }
            else {
                handler.close(false);
            }
        });
    }

    /**
     * Initialize the table
     */
    private void initializeTable() {

        TableColumn libraryNameColumn = new TableColumn("Library");
        libraryNameColumn.setEditable(false);
        TableColumn materialNameColumn = new TableColumn("Material");
        materialNameColumn.setEditable(false);
        TableColumn referenceNameColumn = new TableColumn("Reference");
        referenceNameColumn.setEditable(false);

        sortedList = setFilterFeature(data);
        materialTable.setItems(sortedList);
        setContextMenu(materialTable);

        //select/deselect material on double-click
        materialTable.setRowFactory(tv -> {
            TableRow<Material> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Material rowData = row.getItem();
                    rowData.setSelected(!rowData.isSelected());
                }
            });
            return row ;

        });

        //set CSS to table
        materialTable.setId("materialTable");
        materialTable.getStylesheets().add(getClass().getClassLoader().getResource("tablecell.css").toExternalForm());

        libraryNameColumn.setCellValueFactory(new PropertyValueFactory<Material,String>("library"));
        materialNameColumn.setCellValueFactory(new PropertyValueFactory<Material,String>("materialName"));
        referenceNameColumn.setCellValueFactory(new PropertyValueFactory<Material,String>("referenceName"));

        TableColumn select = new TableColumn("");
        select.setMinWidth(30);
        select.setMaxWidth(30);
        select.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Material,CheckBox>,ObservableValue<CheckBox>>() {

            @Override
            public ObservableValue<CheckBox> call(TableColumn.CellDataFeatures<Material, CheckBox> param) {
                Material material = param.getValue();
                CheckBox checkBox = new CheckBox();
                checkBox.selectedProperty().bindBidirectional(material.getSelectedProperty());
                checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        material.setSelected(newValue);
                    }
                });

                return new SimpleObjectProperty<CheckBox>(checkBox);
            }
        });

        materialTable.getColumns().addAll(select, libraryNameColumn, materialNameColumn,referenceNameColumn);

    }

    /**
     * Implement the filter feature
     * @param initialData
     * @return the sorted data
     */
    private SortedList<Material> setFilterFeature(ObservableList<Material> initialData) {
        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Material> filteredData = new FilteredList<>(initialData, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        filterTextBox.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(material -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (material.getMaterialName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (material.getReferenceName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (material.getLibrary().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<Material> sortedData = new SortedList<>(filteredData);
        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(materialTable.comparatorProperty());

        return sortedData;

    }

    /**
     * Create and set the context menu (Select all/Deselect all)
     * @param tableView
     */
    private void setContextMenu(TableView tableView) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem selectAll = new MenuItem("Select all");
        selectAll.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for(Material material: sortedList) {
                    material.setSelected(true);
                }
            }
        });

        MenuItem deselectAll = new MenuItem("Deselect all");
        deselectAll.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (Material material: sortedList) {
                    material.setSelected(false);
                }
            }
        });
        contextMenu.getItems().addAll(selectAll,deselectAll);
        tableView.setContextMenu(contextMenu);

    }

    /**
     * Add icons to buttons
     */
    private void decorateButton(Button button,String imagePath) {
        URL s = HubController.class.getClassLoader().getResource(imagePath);
        ImageView imageView = new ImageView(new Image(s.toString()));
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        button.setGraphic(imageView);
    }



}
