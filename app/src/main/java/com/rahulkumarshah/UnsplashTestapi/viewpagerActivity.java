package com.rahulkumarshah.UnsplashTestapi;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionBarContainer;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

import Models.Wallpaper;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class viewpagerActivity extends AppCompatActivity implements BottommostDownload_fragment.BottomSheetListener, BottommostWallpaper_fragment.BottomSheetWallListener {

    private static final String TAG = viewpagerActivity.class.getSimpleName();
    private Toolbar toolbar;
    private Animation slideUp,slideDown;
    private ViewPager imageViewPager;
    private ActionBarContainer actionBarContainer;
    private SliderViewPagerAdapter mPagerAdapter;
    private Bundle extras ;
    private static int currentPosition;
    private ConstraintLayout creditsLayout;
    private TextView creditsTextView;
    private Wallpaper mWallpaper;
    private ImageButton infoBtn,shareBtn,downloadBtn,setWallBtn;
    private String fullUrl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_viewpager);


        extras = getIntent().getExtras();
        toolbar = findViewById(R.id.pagerToolbarID);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        imageViewPager = findViewById(R.id.sliderViewPagerID);
        infoBtn = (ImageButton) findViewById(R.id.infoButton);
        shareBtn = (ImageButton) findViewById(R.id.shareButton);
        downloadBtn = (ImageButton) findViewById(R.id.downloadButton);
        setWallBtn = (ImageButton) findViewById(R.id.setWallButton);
        creditsLayout = (ConstraintLayout) findViewById(R.id.creditsLayout);
        creditsTextView = (TextView) findViewById(R.id.creditsTextView);
        actionBarContainer = (ActionBarContainer) findViewById(R.id.action_bar_container);

        if(extras!=null){
            // Fetch data
            String position = extras.getString("position").trim();
            currentPosition = Integer.parseInt(position);

            Log.d(TAG, "onCreateView: wallpaperlist size: " + HomeFragment.wallpaperList.size());
            Log.d(TAG, "onCreateView: currentPosition: " + currentPosition);
        }



        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottommostDownload_fragment sheetFragment = new BottommostDownload_fragment();
                sheetFragment.show(getSupportFragmentManager(),"download");
            }
        });

        setWallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottommostWallpaper_fragment wallpaper_fragment = new BottommostWallpaper_fragment();
                wallpaper_fragment.show(getSupportFragmentManager(),"setWallpaper");
            }
        });

