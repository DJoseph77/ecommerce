package com.example.ecommerce.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.Adapter.CategoriesManagementAdapter;
import com.example.ecommerce.Adapter.UserPermessionAdapter;
import com.example.ecommerce.Helper.SharedPreferencesManager;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityCategoriesAndManageBinding;
import com.example.ecommerce.domain.Category;
import com.example.ecommerce.domain.User;
import com.example.ecommerce.domain.permission;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.UUID;

public class CategoriesAndManage extends AppCompatActivity implements UserPermessionAdapter.OnItemClickListener,CategoriesManagementAdapter.OnItemClickListener1 {
    private ActivityCategoriesAndManageBinding binding;
    private ArrayList<String> spinnerSetup;
    private ArrayList<String> users,categories;
    private ArrayList<String> ids;
    private int position,postion1;
    private String email;
    private Boolean products, orders, categoriesManagement;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageViewCategory;
    private EditText editTextCategoryName;
    private Uri imageUri;
    private FirebaseDatabase database;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoriesAndManageBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        setupSpinner();
        setupRecyclerUsers();
        setupRecyclerCategories();
        editPermessionUser();
        binding.addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCategoryDialog();
            }
        });
        binding.removeCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategoryFromDatabase();
            }
        });
    }

    private void setupSpinner() {
        spinnerSetup = new ArrayList<>();
        spinnerSetup.add("categories");
        spinnerSetup.add("Users Permission");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(CategoriesAndManage.this,
                android.R.layout.simple_spinner_item, spinnerSetup);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerManageCategory.setAdapter(adapter);
        binding.spinnerManageCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("categories")) {
                    binding.layoutCategory.setVisibility(View.VISIBLE);
                    binding.layoutUsers.setVisibility(View.GONE);
                } else {
                    binding.layoutCategory.setVisibility(View.GONE);
                    binding.layoutUsers.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.layoutCategory.setVisibility(View.GONE);
                binding.layoutUsers.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupRecyclerUsers() {
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        String userId = sharedPreferencesManager.getUserId();
        users = new ArrayList<>();
        ids = new ArrayList<>();
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("users");
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                ids.clear(); // Clear previous data to avoid duplicates
                for (DataSnapshot user : snapshot.getChildren()) {
                    User user1 = user.getValue(User.class);
                    users.add(user1.getEmail());
                    ids.add(user.getKey());
                }
                // Notify adapter of data changes
                ((UserPermessionAdapter) binding.RecyclerUser.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });

        binding.RecyclerUser.setLayoutManager(new LinearLayoutManager(CategoriesAndManage.this, LinearLayoutManager.VERTICAL, false));
        UserPermessionAdapter adapter = new UserPermessionAdapter(users, this, this); // Pass `this` for OnItemClickListener
        binding.RecyclerUser.setAdapter(adapter);
    }

    private void setupRecyclerCategories() {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");
        categories=new ArrayList<>();
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories.clear(); // Clear previous data to avoid duplicates
                for (DataSnapshot category : snapshot.getChildren()) {
                    Category category1 = category.getValue(Category.class);
                    categories.add(category1.getName());
                }
                // Notify adapter of data changes
                ((CategoriesManagementAdapter) binding.categoriesRecycler.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });

        binding.categoriesRecycler.setLayoutManager(new LinearLayoutManager(CategoriesAndManage.this, LinearLayoutManager.VERTICAL, false));
        CategoriesManagementAdapter adapter = new CategoriesManagementAdapter(categories, this, this); // Pass `this` for OnItemClickListener
        binding.categoriesRecycler.setAdapter(adapter);
    }


    private void editPermessionUser() {
        binding.editPermessionbtn.setOnClickListener(v -> {
            email = users.get(position);
            showDialog(email);
        });
    }

    @Override
    public void onItemClick(int position) {
        this.position = position;
    }
    public void onItemClick1(int postion1) {
        this.postion1=postion1;
    }

    private void showDialog(String email) {
        final Dialog dialog = new Dialog(CategoriesAndManage.this);
        dialog.setContentView(R.layout.layout_editpermession);

        TextView emailTextView = dialog.findViewById(R.id.emailTextView);
        CheckBox productsCheckBox = dialog.findViewById(R.id.productsCheckBox);
        CheckBox ordersCheckBox = dialog.findViewById(R.id.ordersCheckBox);
        CheckBox categoriesManageCheckBox = dialog.findViewById(R.id.categoriesManageCheckBox);

        // Set the email text
        emailTextView.setText(email);

        // Fetch the permissions from Firebase
        DatabaseReference permessionPath = FirebaseDatabase.getInstance().getReference()
                .child("permissions").child(ids.get(position));

        permessionPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    permission userPermission = snapshot.getValue(permission.class);
                    if (userPermission != null) {
                        productsCheckBox.setChecked(userPermission.getProducts());
                        ordersCheckBox.setChecked(userPermission.getOrders());
                        categoriesManageCheckBox.setChecked(userPermission.getCategoriesManagement());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });

        Button closeButton = new Button(this);
        closeButton.setText("Close");
        Button saveButton = new Button(this);
        saveButton.setText("Save");

        saveButton.setOnClickListener(v -> {
            products = productsCheckBox.isChecked();
            orders = ordersCheckBox.isChecked();
            categoriesManagement = categoriesManageCheckBox.isChecked();

            permission newPermission = new permission();
            newPermission.setCategoriesManagement(categoriesManagement);
            newPermission.setProducts(products);
            newPermission.setOrders(orders);

            permessionPath.setValue(newPermission);
            DatabaseReference userPath = FirebaseDatabase.getInstance().getReference().child("users").child(ids.get(position));
            if (products || orders || categoriesManagement) {
                userPath.child("admin").setValue(true);
            } else {
                userPath.child("admin").setValue(false);
            }
            dialog.dismiss();
        });

        closeButton.setOnClickListener(v -> dialog.dismiss());

        ((LinearLayout) dialog.findViewById(R.id.layout_editpermession)).addView(closeButton);
        ((LinearLayout) dialog.findViewById(R.id.layout_editpermession)).addView(saveButton);

        dialog.show();
    }

    @SuppressLint("MissingInflatedId")
    private void showAddCategoryDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_category, null);
        imageViewCategory = dialogView.findViewById(R.id.imageViewCategory);
        editTextCategoryName = dialogView.findViewById(R.id.editTextCategoryName);

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String categoryName = editTextCategoryName.getText().toString();
                    if (imageUri != null && !categoryName.isEmpty()) {
                        uploadImageAndSaveCategory(categoryName);
                    } else {
                        // Handle case where no image is selected or category name is empty
                        Log.e("MainActivity", "No image selected or category name is empty");
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Set click listener for the ImageView
        imageViewCategory.setOnClickListener(v -> chooseImage());
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewCategory.setImageURI(imageUri);
        }
    }

    private void uploadImageAndSaveCategory(String categoryName) {
        // Get a reference to Firebase Storage
        StorageReference storageRef = storage.getReference();
        // Create a unique filename for the image
        String fileName = UUID.randomUUID().toString();
        StorageReference imageRef = storageRef.child("categories/" + fileName);

        // Upload the image
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            // Get the download URL and save the category data
                            String imageUrl = uri.toString();
                            saveCategoryToDatabase(categoryName, imageUrl);
                        })
                        .addOnFailureListener(exception -> Log.e("MainActivity", "Failed to get download URL", exception))
                )
                .addOnFailureListener(exception -> Log.e("MainActivity", "Image upload failed", exception));
    }

    private void saveCategoryToDatabase(String categoryName, String imageUrl) {
        DatabaseReference categoriesRef = database.getReference("categories");
        String key = categoriesRef.push().getKey();
        if (key != null) {
            Category category = new Category(categoryName, imageUrl);
            categoriesRef.child(key).setValue(category)
                    .addOnSuccessListener(aVoid -> Log.d("MainActivity", "Category added successfully"))
                    .addOnFailureListener(exception -> Log.e("MainActivity", "Failed to add category", exception));
        }
    }
    private void deleteCategoryFromDatabase() {
        if (categories != null && !categories.isEmpty()) {
            String categoryToDelete = categories.get(postion1); // Use postion1 to get the selected category
            DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("categories");

            categoriesRef.orderByChild("name").equalTo(categoryToDelete).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                        categorySnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle possible errors.
                }
            });
        }
    }


}
