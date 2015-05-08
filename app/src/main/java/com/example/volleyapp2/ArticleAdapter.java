package com.example.volleyapp2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Article> {
    private ImageLoader mImageLoader;

    public ArticleAdapter(Context context) {
        super(context, R.layout.image_list_item);

        mImageLoader = new ImageLoader(VolleyApplication.getInstance().getRequestQueue(), new BitmapLruCache());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.image_list_item, parent, false);
        }

        // NOTE: You would normally use the ViewHolder pattern here
        NetworkImageView imageView = (NetworkImageView) convertView.findViewById(R.id.image1);
        TextView textView = (TextView) convertView.findViewById(R.id.text1);
        TextView dateView=(TextView)convertView.findViewById(R.id.date1);
        Article imageRecord = getItem(position);

        imageView.setImageUrl(imageRecord.getUrl(), mImageLoader);
        textView.setText(imageRecord.getTitle());
        dateView.setText(imageRecord.getDat());
        return convertView;
    }

    public void swapArticleRecords(List<Article> objects) {
        clear();

        for(Article object : objects) {
            add(object);
        }

        notifyDataSetChanged();
    }
}
