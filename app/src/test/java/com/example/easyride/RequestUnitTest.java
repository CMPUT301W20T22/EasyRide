package com.example.easyride;

import com.example.easyride.ui.driver.RideRequest;
import com.example.easyride.ui.rider.Ride;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * RideRequest Unit Test
 */
public class RequestUnitTest {

    private RideRequest testRequest() {
        return new RideRequest("abc123", "test_user", "my_place", "goto_place", "100", false, false);

    }
    @Test
    public void createRequestTest() {
        final RideRequest testRequest = testRequest();
        assertEquals(testRequest.getKey(), "abc123");
        assertEquals(testRequest.getRiderUserName(), "test_user");
        assertEquals(testRequest.getPickupPoint(),"my_place");
        assertEquals(testRequest.getTargetPoint(),"goto_place");
        assertEquals(testRequest.getCost(),"100");
        assertFalse(testRequest.isRideAccepted());
        assertFalse(testRequest.isRideCompleted());
    }

    @Test
    public void RequestSetterTest() {
        final RideRequest testRequest = testRequest();

        testRequest.setKey("newkey");
        testRequest.setRiderUserName("newuser");
        testRequest.setPickupPoint("newloc");
        testRequest.setTargetPoint("newdest");
        testRequest.setCost("1000");
        testRequest.setRideAccepted(true);
        testRequest.setRideCompleted(true);

        assertEquals(testRequest.getKey(), "newkey");
        assertEquals(testRequest.getRiderUserName(), "newuser");
        assertEquals(testRequest.getPickupPoint(),"newloc");
        assertEquals(testRequest.getTargetPoint(),"newdest");
        assertEquals(testRequest.getCost(),"1000");
        assertTrue(testRequest.isRideAccepted());
        assertTrue(testRequest.isRideCompleted());


    }


}
