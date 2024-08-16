package com.example.ecommerce.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Adapter.AddCategoryAdapter;
import com.example.ecommerce.Adapter.CategoryAdapter;
import com.example.ecommerce.Adapter.ProductAdapter;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityProductsManagerBinding;
import com.example.ecommerce.domain.Category;
import com.example.ecommerce.domain.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProductsManager extends AppCompatActivity {
    ActivityProductsManagerBinding binding;
    private String title, photoUrl, Description;
    private Double price;
    private Boolean isPopular;
    private DatabaseReference databaseReference;
    private ArrayList<String> CategoriesTypes;
    private AlertDialog dialog; // Declare AlertDialog instance
    private ArrayList<Product> products;
    private boolean isAdmin;
    private ArrayList<String> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductsManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ids = new ArrayList<>();
        products = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        isAdmin = true;
        CategoriesTypes = new ArrayList<>();
        setupCategories();
        binding.addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(ProductsManager.this);
            }
        });

        // Initialize the search filter
        binding.editTextCherche.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text is changed
            }
        });

        ItemsFromFirebase();
    }
    private void setupCategories(){
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("categories");
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CategoriesTypes.clear(); // Clear the list to avoid duplication
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Category category = categorySnapshot.getValue(Category.class);
                    String name=category.getName();
                    CategoriesTypes.add(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(ProductsManager.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showInputDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_input, null);
        ArrayList<String> listCategorieAdd = new ArrayList<>();

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
        RecyclerView catG = dialogView.findViewById(R.id.CategoriesAdd);
        AddCategoryAdapter adapter = new AddCategoryAdapter(listCategorieAdd);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Add the selected category to the list
                String selectedCategory = parent.getItemAtPosition(position).toString();
                if (!listCategorieAdd.contains(selectedCategory)) {
                    listCategorieAdd.add(selectedCategory);
                    adapter.notifyDataSetChanged(); // Notify adapter about data change
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected, if necessary
            }
        });

        catG.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        catG.setAdapter(adapter);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, CategoriesTypes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);

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
                isPopular = checkBoxPopular.isChecked();

                // Handle image upload to Firebase Storage
                if (selectedImageUri != null) {
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("products")
                            .child(selectedImageUri.getLastPathSegment());
                    storageRef.putFile(selectedImageUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    photoUrl = uri.toString();

                                    // Generate a new database reference with push()
                                    DatabaseReference newProductRef = databaseReference.child("products").push();
                                    String productId = newProductRef.getKey(); // Get the generated key

                                    // Create a Product object
                                    Product product = new Product(title, photoUrl, price, Description, listCategorieAdd, isPopular);
                                    product.setId(productId); // Set the key in the Product object

                                    // Save the product to the database
                                    newProductRef.setValue(product)
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

    private void ItemsFromFirebase() {
        databaseReference.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                products.clear(); // Clear previous data
                ids.clear(); // Clear previous IDs
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        String productId = snapshot.getKey(); // Get the Firebase ID
                        product.setId(productId); // Set the product ID
                        products.add(product);
                        ids.add(productId); // Add Firebase ID to the list
                    }
                }
                // Update RecyclerView with all items
                initRecyclerView(products);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void initRecyclerView(ArrayList<Product> items) {
        ProductAdapter adapter = new ProductAdapter(items,isAdmin,ids);
        binding.recycler.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recycler.setAdapter(adapter);
    }

    // Filter products based on search query
    private void filterProducts(String query) {
        ArrayList<Product> filteredProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredProducts.add(product);
            }
        }
        // Update RecyclerView with filtered items
        initRecyclerView(filteredProducts);
    }
}
