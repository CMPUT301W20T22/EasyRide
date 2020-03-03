package com.example.easyride.data;

import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Driver;
import com.example.easyride.data.model.Rider;
import com.example.easyride.data.model.RideRequest;

/**
 * Driver class that captures user information for logged in users retrieved from LoginRepository
 * @author T22
 * @version 1.0
 * @see Driver
 * @see Rider
 * @see RideRequest
 */
public abstract class DataBaseManager {

    abstract Boolean SignupUser(EasyRideUser user);
    abstract Boolean AcceptRideRequest(RideRequest rideRequest);
    abstract void RideRequest();
    abstract void Update();
    abstract Boolean VerifyUser();
}
