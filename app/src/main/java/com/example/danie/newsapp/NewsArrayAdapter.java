package com.example.danie.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewsArrayAdapter extends ArrayAdapter<News>{

    public NewsArrayAdapter(@NonNull Context context, ArrayList<News> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Get the data item for this position
        News news = getItem(position);

        View ListView = convertView;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main_textviews, parent, false);
        }

        TextView nTitle = convertView.findViewById(R.id.nTitle);
        TextView nSection = convertView.findViewById(R.id.nSection);
        TextView nAuthor = convertView.findViewById(R.id.nAuthor);
        TextView nDate = convertView.findViewById(R.id.nDate);

        nTitle.setText(news.getTitle());
        nSection.setText(news.getSection());
        nAuthor.setText(news.getAuthorName());
        nDate.setText(news.getNewsDate());

        return convertView;
    }
}

