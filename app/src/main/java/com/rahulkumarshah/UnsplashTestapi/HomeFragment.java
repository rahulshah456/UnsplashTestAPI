package com.rahulkumarshah.UnsplashTestapi;


import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import Adapters.ConnectionDetector;
import Adapters.ImageListAdapter;
import Models.OrderSelectedEvent;
import Models.Wallpaper;
import Retrofit.WallpaperApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements ImageListAdapter.RecyclerViewClickListener{

    private static final String TAG = HomeFragment.class.getSimpleName();

    // Image fetch order types as specified by UnSplash API
    public static final String ORDER_BY_OLDEST = "oldest";
    public static final String ORDER_BY_POPULAR = "popular";
    public static final String ORDER_BY_LATEST = "latest";
    public static String order = ORDER_BY_LATEST;
    public static final String EVENTBUSKEY_REFRESH_PAGER_ADAPTER = "Refresh Adapter";
    public static final String BUNDLE_LIST_STATE = "list_state";
    public int mCurrRotation = 0; // takes the place of getRotation()


    Context mContext;
    RecyclerView imageRecyclerView;
    ImageView noConnectionGif;
    RelativeLayout loadingLayout,connectionLost;
    ImageListAdapter imageListAdapter;
    public static List<Wallpaper> wallpaperList;
    boolean isLoading = false;
    static int page = 1;
    RecyclerView.LayoutManager mLayoutManager;
    private Parcelable mListState;
    Animation slide_down,slide_up;
    ConnectionDetector networkState;
    SwipeRefreshLayout swipeLayout;


    public static HomeFragment newInstance() {

        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment,container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();
        imageRecyclerView = (RecyclerView) view.findViewById(R.id.images_recycler_view);
        connectionLost = (RelativeLayout) view.findViewById(R.id.noConnectionLayoutID);
        loadingLayout = (RelativeLayout) view.findViewById(R.id.loadingRecyclerID);
        noConnectionGif = (ImageView) view.findViewById(R.id.no_connectionImageID);
        networkState = new ConnectionDetector(mContext);
        swipeLayout = view.findViewById(R.id.swipeRefreshID);




        generateRecyclerView();



        // Adding Listener
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // refresh layouts
                //Toast.makeText(mContext, "Works!", Toast.LENGTH_LONG).show();
                connectionLost.setVisibility(View.INVISIBLE);
                imageListAdapter.clearList();
                imageRecyclerView.setVisibility(View.VISIBLE);
                downloadImages(order);
                generateRecyclerView();
                // To keep animation for 4 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)
                        swipeLayout.setRefreshing(false);
                    }
                }, 4000); // Delay in millis
            }
        });

        // Scheme colors for animation
        swipeLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );

        //Load animation
        slide_down = AnimationUtils.loadAnimation(mContext, R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(mContext, R.anim.slide_up);




        // Load images on app run
        downloadImages(order);


        
        // Load more images onScroll end
        imageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Check if end of page has been reached
                if( !isLoading && ((LinearLayoutManager)mLayoutManager).findLastVisibleItemPosition() == imageListAdapter.getItemCount()-1 ){
                    isLoading = true;
                    Log.d(TAG , "End has reached, loading more images!");
                    loadingLayout.startAnimation(slide_up);
                    loadingLayout.setVisibility(View.VISIBLE);
                    page++;
                    downloadImages(order);
                }
            }
        });

    }


    public void generateRecyclerView(){
        // Set up RecyclerView
        wallpaperList = new ArrayList<>();
        mLayoutManager = new GridLayoutManager(mContext, 2);
        imageListAdapter = new ImageListAdapter(mContext, this);
        imageRecyclerView.setLayoutManager(mLayoutManager);
        imageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        imageRecyclerView.setAdapter(imageListAdapter);
    }







    //Fetch and load images via REST API using Retrofit
    //orderBy - order of images to be retrieved
    public void downloadImages(String orderBy){

        int itemsPerPage = 20;

        // Set Toolbar subtitle
        try {
            ((MainActivity)getActivity()).getSupportActionBar().setSubtitle(orderBy.substring(0,1).toUpperCase() + orderBy.substring(1));
        }catch (Exception e){
            Log.e(TAG, "downloadImages: Exception " + e );
        }

        // Retrofit connect
        WallpaperApi.Factory.getInstance().getWallpapers(orderBy, itemsPerPage, page,WallpaperApi.API_KEY).enqueue(new Callback<List<Wallpaper>>() {
            @Override
            public void onResponse(Call<List<Wallpaper>> call, Response<List<Wallpaper>> response) {
                /*for(Wallpaper wallpaper:response.body()){
                    Log.d(TAG, wallpaper.getUser().getName());
                }*/
                imageListAdapter.addImages(response.body());
                isLoading = false;
                loadingLayout.setVisibility(View.INVISIBLE);
                loadingLayout.startAnimation(slide_down);
                EventBus.getDefault().post(EVENTBUSKEY_REFRESH_PAGER_ADAPTER);
            }
            @Override
            public void onFailure(Call<List<Wallpaper>> call, Throwable t) {
                Log.e(TAG, "Failed " + t.getMessage());
                isLoading = false;
                // reduce page by 1 as page failed to load
                page--;
                imageRecyclerView.setVisibility(View.GONE);

                Glide.with(mContext)
                        .load(R.drawable.no_connection)
                        .asGif()
                        .into(noConnectionGif);
                connectionLost.setVisibility(View.VISIBLE);
            }
        });
    }

    @Subscribe
    public void onOrderSelectedEvent(OrderSelectedEvent event){

        // Clear and reset
        page = 1;
        imageListAdapter.clearList();

        // Set new order type
        order = event.getOrder();

        // Fetch images for selected order type
        downloadImages(order);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        mListState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(BUNDLE_LIST_STATE, mListState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(BUNDLE_LIST_STATE);
        }
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        if(mListState != null) {
            Log.d(TAG, "onResume: Restoring list state ...");
            mLayoutManager.onRestoreInstanceState(mListState);
        }else {
            Log.d(TAG, "onResume: ListState empty!");
        }
        super.onResume();
    }


    @Override
    public void recyclerViewListClicked(View v, int position) {

        Intent intent = new Intent(mContext,viewpagerActivity.class);
        intent.putExtra("position",Integer.toString(position).trim());
        startActivity(intent);

    }
}
