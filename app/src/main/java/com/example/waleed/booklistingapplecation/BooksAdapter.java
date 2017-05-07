package com.example.waleed.booklistingapplecation;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Waleed on 05/05/17.
 */

public class BooksAdapter extends ArrayAdapter<Books> {

    private ArrayList<Books> books;

    public BooksAdapter(Activity context) {
        super(context, 0);
        books = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        // Get the Books object located at this position in the list
        Books currentAndroidFlavor = books.get(position);

        // Find the TextView in the list_item.xml layout with the ID title_name
        TextView nameTextView = (TextView) listItemView.findViewById(R.id.title_name);
        // Get the book name from the current Books object and
        // set this text on the name TextView
        nameTextView.setText(currentAndroidFlavor.getTitle());

        // Find the TextView in the list_item.xml layout with the ID author_name
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author_name);
        // Get the author name from the current Books object and
        // set this text on the name TextView
        authorTextView.setText(currentAndroidFlavor.getAuthor().toString().replace('[',' ').replace(']',' ').replace('\"',' '));

        ImageView img = (ImageView)listItemView.findViewById(R.id.list_item_icon);
        img.setImageBitmap(currentAndroidFlavor.getBitmap());

        return listItemView;
    }

    public void update(ArrayList<Books> books){
        this.books = books;
        notifyDataSetChanged();
    }

    public ArrayList<Books> getBooks(){
        return books;
    }

//    @Override
//    public int getCount() {
//        return books.size();
//    }
}
