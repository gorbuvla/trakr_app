package cz.trakrco.trakr_app.presenter.main;

import java.util.List;

import cz.trakrco.trakr_app.domain.ItemHolder;
import cz.trakrco.trakr_app.domain.Output;

/**
 * Created by vlad on 09/07/2017.
 */

public interface OnDataArrivedListener {
    void odDataArrived(List<ItemHolder> data, List<Output> threshold, String olderKey);
}
