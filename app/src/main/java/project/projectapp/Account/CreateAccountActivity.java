package project.projectapp.Account;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import project.projectapp.MainActivity;
import project.projectapp.R;

public class CreateAccountActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private EditText emailAddress, username, password, passwordCheck;
    private Button createAccountButton;
    private TextView alreadyHasAccount;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private String userId;

    private Uri selectedUri;

    @Override
    @Nullable
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_activity);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance()
                .getReference()
                .child("user_profile_images");

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            toMainActivity();
        }

        emailAddress = findViewById(R.id.edit_text_email_address);
        username = findViewById(R.id.edit_text_username);
        password = findViewById(R.id.edit_text_password);
        passwordCheck = findViewById(R.id.edit_text_password_check);
        profileImage = findViewById(R.id.circle_image_view_select_profile_image);

        progressDialog = new ProgressDialog(this);
        
        createAccountButton = findViewById(R.id.create_new_account);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrationDataChecker();
            }
        });

        alreadyHasAccount = findViewById(R.id.text_view_already_has_account);
        alreadyHasAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSignUp(v);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void toMainActivity(){
        Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void successfulAccountToSignIn(){
        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void registrationDataChecker(){
        final String newEmailAddress = emailAddress.getText().toString().trim();
        final String newUsername = username.getText().toString().trim();
        final String newPassword = password.getText().toString().trim();
        final String newPasswordCheck = passwordCheck.getText().toString().trim();

        if(TextUtils.isEmpty(newEmailAddress)){
            emailAddress.setError("This field cannot be empty");
            emailAddress.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(newUsername)){
            username.setError("This field cannot be empty");
            username.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(newPassword)){
            password.setError("This field cannot be empty");
            password.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(newPasswordCheck)){
            passwordCheck.setError("This field cannot be empty");
            passwordCheck.requestFocus();
            return;
        }
        if(newPassword.length() < 6){
            password.setError("Password must be longer than 6 characters");
            password.requestFocus();
            return;
        }
        //TODO: might need to check against some more characters, double check at some point
        if(newUsername.contains("\",</?>;:'|[]{}+=)(*&^%$#@!~`#\\\\//|\"")){
            username.setError("Illegal character, please select another username");
            username.requestFocus();
            return;
        }

        createNewAccount(newEmailAddress, newUsername, newPassword, newPasswordCheck);

    }

    private void userSignUp(View v){
        Intent intent = new Intent(v.getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        alreadyHasAccount.setTextColor(Color.parseColor("#800199"));
    }

    private void createNewAccount(String newEmailAddress, final String newUsername,
                                  String newPassword, String newPasswordCheck){
        if(TextUtils.equals(newPassword, newPasswordCheck)){
            progressDialog.setMessage("Checking details, please wait...");
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(newEmailAddress, newPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.getException() instanceof
                                    FirebaseAuthUserCollisionException) {
                                emailAddress.setError("This email address is already in use");
                                emailAddress.requestFocus();
                            } else if(!task.isSuccessful()){
                                Toast.makeText(CreateAccountActivity.this,
                                        "Registration failed", Toast.LENGTH_SHORT).show();
                            } else {
                                createProfileImage(newUsername, firebaseAuth.getCurrentUser());
                                finish();
                                toMainActivity();
                            }
                            progressDialog.hide();
                        }
                    });
        } else {
            password.setError("These passwords do not match");
            passwordCheck.setError("These passwords do not match");
            return;
        }
    }

    private void createProfileImage(final String newUsername, final FirebaseUser user){
        final StorageReference imagePath = storageReference.child(selectedUri.getLastPathSegment());
        imagePath.putFile(selectedUri).addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uri)
                                    .setDisplayName(newUsername)
                                    .build();

                            user.updateProfile(changeRequest);
                        }
                    });
            }
        });
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            selectedUri = data.getData();
            profileImage.setImageURI(selectedUri);
        }
    }
}