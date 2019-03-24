package Adapters;

public class wallpaperInfoTransfer {

    public String mUserName;
    public String mRawUrl,mFullUrl,mRegularUrl,mCustomUrl,mSmallUrl,mDeviceCroppedUrl;
    public String wallpaperHeight,wallpaperWidth;


    public wallpaperInfoTransfer(String name) {
        mUserName = name;
    }

    public String getUserName() {
        return mUserName;
    }


}
