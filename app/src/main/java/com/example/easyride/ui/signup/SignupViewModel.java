package com.example.easyride.ui.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Patterns;

import com.example.easyride.data.SignupRepository;
import com.example.easyride.data.Result;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.R;

public class SignupViewModel extends ViewModel {

    private MutableLiveData<SignupFormState> signupFormState = new MutableLiveData<>();
    private MutableLiveData<SignupResult> signupResult = new MutableLiveData<>();
    private SignupRepository signupRepository;

    SignupViewModel(SignupRepository signupRepository) {
        this.signupRepository = signupRepository;
    }

    LiveData<SignupFormState> getSignupFormState() {
        return signupFormState;
    }

    LiveData<SignupResult> getSignupResult() {
        return signupResult;
    }

    public void signup(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<EasyRideUser> result = signupRepository.signup(username, password);

        if (result instanceof Result.Success) {
            EasyRideUser data = ((Result.Success<EasyRideUser>) result).getData();
            signupResult.setValue(new SignupResult(new SignedUpUserView(data.getDisplayName())));
        } else {
            signupResult.setValue(new SignupResult(R.string.signup_failed));
        }
    }

    public void signupDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            signupFormState.setValue(new SignupFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            signupFormState.setValue(new SignupFormState(null, R.string.invalid_password));
        } else {
            signupFormState.setValue(new SignupFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
