package net.devloops.placer.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import net.devloops.placer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Odey M. Khalaf <odey@devloops.net> on 07/23/19.
 */
public class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private static String LOG_TAG = "GooglePlacesAutocomplete";
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private ArrayList<String> resultList;

    public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mContext = context;
    }

    private static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            // sb.append("&components=country:in");

            String sb = "https://maps.googleapis.com/maps/api/place" + "/autocomplete" + "/json" + "?key=" + mContext.getString(R.string.google_maps_key) +
                    "&input=" + URLEncoder.encode(input, "utf8");
            URL url = new URL(sb);

            System.out.println("URL: " + url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (IOException e) {
            //Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        }//Log.e(LOG_TAG, "Error connecting to Places API", e);
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            // Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

   public static Location getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context, Locale.getDefault());
        List<Address> address;
        Location data = new Location("");

        try {
            address = coder.getFromLocationName(strAddress, 1);
            if (address.size() == 0) return new Location("");
            Address location = address.get(0);

            System.out.println("============" + location.getLatitude() + "===================" + location.getLongitude() + "====================");
            System.out.println("=====Local=======" + location.getSubLocality());
            System.out.println("=====City=======" + location.getLocality());
            System.out.println("=====State=======" + location.getAdminArea());
            System.out.println("=====PinCode=======" + location.getPostalCode());
            System.out.println("=====Country=======" + location.getCountryName());
            data.setLatitude(address.get(0).getLatitude());
            data.setLongitude(address.get(0).getLongitude());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}