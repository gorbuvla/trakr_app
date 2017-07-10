package cz.trakrco.trakr_app.model;

import android.os.Looper;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import cz.trakrco.trakr_app.domain.Output;
import cz.trakrco.trakr_app.domain.User;
import cz.trakrco.trakr_app.Utils;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;

/**
 * Created by vlad on 06/07/2017.
 */

public class StorageService {

    private RemoteDBService rdbs;
    private LocalDBService ldbs;


    @Inject
    public StorageService(RemoteDBService rdbs, LocalDBService ldbs) {
        this.rdbs = rdbs;
        this.ldbs = ldbs;
    }


    public Observable<Output> save(Output output, int storageSelect) {
        Log.i("STORAGE_SERVICE", "Save output: " + output.getName() + ", for storage: " + storageSelect);
        return storageSelect == 0 ? ldbs.save(output) : rdbs.save(output);
    }

    public Observable<Output> delete(Output output, User user) {
        return output.getMode().equals("LOCAL") ? ldbs.delete(output) : rdbs.delete(output, user);
    }

    public Observable<Long> localCount(User user) {
        return ldbs.getRecordCountFor(user);
    }

    public Observable<Integer> remoteCount(User user) {
        return rdbs.getRecordCountFor(user);
    }

    public Observable<List<Output>> loadLimitedMergedData(final int localLimit, int localStartAt, int remoteLimit, String remoteEndAt, final List<Output> list, User user) {
        Observable<List<Output>> sourceRemote = rdbs.loadLimitedEndingAt(remoteLimit, remoteEndAt, user);
        Observable<List<Output>> sourceLocal = ldbs.loadLimitedFromRowForUser(localLimit, localStartAt, user);

        return Observable.zip(sourceRemote, sourceLocal, new BiFunction<List<Output>, List<Output>, List<Output>>() {
            @Override
            public List<Output> apply(@NonNull List<Output> outputs, @NonNull List<Output> outputs2) throws Exception {
                Log.i("STORAGE_SERVICE.load", "zipping results -> remote: " + outputs.size() + ", local: " + outputs2.size());
                return Utils.mergeSorted(list, Utils.mergeSorted(outputs, outputs2));
            }
        });
    }

    private boolean onMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

}
