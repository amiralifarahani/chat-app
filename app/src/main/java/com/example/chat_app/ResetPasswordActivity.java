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

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText reemailtext;
    private Button resetpwdbutton;
    private TextView errorlbl;
    private ProgressDialog progressDialog;
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z\\d._%+-]+@[A-Z\\d.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        reemailtext = findViewById(R.id.ReEmailTb);
        resetpwdbutton = findViewById(R.id.ResetPwdBtn);
        errorlbl = findViewById(R.id.errorl2);
        progressDialog = new ProgressDialog(this);
        reemailtext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorlbl.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        resetpwdbutton.setOnClickListener(v -> {
            final String email = reemailtext.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                errorlbl.setText(R.string.enterEmailPromp);
            } else {
                if (validate(email)) {
                    progressDialog.setMessage("Please Wait..");
                    progressDialog.show();
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener(aVoid -> {
                        progressDialog.dismiss();
                        AlertDialog alertDialog = new AlertDialog.Builder(ResetPasswordActivity.this).create();
                        alertDialog.setTitle("Email sent");
                        alertDialog.setMessage("Link for reset password is been sent to " + email);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                (dialog, which) -> dialog.dismiss());
                        alertDialog.show();
                        try {
                            Thread.sleep(5000);
                        } catch (Exception ignored) {
                        }
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        errorlbl.setText(R.string.noAccountError);
                    });
                } else {
                    errorlbl.setText(R.string.invalidEmailError);
                }
            }
        });
    }

    private boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, android.R.anim.slide_out_right);
        finish();
    }
}
