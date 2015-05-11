package com.example.volleyapp2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * A placeholder fragment containing a simple view.
 */

public class PageArtFragment extends Fragment {
    static final Map<String, WeakReference<Drawable>> mDrawableCache = Collections.synchronizedMap(new WeakHashMap<String, WeakReference<Drawable>>());
    public PageArtFragment() {
    }
    TextView htmlTextView;
    String uri;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view			= inflater.inflate(R.layout.fragment_page_art, container, false);
        htmlTextView = (TextView) view.findViewById(R.id.html_text);
        htmlTextView.setClickable(true);
        htmlTextView.setMovementMethod(new LinkMovementMethod());

        Intent intent		= getActivity().getIntent();
        uri		= intent.getStringExtra("uri");
        String newsTitle	= intent.getStringExtra("title");
        PageArt rod=(PageArt)getActivity();
        if(rod != null)
            rod.setTitle(newsTitle);
        fetch();
        return view;
    }
    private void fetch() {


        StringRequest request = new StringRequest(Request.Method.GET, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String data) {
                        Document doc = Jsoup.parse(data);
                        String text = doc.select(".topic-content.text").html();
                        Spanned spannedText = Html.fromHtml(text,
                                new Html.ImageGetter() {
                                    public Drawable getDrawable(String source) {


                                        new ImageDownloadAsyncTask(source, htmlTextView).execute(htmlTextView);
                                        //ѕока он скачиваетс€ устанавливаем пустой рисунок
                                        LevelListDrawable ret = new LevelListDrawable();
                                        Drawable empty = getResources().getDrawable(R.drawable.abc_btn_check_material);
                                        ;
                                        ret.addLevel(0, 0, empty);
                                        ret.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
                                        return ret;
                                    }
                                }, null);
                        //Spannable reversedText = revertSpanned(spannedText);
                        htmlTextView.setText(spannedText);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // Handle error
                    }
                }
        );
        VolleyApplication.getInstance().getRequestQueue().add(request);

    }

    class ImageDownloadAsyncTask extends AsyncTask<TextView, Void, Bitmap> {
        private String source;
        private TextView textView;

        public ImageDownloadAsyncTask(String source, TextView textView) {
            this.source = source;
            this.textView = textView;
        }
        @Override
        protected Bitmap doInBackground(TextView... params) {
            textView = params[0];
            try {
                return Picasso.with(getActivity()).load(source).get();

            } catch (Exception t) {
                return null;
            }
        }


    }
}


