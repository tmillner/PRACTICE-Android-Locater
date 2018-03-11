package com.trevor.showcase.locatractivity;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocatrFragment extends Fragment {

    private static final String TAG = LocatrFragment.class.getName();

    private ImageView mImageView;
    private GoogleApiClient mGoogleApiClient;

    public static LocatrFragment newInstance() {
        return new LocatrFragment();
    }

    public LocatrFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_locatr, container, false);
        mImageView = v.findViewById(R.id.image);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().invalidateOptionsMenu();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        getActivity().invalidateOptionsMenu();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            checkPermission();
        }

    }

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ){//Can add more as per requirement

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_locatr, menu);

        MenuItem searchItem = menu.findItem(R.id.action_locate);
        searchItem.setEnabled(mGoogleApiClient.isConnected());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_locate:
                findImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void findImage() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, request, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i(TAG, "Got fix: " + location );
                        new SearchTask().execute(location);
                    }
                });
    }

    public class SearchTask extends AsyncTask<Location, Void, Void> {
        private GalleryItem mGalleryItem;
        private Bitmap mBitmap;

        @Override
        protected Void doInBackground(Location... locations) {
            FlickrFetcher flickrFetcher = new FlickrFetcher();
            List<GalleryItem> photos = flickrFetcher.searchPhotos(locations[0]);

            if(photos.size() == 0) return null;

            mGalleryItem = photos.get(0);

            try {
                byte[] bytes = flickrFetcher.getURLBytes(mGalleryItem.getUrl());
                mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } catch (IOException e) {
                Log.e(TAG, "Unable to download image from: " + mGalleryItem.getUrl() +
                        " " + e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mImageView.setImageBitmap(mBitmap);
        }
    }
}
