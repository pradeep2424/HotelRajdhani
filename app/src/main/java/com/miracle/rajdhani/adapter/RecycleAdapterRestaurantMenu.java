package com.miracle.rajdhani.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.miracle.rajdhani.R;
import com.miracle.rajdhani.activities.RestaurantDetailsActivity;
import com.miracle.rajdhani.dialog.DialogLoadingIndicator;
import com.miracle.rajdhani.listeners.OnItemAddedToCart;
import com.miracle.rajdhani.model.DishObject;
import com.miracle.rajdhani.model.RestaurantObject;
import com.miracle.rajdhani.service.retrofit.ApiInterface;
import com.miracle.rajdhani.service.retrofit.RetroClient;
import com.miracle.rajdhani.utils.Application;
import com.miracle.rajdhani.utils.InternetConnection;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;
import com.travijuu.numberpicker.library.NumberPicker;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecycleAdapterRestaurantMenu extends RecyclerView.Adapter<RecycleAdapterRestaurantMenu.ViewHolder> {
    DialogLoadingIndicator progressIndicator;

    RestaurantDetailsActivity activity;
    private ArrayList<DishObject> modelArrayList;

    private OnItemAddedToCart onItemAddedToCart;

//    private ViewHolder viewHolderClickedItem;

    public RecycleAdapterRestaurantMenu(RestaurantDetailsActivity activity, ArrayList<DishObject> modelArrayList) {
        this.activity = activity;
        this.modelArrayList = modelArrayList;

        progressIndicator = DialogLoadingIndicator.getInstance();
    }

    public void setOnItemAddedToCart(OnItemAddedToCart onItemAddedToCart) {
        this.onItemAddedToCart = onItemAddedToCart;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoodType;
        TextView tvFoodName;
        TextView tvFoodCategory;
        TextView tvFoodPrice;

        LinearLayout llAddItem;
        NumberPicker numberPickerItemQuantity;

        public ViewHolder(View itemView) {
            super(itemView);
            ivFoodType = itemView.findViewById(R.id.iv_foodType);
            tvFoodName = itemView.findViewById(R.id.tv_foodName);
            tvFoodCategory = itemView.findViewById(R.id.tv_foodCategory);
            tvFoodPrice = itemView.findViewById(R.id.tv_foodPrice);

            llAddItem = itemView.findViewById(R.id.ll_addButton);
            numberPickerItemQuantity = itemView.findViewById(R.id.numberPicker_quantity);
        }
    }

    @Override
    public RecycleAdapterRestaurantMenu.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_restaurant_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final DishObject dishObject = modelArrayList.get(position);
        String foodType = dishObject.getFoodType();

        if (foodType != null && foodType.equalsIgnoreCase("Veg")) {
            holder.ivFoodType.setImageResource(R.mipmap.ic_veg);

        } else if(foodType != null && foodType.equalsIgnoreCase("NonVeg")) {
            holder.ivFoodType.setImageResource(R.mipmap.ic_nonveg);
        }

        holder.tvFoodName.setText(dishObject.getProductName());
        holder.tvFoodCategory.setText(dishObject.getCategoryName());
        holder.tvFoodPrice.setText("₹ " + dishObject.getPrice());

        holder.llAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                viewHolderClickedItem = holder;

                if (onItemAddedToCart != null) {
                    addItemOrUpdateQuantity(holder, 1, position, ActionEnum.INCREMENT.toString());
                }
            }
        });

        holder.numberPickerItemQuantity.setValueChangedListener(new ValueChangedListener() {
            @Override
            public void valueChanged(int value, ActionEnum action) {
//                String actionText = action == ActionEnum.MANUAL ? "manually set" : (action == ActionEnum.INCREMENT ? "incremented" : "decremented");
                String actionText = action == ActionEnum.MANUAL ? "manually set" : (action == ActionEnum.INCREMENT ?
                        ActionEnum.INCREMENT.toString() : ActionEnum.DECREMENT.toString());
                String message = String.format("NumberPicker is %s to %d", actionText, value);

                addItemOrUpdateQuantity(holder, value, position, actionText);

            }
        });

    }

//    public void showHideQuantityAndAddItemButton(ViewHolder holder, int quantity) {
////        if (quantity == 0) {
//////            showAddItemButton();
////
//////            show Add Item Button
////            holder.numberPickerItemQuantity.setVisibility(View.GONE);
////            holder.llAddItem.setVisibility(View.VISIBLE);
////
////        } else {
//////            showItemQuantityPicker();
////
//////            show number picker
////            holder.numberPickerItemQuantity.setVisibility(View.VISIBLE);
////            holder.llAddItem.setVisibility(View.GONE);
////        }
////    }

    public void showAddItemButton(ViewHolder holder) {
        holder.numberPickerItemQuantity.setVisibility(View.GONE);
        holder.llAddItem.setVisibility(View.VISIBLE);
    }

    public void hideAddItemButton(ViewHolder holder) {
//            show number picker
        holder.numberPickerItemQuantity.setVisibility(View.VISIBLE);
        holder.llAddItem.setVisibility(View.GONE);
    }

