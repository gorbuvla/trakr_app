package cz.trakrco.trakr_app;

import java.util.List;

import cz.trakrco.trakr_app.domain.Output;

/**
 * Created by vlad on 09/07/2017.
 */

public class Utils {

    public static List<Output> mergeSorted(List<Output> l1, List<Output> l2) {
        for (int i1 = 0, i2 = 0; i2 < l2.size(); i1++) {
            if (i1 == l1.size() || l1.get(i1).getTimestamp() < l2.get(i2).getTimestamp()) {
                l1.add(i1, l2.get(i2++));
            }
        }
        return l1;
    }
}
