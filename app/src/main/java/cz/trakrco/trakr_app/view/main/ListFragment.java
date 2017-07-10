package cz.trakrco.trakr_app.view.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import cz.trakrco.trakr_app.R;
import cz.trakrco.trakr_app.domain.ItemHolder;
import cz.trakrco.trakr_app.di.components.DaggerListViewComponent;
import cz.trakrco.trakr_app.di.modules.ListViewModule;
import cz.trakrco.trakr_app.presenter.main.OutputListAdapter;
import cz.trakrco.trakr_app.view.dialog.DetailDialogFragment;

/**
 * Created by vlad on 03/07/2017.
 */

public class ListFragment extends Fragment {

    @BindView(R.id.list_view)
    SwipeMenuListView listView;

    @Inject
    OutputListAdapter adapter;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_list, container, false);
        ButterKnife.bind(this, view);

        DaggerListViewComponent.builder()
                .mainActivityComponent(((MainActivity)getActivity()).getComponent())
                .listViewModule(new ListViewModule(this))
                .build().inject(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(adapter);
        listView.setMenuCreator(init());
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        listView.setOnMenuItemClickListener(adapter);

        swipeRefreshLayout.setOnRefreshListener(adapter);

    }

    @OnItemClick(R.id.list_view)
    public void onItemCLicked(int position) {
        Bundle b = new Bundle();
        ItemHolder i = (ItemHolder) adapter.getItem(position);
        b.putParcelable("output", i.getOutput());
        b.putParcelable("image", i.getBitmap());
        DetailDialogFragment ddf = new DetailDialogFragment();
        ddf.setArguments(b);
        ddf.show(getChildFragmentManager(), "DIALOG");
    }

    public void showLoading(boolean b) {
        swipeRefreshLayout.setRefreshing(b);
    }

    private SwipeMenuCreator init() {
        return new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem delete = new SwipeMenuItem(getContext());
                delete.setBackground(R.color.errorRed);
                delete.setWidth(dp2px(150));
                delete.setIcon(R.drawable.garbage32);
                menu.addMenuItem(delete);
            }
        };
    }

    private int dp2px(int dp) {
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (dm.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
