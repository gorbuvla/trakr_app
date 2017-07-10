package cz.trakrco.trakr_app.view.dialog;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.trakrco.trakr_app.R;
import cz.trakrco.trakr_app.domain.Output;

/**
 * Created by vlad on 10/07/2017.
 */

public class DetailDialogFragment extends android.support.v4.app.DialogFragment {

    @BindView(R.id.dialog_image)
    ImageView imageView;

    @BindView(R.id.dialog_name)
    TextView nametv;

    @BindView(R.id.dialog_address)
    TextView addresstv;

    @BindView(R.id.dialog_time)
    TextView timetv;

    Bitmap image;
    Output output;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        image = b.getParcelable("image");
        output = b.getParcelable("output");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_dialog, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView.setImageBitmap(image);
        nametv.setText(output.getName());
        addresstv.setText(output.getAddress());
        Integer i = output.getTime();
        int hours = i/60;
        int minutes = i%60;
        String s = "Duration: " + (hours > 9 ? hours : "0"+hours) + ":" + (minutes > 9 ? minutes : "0"+minutes);
        timetv.setText(s);
    }

    @OnClick(R.id.ok_btn)
    public void onOkClicked() {
        dismiss();
    }
}
