package cz.trakrco.trakr_app.view.main;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import cz.trakrco.trakr_app.domain.Location;

/**
 * Created by vlad on 05/07/2017.
 */

public class AutoCompleteAdapter extends ArrayAdapter<Location> {


    private GoogleApiClient googleApiClient;

    @Inject
    public AutoCompleteAdapter(InputFragment view, GoogleApiClient client) {
        super(view.getContext(), 0);
        googleApiClient = client;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView != null ? convertView : LayoutInflater.from(getContext()).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        ((TextView)v.findViewById(android.R.id.text1)).setText(getItem(position).getAddress());
        return v;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                Log.i("ADAPTER", "CLEAR AND SEARCH");
                clear();
                displayPredictions(charSequence.toString());
                return null;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                notifyDataSetChanged();
            }
        };
    }

    private void displayPredictions(String query) {
        LatLngBounds bounds = new LatLngBounds(new LatLng(47.356828, -4.286346), new LatLng(56.791366, 36.978302));
        AutocompleteFilter filter = new AutocompleteFilter.Builder().setCountry("CZ").build();
        Places.GeoDataApi.getAutocompletePredictions(googleApiClient, query, bounds, filter)
                .setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {


                    @Override
                    public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
                        if (autocompletePredictions.getStatus().isSuccess()) {
                            for (AutocompletePrediction p: autocompletePredictions) {

                                String s = p.getFullText(new StyleSpan(Typeface.NORMAL)).toString();
                                Location l = new Location(p.getPlaceId(), s);
                                add(l);
                            }
                        }
                        autocompletePredictions.release();
                    }


                }, 30, TimeUnit.SECONDS);
    }
}
