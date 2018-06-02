package com.example.danie.newsapp;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    public static NewsArrayAdapter mAdapter;

    public TextView mEmptyStateTextView;
    public ImageView mEmptyStateImageView;
    public ProgressBar mEmptyProgressBar;
    public SwipeRefreshLayout mSwipeRefreshLayout;

    private static final int NEWS_LOADER_ID = 1;
    private static final String SHOW_TAG = "contributor";
    private static final String API_KEY = "test";
    private static final String GUARDIAN_CONTENT =
            "http://content.guardianapis.com/search";
//            "http://content.guardianapis.com/search?q=technology,iot,cyber%20security&api-key=test&show-tags=contributor";

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int id, @Nullable Bundle args) {

        // Create a SharedPreferences obj
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences
        // 1st param = key; 2nd param = default value
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_orderby_key),
                getString(R.string.settings_orderby_default));

        String section = sharedPrefs.getString(
                getString(R.string.settings_section_key),
                getString(R.string.settings_section_default));

        String query = sharedPrefs.getString(
                getString(R.string.settings_query_key),
                getString(R.string.settings_query_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_CONTENT);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value
        uriBuilder.appendQueryParameter("section", section);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-tags", SHOW_TAG);
        uriBuilder.appendQueryParameter("q", query);
        uriBuilder.appendQueryParameter("api-key", API_KEY);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> news) {
        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);

        mAdapter.clear();

        // If there is a valid list of {@link News}s, then add them to the adapter's data set
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find reference for the Swipe Refresh:
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);

        // Create the empty State Views:
        mEmptyStateTextView = findViewById(R.id.empty_view);
        mEmptyProgressBar = findViewById(R.id.loading_spinner);
        mEmptyStateImageView = findViewById(R.id.empty_image);

        // Find a reference to the ListView:
        ListView newsListView = findViewById(R.id.n_list);

        // Create a new Array Adapter that takes an empty list of news articles:
        mAdapter = new NewsArrayAdapter(this, new ArrayList<News>());

        // Set the adapter on the {@link ListView}:
        newsListView.setAdapter(mAdapter);

        // Create the Loader Manager:
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader.
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);

        // Set onItemClickListener to send an intent to a web browser and open the URL
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

        // Set OnScrollListener stub
        newsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                System.out.println("Just a stub");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0);
            }
        });

        // This will update the news feed
        final LoaderManager finalLoaderManager = loaderManager;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            mSwipeRefreshLayout.setRefreshing(true);
            finalLoaderManager.initLoader(NEWS_LOADER_ID, null, MainActivity.this);
            mSwipeRefreshLayout.setRefreshing(false);

            // Write a Toast to let the user know the data was refreshed:
            Toast refreshToast = Toast.makeText(MainActivity.this, "Updating News", Toast.LENGTH_SHORT);
            refreshToast.show();
            }
        });

        // Create the Connectivity Manager Object to test network connectivity:
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        // Create a bool for easier access:
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // if not connected to the internet do this:
        if (!isConnected) {

            // hide the ProgressBar
            mEmptyProgressBar.setVisibility(View.GONE);

            // Set Text for no internet connectivity and tweak some
            mEmptyStateTextView.setText(R.string.no_wifi);
            mEmptyStateTextView.setGravity(Gravity.CENTER);

            // Set image resource to show the no wifi image and tweak some
            mEmptyStateImageView.setImageResource(R.drawable.no_wifi);
            mEmptyStateImageView.setVisibility(View.VISIBLE);

            // Set the empty views
            newsListView.setEmptyView(mEmptyStateTextView);
            newsListView.setEmptyView(mEmptyStateImageView);
        }
        // otherwise do this:
        else {

            // Create the Loader Manager:
            loaderManager = getLoaderManager();

            // Initialize the loader.
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        }
    }
}

