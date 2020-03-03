package com.example.easyride.data;

import com.example.easyride.data.model.Driver;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.RideRequest;

import java.util.ArrayList;




public class DriverDataManager extends DataBaseManager{

    private Driver driver;
    ArrayList<RideRequest> activeRequests;

    Boolean SignupUser(EasyRideUser user){

       return true;
    }

    Boolean AcceptRideRequest(RideRequest rideRequest){
        return true;
    }
    void RideRequest(){

    }



    Driver getDriver(){

    }
    void Update(){

    }
    Boolean VerifyUser(){
        return true;
    }
}
