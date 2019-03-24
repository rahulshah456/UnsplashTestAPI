package Adapters;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.rahulkumarshah.UnsplashTestapi.HomeFragment;
import com.rahulkumarshah.UnsplashTestapi.R;

import java.util.List;

import Models.Wallpaper;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.MyViewHolder> {

    private static final String TAG = "ImageListAdapter";
    private static RecyclerViewClickListener itemListener;
    private static SwipeRefreshLayout swipeRefreshLayout;
    Context mContext;


    public ImageListAdapter(Context context, RecyclerViewClickListener itemListener) {
        mContext = context;
        this.itemListener = itemListener;
    }

    public interface RecyclerViewClickListener
    {
        public void recyclerViewListClicked(View v, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView thumbnail;
        public TextView likes;
        public ProgressBar progressBar;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            thumbnail = (ImageView) itemView.findViewById(R.id.image_thumbnail);
            likes = (TextView) itemView.findViewById(R.id.likesTextID);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        }

        @Override
        public void onClick(View v) {
//            Toast.makeText(ctx, "onClick works!", Toast.LENGTH_SHORT).show();
            Log.d("ImageListAdapter", "onClick works! Position: " + this.getLayoutPosition() + " clicked!");
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(mContext).inflate(R.layout.image_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Wallpaper wallpaper = HomeFragment.wallpaperList.get(position);
        String imageUrl = "";
        switch (PreferenceManager.getDefaultSharedPreferences(mContext).getString("thumbnail_quality", "1")){
            case "0": {
                imageUrl = wallpaper.getUrls().getThumb();
                break;
            }
            case "1": {
                imageUrl = wallpaper.getUrls().getSmall();
                break;
            }

        }
        holder.likes.setText(wallpaper.getLikes().toString().trim() + " Likes");
        holder.progressBar.setVisibility(View.VISIBLE);
        Glide.with(mContext).load(imageUrl)
                .thumbnail(0.5f)
                .crossFade()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        holder.thumbnail.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnail);
    }


    @Override
    public int getItemCount() {
        return HomeFragment.wallpaperList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void addImages(List<Wallpaper> list){
        HomeFragment.wallpaperList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearList(){
        HomeFragment.wallpaperList.clear();
    }

    public List<Wallpaper> getItemList(){
        return HomeFragment.wallpaperList;
    }

}
