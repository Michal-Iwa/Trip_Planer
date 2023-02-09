package com.example.tripplaner.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tripplaner.R;
import com.example.tripplaner.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private Button signUpBtn;
    private EditText emailET;
    private EditText usernameET;
    private EditText passwordET;
    private EditText passwordRepeatET;

    //Authentication
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    //Firebase connection
    private FirebaseFirestore dataBase = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = dataBase.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        //init buttons
        signUpBtn = (Button) findViewById(R.id.activity_register_btn_sign_up);
        emailET = findViewById(R.id.activity_register_tv_email);
        passwordET = findViewById(R.id.activity_register_et_password);
        passwordRepeatET = findViewById(R.id.activity_register_et_password_repeat);
        usernameET = findViewById(R.id.activity_register_et_username);

        signUpBtn.setOnClickListener(v -> {
            if(!TextUtils.isEmpty(emailET.getText().toString())
                    && !TextUtils.isEmpty(passwordET.getText().toString())
                    && !TextUtils.isEmpty(passwordRepeatET.getText().toString())
                    && !TextUtils.isEmpty(usernameET.getText().toString())) {
                String password = passwordET.getText().toString().trim();
                String passwordRepeat = passwordRepeatET.getText().toString().trim();
                if(password.equals(passwordRepeat)) {
                    String username = usernameET.getText().toString().trim();
                    String email = emailET.getText().toString().trim();
                    CreateUserEmailAccount(password,email,username);
                }
                else{
                    Toast.makeText(this, "Passwords are not the same",Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "There are empty fields",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void CreateUserEmailAccount(String password, String email, String username) {

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        //Create activity
                        currentUser = firebaseAuth.getCurrentUser();
                        assert currentUser != null;
                        final String currentUserId = currentUser.getUid();
                        User user = new User(currentUserId,username);

                        collectionReference.document(currentUserId).set(user)
                                .addOnSuccessListener(unused -> {
                                    Intent intent = new Intent(RegisterActivity.this,
                                            LoginActivity.class);

                                    startActivity(intent);
                                }).addOnFailureListener(e ->
                                        Toast.makeText(RegisterActivity.this, "Server coudn't create a user",
                                        Toast.LENGTH_SHORT).show());
                    }
                });
    }
}