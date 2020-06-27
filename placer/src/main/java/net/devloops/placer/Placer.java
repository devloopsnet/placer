package net.devloops.placer;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import net.devloops.placer.location.LocationSearchActivity;

public class Placer {
    public static final String TAG = "PlacerTag: ";
    private Activity mActivity;
    private Fragment fragment;

    public static final int LOCATION_REQUEST_CODE = 1010;

    public Placer() {
    }


    /**
     * get reference From Activity
     *
     * @param activity Activity
     * @return Placer
     */
    public static Placer withActivity(Activity activity) {
        Placer placer = new Placer();
        placer.mActivity = activity;
        return placer;
    }

    /**
     * get reference From Fragment
     *
     * @param fragment Fragment
     * @return Placer
     */
    public static Placer withFragment(Fragment fragment) {
        Placer placer = new Placer();
        placer.mActivity = fragment.getActivity();
        placer.fragment = fragment;
        return placer;
    }

    /**
     * shows the picker dialog
     */
    public void show() {
        if (fragment != null)
            fragment.startActivityForResult(new Intent(mActivity, LocationSearchActivity.class), LOCATION_REQUEST_CODE);
        else
            mActivity.startActivityForResult(new Intent(mActivity, LocationSearchActivity.class), LOCATION_REQUEST_CODE);
    }
}