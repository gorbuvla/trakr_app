package cz.trakrco.trakr_app.model;

import android.util.Log;

import com.j256.ormlite.stmt.PreparedQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import cz.trakrco.trakr_app.domain.Output;
import cz.trakrco.trakr_app.domain.User;
import io.reactivex.Observable;

/**
 * Created by vlad on 06/07/2017.
 */

public class LocalDBService {

    private DatabaseHelper dbHelper;

    @Inject
    public LocalDBService(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public Observable<User> save(final User user) {
        return Observable.fromCallable(new Callable<User>() {
            @Override
            public User call() throws Exception {
                dbHelper.getUserDAO().create(user);
                return user;
            }
        });
    }

    public Observable<Output> save(final Output output) {
        return Observable.fromCallable(new Callable<Output>() {
            @Override
            public Output call() throws Exception {
                dbHelper.getOutputDAO().create(output);
                return output;
            }
        });
    }

    public Observable<Output> delete(final Output output) {
        return Observable.fromCallable(new Callable<Output>() {
            @Override
            public Output call() throws Exception {
                dbHelper.getOutputDAO().delete(output);
                return output;
            }
        });
    }

    public Observable<Long> getRecordCountFor(final User user) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return dbHelper.getOutputDAO().queryBuilder().where().eq("user_id", user.getUid()).countOf();
            }
        });
    }

    public Observable<List<Output>> loadLimitedFromRowForUser(final int limit, final int row, final User user) {
        return Observable.fromCallable(new Callable<List<Output>>() {
            @Override
            public List<Output> call() throws Exception {
                Log.i("LOCAL_DB_SERVICE.load", "limit: " + limit + ", start: " + row);
                if (limit <= 0) {
                    Log.i("LOCAL_DB_SERVICE.load", "result count: 0");
                    return new ArrayList<Output>();
                }
                PreparedQuery<Output> pq = dbHelper.getOutputDAO().queryBuilder()
                        .offset((long)row).limit((long)limit)
                        .where().eq("user_id", user.getUid())
                        .prepare();
                List<Output> result = dbHelper.getOutputDAO().query(pq);
                Collections.reverse(result);
                Log.i("LOCAL_DB_SERVICE.load", "result count: " + result.size());
                return result;
            }
        });
    }
}
