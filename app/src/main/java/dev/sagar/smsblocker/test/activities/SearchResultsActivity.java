package dev.sagar.smsblocker.test.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import dev.sagar.smsblocker.R;

/*
    SearchTag:  #SearchableInterace
    Reason:     Part of Searchable Interface in separate Activity
 */
public class SearchResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        handleIntent(getIntent());
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            Toast.makeText(this, "Query: "+query, Toast.LENGTH_SHORT).show();
        }
    }
}
