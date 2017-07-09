package stes.isami.bjm.components.hub;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import stes.isami.bjm.components.hub.table.JobData;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Hub view class
 */
public class HubView implements Initializable,IHubView<JobData> {

    @FXML private TableView hubTable;

    @FXML private Button runJobButton;

    @FXML private Button runAllButton;

    @FXML private TextField filterField;

    @FXML private VBox mainPane;

    private SimpleIntegerProperty buttonActionType;

    private HubController controller;
    private ObservableList<JobData> modelData = FXCollections.observableArrayList();

    public HubView() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        decorateButton(runJobButton,"images/start-icon.png");
        decorateButton(runAllButton,"images/start-icon.png");

        controller = new HubController(this);
        setupTable(hubTable);
        setDataToTable(hubTable);
    }

    @Override
    public ObservableList<JobData> getData() {
        return modelData;
    }

    @Override
    public void setActionEventHandler(String buttonID,EventHandler<ActionEvent> actionEventEventHandler) {
        Button button = (Button)getControl(mainPane,buttonID);
        if (button != null) {
            button.setOnAction(actionEventEventHandler);

            if (actionEventEventHandler instanceof HubActionEventHandler) {
                HubActionEventHandler hubActionEventHandler = (HubActionEventHandler) actionEventEventHandler;
                hubActionEventHandler.getActionProperty().addListener((observable, oldValue, newValue) -> {
                    Platform.runLater(() -> {
                        Integer newValueInt = (Integer) newValue;
                        if (newValueInt == HubActionEventHandler.RUN_ACTION) {
                            decorateButton(button, "images/start-icon.png");
                            button.setText("Run job");
                        } else if (newValueInt == HubActionEventHandler.STOP_ACTION) {
                            decorateButton(button, "images/stop_red.png");
                            button.setText("Stop job");
                        }
                    });
                });
            }
        }
    }

    @Override
    public void setKeyEventHandler(String controlID, EventHandler<KeyEvent> keyEventEventHandler) {
        Control control = getControl(mainPane,controlID);
        if (control != null) {
            control.setOnKeyReleased(keyEventEventHandler);
        }
    }

    @Override
    public void setMouseEventHandler(String controlID, EventHandler<MouseEvent> mouseEventEventHandler) {
        Control control = getControl(mainPane,controlID);
        if (control != null) {
            control.setOnMouseClicked(mouseEventEventHandler);
        }
    }

    @Override
    public Control getControl(String controlID) {
         return getControl(mainPane,controlID);
    }


    /**
     *
     *
     *              PRIVATE
     *
     */

    private Control getControl(Pane mainPane,String controlID) {
        Control controlFound = null;

        for (Node node: mainPane.getChildren()) {
            if (controlFound != null) {
                return controlFound;
            }

            if (node instanceof Pane) {
                controlFound = getControl((Pane) node,controlID);
            }
            else {
                try {
                    if (node.getId().equals(controlID)) {
                        return  (Control) node;
                    }
                } catch (NullPointerException ex) {
                    //node has no id
                }
            }
        }

        return controlFound;

    }
    private void setupTable(TableView tableView) {

        tableView.getStylesheets().add(HubView.class.getClassLoader().getResource("css/hubTable.css").toExternalForm());
        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<JobData,String>("name"));

        TableColumn destinationCol = new TableColumn("Destination");
        destinationCol.setCellValueFactory(new PropertyValueFactory<JobData,String>("destinationFolder"));

        TableColumn localFolderCol = new TableColumn("Local folder");
        localFolderCol.setCellValueFactory(new PropertyValueFactory<JobData,String>("localFolder"));

        TableColumn typeCol = new TableColumn("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<JobData,String>("type"));
        typeCol.setMaxWidth(150);
        typeCol.setMinWidth(150);
        typeCol.setPrefWidth(150);
        typeCol.setResizable(false);

        TableColumn statusCol = new TableColumn("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<JobData,String>("status"));
        statusCol.setMaxWidth(100);
        statusCol.setMinWidth(100);
        statusCol.setPrefWidth(100);
        statusCol.setResizable(false);

        TableColumn batchIDCol = new TableColumn("Batch ID");
        batchIDCol.setCellValueFactory(new PropertyValueFactory<JobData,String>("batchID"));
        batchIDCol.setMaxWidth(100);
        batchIDCol.setMinWidth(100);
        batchIDCol.setPrefWidth(100);
        batchIDCol.setResizable(false);

        TableColumn idCol = new TableColumn("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<JobData,String>("id"));
        idCol.setVisible(false);

        tableView.getColumns().addAll(nameCol,localFolderCol,destinationCol,typeCol,statusCol,batchIDCol,idCol);
        tableView.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);

//        statusCol.setCellFactory(column -> {
//            return new TableCell<JobData,String>() {
//
//                @Override
//                protected void updateItem(String item, boolean empty) {
//                    super.updateItem(item, empty);
//
//                    setText(empty ? "" : getItem().toString());
//                    setGraphic(null);
//
//                    TableRow<HubTableModel.JobData> currentRow = getTableRow();
//                    if ( !isEmpty() ) {
//                        if (item.equalsIgnoreCase("run")) {
//                            currentRow.setId("row-run");
//                        }
//                        else if (item.equalsIgnoreCase("error")) {
//                            currentRow.setId("row-error");
//                        }
//                        else if (item.equalsIgnoreCase("waiting")) {
//                            currentRow.setId("row-waiting");
//                        }
//                        else if (item.equalsIgnoreCase("finished")) {
//                            currentRow.setId("row-finished");
//                        }
//                        else {
//                            currentRow.setId("");
//                        }
//                    }
//                    else {
//                        currentRow.setId("");
//                    }
//
//                }
//            };
//        });

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


    }

    private void setDataToTable(TableView tableView) {
        //Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList filteredData = new FilteredList<>(modelData,p-> true);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(jobData -> {

                JobData jobData1 = (JobData) jobData;
                // If filter text is empty, display all rows.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (jobData1.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (jobData1.getDestinationFolder().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (jobData1.getLocalFolder().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (jobData1.getType().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (jobData1.getStatus().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (jobData1.getId().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // Does not match.
            });

            tableView.getSelectionModel().clearSelection();
        });

        SortedList<JobData> sortedData = new SortedList<>(filteredData);

        //Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        // Add sorted (and filtered) data to the table.
        tableView.setItems(sortedData);
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
