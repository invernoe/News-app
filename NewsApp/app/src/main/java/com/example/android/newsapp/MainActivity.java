package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    // NewsAdapter being a global variable as we need to use it in LoaderManager Callbacks.
    private NewsAdapter mAdapter;
    // Progress bar view of the loading indicator.
    private ProgressBar mLoadingIndicator;
    // Empty view to display when the list is empty.
    private TextView mEmptyView;

    private static final String GUARDIAN_API_URL = "https://content.guardianapis.com/search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.list);

        mLoadingIndicator = findViewById(R.id.loading_spinner);

        mEmptyView = findViewById(R.id.empty_view);

        // Set empty view for the list View
        listView.setEmptyView(mEmptyView);

        // Create a NewsAdapter with an empty placeholder list so we can initiate it and set it to the listView
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        listView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check if there is network connectivity or not.
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        // If there is a network connection, initiate the Loader as planned
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()){

        getLoaderManager().initLoader(0, null, this);
        }
        // Otherwise, remove loading indicator and display no internet message.
        else{
            mLoadingIndicator.setVisibility(View.GONE);
            mEmptyView.setText(getString(R.string.no_internet));
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(mAdapter.getItem(position).getUrl()));
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu we have in the settings_menu.xml file
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        // Return true to indicate method completion
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get id of the item selected, if it is the settings item, start settings activity.
        if (item.getItemId() == R.id.settings){
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String searchValue = sharedPref.getString(getString(R.string.settings_search_key),
                getString(R.string.settings_search_default));

        String sections = sharedPref.getString(getString(R.string.settings_sections_key),
                getString(R.string.settings_sections_default));

        Uri baseUri = Uri.parse(GUARDIAN_API_URL);

        // buildUpon prepares the baseUri for the additional information that we are going to supply it with
        // based on the user's input
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", searchValue);
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("section", sections);
        uriBuilder.appendQueryParameter("page-size", "15");
        uriBuilder.appendQueryParameter("from-date", "2019-01-01");
        uriBuilder.appendQueryParameter("api-key", "test");

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        // Clear placeholder data in adapter
        mAdapter.clear();

        // If the reference is not null and the list is not empty proceed to add the data
        if (data != null && !data.isEmpty())
            // Put the actual data so it can be displayed in the UI.
            mAdapter.addAll(data);

        // Hide loading indicator because the data has been loaded or there is no data at all.
        mLoadingIndicator.setVisibility(View.GONE);
        // set Text to inform the user that there is no news if the data fails to load or there is no data to be displayed.
        mEmptyView.setText(getString(R.string.no_news));
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Clear all data as the loader is no longer needed or is being reset
        mAdapter.clear();
    }
}
