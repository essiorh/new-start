package com.example.volleyapp2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.volleyapp2.R;
import com.example.volleyapp2.methods.BitmapLruCache;
import com.example.volleyapp2.methods.VolleyApplication;

import java.util.List;

/**
 * Adapter for store the article list
 * @see android.widget.ArrayAdapter
 * @author ilia
 */
public class ArticleAdapter extends ArrayAdapter<Article> {
    private         ImageLoader mImageLoader;
    private         TextView textView;
    private         TextView dateView;
    private         NetworkImageView imageView;

    public ArticleAdapter(Context context) {
        super(context, R.layout.image_list_item);
        mImageLoader = new ImageLoader(VolleyApplication.getInstance().getRequestQueue(), new BitmapLruCache());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.image_list_item, parent, false);
        }

        imageView = (NetworkImageView) convertView.findViewById(R.id.image1);
        textView = (TextView) convertView.findViewById(R.id.text1);
        dateView = (TextView) convertView.findViewById(R.id.date1);
        Article imageRecord = getItem(position);

        if (imageRecord.getUrl() != "") {
            imageView.setImageUrl(imageRecord.getUrl(), mImageLoader);
        } else {
            imageView.setImageResource(R.drawable.goodline_picture);
        }
        textView.setText(imageRecord.getTitle());
        dateView.setText(imageRecord.getDat());
        return convertView;
    }

    /**
     * This method merges new List objects with old article records
     * @param objects new list objects which must unite
     */
    public void swapArticleRecords(List<Article> objects) {
        clear();

        for (Article object : objects) {
            add(object);
        }
    }
}
