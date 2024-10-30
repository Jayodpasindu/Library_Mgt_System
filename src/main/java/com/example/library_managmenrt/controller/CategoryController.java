package com.example.library_managmenrt.controller;

import com.example.library_managmenrt.dao.CategoryDeo;
import com.example.library_managmenrt.model.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.Locale;

public class CategoryController {
    @FXML
    private TableView<Category> categoryTable;

    @FXML
    private TableColumn<Category, Integer> idColumn;

    @FXML
    private TableColumn<Category, String> nameColumn;

    @FXML
    private TextField idField; // New field for ID

    @FXML
    private TextField nameField;

    @FXML
    private TableColumn<Category, Void> editColumn; // Update to Void for edit button, if needed
    @FXML
    private TableColumn<Category, Void> deleteColumn; // Use Void for the delete button column

    @FXML
    private Button addButton;

    private ObservableList<Category> categoryList = FXCollections.observableArrayList();

    private CategoryDeo categoryDeo;

    private Category selectedCategory; // To hold the category being edited

    public CategoryController() {
        categoryDeo = new CategoryDeo();
    }

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Configure the add button action
        addButton.setOnAction(event -> addCategory());

        // Set up the delete column with a button
        deleteColumn.setCellValueFactory(new PropertyValueFactory<>("dummy")); // This can remain as it was
        deleteColumn.setCellFactory(new Callback<TableColumn<Category, Void>, TableCell<Category, Void>>() {
            @Override
            public TableCell<Category, Void> call(TableColumn<Category, Void> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setOnAction(event -> {
                            Category categoryToDelete = getTableView().getItems().get(getIndex());
                            boolean isDeleted = categoryDeo.deleteCategory(categoryToDelete); // Assuming this method returns a boolean

                            if (isDeleted) {
                                categoryList.remove(categoryToDelete); // Remove from the ObservableList
                                categoryTable.refresh(); // Refresh the table view
                                showAlert(Alert.AlertType.INFORMATION, "Category Deleted", "Deleted: " + categoryToDelete.getName());
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete: " + categoryToDelete.getName());
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
        editColumn.setCellFactory(new Callback<TableColumn<Category, Void>, TableCell<Category, Void>>() {
            @Override
            public TableCell<Category, Void> call(TableColumn<Category, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = new Button("Edit");

                    {
                        editButton.setOnAction(event -> {
                            selectedCategory = getTableView().getItems().get(getIndex()); // Get the selected category
                            idField.setText(String.valueOf(selectedCategory.getId())); // Load ID into input
                            nameField.setText(selectedCategory.getName()); // Load Name into input
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

        // Load the categories from database
        loadCategories();
    }


    private void loadCategories() {
        categoryList.clear();
        categoryList.addAll(categoryDeo.getAllCategories());
        categoryTable.setItems(categoryList);
    }

    private void addCategory() {
        String idText = idField.getText();
        String name = nameField.getText();

        if (!idText.isEmpty() && !name.isEmpty()) {
            try {
                int id = Integer.parseInt(idText); // Parse the ID to integer
                if (selectedCategory == null) { // Adding a new category
                    if (!categoryList.stream().anyMatch(c -> c.getId() == id)) {
                        Category newCategory = new Category(id, name);
                        categoryDeo.saveCategory(newCategory);
                        loadCategories();
                        clearFields();
                        showAlert(Alert.AlertType.INFORMATION, "Category Added", "ID: " + id + "\nName: " + name);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Category ID already exists.");
                    }
                } else { // Updating an existing category
                    selectedCategory.setName(name);
                    categoryDeo.updateCategory(selectedCategory); // Assume you have an update method in CategoryDeo
                    loadCategories();
                    clearFields();
                    selectedCategory = null; // Reset selection
                    showAlert(Alert.AlertType.INFORMATION, "Category Updated", "ID: " + id + "\nName: " + name);
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid ID format. Please enter a numeric ID.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter both ID and Name.");
        }
    }

    private void clearFields() {
        idField.clear(); // Clear input fields
        nameField.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
