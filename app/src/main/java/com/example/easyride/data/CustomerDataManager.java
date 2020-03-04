package com.example.easyride.data;

import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.RideRequest;

public class CustomerDataManager extends DataBaseManager{

    Boolean SignupUser(EasyRideUser user){

        return true;
    }

    Boolean AcceptRideRequest(RideRequest rideRequest){
        return true;
    }
    void RideRequest(){

    }
    void Update(){

    }
    Boolean VerifyUser(){
        return true;
    }
}



