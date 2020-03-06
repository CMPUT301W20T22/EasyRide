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

/* So we tried to extends the DataBaseManager Class to FirebaseFireStore but it didn't work
   and we think that we don't need all this DbManager Classes in the Project. Whenever we need to access to
   the Firebase, we just create an instance of it and fetch the data from the FireBase.
 */
public abstract class DataBaseManager {

    abstract Boolean SignupUser(EasyRideUser user);
    abstract Boolean AcceptRideRequest(RideRequest rideRequest);
    abstract void RideRequest();
    abstract void Update();
    abstract Boolean VerifyUser();
}
