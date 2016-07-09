package com.notesinshort.notesinshort;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by mayank on 9/7/16.
 */
public class SignupActivity extends Activity {
    EditText mUserName;
    EditText mPassword;
    Button mAlreadyRegistedButton;
    Button mSignupButton;
    String TAG = SignupActivity.class.getSimpleName();
    String username;
    String password;
    ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_signup);
        mUserName = (EditText) findViewById(R.id.Username);
        mPassword = (EditText) findViewById(R.id.password);
        mAlreadyRegistedButton = (Button) findViewById(R.id.btnLinkToLoginScreen);
        mSignupButton = (Button) findViewById(R.id.verifyButton);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("FireAuth", "onAuthStateChanged:signed_in:" + user.getUid());

                    Log.d(TAG, "Already Logged in: "+user.getEmail());

                } else {
                    // User is signed out
                    Log.d("FireAuth", "onAuthStateChanged:signed_out");
                }
            }
        };

        mAlreadyRegistedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = mUserName.getText().toString();
                password = mPassword.getText().toString();
                username = username.trim();
                password = password.trim();
                if (username.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    builder.setMessage(R.string.signup_error_message);
                    builder.setTitle(R.string.signup_error_title);
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    showProgressDialog();
                    startRegistration();
                }
            }
        });
    }

    private void showProgressDialog() {
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
    }

    private void hideProgressDialog() {
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    private void startRegistration() {
        setProgressBarIndeterminateVisibility(true);
        Log.d(TAG, "Username : " + username);
        Log.d(TAG, "Full Name : " + username);
        Log.d(TAG, "Password : " + password);
        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Auth", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {

                            hideProgressDialog();
                            setProgressBarIndeterminateVisibility(false);

                            Intent i = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(i);

                            Log.d(TAG, "Signup Successful!");
                        }
                    }
                });

            }


}
