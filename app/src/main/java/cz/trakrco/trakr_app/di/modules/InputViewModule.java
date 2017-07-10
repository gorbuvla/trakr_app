package cz.trakrco.trakr_app.di.modules;

import android.widget.ArrayAdapter;

import com.codetroopers.betterpickers.timepicker.TimePickerBuilder;

import java.util.ArrayList;
import java.util.List;

import cz.trakrco.trakr_app.R;
import cz.trakrco.trakr_app.di.scopes.FragmentScope;
import cz.trakrco.trakr_app.view.main.InputFragment;
import dagger.Module;
import dagger.Provides;

/**
 * Created by vlad on 05/07/2017.
 */
@Module
public class InputViewModule {

    private InputFragment view;
    private ArrayAdapter<String> spinnerAdapter;
    private TimePickerBuilder tpb;

    public InputViewModule(InputFragment view) {
        this.view = view;
        initialize();
    }

    @Provides
    @FragmentScope
    InputFragment provideView() {
        return view;
    }

    @Provides
    @FragmentScope
    ArrayAdapter<String> provideSpinnerAdapter() {
        return spinnerAdapter;
    }

    @Provides
    @FragmentScope
    TimePickerBuilder provideTimePickerBuilder() {
        return tpb;
    }

    private void initialize() {
        List<String> categories = new ArrayList<>();
        categories.add("Local Storage");
        categories.add("Remote Storage");
        spinnerAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tpb = new TimePickerBuilder()
                .setFragmentManager(view.getFragmentManager())
                .setStyleResId(R.style.BetterPickersDialogFragment_Light);
    }
}
