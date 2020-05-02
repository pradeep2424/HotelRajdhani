package com.miracle.rajdhani.utils;

import com.miracle.rajdhani.model.CartObject;
import com.miracle.rajdhani.model.DishObject;
import com.miracle.rajdhani.model.OrderDetailsObject;
import com.miracle.rajdhani.model.RestaurantObject;
import com.miracle.rajdhani.model.SMSGatewayObject;
import com.miracle.rajdhani.model.UserDetails;
import com.sucho.placepicker.AddressData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pradeep Jadhav on 23/10/2019.
 */

public class Application extends android.app.Application {
    private static Application mInstance;

    public static UserDetails userDetails = new UserDetails();
    public static RestaurantObject restaurantObject = new RestaurantObject();
    public static DishObject dishObject = new DishObject();
//    public static CartObject cartObject = new CartObject();
    public static ArrayList<CartObject> listCartItems = new ArrayList();
    public static OrderDetailsObject orderDetailsObject = new OrderDetailsObject();

//    public static AddressDetails addressDetails = new AddressDetails();
    public static SMSGatewayObject smsGatewayObject = new SMSGatewayObject();
    public static AddressData locationAddressData;
    public static int MINIMUM_FREE_DELIVERY_AMOUNT;

    public static HashMap<String, String> mapBannerDetails = new HashMap<>();

    public Application getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

//        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
//        appSignatureHelper.getAppSignatures();
    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }

}

