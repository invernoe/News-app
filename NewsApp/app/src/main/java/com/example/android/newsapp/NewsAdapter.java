package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, List<News> news){
        super(context,0,news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the position of the current News instance to be displayed.
        News currentNews = getItem(position);

        TextView titleView = listItemView.findViewById(R.id.title);
        titleView.setText(currentNews.getTitle());

        TextView sectionView = listItemView.findViewById(R.id.section);
        sectionView.setText(currentNews.getSection());

        // Split the value of the date and time to display only date by using the Letter T as the regex.
        TextView dateView = listItemView.findViewById(R.id.date);
        String date = currentNews.getPublicationDate();
        String[] dateAndTime = date.split("T");
        dateView.setText(dateAndTime[0]);

        TextView authorView = listItemView.findViewById(R.id.author);

        // if the author's name is editorial means it is anonymous therefore the TextView for the author is unneeded
        // and we should remove it, otherwise display the textView as is
        if (currentNews.getAuthor().equals("Editorial")){
            authorView.setVisibility(View.GONE);
        } else{
            authorView.setText(currentNews.getAuthor());
            authorView.setVisibility(View.VISIBLE);
        }

        return listItemView;
    }
}
