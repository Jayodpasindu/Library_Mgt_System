package com.example.library_managmenrt.controller;

import com.example.library_managmenrt.dao.BookDao;
import com.example.library_managmenrt.model.Book;
import com.example.library_managmenrt.model.Category;
import com.example.library_managmenrt.utill.Navigation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;


public class BookController {
    @FXML
    private Button backButton;
    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField isbnField;
    @FXML
    private TextField categoryNameField;
    @FXML
    private Button addButton;
    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, Integer> idColumn;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, String> isbnColumn;
    @FXML
    private TableColumn<Book, String> categoryNameColumn;
    @FXML
    private TableColumn<Book, Void> editColumn; // Update to Void for edit button, if needed
    @FXML
    private TableColumn<Book, Void> deleteColumn; // Use Void for the delete button column

    private ObservableList<Book> bookList = FXCollections.observableArrayList();

    private BookDao bookDao;

    private Book selectedBook;

    public BookController(){
        bookDao = new BookDao();
    }

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));


        // Configure the add button action
        addButton.setOnAction(event -> addBook());

        // Set up the delete column with a button
        deleteColumn.setCellValueFactory(new PropertyValueFactory<>("dummy")); // This can remain as it was
        deleteColumn.setCellFactory(new Callback<TableColumn<Book, Void>, TableCell<Book, Void>>() {
            @Override
            public TableCell<Book, Void> call(TableColumn<Book, Void> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setOnAction(event -> {
                            Book bookToDelete = getTableView().getItems().get(getIndex());
                            boolean isDeleted = bookDao.deleteBook(bookToDelete); // Assuming this method returns a boolean

                            if (isDeleted) {
                                bookList.remove(bookToDelete); // Remove from the ObservableList
                                bookTable.refresh(); // Refresh the table view
                                showAlert(Alert.AlertType.INFORMATION, "Category Deleted", "Deleted: " + bookToDelete.getTitle());
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete: " + bookToDelete.getTitle());
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
        editColumn.setCellFactory(new Callback<TableColumn<Book, Void>, TableCell<Book, Void>>() {
            @Override
            public TableCell<Book, Void> call(TableColumn<Book, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = new Button("Edit");

                    {
                        editButton.setOnAction(event -> {
                            selectedBook = getTableView().getItems().get(getIndex()); // Get the selected Book
                            titleField.setText(selectedBook.getTitle()); // Load title
                            authorField.setText(selectedBook.getAuthor());
                            isbnField.setText(selectedBook.getISBN());
                            categoryNameField.setText(selectedBook.getCategoryName());
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
        loadBooks();
    }

    private void loadBooks() {
        bookList.clear();
        bookList.addAll(bookDao.getAllBooks());
        bookTable.setItems(bookList);
    }

    private void addBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        String isbn = isbnField.getText();
        String categoryName = categoryNameField.getText();

        if (!title.isEmpty() && !author.isEmpty() && !isbn.isEmpty() && !categoryName.isEmpty()) {
            try {
                if (selectedBook == null) { // Adding a new book
                    if (!bookList.stream().anyMatch(c -> c.getTitle() == title)) {
                        Book newBook = new Book(title, author, isbn, categoryName);
                        bookDao.saveBook(newBook);
                        loadBooks();
                        clearFields();
                        showAlert(Alert.AlertType.INFORMATION, "Book Added", "\nTitle: " + title);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Book is already exists.");
                    }
                } else { // Updating an existing category
                    selectedBook.setTitle(title);
                    selectedBook.setAuthor(author);
                    selectedBook.setISBN(isbn);
                    selectedBook.setCategoryName(categoryName);
                    bookDao.updateBookDetails(selectedBook); // Assume you have an update method in CategoryDeo
                    loadBooks();
                    clearFields();
                    selectedBook = null; // Reset selection
                    showAlert(Alert.AlertType.INFORMATION, "Category Updated",  "\nTitle: " + title);
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid ID format. Please enter a numeric ID.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter relevant details");
        }
    }

    private void clearFields() {
        titleField.clear();
        authorField.clear();
        isbnField.clear();
        categoryNameField.clear();
    }

        private void showAlert(Alert.AlertType alertType, String title, String message) {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

    public void handleBackButton(ActionEvent actionEvent) {
        Navigation.navigateToScene("dashboard.fxml", backButton);
    }
}
