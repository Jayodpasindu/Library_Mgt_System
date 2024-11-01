package com.example.library_managmenrt.controller;

import com.example.library_managmenrt.dao.MemberDao;
import com.example.library_managmenrt.dao.TransactionDao;
import com.example.library_managmenrt.model.Member;
import com.example.library_managmenrt.model.Transaction;
import com.example.library_managmenrt.utill.Navigation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class transactionController {
    @FXML
    private Button backButton;
    @FXML
    private TableColumn<Transaction, Void> returnedButtonColumn;
    @FXML
    private TableView<Transaction> transactionTable;
    @FXML
    private TableColumn<Transaction, Integer> idColumn;
    @FXML
    private TableColumn<Transaction, String> memberIdColumn;
    @FXML
    private TableColumn<Transaction, String> memberNameColumn;
    @FXML
    private TableColumn<Transaction, String> bookIdColumn;
    @FXML
    private TableColumn<Transaction, String> bookNameColumn;
    @FXML
    private TableColumn<Transaction, LocalDate> borrowedDateColumn;
    @FXML
    private TableColumn<Transaction, LocalDate> returnedDateColumn;
    @FXML
    private TableColumn<Transaction, Void> deleteColumn;
    @FXML
    private Button addButton;
    @FXML
    private DatePicker returnedDatePicker;
    @FXML
    private DatePicker borrowedDatePicker;
    @FXML
    private TextField bookNameField;
    @FXML
    private TextField memberNameField;
    @FXML
    private TextField bookIdField;
    @FXML
    private TextField memberIdField;

    private ObservableList<Transaction> transactionsList = FXCollections.observableArrayList();

    private TransactionDao transactionDao;

    private Transaction selectedTransaction;

    public transactionController(){
        transactionDao = new TransactionDao();
    }

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        memberIdColumn.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        memberNameColumn.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        borrowedDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowedDate"));
        returnedDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnedDate"));


        // Configure the add button action
        addButton.setOnAction(event -> addTransaction());

        // Set up the delete column with a button
        deleteColumn.setCellValueFactory(new PropertyValueFactory<>("dummy")); // This can remain as it was
        deleteColumn.setCellFactory(new Callback<TableColumn<Transaction, Void>, TableCell<Transaction, Void>>() {
            @Override
            public TableCell<Transaction, Void> call(TableColumn<Transaction, Void> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setOnAction(event -> {
                            Transaction transactionToDelete = getTableView().getItems().get(getIndex());
                            boolean isDeleted = transactionDao.deleteTransaction(transactionToDelete); // Assuming this method returns a boolean

                            if (isDeleted) {
                                transactionsList.remove(transactionToDelete); // Remove from the ObservableList
                                transactionTable.refresh(); // Refresh the table view
                                showAlert(Alert.AlertType.INFORMATION, "Transaction Deleted", "Deleted: ");
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete: " );
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


        // Set up the returned column with a button
        returnedButtonColumn.setCellValueFactory(new PropertyValueFactory<>("dummy")); // This can remain as it was
        returnedButtonColumn.setCellFactory(new Callback<TableColumn<Transaction, Void>, TableCell<Transaction, Void>>() {
            @Override
            public TableCell<Transaction, Void> call(TableColumn<Transaction, Void> param) {
                return new TableCell<>() {
                    private final Button returnButton = new Button("Return Book");

                    {
                        returnButton.setOnAction(event -> {
                            Transaction transactionToReturn = getTableView().getItems().get(getIndex());

                            if(transactionToReturn.getReturnedDate().isBefore(LocalDate.now())){
                                long daysBetween = ChronoUnit.DAYS.between(transactionToReturn.getReturnedDate(), LocalDate.now());
                                long cost = daysBetween * 5; // Rs.5 per day

                                showAlert(Alert.AlertType.INFORMATION, "Late Return", "Return cost is: Rs. " + cost);
                                boolean isDeleted = transactionDao.deleteTransaction(transactionToReturn); // Assuming this method returns a boolean

                                if (isDeleted) {
                                    transactionsList.remove(transactionToReturn); // Remove from the ObservableList
                                    transactionTable.refresh(); // Refresh the table view
                                }
                            }else{
                                boolean isDeleted = transactionDao.deleteTransaction(transactionToReturn); // Assuming this method returns a boolean

                                if (isDeleted) {
                                    transactionsList.remove(transactionToReturn); // Remove from the ObservableList
                                    transactionTable.refresh(); // Refresh the table view
                                    showAlert(Alert.AlertType.INFORMATION, "Book returned", "Returned book is : " + transactionToReturn.getBookName());
                                } else {
                                    showAlert(Alert.AlertType.ERROR, "Failed to return the book", "Name: " + transactionToReturn.getBookName());
                                }
                            }

                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(returnButton);
                        }
                    }
                };
            }
        });


        // Loads all books
        loadMembers();
    }

    private void addTransaction() {
        String bookId = bookIdField.getText();
        String bookName = bookNameField.getText();
        String memberId = memberIdField.getText();
        String memberName = memberNameField.getText();
        LocalDate borrowedDate = borrowedDatePicker.getValue();
        LocalDate returnedDate = returnedDatePicker.getValue();

        if (!bookId.isEmpty() && !bookName.isEmpty() && !memberId.isEmpty() && !memberName.isEmpty()) {
            try {
                if (selectedTransaction == null) { // Adding a new book
                    if (!transactionsList.stream().anyMatch(c -> c.getBookName() == bookName)) {
                        Transaction newTransaction = new Transaction(bookId, bookName, memberId, memberName, borrowedDate, returnedDate);
                        transactionDao.saveTransaction(newTransaction);
                        loadMembers();
                        clearFields();
                        showAlert(Alert.AlertType.INFORMATION, "Transaction Added", "\n Book: " + bookName);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Book is already exists.");
                    }
                } else { // Updating an existing member
                    selectedTransaction.setBookId(bookId);
                    selectedTransaction.setBookName(bookName);
                    selectedTransaction.setMemberId(memberId);
                    selectedTransaction.setMemberName(memberName);
                    selectedTransaction.setBorrowedDate(borrowedDate);
                    selectedTransaction.setReturnedDate(returnedDate);
                    transactionDao.updateTransactionDetails(selectedTransaction);
                    loadMembers();
                    clearFields();
                    selectedTransaction = null; // Reset selection
                    showAlert(Alert.AlertType.INFORMATION, "Category Updated",  "\nBook: " + bookName);
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid ID format. Please enter a numeric ID.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter relevant details");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadMembers() {
        transactionsList.clear();
        transactionsList.addAll(transactionDao.getAllTransactions());
        transactionTable.setItems(transactionsList);
    }

    private void clearFields() {
        memberNameField.clear();
        memberIdField.clear();
        bookIdField.clear();
        bookNameField.clear();
    }

    public void handleBackButton(ActionEvent actionEvent) {
        Navigation.navigateToScene("dashboard.fxml", backButton);
    }
}