//        mWallpaper = HomeFragment.wallpaperList.get(currentPosition);
//        mPagerAdapter.getItemPosition(mWallpaper);
//        String credits = "Photo by " + mWallpaper.getUser().getName() + " / Unsplash";
//        creditsTextView.setText(credits);
//
//
//
//
//        creditsLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Uri uri = Uri.parse(mWallpaper.getUser().getLinks().getHtml());
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
//            }
//        });


        // Set up viewpager
        mPagerAdapter = new SliderViewPagerAdapter();
        imageViewPager.setAdapter(mPagerAdapter);
        imageViewPager.setCurrentItem(currentPosition);


    }


    @SuppressLint("RestrictedApi")
    @Subscribe
    public void onEvent(ImmenseLayout event) {
        // Show/Hide Credits layout
        if(event.getTouch() == "touched"){

            if(creditsLayout.getVisibility() == View.VISIBLE){
                actionBarContainer.setVisibility(View.INVISIBLE);
                creditsLayout.setVisibility(View.INVISIBLE);

            }else {
                actionBarContainer.setVisibility(View.VISIBLE);
                creditsLayout.setVisibility(View.VISIBLE);
            }
        }
    }



    @Subscribe
    public void onEvent(WallpaperInfo event) {
        //Toast.makeText(this, "Hey, my message" + event.getUserName(), Toast.LENGTH_SHORT).show();
        creditsTextView.setText(event.getmUserName());
        fullUrl = event.getFullUrl();
    }


    public class ImmenseLayout {

        public String Touch;

        public ImmenseLayout(String touch){
            this.Touch = touch;
        }

        public String getTouch() {
            return Touch;
        }
    }


    public class WallpaperInfo {

        public String mUserName;
        public String fullUrl;

        public WallpaperInfo(String mUserName, String fullUrl) {
            this.mUserName = mUserName;
            this.fullUrl = fullUrl;
        }

        public String getmUserName() {
            return mUserName;
        }

        public String getFullUrl() {
            return fullUrl;
        }
    }


    private void fixed() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int SCREEN_WIDTH = displayMetrics.heightPixels;
        final int SCREEN_HEIGHT = displayMetrics.widthPixels;

        Toast.makeText(viewpagerActivity.this,fullUrl,Toast.LENGTH_LONG).show();

        Glide.with(this)
                .load(fullUrl)
                .asBitmap()
                .centerCrop()
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        try {

                            WallpaperManager manager = (WallpaperManager)getSystemService(Context.WALLPAPER_SERVICE);
                            manager.setWallpaperOffsetSteps(10,10);
                            manager.suggestDesiredDimensions(SCREEN_WIDTH, SCREEN_HEIGHT);
                            manager.getInstance(viewpagerActivity.this).setBitmap(resource);

                            Toast.makeText(viewpagerActivity.this,"Wallpaper Set Successfully",Toast.LENGTH_LONG).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.slider_preview_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       switch (item.getItemId()){

           case R.id.favourites_menuID:
               break;

           case R.id.orignal_sizeID:
               break;

           case R.id.slideshow_menuID:
               break;

           case R.id.editSave_menuID:
               break;

       }
        return super.onOptionsItemSelected(item);
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

    @Subscribe
    public void onEvent(String key){
        if(key.equals(HomeFragment.EVENTBUSKEY_REFRESH_PAGER_ADAPTER)) {
            Log.d(TAG, "onEvent: Calling notifyDataSetChanged on PagerAdapter ...");
            mPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDownloadButtonClicked(String click) {
        if (click == "custom"){
            Toast.makeText(this,"Custom Image Clicked",Toast.LENGTH_SHORT).show();
        }
        else if (click == "compress"){
            Toast.makeText(this,"Compressed Image CLicked",Toast.LENGTH_SHORT).show();
        }
        else if (click == "orignal"){
            Toast.makeText(this,"Orininal Image Clicked",Toast.LENGTH_SHORT).show();
        }
        else if (click == "regular"){
            Toast.makeText(this,"Regular Image Clicked",Toast.LENGTH_SHORT).show();
        }
        else if (click == "device"){
            Toast.makeText(this,"Device Size Image Clicked",Toast.LENGTH_SHORT).show();
        }
        else if (click == "raw"){
            Toast.makeText(this,"Raw Image Clicked",Toast.LENGTH_SHORT).show();
        }

        else {
            Toast.makeText(this,"Nothing Clicked",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWallButtonClicked(String click) {
        if (click == "scrollable"){
            //scrollable();
        }
        else if (click == "fixed"){
            fixed();
        }
        else {
            Toast.makeText(this,"Nothing Clicked",Toast.LENGTH_SHORT).show();
        }
    }


    public class SliderViewPagerAdapter extends PagerAdapter {

        LayoutInflater layoutInflater;
        String userName,url;


        @Override
        public int getCount() {
            return HomeFragment.wallpaperList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fragment_preview, container, false);
            view.setTag(position);
            final ImageViewTouch previewImageView = (ImageViewTouch) view.findViewById(R.id.image_preview);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            final Wallpaper wallpaper = HomeFragment.wallpaperList.get(position);


            Log.d(TAG, "instantiateItem: WallpaperURL: " + wallpaper.getUrls().getFull());
            userName = "Photo by " + wallpaper.getUser().getName().trim() + " / Unsplash";
            url = wallpaper.getUrls().getFull().toString().trim();





            Glide.with(viewpagerActivity.this).load(wallpaper.getUrls().getRegular())
                    .thumbnail(0.5f)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .crossFade()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            Log.d(TAG, "onResourceReady: Loaded!");
                            progressBar.setVisibility(View.GONE);
                            previewImageView.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(previewImageView);
            previewImageView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_HEIGHT);





            final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    //do something
                    EventBus.getDefault().post(new ImmenseLayout("touched"));
                    return true;
                }
            });


            previewImageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });


            EventBus.getDefault().post(new WallpaperInfo(userName,url));




            container.addView(view);

            return (view);
        }


        //required for handling EventBus
        public void testRefresh(int position) {
            View view = imageViewPager.findViewWithTag(position + 1);
            if(view != null) {
                Log.d(TAG, "testRefresh: View at " + (position + 1) + " Found!");

                // Fetch CreditsLayout
                LinearLayout creditsLayout = (LinearLayout) view.findViewById(R.id.creditsLayout);
                if(creditsLayout != null) {
                    creditsLayout.setAlpha(1.0f);
                    Log.d(TAG, "testRefresh: creditsLayout at " + (position + 1) + " set to " + creditsLayout.getAlpha() );
                }else {
                    Log.e(TAG, "testRefresh: creditsTextView not found!");
                }
                // TODO hide credits for position + 1
            }else{
                Log.e(TAG, "testRefresh: View not found!");
            }
        }






        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((View)object);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }


    }
}
