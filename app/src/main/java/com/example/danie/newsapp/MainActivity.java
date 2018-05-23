package com.example.danie.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.app.LoaderManager;
import android.content.Loader;
import android.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    public static NewsArrayAdapter mAdapter;
    public TextView mEmptyStateTextView;
    public ImageView mEmptyStateImageView;
    public ProgressBar mEmptyProgressBar;
    private static final int NEWS_LOADER_ID = 1;
    public SwipeRefreshLayout mSwipeRefreshLayout;
//    private static final String API_KEY = "6cdc5ebc-d547-426b-9a3d-d4cf696d18f1";
    private static final String API_KEY = "test";
    private static final String GUARDIAN_CONTENT =
            "https://content.guardianapis.com/search?order-by=newest&use-date=published&q=information%20security%2C%20cyber%20security%2C%20iot%2C%20business&api-key=" + API_KEY;

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int id, @Nullable Bundle args) {
        return new NewsLoader(this, GUARDIAN_CONTENT);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> news) {
        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No earthquakes found."
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous data
        mAdapter.clear();

        // If there is a valid list of {@link News}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
        mAdapter.clear();
    }

    private static class NewsLoader extends AsyncTaskLoader<List<News>>{

        private String mUrl;

        NewsLoader(@NonNull Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Nullable
        @Override
        public List<News> loadInBackground() {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (mUrl == null) {
                return null;
            }
            return NewsUtils.fetchNews(mUrl);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find reference for the swipe refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.n_list);

        // Create a new adapter that takes an empty list of news articles
        mAdapter = new NewsArrayAdapter(this, new ArrayList<News>());

        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // Create the empty TextView
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mEmptyProgressBar = (ProgressBar) findViewById(R.id.loading_spinner);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        // Set the empty views
        newsListView.setEmptyView(mEmptyStateTextView);

        if (isConnected) {
            mEmptyStateTextView.setText(R.string.no_news);
            mEmptyStateTextView.setGravity(Gravity.CENTER);
            newsListView.setEmptyView(mEmptyStateTextView);
        }
        else {
            mEmptyProgressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.internet_con);
            mEmptyStateTextView.setGravity(Gravity.CENTER);
            newsListView.setEmptyView(mEmptyStateTextView);
        }

        // Create the Loader Manager
        final android.app.LoaderManager loaderManager = getLoaderManager();

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected article.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current article that was clicked on
                News currentNews = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNews.getNewsURL());

                // Create a new intent to view the news article URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        newsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                String LOG_REF = MainActivity.class.getSimpleName();
                Log.e(LOG_REF, "Just a log for the onScrollStateChange.");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                loaderManager.initLoader(NEWS_LOADER_ID, null, MainActivity.this);
                mSwipeRefreshLayout.setRefreshing(false);

                // Write a Toast to let the user know the data was refreshed:
                Toast refreshToast = Toast.makeText(MainActivity.this, "Updating News", Toast.LENGTH_SHORT);
                refreshToast.show();
            }
        });

        // Initialize the loader.
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);
    }
}

