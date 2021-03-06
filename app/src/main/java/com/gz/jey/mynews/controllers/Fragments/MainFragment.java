package com.gz.jey.mynews.controllers.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gz.jey.mynews.R;
import com.gz.jey.mynews.controllers.activities.MainActivity;
import com.gz.jey.mynews.models.Data;
import com.gz.jey.mynews.models.NewsSection;
import com.gz.jey.mynews.models.Result;
import com.gz.jey.mynews.utils.ApiStreams;
import com.gz.jey.mynews.utils.ItemClickSupport;
import com.gz.jey.mynews.views.NewsAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class MainFragment extends Fragment implements NewsAdapter.Listener{
    // FOR DESIGN
    @BindView(R.id.fragment_main_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.fragment_main_swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.no_result)
    LinearLayout noResult;
    @BindView(R.id.no_result_text)
    TextView noResultTx;
    @BindView(R.id.new_search)
    Button newSearch;

    //FOR DATA
    @SuppressLint("StaticFieldLeak")
    static MainActivity mact;
    private Disposable disposable;
    private ArrayList<Result> results;
    private NewsAdapter newsAdapter;
    private static final String TAG = MainFragment.class.getSimpleName();


    /**
     * the public acces of this fragment
     */
    public MainFragment(){ }

    /**
     * @param mainActivity MainActivity
     * @return new MainFragment()
     */
    public static MainFragment newInstance(MainActivity mainActivity){
        mact = mainActivity;
        return (new MainFragment());
    }

    /**
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        mact.ProgressLoad();
        SetRecyclerView();
        SetSwipeRefreshLayout();
        SetOnClickRecyclerView();
        SetOnClickNewSearch();
        executeHttpRequestWithRetrofit();
        return view;
    }


    /**
     * to Destroy fragment
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        disposeWhenDestroy();
    }

    // -----------------
    // ACTION
    // -----------------

    /**
     * to Set the onClick function from items in RecyclerView
     */
    protected void SetOnClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_main_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Result results = newsAdapter.getNews(position);
                        Data.setUrl(results.getUrl());
                        Toast.makeText(getContext(), "You clicked on news : "+
                                results.getTitle(), Toast.LENGTH_SHORT).show();
                        mact.SetWebView();
                    }
                });
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * to set the RecyclerView
     */
    private void SetRecyclerView(){
        results = new ArrayList<>();
        // Create newsAdapter passing in the sample user data
        newsAdapter = new NewsAdapter(results, Glide.with(this), this);
        // Attach the newsAdapter to the recyclerview to populate items
        recyclerView.setAdapter(newsAdapter);
        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /**
     * to set the OnClick New Search
     */
    private void SetOnClickNewSearch(){
        newSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mact.SetNewsQuery();
            }
        });
    }

    /**
     * to set the SwipeRefreshLayout Listeners
     */
    private void SetSwipeRefreshLayout(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeHttpRequestWithRetrofit();
            }
        });
    }

    /**
     * the onResume function when any datas change from MainActivity
     */
    public void onResume() {
        super.onResume();
        mact.ProgressLoad();
        executeHttpRequestWithRetrofit();
    }



    // -------------------
    // HTTP (RxJAVA)
    // -------------------

    /**
     * the HttpRequest with Retrofit
     */
    private void executeHttpRequestWithRetrofit(){
        switch(Data.getActualTab()) {
            case 0:
                noResultTx.setText(R.string.noResultsCat);
                String ts_cat = mact.getResources().getStringArray(R.array.ts_category)[Data.getSecTop()];
                disposable = ApiStreams.streamFetchTopStories(ts_cat)
                        .subscribeWith(new DisposableObserver<NewsSection>() {
                            /**
                             * @param results NewsSection
                             */
                            @Override
                            public void onNext(NewsSection results) {
                                UpdateUI(results);
                            }

                            /**
                             * @param e Throwable
                             */
                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, e.toString());
                            }

                            /**
                             * to close progressDialog when request is terminated
                             */
                            @Override
                            public void onComplete() {
                                mact.TerminateLoad();
                            }
                        });
                break;
            case 1:
                noResultTx.setText(R.string.noResultsCat);
                String mp_cat = mact.getResources().getStringArray(R.array.mp_category)[Data.getSecMost()];
                String mp_typ = mact.getResources().getStringArray(R.array.mp_type)[Data.gettNum()];
                String mp_per = mact.getResources().getStringArray(R.array.mp_period)[Data.getpNum()];
                disposable = ApiStreams.streamFetchMost(mp_typ, mp_cat,mp_per)
                        .subscribeWith(new DisposableObserver<NewsSection>() {

                            /**
                             * @param results NewsSection
                             */
                            @Override
                            public void onNext(NewsSection results) {
                                UpdateUI(results);
                            }

                            /**
                             * @param e Throwable
                             */
                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, e.toString());
                            }

                            /**
                             * to close progressDialog when request is terminated
                             */
                            @Override
                            public void onComplete() {
                                mact.TerminateLoad();
                            }
                        });
                break;

            case 2:
                noResultTx.setText(R.string.noResultsFilt);
                String query = Data.isLoadNotif()?Data.getNotifQuery():Data.getSearchQuery();
                String fquery = Data.isLoadNotif()?Data.getNotifFilters():Data.getSearchFilters();
                String begin = Data.isLoadNotif()?"":Data.getBeginDate();
                String end = Data.isLoadNotif()?"":Data.getEndDate();
                disposable = ApiStreams.streamFetchASearch(query, fquery, begin, end)
                        .subscribeWith(new DisposableObserver<NewsSection>() {
                            /**
                             * @param results NewsSection
                             */
                            @Override
                            public void onNext(NewsSection results) {
                                UpdateUI(results);
                            }

                            /**
                             * @param e Throwable
                             */
                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, e.toString());
                            }

                            /**
                             * to close progressDialog when request is terminated
                             */
                            @Override
                            public void onComplete() {
                                mact.TerminateLoad();
                            }
                        });
                break;
        }
    }

    /**
     * to destroy disposable and avoid memory leaks
     */
    private void disposeWhenDestroy(){
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
    }

    // -------------------
    // UPDATE UI
    // -------------------

    /**
     * @param news NewsSection
     * called while request get back models
     */
    private void UpdateUI(NewsSection news){
        if(results!= null)
            results.clear();
        else
            results = new ArrayList<>();

        results.addAll(news.getResults());

        if(newSearch==null)
            mact.pager.setCurrentItem(Data.getActualTab());
        else
            if(results.size()!=0) {
                newSearch.setVisibility(View.GONE);
                noResult.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                newsAdapter.notifyDataSetChanged();
            }else{
                noResult.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);

                if(Data.getActualTab()==2)
                    newSearch.setVisibility(View.VISIBLE);
                else
                    newSearch.setVisibility(View.GONE);
            }
        swipeRefreshLayout.setRefreshing(false);
    }

}

