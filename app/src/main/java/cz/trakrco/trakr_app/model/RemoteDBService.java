package cz.trakrco.trakr_app.model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import cz.trakrco.trakr_app.domain.Output;
import cz.trakrco.trakr_app.domain.User;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by vlad on 06/07/2017.
 */

public class RemoteDBService {

    private DatabaseReference dbRef;


    public RemoteDBService(DatabaseReference ref) {
        this.dbRef = ref;
    }


    public Observable<User> save(final User user) {
        return Observable.fromCallable(new Callable<User>() {
            @Override
            public User call() throws Exception {
                dbRef.child("users").child(user.getUid()).setValue(user);
                return user;
            }
        });
    }

    public Observable<Integer> getRecordCountFor(final User user) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(final @NonNull ObservableEmitter<Integer> e) throws Exception {
                dbRef.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user1 = dataSnapshot.getValue(User.class);
                        Log.i("RemoteService", "Got user: " + user1.getUid());
                        Log.i("RemoteService", "Got counter: "+ user1.getCounter());
                        e.onNext(user1.getCounter());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public Observable<Output> save(final Output output) {
        return Observable.fromCallable(new Callable<Output>() {

            @Override
            public Output call() throws Exception {
                final String uid = output.getUser().getUid();
                dbRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            Log.e("RemoteDBService", "Attempt to store output for non existing user");
                            return;
                        }

                        user.setCounter(user.getCounter() + 1);
                        String key = dbRef.child("outputs").push().getKey();
                        output.setId(key);
                        Map<String, Object> outputValues = output.toMap();

                        Map<String, Object> childUpdate = new HashMap<>();
                        childUpdate.put("/outputs/" + key, outputValues);
                        childUpdate.put("/user-outputs/" + uid + "/" + key, outputValues);
                        childUpdate.put("/users/" + uid, user.toMap());
                        dbRef.updateChildren(childUpdate);
                        Log.i("REMOTE_DB_SERVICE.save", "Output: " + output.getName() + " saved");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("REMOTE_DB_SERVICE.save", databaseError.getDetails());
                    }
                });
                return output;
            }
        });
    }

    public Observable<Output> delete(final Output output, final User user) {
        return Observable.fromCallable(new Callable<Output>() {
            @Override
            public Output call() throws Exception {
                dbRef.child("outputs").child(output.getId()).addListenerForSingleValueEvent(deleteListener);
                dbRef.child("user-outputs").child(user.getUid()).child(output.getId()).addListenerForSingleValueEvent(deleteListener);
                dbRef.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);
                        u.setCounter(u.getCounter() - 1);
                        dbRef.child("users").child(u.getUid()).updateChildren(u.toMap());
                        Log.i("REMOTE_DB_SERVICE.del", "User updated");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("REMOTE_DB_SERVICE.del", databaseError.getMessage());
                    }
                });
                return output;
            }
        });
    }


    public Observable<List<Output>> loadLimitedEndingAt(final int limit, final String endAt, final User user) {
        return Observable.create(new ObservableOnSubscribe<List<Output>>() {
            @Override
            public void subscribe(final @NonNull ObservableEmitter<List<Output>> e) throws Exception {
                Log.i("REMOTE_DB_SERVICE.load", "incoming limit: " + limit + ", incoming endAt: " + endAt);

                if (limit <= 0) {
                    Log.i("REMOTE_DB_SERVICE.load", "result count: 0");
                    e.onNext(new ArrayList<Output>());
                    return;
                }
                final Boolean dropLast;

                Query q = null;

                if (endAt == null) {
                    dropLast = false;
                    q = dbRef.child("user-outputs").child(user.getUid()).orderByKey().limitToLast(limit);
                } else {
                    dropLast = true;
                    q = dbRef.child("user-outputs").child(user.getUid()).orderByKey().endAt(endAt).limitToLast(limit + 1);
                }

                final ArrayList<Output> list = new ArrayList<>(limit);

                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            list.add(ds.getValue(Output.class));
                        }
                        if (dropLast) {
                            list.remove(list.size()- 1);
                        }
                        Collections.reverse(list);

                        Log.i("REMOTE_DB_SERVICE.load", "result count: " + list.size());
                        e.onNext(list);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("REMOTE_DB_SERVICE", databaseError.getMessage());
                    }
                });
            }
        });
    }


    private ValueEventListener deleteListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                snapshot.getRef().removeValue();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e("REMOTE_DB_SERVICE.del", databaseError.getDetails());
        }
    };


}
