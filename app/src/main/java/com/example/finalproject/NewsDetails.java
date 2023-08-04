package com.example.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NewsDetails extends AppCompatActivity {

    private String articleTitle;
    private String articleDescription;
    private String articleDate;
    private String articleLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail);

        TextView newsTitleTextView = findViewById(R.id.newsTitleTextView);
        TextView newsDateTextView = findViewById(R.id.newsDateTextView);
        TextView newsDescriptionTextView = findViewById(R.id.newsDescriptionTextView);
        Button addToFavoriteButton = findViewById(R.id.addToFavoriteButton1);

        Intent intent = getIntent();
        if (intent != null) {
            articleTitle = intent.getStringExtra("title");
            articleDescription = intent.getStringExtra("description");
            articleDate = intent.getStringExtra("date");
            articleLink = intent.getStringExtra("link");

            newsTitleTextView.setText(articleTitle);
            newsDateTextView.setText(articleDate);
            newsDescriptionTextView.setText(articleDescription);
        }

        addToFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFavorites();
            }
        });
    }

    private void addToFavorites() {
        FavouriteNews favouriteNewsActivity = new FavouriteNews();
        favouriteNewsActivity.addFavoriteNewsItem(articleTitle, articleDescription);
        Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
    }

    public void openArticleLink(View view) {
        if (articleLink != null && !articleLink.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(articleLink));
            startActivity(intent);
        }
    }
}

