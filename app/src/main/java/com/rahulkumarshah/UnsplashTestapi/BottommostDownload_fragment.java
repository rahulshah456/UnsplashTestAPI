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

public class BottommostDownload_fragment extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;
    private ImageView rawImage,regularImage,deviceImage,customImage,compressedImage,orignalImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_download,container,false);

        rawImage = (ImageView) view.findViewById(R.id.rawImageID);
        customImage = (ImageView) view.findViewById(R.id.customImageID);
        compressedImage = (ImageView) view.findViewById(R.id.compressedImageID);
        orignalImage = (ImageView) view.findViewById(R.id.orignalImageID);
        regularImage = (ImageView) view.findViewById(R.id.regularImageID);
        deviceImage = (ImageView) view.findViewById(R.id.deviceImageID);

        customImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDownloadButtonClicked("custom");
                dismiss();
            }
        });

        compressedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDownloadButtonClicked("compress");
                dismiss();
            }
        });

        orignalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDownloadButtonClicked("orignal");
                dismiss();
            }
        });

        rawImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDownloadButtonClicked("raw");
                dismiss();
            }
        });

        regularImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDownloadButtonClicked("regular");
                dismiss();
            }
        });

        deviceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDownloadButtonClicked("device");
                dismiss();
            }
        });


        return view;
    }

    public interface BottomSheetListener{
        void onDownloadButtonClicked(String click);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mListener = (BottomSheetListener) context;

    }
}
