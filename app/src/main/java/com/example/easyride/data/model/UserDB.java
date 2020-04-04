package com.example.easyride.data.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.Objects;

import static com.android.volley.VolleyLog.TAG;

public class UserDB {

  private UserType userType;
  private String email;
  private FirebaseFirestore db;
  private Double balance;
  private String userId;
  private String phone;
  private String name;
  private int goodReviews;
  private int badReviews;


  public UserDB(UserType userType, String email) {
    this.userType = userType;
    this.email = email;
    db = FirebaseFirestore.getInstance();
    Query q = db.collection(userType.getType()).whereEqualTo("Email", email);
    q.addSnapshotListener(new EventListener<QuerySnapshot>() {
      @Override
      public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
          Log.w(TAG, "Listen failed.", e);
          return;
        }
        fetch();
      }
    });
    fetch();
  }

  public void fetch() {
    db.collection(userType.getType()).whereEqualTo("Email", email)
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
          @Override
          public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
              for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                Map data = doc.getData();
                name = data.get("Name").toString();
                phone = data.get("Phone").toString();
//              email = doc.getString("Email");
                userId = doc.getId();
                balance = Double.parseDouble(data.get("Balance").toString());
                if (userType == UserType.DRIVER) {
                  Integer.parseInt(data.get("good_reviews").toString());
                  goodReviews = Integer.parseInt(data.get("good_reviews").toString());
                  badReviews = Integer.parseInt(data.get("bad_reviews").toString());
                }
                Log.d("Document", userId + " => " + data);
                userDataLoaded();
              }
            }
          }
        });
  }

  public void push() {
    DocumentReference rideRequestRef = db.collection(userType.getType()).document(userId);
    Task<Void> updateTask;
    if (userType == UserType.RIDER)
      updateTask = rideRequestRef.update("Balance", Double.toString(balance),
          "Phone", phone, "Name", name);
    else
      updateTask = rideRequestRef.update("Balance", Double.toString(balance),
          "Phone", phone, "Name", name,
          "good_reviews", Integer.toString(goodReviews),
          "bad_reviews", Integer.toString(badReviews));
  }

  public void userDataLoaded() {
  }

  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getGoodReviews() {
    return goodReviews;
  }

  public void setGoodReviews(int goodReviews) {
    this.goodReviews = goodReviews;
  }

  public int getBadReviews() {
    return badReviews;
  }

  public void setBadReviews(int badReviews) {
    this.badReviews = badReviews;
  }

  public String getEmail() {
    return email;
  }

  public void increaseBalance(Double valueOf) {
    setBalance(valueOf + getBalance());
  }

  public void decreaseBalance(Double valueOf) {
    setBalance(getBalance() - valueOf);
  }

  public void updateReviews(Long rating) {
    if (userType == UserType.DRIVER) {
      if (rating > 0)
        setGoodReviews(getGoodReviews() + 1);
      else
        setBadReviews(getBadReviews() + 1);
    }
  }
  public UserType getUserType() {
    return userType;
  }
}
