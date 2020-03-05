package  com.example.easyride.ui.signup;

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
import com.example.easyride.ui.login.LoginActivity;
import com.example.easyride.ui.signup.SignupViewModel;
import com.example.easyride.ui.signup.SignupViewModelFactory;

public class SignupActivity extends AppCompatActivity {
  private boolean isRider;
  private SignupViewModel signupViewModel;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signup);
    signupViewModel = ViewModelProviders.of(this, new SignupViewModelFactory())
        .get(SignupViewModel.class);

    final EditText usernameEditText = findViewById(R.id.username);
    final EditText passwordEditText = findViewById(R.id.password);
    final EditText nameEditText = findViewById(R.id.firs_tname);
    final Button signupButton = findViewById(R.id.signup);
    final ProgressBar loadingProgressBar = findViewById(R.id.loading);
    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    final boolean isRider = extras.getBoolean("isRider");

    signupViewModel.getSignupFormState().observe(this, new Observer<SignupFormState>() {
      @Override
      public void onChanged(@Nullable SignupFormState signupFormState) {
        if (signupFormState == null) {
          return;
        }
        signupButton.setEnabled(signupFormState.isDataValid());
        if (signupFormState.getUsernameError() != null) {
          usernameEditText.setError(getString(signupFormState.getUsernameError()));
        }
        if (signupFormState.getPasswordError() != null) {
          passwordEditText.setError(getString(signupFormState.getPasswordError()));
        }
      }
    });

    signupViewModel.getSignupResult().observe(this, new Observer<SignupResult>() {
      @Override
      public void onChanged(@Nullable SignupResult signupResult) {
        if (signupResult == null) {
          return;
        }
        loadingProgressBar.setVisibility(View.GONE);
        if (signupResult.getError() != null) {
          showSignupFailed(signupResult.getError());
        }
        if (signupResult.getSuccess() != null) {
          updateUiWithUser(signupResult.getSuccess());
        }
        setResult(Activity.RESULT_OK);

        //Complete and destroy signup activity once successful
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
        signupViewModel.signupDataChanged(usernameEditText.getText().toString(),
            passwordEditText.getText().toString());
      }
    };
    usernameEditText.addTextChangedListener(afterTextChangedListener);
    passwordEditText.addTextChangedListener(afterTextChangedListener);
    passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
          signupViewModel.signup(usernameEditText.getText().toString(),
              passwordEditText.getText().toString(), nameEditText.getText().toString(), true);
        }
        return false;
      }
    });

    signupButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        loadingProgressBar.setVisibility(View.VISIBLE);
        signupViewModel.signup(usernameEditText.getText().toString(),
            passwordEditText.getText().toString(), nameEditText.getText().toString(), true);
      }
    });
  }

  private void updateUiWithUser(SignedUpUserView model) {
    String welcome = getString(R.string.welcome) + model.getDisplayName();
    // TODO : initiate successful logged in experience
    Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
    startActivity(intent);
  }

  private void showSignupFailed(@StringRes Integer errorString) {
    Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
  }
}
