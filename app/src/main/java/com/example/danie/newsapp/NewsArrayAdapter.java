package com.example.danie.newsapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsArrayAdapter extends ArrayAdapter<News>{

    NewsArrayAdapter(@NonNull Context context, ArrayList<News> news) {
        super(context, 0, news);
    }

    private static class NewsHolder {
        private TextView titleTextView;
        private TextView sectionTextView;
        private TextView authorTextView;
        private TextView dateTextView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        NewsHolder newsholder;
        View ListView = convertView;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.activity_main_textviews, parent, false);
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main_textviews, parent, false);

            newsholder = new NewsHolder();
            newsholder.titleTextView = convertView.findViewById(R.id.nTitle);
            newsholder.sectionTextView = convertView.findViewById(R.id.nSection);
            newsholder.authorTextView = convertView.findViewById(R.id.nAuthor);
            newsholder.dateTextView = convertView.findViewById(R.id.nDate);

            convertView.setTag(newsholder);
        } else {
            newsholder = (NewsHolder) convertView.getTag();
        }

        // Get the data item for this position
        News news = getItem(position);

        if (news != null) {
            newsholder.titleTextView.setText(news.getTitle());
            newsholder.sectionTextView.setText(news.getSection());
            newsholder.authorTextView.setText(news.getAuthorName());
            newsholder.dateTextView.setText(news.getNewsDate());

        }

        return convertView;
    }
}

