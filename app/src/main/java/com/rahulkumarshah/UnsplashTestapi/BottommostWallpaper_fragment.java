package com.rahulkumarshah.UnsplashTestapi;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Edward on 3/24/2018.
 */

public class BottommostWallpaper_fragment extends BottomSheetDialogFragment {

    private BottomSheetWallListener mListener;
    private ImageView scrolllable,fixed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_setwallpaper,container,false);

        scrolllable = (ImageView) view.findViewById(R.id.scrollableImageID);
        fixed = (ImageView) view.findViewById(R.id.fixedImageID);

        scrolllable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onWallButtonClicked("scrollable");
                dismiss();
            }
        });

        fixed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onWallButtonClicked("fixed");
                dismiss();
            }
        });

        return view;
    }

    public interface BottomSheetWallListener {
        void onWallButtonClicked(String click);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mListener = (BottomSheetWallListener) context;

    }
}
