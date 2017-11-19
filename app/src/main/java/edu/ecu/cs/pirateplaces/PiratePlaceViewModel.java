package edu.ecu.cs.pirateplaces;

import android.app.Activity;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * View Model for binding of Pirate Place items
 *
 * @author Mark Hills (mhills@cs.ecu.edu)
 * @version 1.0
 */
public class PiratePlaceViewModel extends BaseObservable
{
    private PiratePlace mPiratePlace;

    private PirateBase mPirateBase;

    private Context mContext;

    public PiratePlaceViewModel(PirateBase pirateBase, Context context)
    {
        mPirateBase = pirateBase;
        mContext = context;
    }

    public PiratePlace getPiratePlace()
    {
        return mPiratePlace;
    }

    @Bindable
    public void setPiratePlace(PiratePlace piratePlace)
    {
        mPiratePlace = piratePlace;
        notifyChange();
    }

    public String getPlaceName()
    {
        return mPiratePlace.getPlaceName();
    }

    @Bindable
    public void setPlaceName(String placeName)
    {
        mPiratePlace.setPlaceName(placeName);
        mPirateBase.updatePiratePlace(mPiratePlace);
        notifyChange();
    }

    public String getFormattedLastVisitedDate()
    {
        String lastVisitedDate = DateFormat.getDateFormat(mContext).format(mPiratePlace.getLastVisited());
        String lastVisitedTime = DateFormat.getTimeFormat(mContext).format(mPiratePlace.getLastVisited());
        return lastVisitedDate + " " + lastVisitedTime;
    }

    public Date getLastVisitedDate()
    {
        return mPiratePlace.getLastVisited();
    }

    @Bindable
    public void setLastVisitedDate(Date lastVisitedDate)
    {
        mPiratePlace.setLastVisited(lastVisitedDate);
        mPirateBase.updatePiratePlace(mPiratePlace);
        notifyChange();
    }

    @Bindable
    public Drawable getFirstImage()
    {
        Bitmap bitmap = null;
        List<File> fileList = mPirateBase.getPhotoFiles(mPiratePlace);
        if (fileList.size() > 0) {
            File imageFile = fileList.get(0);
            bitmap = PictureUtils.getScaledBitmap(imageFile.getPath(), 120, 120);
        }
        return new BitmapDrawable(mContext.getResources(), bitmap);
    }
}
