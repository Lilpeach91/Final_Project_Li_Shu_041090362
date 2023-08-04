package com.example.finalproject;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText searchEditText;
    private TextView headlineTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchEditText = findViewById(R.id.searchEditText);
        Button searchButton = findViewById(R.id.searchButton);
        headlineTextView = findViewById(R.id.newsHeadlineTextView);
        progressBar = findViewById(R.id.progressBar);
        Locale locale = new Locale("fr");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        ImageView homeIcon = toolbar.findViewById(R.id.homeIcon);
        homeIcon.setOnClickListener(v -> {
            goToHomePage();
            new SearchTask().execute();

        });

        searchButton.setOnClickListener(v -> {
            String keyword = searchEditText.getText().toString().trim();
            if (!keyword.isEmpty()) {
                new SearchTask().execute(keyword);
            } else {
                headlineTextView.setText("Please enter a keyword to search.");
            }
        });

        new SearchTask().execute();
    }

    private void goToHomePage() {
        Toast.makeText(this, "Go to Home Page", Toast.LENGTH_SHORT).show();
    }

    private class SearchTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            headlineTextView.setVisibility(View.GONE);
        }

        protected String doInBackground(String... params) {
            if (params != null && params.length > 0) {
                String keyword = params[0];
                return searchArticles(keyword);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String searchResults) {
            super.onPostExecute(searchResults);
            progressBar.setVisibility(View.GONE);
            headlineTextView.setVisibility(View.VISIBLE);
            if (searchResults != null && !searchResults.isEmpty()) {
                headlineTextView.setText(searchResults);
                showSnackbar(searchResults);
            } else {
                headlineTextView.setText("No results found.");
            }
        }

        private String searchArticles(String keyword) {
            String searchResults = "";
            try {
                URL url = new URL("http://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream inputStream = conn.getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(inputStream, null);

                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG && parser.getName().equals("title")) {
                        parser.next();
                        String title = parser.getText().trim();
                        String keywordLower = keyword.toLowerCase();
                        if (title.toLowerCase().contains(keywordLower)) {
                            searchResults += title + "\n";
                        }
                    }
                    eventType = parser.next();
                }

                inputStream.close();
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
            return searchResults;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_help) {
            showHelpDialog();
            return true;
        } else if (id == R.id.menu_favorites) {
            openFavorites();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFavorites() {
        Intent intent = new Intent(this, FavouriteNews.class);
        startActivity(intent);
    }

    private void openNewsDetailActivity(String headline) {
        Intent intent = new Intent(this, NewsDetails.class);
        intent.putExtra("title", headline);
        startActivity(intent);
    }

    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Help");
        builder.setView(R.layout.dialog_help);

        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSnackbar(String message) {
        View rootView = findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }
}
