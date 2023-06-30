package com.example.chat_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpPageActivity extends AppCompatActivity {
    EditText emailText, passwordText, usernameText;
    TextView errortv, logintv;
    Button registerButton;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z\\d._%+-]+@[A-Z\\d.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        firebaseAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.EmailTb2);
        passwordText = findViewById(R.id.PasswordTb2);
        usernameText = findViewById(R.id.UserNameTB);
        errortv = findViewById(R.id.ErrorLabel2);
        logintv = findViewById(R.id.LoginLink);
        registerButton = findViewById(R.id.RegisterBtn);
        registerButton.setOnClickListener(v -> registerUser());
        logintv.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_from_top_right, R.anim.slide_in_from_right);
        });
        progressDialog = new ProgressDialog(this);
        keyLiss(emailText);
        keyLiss(passwordText);
    }

    private void registerUser() {
        errortv.setText("");
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        final String username = usernameText.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            errortv.setText(R.string.enterEmailPromp);
        } else {
            if (validate(email)) {
                if (TextUtils.isEmpty(password)) {
                    errortv.setText(R.string.enterPasswordPrompt);
                } else {
                    if (TextUtils.isEmpty(username)) {
                        errortv.setText(R.string.enterPasswordPrompt);
                    } else {
                        progressDialog.setMessage("Register Please Wait..");
                        progressDialog.show();
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Objects.requireNonNull(firebaseAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                        String userid = firebaseUser.getUid();
                                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("id", userid);
                                        hashMap.put("username", username);
                                        hashMap.put("imageURL", "default");
                                        hashMap.put("status", "offline");
                                        reference.setValue(hashMap).addOnCompleteListener(task11 -> {
                                            progressDialog.dismiss();
                                            AlertDialog alertDialog = new AlertDialog.Builder(SignUpPageActivity.this).create();
                                            alertDialog.setTitle("Verify by Email");
                                            alertDialog.setMessage("Registered successfully. Please check email for verification.");
                                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                    (dialog, which) -> {
                                                        dialog.dismiss();
                                                        FirebaseAuth.getInstance().signOut();
                                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                        startActivity(i);
                                                    });
                                            alertDialog.show();
                                            emailText.setText("");
                                            passwordText.setText("");
                                            errortv.setText("");
                                        }).addOnFailureListener(e -> {
                                            progressDialog.dismiss();
                                            errortv.setText(R.string.errorInRegisteration);
                                        });

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(SignUpPageActivity.this, "Could'nt register make you are connected to internet", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (FirebaseAuthWeakPasswordException weakPassword) {
                                    progressDialog.dismiss();
                                    errortv.setText(R.string.weakPassword);
                                } catch (FirebaseAuthUserCollisionException existEmail) {
                                    progressDialog.dismiss();
                                    errortv.setText(R.string.alreadyRegisteredError);
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(SignUpPageActivity.this, "Could'nt register make you are connected to internet", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            } else {
                errortv.setText(R.string.invalidEmailAddress);
            }
        }
    }

    private boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    private void keyLiss(EditText e) {

        e.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errortv.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_from_top_right, R.anim.slide_in_from_right);
        finish();
    }
}
