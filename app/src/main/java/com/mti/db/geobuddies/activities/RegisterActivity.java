package com.mti.db.geobuddies.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mti.db.geobuddies.R;
import com.mti.db.geobuddies.database.AccountDAO;
import com.mti.db.geobuddies.database.GeoBuddiesDAOFactory;
import com.mti.db.geobuddies.model.GeoAccount;

import java.util.LinkedHashMap;
import java.util.Map;

public class RegisterActivity extends Activity {

    private static final String TAG = "RegisterActivity";

    /**
     * Keep track of the register task to ensure we can cancel it if requested.
     */
    private UserRegisterTask userRegisterTask = null;

    // UI references.
    private EditText etUsername;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPasswordConfirm;

    private View vRegisterForm;
    private View vProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set up the register form.
        etUsername = (EditText) findViewById(R.id.register_username);
        etFirstName = (EditText) findViewById(R.id.register_first_name);
        etLastName = (EditText) findViewById(R.id.register_last_name);
        etEmail = (EditText) findViewById(R.id.register_email);
        etPassword = (EditText) findViewById(R.id.register_password);
        etPasswordConfirm = (EditText) findViewById(R.id.register_password_confirm);

        etPasswordConfirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    registerNewAccount();
                    return true;
                }
                return false;
            }
        });

        Button btnRegister = (Button) findViewById(R.id.register_register_button);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerNewAccount();
            }
        });

        Button btnBack = (Button) findViewById(R.id.register_back_button);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        vRegisterForm = findViewById(R.id.user_register_form);
        vProgress = findViewById(R.id.register_progress);
    }


    private void registerNewAccount() {

        Log.d(TAG, "Attempting to register new account");

        if (userRegisterTask != null) {
            return;
        }

        // Reset errors.
        etUsername.setError(null);
        etFirstName.setError(null);
        etLastName.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);
        etPasswordConfirm.setError(null);

        // Store values at the time of the register attempt.
        String username = etUsername.getText().toString();
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String passwordConfirm = etPasswordConfirm.getText().toString();

        // Create map to store fields that are required for registration.
        Map<EditText, String> requiredFields = new LinkedHashMap<EditText, String>();

        // Populate map with required fields using EditTexts as keys in reverse order.
        // LinkedHashMap maintains the order of they keys how they were entered.
        requiredFields.put(etPasswordConfirm, passwordConfirm);
        requiredFields.put(etPassword, password);
        requiredFields.put(etEmail, email);
        requiredFields.put(etLastName, lastName);
        requiredFields.put(etFirstName, firstName);
        requiredFields.put(etUsername, username);

        boolean cancel = false; // determines whether to attempt registration or not
        View focusView = null; // gets last View to be focused on

        // Check if password fields contain same password
        if (!password.equals(passwordConfirm)) {
            etPasswordConfirm.setError(getString((R.string.error_passwords_mismatch)));
            focusView = etPasswordConfirm;
            cancel = true;
        }

        // Check each field in a map if it's empty.
        for (EditText field : requiredFields.keySet()) {

            // If text in the field is empty, set error message on the field
            if (TextUtils.isEmpty(requiredFields.get(field))) {
                field.setError(getString((R.string.error_field_required)));
                focusView = field;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user register attempt.

            showProgress(true);
            userRegisterTask = new UserRegisterTask(username, firstName, lastName, email, password);
            userRegisterTask.execute((Void) null);
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            vRegisterForm.setVisibility(show ? View.GONE : View.VISIBLE);
            vRegisterForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    vRegisterForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            vProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            vProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    vProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            vProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            vRegisterForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String strUsername;
        private final String strFirstName;
        private final String strLastName;
        private final String strEmail;
        private final String strPassword;

        // Set up DAO factory.
        GeoBuddiesDAOFactory factory = new GeoBuddiesDAOFactory(getBaseContext());

        // Set up account database implementation.
        AccountDAO daoAccount;

        /**
         * Constructor
         * @param username - User name of account holder
         * @param firstName - First name of account holder
         * @param lastName - last name of account holder
         * @param email - Email of account holder
         * @param password - Password to the account
         */
        UserRegisterTask (String username, String firstName, String lastName, String email, String password) {
            strUsername = username;
            strFirstName = firstName;
            strLastName = lastName;
            strEmail = email;
            strPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            // Create account database implementation.
            daoAccount = factory.createAccountDAO();

            // Create geoAccount object
            GeoAccount user =
                    new GeoAccount(strUsername, strFirstName, strLastName, strEmail, strPassword);

            boolean result;
            result = daoAccount.registerAccount(user);

           return result;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            userRegisterTask = null;
            showProgress(false);

            if (success) {
                // If returned boolean is true, account was successfully registered
                toast("Successfully registered account");

                // Return user to login activity
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();

            } else {
                // If returned boolean is false, display error and set focus to username field
                etUsername.setError(getString(R.string.error_user_exists));
                etUsername.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {

        }
    }

    public void toast(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    } // toast(String msg)
}
