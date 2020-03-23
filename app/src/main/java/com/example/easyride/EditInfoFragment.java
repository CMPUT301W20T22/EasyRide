package com.example.easyride;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Create a Dialog Fragment for the user to edit their contact information
// https://stackoverflow.com/questions/13257038/custom-layout-for-dialogfragment-oncreateview-vs-oncreatedialog/15602648

public class EditInfoFragment extends AppCompatDialogFragment {

    // widgets
    private EditText newEmail, newPhone;

    // interface
    private myListener mListener;

    // var
    private boolean PhoneValid = true;
    private boolean EmailValid = true;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_edit_info_fragment, null);

        newEmail = view.findViewById(R.id.editEmail);
        newPhone = view.findViewById(R.id.editPhone);


        // Update the Contact Information
        builder.setView(view);
        builder.setTitle("Update Contact Information");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mEmail = newEmail.getText().toString();
                String mPhone = newPhone.getText().toString();

                if (!(mEmail.isEmpty()) && !(Patterns.EMAIL_ADDRESS.matcher(mEmail).matches())) {
                    EmailValid = false;
                }

                if (0 < mPhone.length() && mPhone.length() < 10 || mPhone.length() > 10) {
                    PhoneValid = false;
                }

                if (EmailValid && PhoneValid) {
                    mListener.updateInfo(mEmail, mPhone);
                }
                else if (!EmailValid){
                    Toast.makeText(getActivity(), "The Email Address you entered is not in the correct format! Please update it again",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "The Phone Number you entered is not in the correct format! Please update it again",
                            Toast.LENGTH_SHORT).show();
                }
            }
            })
            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (myListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + e.getMessage());
        }
    }

    public interface myListener {
        void updateInfo(String email, String phone);
    }
}



