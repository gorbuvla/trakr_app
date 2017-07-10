package cz.trakrco.trakr_app.domain;

import android.graphics.Bitmap;

/**
 * Created by vlad on 09/07/2017.
 */

public class ItemHolder {

    private Bitmap bitmap;
    private Output output;

    public ItemHolder(Bitmap bitmap, Output output) {
        this.bitmap = bitmap;
        this.output = output;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Output getOutput() {
        return output;
    }
}
