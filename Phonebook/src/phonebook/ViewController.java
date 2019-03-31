package phonebook;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;


public class ViewController implements Initializable {

    @FXML
    private Label label;

    @FXML
    private TableView table;
    @FXML
    private StackPane menuPane;
    @FXML
    private Pane exportPane;
    @FXML
    private Pane contactPane;
    @FXML
    private TextField inputFirstName;
    @FXML
    private TextField inputLastName;
    @FXML
    private TextField inputEmail;
    @FXML
    private Button addNewContactButton;
    @FXML
    private TextField inputExport;
    @FXML
    private Button buttonExport;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private SplitPane mainSplit;
    DB db = new DB();


    private final ObservableList data = FXCollections.observableArrayList();


    private final String MENU_CONTACTS = "Kontaktok";
    private final String MENU_EXIT = "Kilépés";
    private final String MENU_LIST = "Lista";
    private final String MENU_EXPORT = "Exportálás";

    public void addContact(ActionEvent event) {
        String email = inputEmail.getText();
        if (email.length() > 3 && email.contains("@") && email.contains(".")) {
            Person newPerson = new Person(inputLastName.getText(), inputFirstName.getText(), email);
            data.add(newPerson);
            db.addContact(newPerson);
            inputFirstName.clear();
            inputLastName.clear();
            inputEmail.clear();
        } else {
            alert("Adj meg egy valódi e-mail címet!");
        }
    }

    public void exportListToPdf(ActionEvent event) {
        String fileName = inputExport.getText();
        fileName = fileName.replaceAll("\\s+", "");
        if (fileName != null && !fileName.equals("")) {
            PdfGeneration pdfGen = new PdfGeneration();
            pdfGen.pdfGeneration(fileName, data);
            inputExport.clear();
        } else {
            alert("Adj meg egy fájlnevet!");
        }

    }

    public void setTableData() {
        TableColumn firstNameCol = new TableColumn("Keresztnév");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));

        firstNameCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Person, String> t) {
                        Person actualPerson = ((Person) t.getTableView().getItems().get(
                                t.getTablePosition().getRow()));
                        actualPerson.setFirstName(t.getNewValue());
                        db.updateContact(actualPerson);
                    }
                }
        );

        TableColumn lastNameCol = new TableColumn("Vezetéknév");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("lastName"));

        lastNameCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Person, String> t) {
                        Person actualPerson = ((Person) t.getTableView().getItems().get(
                                t.getTablePosition().getRow()));
                        actualPerson.setLastName(t.getNewValue());
                        db.updateContact(actualPerson);
                    }
                }
        );


        TableColumn emailCol = new TableColumn("Email cím");
        emailCol.setMinWidth(100);
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        emailCol.setCellValueFactory(new PropertyValueFactory<Person, String>("email"));

        emailCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Person, String> t) {
                        Person actualPerson = ((Person) t.getTableView().getItems().get(
                                t.getTablePosition().getRow()));
                        actualPerson.setEmail(t.getNewValue());
                        db.updateContact(actualPerson);
                    }
                }
        );
        TableColumn removeCol = new TableColumn( "Törlés" );
        emailCol.setMinWidth(100);

        Callback<TableColumn<Person, String>, TableCell<Person, String>> cellFactory =
                new Callback<TableColumn<Person, String>, TableCell<Person, String>>()
                {
                    @Override
                    public TableCell call( final TableColumn<Person, String> param )
                    {
                        final TableCell<Person, String> cell = new TableCell<Person, String>()
                        {
                            final Button btn = new Button( "Törlés" );

                            @Override
                            public void updateItem( String item, boolean empty )
                            {
                                super.updateItem( item, empty );
                                if ( empty )
                                {
                                    setGraphic( null );
                                    setText( null );
                                }
                                else
                                {
                                    btn.setOnAction( ( ActionEvent event ) ->
                                    {
                                        Person person = getTableView().getItems().get( getIndex() );
                                        data.remove(person);
                                        db.removeContact(person);
                                    } );
                                    setGraphic( btn );
                                    setText( null );
                                }
                            }
                        };
                        return cell;
                    }
                };

        table.getColumns().addAll(lastNameCol, firstNameCol, emailCol, removeCol);

        data.addAll(db.getAllContacts());

        table.setItems(data);

    }

    ;

    private void setMenuData() {
        TreeItem treeItemRoot1 = new TreeItem<>("Menü");
        TreeView treeView = new TreeView<>(treeItemRoot1);
        treeView.setShowRoot(false);

        TreeItem nodeItemA = new TreeItem<>(MENU_CONTACTS);
        TreeItem nodeItemB = new TreeItem<>(MENU_EXIT);

        Node contactsNode = new ImageView(new Image(getClass().getResourceAsStream("/contacts.png")));
        Node exportNode = new ImageView(new Image(getClass().getResourceAsStream("/export.png")));

        TreeItem nodeItemA1 = new TreeItem<>(MENU_LIST, contactsNode);
        TreeItem nodeItemA2 = new TreeItem<>(MENU_EXPORT, exportNode);

        nodeItemA.getChildren().addAll(nodeItemA1, nodeItemA2);

        treeItemRoot1.getChildren().addAll(nodeItemA, nodeItemB);
        menuPane.getChildren().add(treeView);

        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                String selectedMenu;
                selectedMenu = selectedItem.getValue();

                if (null != selectedMenu) {
                    switch (selectedMenu) {
                        case MENU_CONTACTS:
                            selectedItem.setExpanded(true);
                            break;
                        case MENU_LIST:
                            contactPane.setVisible(true);
                            exportPane.setVisible(false);
                            break;
                        case MENU_EXPORT:
                            contactPane.setVisible(false);
                            exportPane.setVisible(true);
                            break;
                        case MENU_EXIT:
                            System.exit(0);
                            break;

                    }
                }
            }
        });

    }

    private void alert(String text) {
        mainSplit.setVisible(true);
        mainSplit.setOpacity(0.3);

        Label label = new Label(text);
        Button alertButton = new Button("OK");
        VBox vBox = new VBox(label, alertButton);
        vBox.setAlignment(Pos.CENTER);

        alertButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mainSplit.setDisable(false);
                mainSplit.setOpacity(1);
                vBox.setVisible(false);

            }
        });

        anchorPane.getChildren().add(vBox);
        anchorPane.setTopAnchor(vBox, 300.0);
        anchorPane.setLeftAnchor(vBox, 300.0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTableData();
        setMenuData();
    }


}
