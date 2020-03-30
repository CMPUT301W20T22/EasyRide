package com.example.easyride.ui.driver;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.easyride.R;

// Set up RiderProfileFragment for rider
// Using DialogFragment from Android library

public class RiderProfileFragment extends DialogFragment {

    // widgets
    private TextView User, Email, Phone;
    private TextView mActionOk;

    // String
    private String mUser, mEmail, mPhone;

    public static RiderProfileFragment newInstance(String user, String email, String phone) {
        RiderProfileFragment dialog = new RiderProfileFragment();

        Bundle args = new Bundle();
        args.putString("user", user);
        args.putString("email", email);
        args.putString("phone", phone);

        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo_Light_Dialog;
        setStyle(style, theme);

        mUser = getArguments().getString("user");
        mEmail = getArguments().getString("email");
        mPhone = getArguments().getString("phone");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rider_fragment, container, false);
        User = view.findViewById(R.id.Rider);
        Email = view.findViewById(R.id.riderEmail);
        Phone = view.findViewById(R.id.riderPhone);
        mActionOk = view.findViewById(R.id.Ok);

        getDialog().setTitle("Rider Profile");

        User.setText(mUser);
        Email.setText(mEmail);
        Phone.setText("");

        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}
