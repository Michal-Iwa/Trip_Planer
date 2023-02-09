package com.example.tripplaner.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tripplaner.R;
import com.example.tripplaner.model.CurrentUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private Button signInBtn;
    private Button signUpBtn;
    private EditText emailET;
    private EditText passwordET;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore dataBase = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = dataBase.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        //Init widgets
        signInBtn = (Button) findViewById(R.id.activity_login_btn_sign_in);
        signUpBtn = (Button) findViewById(R.id.activity_login_btn_sign_up);
        emailET = findViewById(R.id.activity_login_tv_email);
        passwordET = findViewById(R.id.activity_login_et_password);

        signUpBtn.setOnClickListener(v -> {
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
        });

        signInBtn.setOnClickListener(v -> {
            if(!TextUtils.isEmpty(emailET.getText().toString())
                    && !TextUtils.isEmpty(passwordET.getText().toString())){
                LoginEmailPasswordUser(emailET.getText().toString(),passwordET.getText().toString());
            }
            else{
                Toast.makeText(this, "There are empty fields",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void LoginEmailPasswordUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(authResult -> {

                    currentUser = firebaseAuth.getCurrentUser();
                    assert currentUser != null;
                    String userId = currentUser.getUid();

                    collectionReference.document(userId).addSnapshotListener((value, error) -> {
                        CurrentUser user = CurrentUser.getInstance();
                        user.setUserId(value.getString("userId"));
                        user.setUsername(value.getString("username"));

                        Intent intent = new Intent(LoginActivity.this, DisplayDataActivity.class);
                        startActivity(intent);
                    });


                })
                .addOnFailureListener(e -> Toast.makeText(LoginActivity.this,
                        "Wasn't able to login :( maybe next time" ,Toast.LENGTH_SHORT).show());
    }
}