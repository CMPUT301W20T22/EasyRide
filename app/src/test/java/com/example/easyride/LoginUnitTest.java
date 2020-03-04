package com.example.easyride;

import com.example.easyride.data.DataBaseManager;
import com.example.easyride.data.DataManager;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import static org.junit.Assert.*;

public class LoginUnitTest {

    @Test
    public void testIsUser(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String driver = "jaysinh";
        DataManager database = new DataManager();
        assertTrue(database.isDriver(driver));
    }
}
