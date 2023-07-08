package com.example.chat_app;

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

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private EditText emailTextBox;
    private EditText passwordTextBox;
    private TextView errorTextview;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z\\d._%+-]+@[A-Z\\d.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
//        if (firebaseAuth.getCurrentUser() != null) {
//            Intent i = new Intent(getApplicationContext(), MainPageActivity.class);
//            startActivity(i);
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        TextView resetpwdtv = findViewById(R.id.ResetPwdLink);
        emailTextBox = findViewById(R.id.EmailTb2);
        passwordTextBox = findViewById(R.id.PasswordTb2);
        Button loginButton = findViewById(R.id.LoginBtn);
        TextView signUpLinkTextView = findViewById(R.id.SignUpLink);
        errorTextview = findViewById(R.id.ErrorLabel2);
        loginButton.setOnClickListener(v -> userLogin());
        signUpLinkTextView.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), SignUpPageActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_from_bottom_right, android.R.anim.slide_out_right);
        });
        resetpwdtv.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), ResetPasswordActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
        keyLiss(emailTextBox);
        keyLiss(passwordTextBox);
    }

    private boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    private void userLogin() {
        String email = emailTextBox.getText().toString().trim();
        String password = passwordTextBox.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            errorTextview.setText(R.string.enterEmailPromp);
        } else {

            if (TextUtils.isEmpty(password)) {
                errorTextview.setText(R.string.enterPasswordPromp);
            } else {
                if (validate(email)) {
                    progressDialog.setMessage("Authenticating Please Wait..");
                    progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {

                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Intent i = new Intent(getApplicationContext(), MainPageActivity.class);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.slide_in_left, R.anim.nav_to_message);
                        } else {
                            progressDialog.dismiss();
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException invalidEmail) {
                                errorTextview.setText(R.string.NoAccountError);
                            } catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
                                errorTextview.setText(R.string.invalidPasswordError);
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Couldn't able to login please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    errorTextview.setText(R.string.invalidEmailAddress);
                }
            }
        }
    }

    private void keyLiss(EditText e) {

        e.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorTextview.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
