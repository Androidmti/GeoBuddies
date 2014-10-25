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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mti.db.geobuddies.R;
import com.mti.db.geobuddies.database.AccountDAO;
import com.mti.db.geobuddies.database.GeoBuddiesDAOFactory;
import com.mti.db.geobuddies.model.GeoAccount;


public class LoginActivity extends Activity {

    private final static String TAG = "LoginActivity";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask userLoginTask = null;

    // UI references.
    private EditText etUsername;
    private EditText etPassword;
    private View vLoginForm;
    private View vProgress;
    private CheckBox chkRememberSignIn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        etUsername = (EditText) findViewById(R.id.username);

        etPassword = (EditText) findViewById(R.id.password);
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button btnRegister = (Button) findViewById(R.id.register_button);
        btnRegister.setOnClickListener(new View.OnClickListener() {
           @Override
                   public void onClick(View view) {
                registerNewAccount();
            }
        });

        vLoginForm = findViewById(R.id.user_login_form);
        vProgress = findViewById(R.id.login_progress);

        initialize();
    }

    /**
     * Initialization of database
     */
    private void initialize() {

        // Set up DAO factory.
        GeoBuddiesDAOFactory factory = new GeoBuddiesDAOFactory(getBaseContext());

        // Set up account database implementation.
        AccountDAO daoAccount;

        // Create account database implementation.
        daoAccount = factory.createAccountDAO();

        // Open database to populate with sample data.
        daoAccount.open();

        // Close database.
        daoAccount.close();
    }

    /**
     * Attempts to sign in into the account specified by the login form.
     * If there are form errors (invalid username or password), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (userLoginTask != null) {
            return;
        }

        // Reset errors.
        etUsername.setError(null);
        etPassword.setError(null);

        // Store values at the time of the login attempt.
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {

            etPassword.setError(getString((R.string.error_field_required)));
            focusView = etPassword;
            cancel = true;
        } else if (!isPasswordValid(password)) {

            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {

            etUsername.setError(getString(R.string.error_field_required));
            focusView = etUsername;
            cancel = true;
        } else if (!isUsernameValid(username)) {

            etUsername.setError(getString(R.string.error_invalid_username));
            focusView = etUsername;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            userLoginTask = new UserLoginTask(username, password);
            userLoginTask.execute((Void) null);
        }
    }

    private boolean isUsernameValid(String username) {

        return username.length() > 0;
    }

    private boolean isPasswordValid(String password) {

        return password.length() > 0;
    }

    private void registerNewAccount() {

        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            vLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
            vLoginForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    vLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
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
            vLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
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


    /**
     * Represents an asynchronous login task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String strUsername;
        private final String strPassword;

        // Set up DAO factory.
        GeoBuddiesDAOFactory factory = new GeoBuddiesDAOFactory(getBaseContext());

        // Set up account database implementation.
        AccountDAO daoAccount;

        /**
         * Constructor
         * @param username - User name entered by user
         * @param password - Password entered by user
         */
        UserLoginTask(String username, String password) {

            strUsername = username;
            strPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            // Create account database implementation.
            daoAccount = factory.createAccountDAO();

            // Create user object and attempt to sign in
            GeoAccount user = daoAccount.signIn(strUsername, strPassword);

            // If user object is null, account doesn't exist
            // or username and password combination is wrong.
            return (user != null);
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            userLoginTask = null;
            showProgress(false);

            if (success) {
                toast("Succesfully loged in");

                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                startActivity(intent);
                finish();
            } else {
                etPassword.setError(getString(R.string.error_incorrect_password));
                etPassword.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            userLoginTask = null;
            showProgress(false);
        }

    }

    public void toast(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    } // toast(String msg)
}