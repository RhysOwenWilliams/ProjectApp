package project.projectapp.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import project.projectapp.MainActivity;
import project.projectapp.R;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailAddress, loginPassword;
    private TextView createAccount;
    private Button logIn;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    @Nullable
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            toMainActivity();
        }

        loginEmailAddress = findViewById(R.id.userEmailAddress);
        loginPassword = findViewById(R.id.userPassword);

        logIn = findViewById(R.id.logIn);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        createAccount = findViewById(R.id.createAccount);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userCreateAccount();
            }
        });

        progressDialog = new ProgressDialog(this);
    }

    private void toMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void userLogin(){
        String emailAddress = loginEmailAddress.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if(TextUtils.isEmpty(emailAddress) ||
                TextUtils.isEmpty(password)){
            Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        progressDialog.setMessage("Signing in, please wait...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    finish();
                    toMainActivity();
                } else {
                    loginEmailAddress.setError("Email and password do not match");
                    loginPassword.setError("Email and password do not match");
                }
            }
        });
    }

    private void userCreateAccount(){
        Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        createAccount.setTextColor(Color.parseColor("#800199"));
    }
}