package com.example.easyride.ui.driver;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.easyride.R;

/**
 * Set up RiderProfileFragment for rider using DialogFragment from Android library.
 * @author T22
 * @version 1.0
 */
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


        String emailLink = "mailto:" + mEmail;
        String phoneLink = "tel:" + mPhone;

        int emailLen = mEmail.length();
        int phoneLen = mPhone.length();

        SpannableString riderSpanEmail = new SpannableString(mEmail);
        riderSpanEmail.setSpan(new URLSpan(emailLink), 0, emailLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString riderSpanPhone = new SpannableString(mPhone);
        riderSpanPhone.setSpan(new URLSpan(phoneLink), 0, phoneLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        getDialog().setTitle("Rider Profile");

        User.setText(mUser);
        Email.setText(riderSpanEmail);
        Phone.setText(riderSpanPhone);

        Email.setMovementMethod(LinkMovementMethod.getInstance());
        Phone.setMovementMethod(LinkMovementMethod.getInstance());

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
