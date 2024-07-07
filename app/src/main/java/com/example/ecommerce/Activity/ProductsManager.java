package com.example.ecommerce.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityProductsManagerBinding;
import com.example.ecommerce.domain.Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProductsManager extends AppCompatActivity {
    ActivityProductsManagerBinding binding;
    private String title, photoUrl, Description, Category;
    private ArrayList<String> Categories = new ArrayList<>();
    private Double price;
    private Boolean isPopular;
    private DatabaseReference databaseReference;
    private ArrayList<String> CategoriesTypes;
    private AlertDialog dialog; // Declare AlertDialog instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductsManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = FirebaseDatabase.getInstance().getReference();
        CategoriesTypes = new ArrayList<>();

        binding.addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(ProductsManager.this);
            }
        });
    }

    private void showInputDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_input, null);

        // Build AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        dialog = builder.create(); // Initialize dialog here

        EditText inputFieldTitle = dialogView.findViewById(R.id.TitleInput);
        EditText inputFieldPrice = dialogView.findViewById(R.id.priceInput);
        EditText inputFieldDescription = dialogView.findViewById(R.id.descriptionInput);
        ImageView imageField = dialogView.findViewById(R.id.imageProduit);
        Spinner spinnerCategory = dialogView.findViewById(R.id.category);
        CheckBox checkBoxPopular = dialogView.findViewById(R.id.isPopular);

        CategoriesTypes.add("Category 1");
        CategoriesTypes.add("Category 2");
        CategoriesTypes.add("Category 3");
        CategoriesTypes.add("Category 4");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, CategoriesTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Image selection from gallery
        imageField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        Button buttonAdd = dialogView.findViewById(R.id.dialogButtonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the input data
                title = inputFieldTitle.getText().toString();
                price = Double.valueOf(inputFieldPrice.getText().toString());
                Description = inputFieldDescription.getText().toString();
                Category = spinnerCategory.getSelectedItem().toString();
                isPopular = checkBoxPopular.isChecked();

                Categories.add(Category);

                // Handle image upload to Firebase Storage
                if (selectedImageUri != null) {
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("products")
                            .child(selectedImageUri.getLastPathSegment());
                    storageRef.putFile(selectedImageUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    photoUrl = uri.toString();

                                    // Create a Product object
                                    Product product = new Product(title, photoUrl, price, Description, Categories, isPopular);

                                    // Save the product to the database
                                    databaseReference.child("products").push().setValue(product)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ProductsManager.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss(); // Dismiss dialog after successful upload
                                                } else {
                                                    Toast.makeText(ProductsManager.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(ProductsManager.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(ProductsManager.this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button customCancelButton = dialogView.findViewById(R.id.dialogButtonCancel);
        customCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Dismiss dialog on cancel button click
            }
        });

        dialog.show(); // Show the dialog
    }

    // Variable to store selected image URI
    private Uri selectedImageUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            // Find ImageView within the dialog view
            ImageView imageField = dialog.findViewById(R.id.imageProduit);
            if (imageField != null) {
                imageField.setImageURI(selectedImageUri);
            } else {
                Toast.makeText(this, "Failed to set image. ImageView not found.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
