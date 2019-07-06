package com.makebit.filterss;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.makebit.filterss.helpers.SupportEmailListener;
import com.makebit.filterss.models.SQLOperation;
import com.makebit.filterss.persistence.UserPrefs;
import com.makebit.filterss.restful_api.LoadUserCollections;
import com.makebit.filterss.restful_api.RESTMiddleware;
import com.makebit.filterss.restful_api.callbacks.SQLOperationCallback;
import com.makebit.filterss.restful_api.interfaces.AsyncResponse;

import java.util.Locale;

import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();
    private RESTMiddleware api;
    private Button signUpButton;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Context context;
    private int registeredId;
    private UserPrefs prefs;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        context = this;

        //Instantiate the Middleware for the RESTful API's
        api = new RESTMiddleware(this);

        signUpButton = findViewById(R.id.signUpButton);
        TextView loginTextView = findViewById(R.id.loginTextView);
        nameEditText = findViewById(R.id.signUpNameEditText);
        emailEditText = findViewById(R.id.signUpMailEditText);
        passwordEditText = findViewById(R.id.signUpPasswordEditText);

        TextView tcTextView = findViewById(R.id.textViewTermsSignup);
        tcTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://filterssapp.makeb.it/tc-pp.html"));
                startActivity(i);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginActivity();
            }
        });

    }

    public void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        signUpButton.setEnabled(false);

        progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getText(R.string.creating_account));
        progressDialog.show();

        final String name = nameEditText.getText().toString();
        final String surname = "";
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        final String locale = Locale.getDefault().getLanguage();

        //Register User and persist it
        api.registerNewUser(name, surname, email, password, locale, new SQLOperationCallback() {
            @Override
            public void onLoad(Response<SQLOperation> response) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Registration completed, response: " + response);

                if (response.code() == 200 && response.body() != null && response.body().getInsertId() != 0) {
                    progressDialog.dismiss();
                    // show activation text
                    AlertDialog alertDialog = new AlertDialog.Builder(SignupActivity.this).create();
                    alertDialog.setTitle(getString(R.string.activate_account_title));
                    alertDialog.setMessage(getString(R.string.activate_account_message));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    startLoginActivity();
                                }
                            });
                    alertDialog.show();
                } else if (response.code() == 409) {
                    Log.e(ArticleActivity.logTag + ":" + TAG, "Response is: " + response.code());
                    progressDialog.dismiss();

                    onSignupDuplicate();
                } else {
                    Log.e(ArticleActivity.logTag + ":" + TAG, "Response is: " + response.code());
                    progressDialog.dismiss();

                    onSignupFailed();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(ArticleActivity.logTag + ":" + TAG, "Error on registerNewUser: " + t.getMessage());
                progressDialog.dismiss();

                onSignupFailed();
            }
        });

    }

    public void onSignupSuccess() {
        Log.d(ArticleActivity.logTag + ":" + TAG, "Sign up success");
        signUpButton.setEnabled(true);

        new LoadUserCollections(new AsyncResponse() {
            @Override
            public void processFinish(Integer output) {
                progressDialog.dismiss();
                startArticlesListActivity();
                finish();
            }
        }, context, prefs.retrieveUser()).execute();

    }

    public void onSignupDuplicate() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.duplicate_account, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.support, new SupportEmailListener(this));
        snackbar.show();
        signUpButton.setEnabled(true);
    }

    public void onSignupFailed() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.signup_failed, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.support, new SupportEmailListener(this));
        snackbar.show();
        signUpButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameEditText.setError(getText(R.string.name_not_valid));
            valid = false;
        } else {
            nameEditText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(getText(R.string.email_not_valid));
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            passwordEditText.setError(getText(R.string.password_not_valid));
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }

    private void startArticlesListActivity() {
        Intent intent = new Intent(this, ArticlesListActivity.class);
        startActivity(intent);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}
