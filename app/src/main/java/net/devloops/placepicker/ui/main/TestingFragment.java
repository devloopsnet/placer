package net.devloops.placepicker.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;

import net.devloops.placepicker.R;
import net.devloops.placer.Placer;
import net.devloops.placer.util.Logger;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static net.devloops.placer.Placer.TAG;

public class TestingFragment extends Fragment {
    private TextView txtLocation;

    public static TestingFragment newInstance() {
        return new TestingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        txtLocation = view.findViewById(R.id.message);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Placer.withFragment(this).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Placer.LOCATION_REQUEST_CODE) {
                Bundle intent = Objects.requireNonNull(data).getExtras();
                if (intent != null) {
                    String address = data.getStringExtra("address");
                    LatLng latLng = (LatLng) intent.get("LatLng");
                    Logger.i(TAG, "Address: " + address);
                    if (latLng != null) {
                        String text = address + " / " + "Latitude: " + latLng.latitude + " , Longitude: " + latLng.longitude;
                        txtLocation.setText(text);
                    } else
                        Toast.makeText(getActivity(), getString(R.string.err_server), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}