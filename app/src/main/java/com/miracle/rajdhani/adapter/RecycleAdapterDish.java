package com.miracle.rajdhani.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.miracle.rajdhani.R;
import com.miracle.rajdhani.listeners.OnUserMayLikedClickListener;
import com.miracle.rajdhani.model.DishObject;

import java.util.List;
import java.util.Random;

public class RecycleAdapterDish extends RecyclerView.Adapter<RecycleAdapterDish.MyViewHolder> {
    Context context;
    private OnUserMayLikedClickListener clickListener;

    private List<DishObject> listDish;

    public RecycleAdapterDish(Context context, List<DishObject> listDish) {
        this.listDish = listDish;
        this.context = context;
    }

    public void setClickListener(OnUserMayLikedClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView dish_name;
        TextView dish_type;
        TextView price;

        TextView tvTotalReviews;
        RatingBar ratingBar;


        public MyViewHolder(View view) {
            super(view);

            image =  view.findViewById(R.id.image);
            dish_name =  view.findViewById(R.id.tv_dishName);
            dish_type =  view.findViewById(R.id.tv_dishType);
            price =  view.findViewById(R.id.tv_price);

            tvTotalReviews =  view.findViewById(R.id.tv_review);
            ratingBar =  view.findViewById(R.id.ratingBar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onUserMayLikedClick(view, getAdapterPosition());
            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_dish_user_may_like, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        DishObject movie = listDish.get(position);

        holder.dish_name.setText(movie.getProductName());
        holder.dish_type.setText(movie.getCategoryName());
        holder.price.setText("₹ " + movie.getPrice());
        holder.image.setImageResource(Integer.parseInt(movie.getProductImage()));

         int totalReviews = new Random().nextInt((250 - 49) + 1) + 49;
        float rating = 3.5f + new Random().nextFloat() * (5 - 3.5f);

        holder.ratingBar.setRating(rating);
        holder.tvTotalReviews.setText(totalReviews + " Reviews");



//        Glide.with(context)
//                .load(R.drawable.resource_id)
//                .into(imageView);

//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
//                // You can pass your own memory cache implementation
//                .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
//                .build();
//
//        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .displayer(new RoundedBitmapDisplayer(10)) //rounded corner bitmap
//                .cacheInMemory(true)
//                .cacheOnDisc(true)
//                .build();
//
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        imageLoader.init(config);
//        imageLoader.displayImage("drawable://" + movie.getImage(), holder.image, options);


    }

    @Override
    public int getItemCount() {
        return listDish.size();
    }


}


