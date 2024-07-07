package com.example.ecommerce.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivitySignupBinding;
import com.example.ecommerce.domain.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class signup extends AppCompatActivity {
    ActivitySignupBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    String country, name, password, phoneNumber, address, email;
    ArrayList<String> countries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        countries = new ArrayList<>();
        setUpSpinner();
        binding.textAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent9=new Intent(signup.this, Login.class);
                startActivity(intent9);
            }
        });

        binding.countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                country = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = binding.editTextName.getText().toString();
                password = binding.editTextPassword.getText().toString();
                phoneNumber = binding.editTextPhoneNumber.getText().toString();
                address = binding.editTextAdress.getText().toString();
                email = binding.editTextEmail.getText().toString();

                if (validateFields(name, email, password, phoneNumber, address, country)) {
                    createAccount(name, email, password, phoneNumber, country, address);
                }else {
                    binding.textError.setVisibility(View.VISIBLE);
                    binding.textError.setText("Please fill all fields!");
                }
            }
        });
    }

    private void setUpSpinner() {
        countries.add("Tunis");
        countries.add("Bizert");
        countries.add("ariena");
        countries.add("manouba");
        countries.add("Ben Arous");
        countries.add("zaghouen");
        countries.add("Nabeul");
        countries.add("jandouba");
        countries.add("Beja");
        countries.add("Elkef");
        countries.add("Siliana");
        countries.add("Sousse");
        countries.add("Monastir");
        countries.add("Mahdia");
        countries.add("Sfax");
        countries.add("Kairouan");
        countries.add("Gassrine");
        countries.add("Sidi bouzid");
        countries.add("Gabes");
        countries.add("Mednine");
        countries.add("Tataouen");
        countries.add("Gafsa");
        countries.add("Touzeur");
        countries.add("gbelli");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.countrySpinner.setAdapter(adapter);
    }

    private boolean validateFields(String name, String email, String password, String phoneNumber, String address, String country) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty() || address.isEmpty() || country.isEmpty()) {
            return false;
        }
        return true;
    }

    private void createAccount(String name, String email, String password, String phoneNumber, String country, String address) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(signup.this, "Auth successful", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        // Get user ID
                        String userId = user.getUid();
                        // Write user data to the database
                        writeNewUser(userId, name, phoneNumber, address, country);
                        Intent intent3 = new Intent(signup.this, Login.class);
                        startActivity(intent3);
                    }
                } else {
                    binding.textError.setVisibility(View.VISIBLE);
                    binding.textError.setText("Email used");
                }
            }
        });
    }

    private void writeNewUser(String userId, String name, String phoneNumber, String address, String country) {
        // Create a User object
        User user = new User(country, password, name, phoneNumber, address);
        // Write a User object to the Firebase Realtime Database
        mDatabase.child("users").child(userId).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(signup.this, "User data saved.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(signup.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
