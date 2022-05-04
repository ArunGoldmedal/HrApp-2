package com.goldmedal.hrapp.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.goldmedal.hrapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class GoogleMapInfoAdapter implements GoogleMap.InfoWindowAdapter {

    private View mWindow;

    public GoogleMapInfoAdapter(Context mContext) {
        mWindow = LayoutInflater.from(mContext).inflate(R.layout.google_maps_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowView(marker, mWindow);
        return mWindow;
    }

    private void renderWindowView(Marker marker, View view) {

        String title = marker.getTitle();
        TextView mTextViewName = view.findViewById(R.id.txtTitle);

        if (title != null) {
            if (!title.equals("")) {
                mTextViewName.setText(title);
            }
        }

        String snippet = marker.getSnippet();
        TextView mTextViewAddress = view.findViewById(R.id.txtAddress);

        if (snippet != null) {
            if (!snippet.equals("")) {
                mTextViewAddress.setText(snippet);
            }
        }


    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowView(marker, mWindow);
        return mWindow;
    }
}
