package cz.trakrco.trakr_app.presenter.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import cz.trakrco.trakr_app.R;
import cz.trakrco.trakr_app.domain.ItemHolder;
import cz.trakrco.trakr_app.domain.Output;
import cz.trakrco.trakr_app.domain.User;
import cz.trakrco.trakr_app.model.StorageService;
import cz.trakrco.trakr_app.view.main.ListFragment;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by vlad on 06/07/2017.
 */

public class OutputListAdapter extends BaseAdapter implements AbsListView.OnScrollListener, OnDataArrivedListener, SwipeRefreshLayout.OnRefreshListener, SwipeMenuListView.OnMenuItemClickListener {

    private static int limit = 10;

    private int localCount;
    private int nextLocalStart;
    private int nextLocalLimit;

    private int remoteCount;
    private String nextRemoteEndAt;
    private int nextRemoteLimit;

    private StorageService storage;
    private User user;

    private List<Output> thresholdList;
    private List<ItemHolder> loadedData;
    private SimpleDateFormat sdf;
    private Boolean loading = true;

    private ListFragment view;

    private final String BASE_URL = "http://maps.google.com/maps/api/staticmap?center=";
    private final String P2  ="&zoom=16&size=400x400&sensor=false&&markers=color:red%7Clabel:S%7C";
    private final String key = "&key=";