//    private void showAddItemButton(ViewHolder holder) {
//        viewHolderClickedItem.numberPickerItemQuantity.setVisibility(View.GONE);
//        viewHolderClickedItem.llAddItem.setVisibility(View.VISIBLE);
//    }
//
//    private void showItemQuantityPicker(ViewHolder holder) {
//        viewHolderClickedItem.numberPickerItemQuantity.setVisibility(View.VISIBLE);
//        viewHolderClickedItem.llAddItem.setVisibility(View.GONE);
//    }

    private void addItemOrUpdateQuantity(ViewHolder holder, final int quantity, final int position, final String incrementOrDecrement) {
        holder.numberPickerItemQuantity.setValue(quantity);
        if (quantity == 0) {
            showAddItemButton(holder);

        } else {
            hideAddItemButton(holder);
        }

        String mobileNo = Application.userDetails.getMobile();
        if (mobileNo != null) {
            final DishObject dishObject = modelArrayList.get(position);
            addItemToCart(dishObject, quantity, position, incrementOrDecrement);

        } else {
            successOnAddToCart(quantity, position, incrementOrDecrement);
        }
    }

    private JsonObject createJsonCart(DishObject dishObject, int quantity) {
        double totalPrice;

        RestaurantObject restaurantObject = Application.restaurantObject;

        if (restaurantObject.getTaxable()) {
            double productPrice = dishObject.getPrice();
            double cgst = dishObject.getCgst();
            double sgst = dishObject.getCgst();

//            totalPrice = productPrice * ()
        }

        JsonObject postParam = new JsonObject();

        try {
            postParam.addProperty("ProductId", dishObject.getProductID());
            postParam.addProperty("ProductName", dishObject.getProductName());
            postParam.addProperty("ProductRate", dishObject.getPrice());
            postParam.addProperty("ProductAmount", dishObject.getPrice());
            postParam.addProperty("ProductSize", "Regular");
            postParam.addProperty("cartId", 0);
            postParam.addProperty("ProductQnty", quantity);
            postParam.addProperty("Taxableval", dishObject.getPrice());    // doubt
            postParam.addProperty("CGST", dishObject.getCgst());
            postParam.addProperty("SGST", dishObject.getSgst());
            postParam.addProperty("HotelName", restaurantObject.getRestaurantName());
            postParam.addProperty("IsIncludeTax", restaurantObject.getIncludeTax());
            postParam.addProperty("IsTaxApplicable", restaurantObject.getTaxable());
            postParam.addProperty("DeliveryCharge", 30.00);
            postParam.addProperty("Userid", Application.userDetails.getUserID());
            postParam.addProperty("Clientid", restaurantObject.getRestaurantID());
            postParam.addProperty("TotalAmount", dishObject.getPrice());
            postParam.addProperty("TaxId", 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return postParam;
    }

    public void addItemToCart(final DishObject dishObject, final int quantity, final int position, final String incrementOrDecrement) {
        if (InternetConnection.checkConnection(activity)) {
            showDialog();

//            holder.numberPickerItemQuantity.setValue(quantity);
//            if (quantity == 0) {
//                showAddItemButton(holder);
//
//            } else {
//                hideAddItemButton(holder);
//            }
//
//            final DishObject dishObject = modelArrayList.get(position);


            ApiInterface apiService = RetroClient.getApiService(activity);
            Call<ResponseBody> call = apiService.addItemToCart(createJsonCart(dishObject, quantity));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        int statusCode = response.code();

                        if (response.isSuccessful()) {
                            String responseString = response.body().string();

                            successOnAddToCart(quantity, position, incrementOrDecrement);

//                            if (onItemAddedToCart != null) {
//                                onItemAddedToCart.onItemChangedInCart(quantity, position, incrementOrDecrement);
//                            }

                        } else {
                            activity.showSnackbarErrorMsg(activity.getResources().getString(R.string.something_went_wrong));
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
                        activity.showSnackbarErrorMsg(activity.getResources().getString(R.string.server_conn_lost));
                        Log.e("Error onFailure : ", t.toString());
                        t.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
//            signOutFirebaseAccounts();

            Snackbar.make(activity.rlRootLayout, activity.getResources().getString(R.string.no_internet),
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addItemToCart(dishObject, quantity, position, incrementOrDecrement);
                        }
                    })
//                    .setActionTextColor(getResources().getColor(R.color.colorSnackbarButtonText))
                    .show();
        }
    }

    private void successOnAddToCart(final int quantity, final int position, final String incrementOrDecrement) {
        if (onItemAddedToCart != null) {
            onItemAddedToCart.onItemChangedInCart(quantity, position, incrementOrDecrement);
        }
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public void showDialog() {
        progressIndicator.showProgress(activity);
    }

    public void dismissDialog() {
        if (progressIndicator != null) {
            progressIndicator.hideProgress();
        }
    }
}
