package com.miracle.rajdhani.activities;//package com.miracle.dronam.activities;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.DefaultItemAnimator;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.miracle.dronam.R;
//import com.miracle.dronam.adapter.RecycleAdapterRestaurant;
//import com.miracle.dronam.listeners.OnRecyclerViewClickListener;
//import com.miracle.dronam.model.RestaurantObject;
//import com.miracle.dronam.service.retrofit.ApiInterface;
//import com.miracle.dronam.service.retrofit.RetroClient;
//import com.miracle.dronam.utils.InternetConnection;
//import com.google.android.material.snackbar.Snackbar;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class SeeMoreActivity extends AppCompatActivity implements OnRecyclerViewClickListener {
//    RelativeLayout rlRootLayout;
//    private RecyclerView rvSeeMoreRestaurant;
//    private RecycleAdapterRestaurant adapterRestaurant;
//
//    private ArrayList<RestaurantObject> listRestaurantObject;
//
//    Integer image2[] ={R.mipmap.temp_img1, R.mipmap.temp_img2, R.mipmap.temp_img3,
//            R.mipmap.temp_img4, R.mipmap.temp_img5, R.mipmap.temp_img6 , R.mipmap.temp_img7};
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_see_more);
//
//        initComponents();
//        setupRecyclerViewRestaurant();
//    }
//
//    private void initComponents() {
//        rlRootLayout = findViewById(R.id.rl_rootLayout);
//        rvSeeMoreRestaurant = (RecyclerView) findViewById(R.id.recyclerView_seeMore);
//    }
//
//    private void setupRecyclerViewRestaurant()
//    {
//        adapterRestaurant = new RecycleAdapterRestaurant(this, listRestaurantObject);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        rvSeeMoreRestaurant.setLayoutManager(layoutManager);
//        rvSeeMoreRestaurant.setItemAnimator(new DefaultItemAnimator());
//        rvSeeMoreRestaurant.setAdapter(adapterRestaurant);
//        adapterRestaurant.setClickListener(this);
//    }
//
//
//    public void showSnackbarErrorMsg(String erroMsg) {
////        Snackbar.make(fragmentRootView, erroMsg, Snackbar.LENGTH_LONG).show();
//
//        Snackbar snackbar = Snackbar.make(rlRootLayout, erroMsg, Snackbar.LENGTH_LONG);
//        View snackbarView = snackbar.getView();
//        TextView snackTextView = (TextView) snackbarView
//                .findViewById(R.id.snackbar_text);
//        snackTextView.setMaxLines(4);
//        snackbar.show();
//    }
//
//    private void getRestaurantData() {
//        if (InternetConnection.checkConnection(this)) {
//
//            ApiInterface apiService = RetroClient.getApiService(this);
////            Call<ResponseBody> call = apiService.getUserDetails(createJsonUserDetails());
//            Call<ResponseBody> call = apiService.getRestaurantDetails("416004");
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//                    try {
//                        int statusCode = response.code();
//
//                        if (response.isSuccessful()) {
//                            String responseString = response.body().string();
//                            listRestaurantObject = new ArrayList<>();
//
//                            JSONArray jsonArray = new JSONArray(responseString);
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject jsonObj = jsonArray.getJSONObject(i);
//
//                                int categoryID = jsonObj.optInt("CategoryId");
//                                String categoryName = jsonObj.optString("CategoryName");
//                                int restaurantID = jsonObj.optInt("ClientId");
//                                String restaurantName = jsonObj.optString("RestaurantName");
//                                String restaurantAddress = jsonObj.optString("ClientAddress");
//                                String openTime = jsonObj.optString("OpentTime");
//                                String closeTime = jsonObj.optString("CloseTime");
//                                String contact = jsonObj.optString("Contact");
//                                String description = jsonObj.optString("Description");
//                                String longitude = jsonObj.optString("Langitude");
//                                String latitude = jsonObj.optString("Latitude");
//                                String rating = jsonObj.optString("Rating");
//                                int foodTypeID = jsonObj.optInt("FoodTypeId");
//                                String foodTypeName = jsonObj.optString("FoodTypeName");
//                                String logo = jsonObj.optString("Logo");
//                                String taxID = jsonObj.optString("TaxId");
//                                boolean taxable = Boolean.parseBoolean(jsonObj.optString("Taxable"));
//                                boolean includeTax = Boolean.parseBoolean(jsonObj.optString("IncludeTax"));
//
//                                RestaurantObject restaurantObject = new RestaurantObject();
//                                restaurantObject.setCategoryID(categoryID);
//                                restaurantObject.setCategoryName(categoryName);
//                                restaurantObject.setRestaurantID(restaurantID);
//                                restaurantObject.setRestaurantName(restaurantName);
//                                restaurantObject.setRestaurantAddress(restaurantAddress);
//                                restaurantObject.setOpenTime(openTime);
//                                restaurantObject.setCloseTime(closeTime);
//                                restaurantObject.setContact(contact);
//                                restaurantObject.setDescription(description);
//                                restaurantObject.setLongitude(longitude);
//                                restaurantObject.setLatitude(latitude);
//                                restaurantObject.setRating(rating);
//                                restaurantObject.setFoodTypeID(foodTypeID);
//                                restaurantObject.setFoodTypeName(foodTypeName);
//                                restaurantObject.setLogo(logo);
//                                restaurantObject.setTaxID(taxID);
//                                restaurantObject.setTaxable(taxable);
//                                restaurantObject.setIncludeTax(includeTax);
//
//                                listRestaurantObject.add(restaurantObject);
//                            }
//
//                        } else {
//                            showSnackbarErrorMsg(getResources().getString(R.string.something_went_wrong));
//                        }
//
//                        setupRecyclerViewRestaurant();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    try {
//                        showSnackbarErrorMsg(getResources().getString(R.string.server_conn_lost));
//                        Log.e("Error onFailure : ", t.toString());
//                        t.printStackTrace();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        } else {
////            signOutFirebaseAccounts();
//
//            Snackbar.make(rlRootLayout, getResources().getString(R.string.no_internet),
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setAction("RETRY", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            getRestaurantData();
//                        }
//                    })
////                    .setActionTextColor(getResources().getColor(R.color.colorSnackbarButtonText))
//                    .show();
//        }
//    }
//
//
//    @Override
//    public void onClick(View view, int position) {
//        Intent intent = new Intent(this, RestaurantDetailsActivity.class);
//        startActivity(intent);
//    }
//}
