package com.example.library_managmenrt.controller;

import com.example.library_managmenrt.dao.BookDao;
import com.example.library_managmenrt.dao.MemberDao;
import com.example.library_managmenrt.model.Book;
import com.example.library_managmenrt.model.Member;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class MemberController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField contactField;
    @FXML
    private TextField addressField;
    @FXML
    private Button addButton;
    @FXML
    private TableView<Member> memberTable;
    @FXML
    private TableColumn<Member, Integer> idColumn;
    @FXML
    private TableColumn<Member, String> nameColumn;
    @FXML
    private TableColumn<Member, String> contactColumn;
    @FXML
    private TableColumn<Member, String> addressColumn;
    @FXML
    private TableColumn<Member, Void> editColumn;
    @FXML
    private TableColumn<Member, Void> deleteColumn;

    private ObservableList<Member> memberList = FXCollections.observableArrayList();

    private MemberDao memberDao;

    private Member selectedMember;

    public MemberController(){
        memberDao = new MemberDao();
    }

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));


        // Configure the add button action
        addButton.setOnAction(event -> addMember());

        // Set up the delete column with a button
        deleteColumn.setCellValueFactory(new PropertyValueFactory<>("dummy")); // This can remain as it was
        deleteColumn.setCellFactory(new Callback<TableColumn<Member, Void>, TableCell<Member, Void>>() {
            @Override
            public TableCell<Member, Void> call(TableColumn<Member, Void> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setOnAction(event -> {
                            Member memberToDelete = getTableView().getItems().get(getIndex());
                            boolean isDeleted = memberDao.deleteMember(memberToDelete); // Assuming this method returns a boolean

                            if (isDeleted) {
                                memberList.remove(memberToDelete); // Remove from the ObservableList
                                memberTable.refresh(); // Refresh the table view
                                showAlert(Alert.AlertType.INFORMATION, "Member Deleted", "Deleted: " + memberToDelete.getName());
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete: " + memberToDelete.getName());
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                };
            }
        });

        // Set up the edit column with a button
        editColumn.setCellValueFactory(new PropertyValueFactory<>("dummy")); // Dummy value for the edit column
        editColumn.setCellFactory(new Callback<TableColumn<Member, Void>, TableCell<Member, Void>>() {
            @Override
            public TableCell<Member, Void> call(TableColumn<Member, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = new Button("Edit");

                    {
                        editButton.setOnAction(event -> {
                            selectedMember = getTableView().getItems().get(getIndex()); // Get the selected Book
                            nameField.setText(selectedMember.getName()); // Load title
                            contactField.setText(selectedMember.getContact());
                            addressField.setText(selectedMember.getAddress());
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(editButton);
                        }
                    }
                };
            }
        });

        // Loads all books
        loadMembers();
    }

    private void addMember() {
        String name = nameField.getText();
        String contact = contactField.getText();
        String address = addressField.getText();

        if (!name.isEmpty() && !contact.isEmpty() && !address.isEmpty()) {
            try {
                if (selectedMember == null) { // Adding a new book
                    if (!memberList.stream().anyMatch(c -> c.getName() == name)) {
                        Member newMember = new Member(name, contact, address);
                        memberDao.saveMember(newMember);
                        loadMembers();
                        clearFields();
                        showAlert(Alert.AlertType.INFORMATION, "Member Added", "\nTitle: " + name);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Book is already exists.");
                    }
                } else { // Updating an existing member
                    selectedMember.setName(name);
                    selectedMember.setContact(contact);
                    selectedMember.setAddress(address);
                    memberDao.updateMemberDetails(selectedMember);
                    loadMembers();
                    clearFields();
                    selectedMember = null; // Reset selection
                    showAlert(Alert.AlertType.INFORMATION, "Category Updated",  "\nTitle: " + name);
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid ID format. Please enter a numeric ID.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter relevant details");
        }
    }

    private void loadMembers() {
        memberList.clear();
        memberList.addAll(memberDao.getAllMembers());
        memberTable.setItems(memberList);
    }

    private void clearFields() {
        nameField.clear();
        contactField.clear();
        addressField.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
