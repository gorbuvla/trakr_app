package cz.trakrco.trakr_app.view.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.timepicker.TimePickerBuilder;
import com.codetroopers.betterpickers.timepicker.TimePickerDialogFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.Unbinder;
import cz.trakrco.trakr_app.R;
import cz.trakrco.trakr_app.domain.Location;
import cz.trakrco.trakr_app.domain.User;
import cz.trakrco.trakr_app.di.components.DaggerInputViewComponent;
import cz.trakrco.trakr_app.di.modules.InputViewModule;
import cz.trakrco.trakr_app.presenter.main.InputPresenter;

/**
 * Created by vlad on 02/07/2017.
 */

public class InputFragment extends Fragment implements TimePickerDialogFragment.TimePickerDialogHandler, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    private static final int PLACE_PICKER_REQUEST = 101;

    @BindView(R.id.input_activity_label)
    EditText activityNameEditText;

    @BindView(R.id.input_activity_time)
    TextView activityTime;

    @BindView(R.id.map_button)
    ImageView imageView;

    @BindView(R.id.input_err_msg)
    TextView errorTextView;

    @BindView(R.id.storage_select)
    Spinner spinner;

    @BindView(R.id.predict_text_view)
    AutoCompleteTextView predictTextView;

    @Inject
    ArrayAdapter<String> spinnerAdapter;

    @Inject
    InputPresenter presenter;

    @Inject
    AutoCompleteAdapter predictAdapter;

    @Inject
    GoogleApiClient googleApiClient;

    @Inject
    TimePickerBuilder tpb;

    @Inject
    User user;

    private Unbinder unbinder;
    private ProgressDialog pd;

    private Place place;
    private Location location;
    private Integer timeHours;
    private Integer timeMinutes;
    private Integer storageIdentifier = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerInputViewComponent.builder()
                .mainActivityComponent(((MainActivity)getActivity()).getComponent())
                .inputViewModule(new InputViewModule(this)).build().inject(this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_input_form, container, false);
        tpb.addTimePickerDialogHandler(this);
        unbinder = ButterKnife.bind(this, view);
        pd = new ProgressDialog(getActivity());
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinner.setAdapter(spinnerAdapter);
        predictTextView.setAdapter(predictAdapter);

        predictTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location = null;
            }
        });
        predictTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                place = null;
                location = (Location)adapterView.getItemAtPosition(i);
                predictTextView.setText(location.getAddress());
            }
        });
    }

    @OnClick(R.id.input_activity_time)
    public void onTimeFieldClicked() {
        tpb.show();
    }

    @Override
    public void onDialogTimeSet(int reference, int hourOfDay, int minute) {
        timeHours = hourOfDay;
        timeMinutes = minute;
        activityTime.setText((hourOfDay > 9 ? hourOfDay : "0" + hourOfDay ) + ":" + (minute > 9 ? minute : "0" + minute));
    }

    @OnClick(R.id.map_button)
    public void onMapButtonClicked() {
        display();
    }

    @OnClick(R.id.save_button)
    public void onSaveButtonClicked() {
        String label = activityNameEditText.getText().toString();
        Integer hours = timeHours;
        Integer minutes = timeMinutes;
        Integer storage = storageIdentifier;


        if (place != null) {
            presenter.save(label, place, hours, minutes, storage);
        } else {
            presenter.save(label, location, hours, minutes, storage);
        }


    }

    @OnItemSelected(R.id.storage_select)
    public void onItemSelected(int position) {
        storageIdentifier = position;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    public void display() {

        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            location = null;
            place = PlacePicker.getPlace(getContext(), data);
            String label = place.getName() + ", " + place.getAddress();
            predictTextView.setText(label);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Permission required", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //left blank
    }

    @Override
    public void onConnectionSuspended(int i) {
        //left blank
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //left blank
    }

    public void showLoading() {
        pd.setMessage("Saving...");
        pd.show();
    }

    public void stopLoading() {
        activityNameEditText.setText("");
        predictTextView.setText("");
        activityTime.setText("");
        pd.dismiss();
    }


    public void onSuccess() {
        errorTextView.setText(R.string.record_save_success);
        errorTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.successGreen));
        errorTextView.setVisibility(View.VISIBLE);
    }

    public void onError(String msg) {
        errorTextView.setText(msg);
        errorTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