    @Inject
    public OutputListAdapter(ListFragment view, StorageService storage, User user) {
        this.view = view;
        this.storage = storage;
        this.user = user;
        sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.GERMANY);
        initialize();
    }

    @Override
    public int getCount() {
        return loadedData.size();
    }

    @Override
    public Object getItem(int i) {
        return loadedData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = LayoutInflater.from(this.view.getContext()).inflate(R.layout.layout_listview_item, viewGroup, false);
        }

        ItemHolder item = loadedData.get(i);
        Output o = item.getOutput();
        Bitmap b = item.getBitmap();


        TextView tv1 = (TextView) view.findViewById(R.id.notif_title);
        TextView tv2 = (TextView) view.findViewById(R.id.notif_location_label);
        TextView tv3 = (TextView) view.findViewById(R.id.notif_time);
        TextView tv4 = (TextView) view.findViewById(R.id.notif_date_label);
        TextView tv5 = (TextView) view.findViewById(R.id.notif_storage_label);
        ImageView iv = (ImageView) view.findViewById(R.id.item_thumbnail);

        int background = o.getMode().equals("LOCAL") ? R.color.lightBlue : R.color.lightYellow;

        view.setBackgroundColor(ContextCompat.getColor(this.view.getContext(), background));
        tv1.setText(o.getName());
        tv2.setText(o.getAddress());
        int hours = o.getTime()/60;
        int minutes = o.getTime()%60;
        String time = "Duration: " + (hours < 10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes);
        tv3.setText(time);
        tv4.setText(sdf.format(new Date(o.getTimestamp())));
        String stid = o.getMode() + " DB";
        tv5.setText(stid);
        iv.setImageBitmap(b);
        return view;
    }


    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        if (i + i1 != 0 && i + i1 == i2 && !loading) {
            if (nextLocalLimit > 0 || nextRemoteLimit > 0 || !thresholdList.isEmpty()) {
                loading = true;
                load();
            }
        }
    }

    private void load() {
        view.showLoading(true);
        Observable<Object> o = load(this);
        o.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        loading = false;
                        notifyDataSetChanged();
                        view.showLoading(false);
                        Log.i("LIST_ADAPTER", "data set changed");
                    }
                });
    }


    private Observable<Object> load(final OnDataArrivedListener listener) {
        Observable<List<Output>> o1 = storage.loadLimitedMergedData(nextLocalLimit, nextLocalStart, nextRemoteLimit, nextRemoteEndAt, thresholdList, user);
        return o1.flatMap(new Function<List<Output>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(@NonNull List<Output> outputs) throws Exception {

                Log.i("LIST_ADAPTER", "merged list size: " + outputs.size());

                Observable<String> s1 = remoteKey(outputs);
                Observable<List<Output>> s2 = thresholdData(outputs);
                Observable<List<ItemHolder>> s3 = preparedData(outputs);

                return Observable.zip(s1, s2, s3, new Function3<String, List<Output>, List<ItemHolder>, Object>() {
                    @Override
                    public Boolean apply(@NonNull String s, @NonNull List<Output> outputs, @NonNull List<ItemHolder> itemHolders) throws Exception {
                        Log.i("LIST_ADAPTER", "Threshold size: " + outputs.size());
                        Log.i("LIST_ADAPTER", "Data size: " + itemHolders.size());
                        listener.odDataArrived(itemHolders, outputs, s);
                        return true;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        });
    }


    /**
     * Initialization step
     * Get remote & local record count, zip them, update limits and start/end points for further load() operation
     * flatMap those and finally load initial N (N = limit) items to listview
     */
    private void initialize() {
        view.showLoading(true);
        loadedData = new ArrayList<>();
        thresholdList = new ArrayList<>();

        Observable<Long> o1 = storage.localCount(user);
        Observable<Integer> o2 = storage.remoteCount(user);
        Observable.zip(o1, o2, new BiFunction<Long, Integer, Object>() {

            @Override
            public Object apply(@NonNull Long aLong, @NonNull Integer integer) throws Exception {
                localCount = aLong.intValue();
                Log.i("LIST_ADAPTER.init", "local count: " + localCount);
                nextLocalStart = (localCount - limit) < 0 ? 0 : (localCount - limit);
                nextLocalLimit = localCount < limit ? localCount : limit;

                remoteCount = integer;
                Log.i("LIST_ADAPTER.init", "remote count: " + remoteCount);

                nextRemoteEndAt = null;
                nextRemoteLimit = remoteCount < limit ? remoteCount : limit;

                Log.i("LA.init", "nextLocalStart: " + nextLocalStart);
                Log.i("LA.init", "nextLocalLimit: " + nextLocalLimit);
                Log.i("LA.init", "nextRemoteEndAt: " + nextRemoteEndAt);
                Log.i("LA.init", "nextRemoteLimit: " + nextRemoteLimit);
                return true;
            }
        }).flatMap(new Function<Object, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(@NonNull Object o) throws Exception {
                return load(OutputListAdapter.this).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        loading = false;
                        notifyDataSetChanged();
                        Log.i("LIST_ADAPTER", "Notify data set changed");
                        view.showLoading(false);
                    }
                });

    }

    /**
     * Get the oldest remote Output key
     */
    private Observable<String> remoteKey(final List<Output> list) {
        return Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String key = "";
                Output o = null;
                for (int i = list.size()-1; i >= 0; i--) {
                    o = list.get(i);
                    if (o.getMode().equals("REMOTE")) {
                        key = o.getId();
                        break;
                    }
                }
                return key;
            }
        });
    }

    /**
     * Get first N (N = limit) results (or less, depending on incoming list size) and load necessary stuff
     * @param list loaded and merged Outputs (remote + local + threshold)
     * @return Observable with newly prepared listview entries
     */
    private Observable<List<ItemHolder>> preparedData(final List<Output> list) {

        return Observable.fromCallable(new Callable<List<ItemHolder>>() {
            @Override
            public List<ItemHolder> call() throws Exception {
                int end = limit < list.size() ? limit : list.size();
                List<Output> bestN = list.subList(0, end);
                List<ItemHolder> data = new ArrayList<ItemHolder>(end);

                for (Output o : bestN) {
                    String latlng = o.getLatitude() + "," + o.getLongitude();
                    URL url = new URL(BASE_URL + latlng + P2 + latlng + key + view.getResources().getString(R.string.static_maps_key));
                    Bitmap b = BitmapFactory.decodeStream(url.openStream());
                    data.add(new ItemHolder(b, o));
                }
                return data;
            }
        });
    }

    private Observable<List<Output>> thresholdData(final List<Output> list) {
        return Observable.fromCallable(new Callable<List<Output>>() {
            @Override
            public List<Output> call() throws Exception {
                if (list.size() <= limit) {
                    return new ArrayList<Output>();
                }
                return list.subList(limit, list.size());
            }
        });
    }

    @Override
    public void odDataArrived(List<ItemHolder> data, List<Output> threshold, String olderKey) {

        //update existing limits, start/end points for future load()
        localCount -= nextLocalLimit;
        nextLocalStart = (localCount - limit) < 0 ? 0 : (localCount - limit);
        nextLocalLimit = localCount < limit ? localCount : limit;

        remoteCount -= nextRemoteLimit;
        nextRemoteLimit = remoteCount < limit ? remoteCount : limit;
        nextRemoteEndAt = olderKey.isEmpty() ? nextRemoteEndAt : olderKey;

        loadedData.addAll(data);
        thresholdList = threshold;


        Log.i("LA.init", "localCount: " + localCount);
        Log.i("LA.init", "remoteCount: " + remoteCount);
        Log.i("LA.init", "nextLocalStart: " + nextLocalStart);
        Log.i("LA.init", "nextLocalLimit: " + nextLocalLimit);
        Log.i("LA.init", "nextRemoteEndAt: " + nextRemoteEndAt);
        Log.i("LA.init", "nextRemoteLimit: " + nextRemoteLimit);
    }

    @Override
    public void onRefresh() {
        loadedData.clear();
        thresholdList.clear();
        initialize();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        //left blank
    }


    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
        Log.i("onMenuItemCLick", "Delete clicked");
        ItemHolder i = loadedData.remove(position);
        i.getBitmap().recycle();
        Observable<Output> o = storage.delete(i.getOutput(), user);
        o.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Output>() {
                    @Override
                    public void accept(@NonNull Output output) throws Exception {
                        notifyDataSetChanged();
                    }
                });
        return false;
    }
}
