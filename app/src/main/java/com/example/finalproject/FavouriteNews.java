package com.example.finalproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavouriteNews extends AppCompatActivity {

    private ArrayList<ListItem> favoriteNewsList;
    private ArrayAdapter<ListItem> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite_news);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView favoriteNewsListView = findViewById(R.id.favoriteNewsListView);
        favoriteNewsList = new ArrayList<>();

        favoriteNewsList.addAll(loadItemsFromDatabase());

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, favoriteNewsList);
        favoriteNewsListView.setAdapter(adapter);

        favoriteNewsListView.setOnItemLongClickListener((parent, view, position, id) -> {
            ListItem deletedNews = favoriteNewsList.get(position);
            showDeleteConfirmationDialog(deletedNews);
            return true;
        });

        findViewById(R.id.addToFavouriteButton).setOnClickListener(v -> showAddItemDialog());

        favoriteNewsListView.setOnItemClickListener((parent, view, position, id) -> {
            ListItem selectedNews = favoriteNewsList.get(position);
            openNewsDetailActivity(selectedNews.getTitle());
        });

    }

    private List<ListItem> loadItemsFromDatabase() {
        List<ListItem> favoriteList = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE);
        Set<String> favoriteNewsSet = sharedPreferences.getStringSet("favoriteList", new HashSet<>());
        for (String newsItemString : favoriteNewsSet) {
            String[] parts = newsItemString.split("##");
            if (parts.length == 2) {
                favoriteList.add(new ListItem(parts[0], parts[1]));
            }
        }
        return favoriteList;
    }

    private void showDeleteConfirmationDialog(ListItem deletedNews) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this news item?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            deleteFavoriteNewsItem(deletedNews);
            Toast.makeText(FavouriteNews.this, "Deleted: " + deletedNews.getTitle(), Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void addFavoriteNewsItem(String title, String description) {
        favoriteNewsList.add(new ListItem(title, description));
        adapter.notifyDataSetChanged();
        saveFavoriteNewsItems();
    }

    private void deleteFavoriteNewsItem(ListItem newsItem) {
        favoriteNewsList.remove(newsItem);
        adapter.notifyDataSetChanged();
        saveFavoriteNewsItems();
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Item");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.news_detail, null);
        final EditText titleEditText = dialogView.findViewById(R.id.newsTitleTextView);
        final EditText descriptionEditText = dialogView.findViewById(R.id.newsDescriptionTextView);
        builder.setView(dialogView);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            if (!title.isEmpty() && !description.isEmpty()) {
                addFavoriteNewsItem(title, description);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveFavoriteNewsItems() {
        Set<String> favoriteNewsSet = new HashSet<>();
        for (ListItem item : favoriteNewsList) {
            String newsItemString = item.getTitle() + "##" + item.getDescription();
            favoriteNewsSet.add(newsItemString);
        }
        SharedPreferences sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("favoriteList", favoriteNewsSet);
        editor.apply();
    }


    private void openNewsDetailActivity(String title) {
        Intent intent = new Intent(this, NewsDetails.class);
        intent.putExtra("title", title);
        startActivity(intent);
    }
}
