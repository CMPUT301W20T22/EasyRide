package com.example.easyride;

import com.example.easyride.data.model.Ride;
import com.google.firebase.firestore.GeoPoint;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class RideUnitTest {

    private Ride TestRide() {
        return new Ride("origin", "destination", "1", "test_user", "10", null, null);
    }

    /**
     * test if ride can be created
     */

    @Test
    public void createRideTest() {
        final Ride testRide = TestRide();
        assertEquals(testRide.getFrom(), "origin");
        assertEquals(testRide.getTo(), "destination");
        assertEquals(testRide.getCost(), "1");
        assertEquals(testRide.getUser(), "test_user");
        assertEquals(testRide.getDistance(), "10");
    }

    /**
     * test if ride can be modified
     */
    @Test
    public void rideSetterTest() {
        final Ride testRide = TestRide();

        testRide.setFrom("start");
        testRide.setTo("finish");
        testRide.setCost("2");
        testRide.setUser("");
        testRide.setDistance("20");

        assertEquals(testRide.getFrom(), "start");
        assertEquals(testRide.getTo(), "finish");
        assertEquals(testRide.getCost(), "2");
        assertEquals(testRide.getUser(), "");
        assertEquals(testRide.getDistance(), "20");

        assertFalse(testRide.isRideAccepted());
        assertFalse(testRide.isRideCompleted());
        assertFalse(testRide.isRideConfirmAccepted());
        assertFalse(testRide.isRidePaid());

        testRide.setRidePaid(true);
        testRide.setRideConfirmAccepted(true);
        testRide.setRidePaid(true);
        testRide.setRideCompleted(true);

        assertTrue(testRide.isRideCompleted());
        assertTrue((testRide.isRidePaid()));
        assertTrue(testRide.isRideConfirmAccepted());
        assertTrue(testRide.isRidePaid());

    }








}
