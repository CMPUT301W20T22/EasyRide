package  com.example.easyride.ui.login;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easyride.MainActivity;
import com.example.easyride.R;
import com.example.easyride.map.MapsActivity;
import com.example.easyride.ui.signup.SignupActivity;

public class LoginActivity extends AppCompatActivity {

  private LoginViewModel loginViewModel;
  private boolean isRider;
  private String mode;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    Intent intent = getIntent();
//    Bundle extras = intent.getExtras();
//    final boolean isRider = extras.getBoolean("isRider");
    mode = intent.getStringExtra(MainActivity.mode);
    if(mode=="rider")
      isRider=true;
    else
      isRider=false;

    System.out.println(isRider);
    loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
        .get(LoginViewModel.class);

    final EditText usernameEditText = findViewById(R.id.username);
    final EditText passwordEditText = findViewById(R.id.password);
    final Button loginButton = findViewById(R.id.login);
    final Button signupButton = findViewById(R.id.signup);
    final ProgressBar loadingProgressBar = findViewById(R.id.loading);

    loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
      @Override
      public void onChanged(@Nullable LoginFormState loginFormState) {
        if (loginFormState == null) {
          return;
        }
        loginButton.setEnabled(loginFormState.isDataValid());
        if (loginFormState.getUsernameError() != null) {
          usernameEditText.setError(getString(loginFormState.getUsernameError()));
        }
        if (loginFormState.getPasswordError() != null) {
          passwordEditText.setError(getString(loginFormState.getPasswordError()));
        }
      }
    });

    loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
      @Override
      public void onChanged(@Nullable LoginResult loginResult) {
        if (loginResult == null) {
          return;
        }
        loadingProgressBar.setVisibility(View.GONE);
        if (loginResult.getError() != null) {
          showLoginFailed(loginResult.getError());
        }
        if (loginResult.getSuccess() != null) {
          updateUiWithUser(loginResult.getSuccess());
        }
        setResult(Activity.RESULT_OK);

        //Complete and destroy login activity once successful
        finish();
      }
    });

    TextWatcher afterTextChangedListener = new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // ignore
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        // ignore
      }

      @Override
      public void afterTextChanged(Editable s) {
        loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
            passwordEditText.getText().toString());
      }
    };
    usernameEditText.addTextChangedListener(afterTextChangedListener);
    passwordEditText.addTextChangedListener(afterTextChangedListener);
    passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
          loginViewModel.login(usernameEditText.getText().toString(),
              passwordEditText.getText().toString(), isRider);
        }
        return false;
      }
    });

    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        loadingProgressBar.setVisibility(View.VISIBLE);
        loginViewModel.login(usernameEditText.getText().toString(),
                passwordEditText.getText().toString(), isRider);

        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);

      }
    });

    signupButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(i);
      }
    });

  }

  private void updateUiWithUser(LoggedInUserView model) {
    String welcome = getString(R.string.welcome) + model.getDisplayName();
    // TODO : initiate successful logged in experience
    Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
    startActivity(intent);
  }

  private void showLoginFailed(@StringRes Integer errorString) {
    Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
  }
}
