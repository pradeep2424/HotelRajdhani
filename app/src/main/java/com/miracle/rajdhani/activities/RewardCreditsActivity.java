package com.miracle.rajdhani.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.miracle.rajdhani.R;
import com.miracle.rajdhani.adapter.RecycleAdapterReferralPoints;
import com.miracle.rajdhani.dialog.DialogLoadingIndicator;
import com.miracle.rajdhani.model.ReferralDetails;
import com.miracle.rajdhani.service.retrofit.ApiInterface;
import com.miracle.rajdhani.service.retrofit.RetroClient;
import com.miracle.rajdhani.utils.Application;
import com.miracle.rajdhani.utils.InternetConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RewardCreditsActivity extends AppCompatActivity {
    private DialogLoadingIndicator progressIndicator;

    private LinearLayout llRootLayout;

    View viewToolbar;
    TextView tvToolbarTitle;
    ImageView ivBack;

    private LinearLayout llReferredUsers;
    private TextView tvBalanceReferralPoints;
    private RecyclerView rvReferralPoints;
    private RecycleAdapterReferralPoints adapterReferralPoints;

    private ArrayList<ReferralDetails> listReferralDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_credits);

        initComponents();
        componentEvents();
        getReferralDetails();

//        setupRecyclerViewReferralPoints();

    }

    private void initComponents() {
        progressIndicator = DialogLoadingIndicator.getInstance();
        llRootLayout = findViewById(R.id.rl_rootLayout);

        viewToolbar = findViewById(R.id.view_toolbarRewardCredits);
        ivBack = viewToolbar.findViewById(R.id.iv_back);
        tvToolbarTitle = findViewById(R.id.tv_toolbarTitle);
        tvToolbarTitle.setText(getString(R.string.referral_credits));

        llReferredUsers = findViewById(R.id.ll_referredUsersLayout);
        tvBalanceReferralPoints = findViewById(R.id.tv_balanceReferralPoints);
        rvReferralPoints = findViewById(R.id.rv_referredUsers);

        double totalReferralPoints = Application.userDetails.getTotalReferralPoints();
        String formattedPoints = getFormattedNumberDouble(totalReferralPoints);
        tvBalanceReferralPoints.setText(formattedPoints + " " + getString(R.string.rupees));
    }

    private void componentEvents() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupRecyclerViewReferralPoints() {
        llReferredUsers.setVisibility(View.VISIBLE);
//        getDummyReferralData();

        adapterReferralPoints = new RecycleAdapterReferralPoints(this, listReferralDetails);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(this);
        rvReferralPoints.setLayoutManager(mLayoutManager1);
        rvReferralPoints.setItemAnimator(new DefaultItemAnimator());
        rvReferralPoints.setAdapter(adapterReferralPoints);
    }

    private void getDummyReferralData() {
        listReferralDetails = new ArrayList<>();

        ReferralDetails referralDetails1 = new ReferralDetails();
        referralDetails1.setFirstName("Pradeep");
        referralDetails1.setLastName("Jadhav");
        referralDetails1.setTotalAmount(80);

        ReferralDetails referralDetails2 = new ReferralDetails();
        referralDetails2.setFirstName("Rahul");
        referralDetails2.setLastName("Patil");
        referralDetails2.setTotalAmount(50);

        ReferralDetails referralDetails3 = new ReferralDetails();
        referralDetails3.setFirstName("Tushar");
        referralDetails3.setLastName("Dalvi");
        referralDetails3.setTotalAmount(70);

        listReferralDetails.add(referralDetails1);
        listReferralDetails.add(referralDetails2);
        listReferralDetails.add(referralDetails3);


//        for (int i = 0; i < 10; i++) {
//            Random random = new Random();
//            int amount = random.nextInt(900) + 100;
//
//            ReferralDetails referralDetails = new ReferralDetails();
//            referralDetails.setFirstName("FName");
//            referralDetails.setLastName("LName");
//            referralDetails.setTotalAmount(amount);
//
//            listReferralDetails.add(referralDetails);
//        }
    }

    private String getFormattedNumberDouble(double amount) {
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }

    private void getReferralDetails() {
        if (InternetConnection.checkConnection(this)) {
            showDialog();

            int userTypeID = Application.userDetails.getUserID();

            ApiInterface apiService = RetroClient.getApiService(this);
//            Call<ResponseBody> call = apiService.getReferralDetails(1);
            Call<ResponseBody> call = apiService.getReferralDetails(userTypeID);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        int statusCode = response.code();

                        if (response.isSuccessful()) {
                            String responseString = response.body().string();
                            listReferralDetails = new ArrayList<>();

                            JSONArray jsonArray = new JSONArray(responseString);
                            if (jsonArray != null) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObj = jsonArray.getJSONObject(i);

                                    String firstName = jsonObj.optString("FirstName");
                                    String lastName = jsonObj.optString("LastName");
                                    int newUserID = jsonObj.optInt("NewUserID");
                                    int pointID = jsonObj.optInt("PointID");
                                    double totalAmount = jsonObj.optDouble("TotalAmount");

                                    ReferralDetails referralDetails = new ReferralDetails();
                                    referralDetails.setFirstName(firstName);
                                    referralDetails.setLastName(lastName);
                                    referralDetails.setNewUserID(newUserID);
                                    referralDetails.setReferralID(pointID);
                                    referralDetails.setTotalAmount(totalAmount);
                                    listReferralDetails.add(referralDetails);
                                }

                                setupRecyclerViewReferralPoints();

                            } else {
                                llReferredUsers.setVisibility(View.GONE);
                            }

                        } else {
                            showSnackBarErrorMsg(getResources().getString(R.string.something_went_wrong));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dismissDialog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    try {
                        dismissDialog();
                        showSnackBarErrorMsg(getResources().getString(R.string.server_conn_lost));
                        Log.e("Error onFailure : ", t.toString());
                        t.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
//            signOutFirebaseAccounts();

            Snackbar.make(llRootLayout, getResources().getString(R.string.no_internet),
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getReferralDetails();
                        }
                    })
//                    .setActionTextColor(getResources().getColor(R.color.colorSnackbarButtonText))
                    .show();
        }
    }

    public void showSnackBarErrorMsg(String erroMsg) {
//        Snackbar.make(fragmentRootView, erroMsg, Snackbar.LENGTH_LONG).show();

        Snackbar snackbar = Snackbar.make(llRootLayout, erroMsg, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        TextView snackTextView = (TextView) snackbarView
                .findViewById(R.id.snackbar_text);
        snackTextView.setMaxLines(4);
        snackbar.show();
    }

    public void showSnackBarErrorMsgWithButton(String erroMsg) {
        Snackbar.make(llRootLayout, erroMsg, Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.colorAccent))
                .show();
    }

    public void showDialog() {
        progressIndicator.showProgress(RewardCreditsActivity.this);
    }

    public void dismissDialog() {
        if (progressIndicator != null) {
            progressIndicator.hideProgress();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
