package com.example.easyride;

import com.example.easyride.data.DataBaseManager;
import com.example.easyride.data.DataManager;

import org.junit.Test;
import static org.junit.Assert.*;

public class LoginUnitTest {

    @Test
    public void testIsUser(){
        String driver = "jaysinh";
        DataManager database = new DataManager();
        assertTrue(database.isDriver(driver));
    }
}
