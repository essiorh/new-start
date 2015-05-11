package com.example.volleyapp2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Point;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
    TextView titleView;
    String uri;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view			= inflater.inflate(R.layout.fragment_page_art, container, false);
        htmlTextView = (TextView) view.findViewById(R.id.html_text);
        htmlTextView.setClickable(true);
        htmlTextView.setMovementMethod(new LinkMovementMethod());
        titleView=(TextView)view.findViewById(R.id.title_text);

        Intent intent		= getActivity().getIntent();
        uri		= intent.getStringExtra("uri");
        String newsTitle	= intent.getStringExtra("title");
        titleView.setText(newsTitle);

        PageArt rod=(PageArt)getActivity();
        if(rod != null)
            rod.setTitle(newsTitle);
        fetch();
        return view;
    }
    private void fetch() {


        StringRequest getReq    = new StringRequest(Request.Method.GET
                , uri
                , new Response.Listener<String>()
        {

            @Override
            public void onResponse(String data)
            {
                Document doc = Jsoup.parse(data);
                String text = doc.select(".topic-content.text").html();

                Spanned spanned = Html.fromHtml(text,
                        new Html.ImageGetter()
                        {
                            @Override
                            public Drawable getDrawable(String source)
                            {
                                LevelListDrawable d = new LevelListDrawable();
                                Drawable empty = getResources().getDrawable(R.drawable.abc_btn_check_material);;
                                d.addLevel(0, 0, empty);
                                d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
                                new ImageGetterAsyncTask(getActivity(), source, d).execute(htmlTextView);

                                return d;
                            }
                        }, null);
                htmlTextView.setText(spanned);

            }
        }, new Response.ErrorListener()
        {

            @Override
            public void onErrorResponse(VolleyError error)
            {
            }
        });
        VolleyApplication.getInstance().getRequestQueue().add(getReq);
    }

    class ImageGetterAsyncTask extends AsyncTask<TextView, Void, Bitmap>
    {
        private LevelListDrawable levelListDrawable;
        private Context context;
        private String source;
        private TextView t;

        public ImageGetterAsyncTask(Context context, String source, LevelListDrawable levelListDrawable)
        {
            this.context = context;
            this.source = source;
            this.levelListDrawable = levelListDrawable;
        }

        @Override
        protected Bitmap doInBackground(TextView... params)
        {
            t = params[0];
            try
            {
                return Picasso.with(context).load(source).get();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final Bitmap bitmap)
        {
            try {
                Drawable d = new BitmapDrawable(getResources(), bitmap);
                Point size = new Point();
                getActivity().getWindowManager().getDefaultDisplay().getSize(size);
                int multiplier = size.x / bitmap.getWidth();
                levelListDrawable.addLevel(1, 1, d);
                levelListDrawable.setBounds(0, 0, bitmap.getWidth() * multiplier, bitmap.getHeight() * multiplier);
                levelListDrawable.setLevel(1);
                t.setText(t.getText());
            } catch (Exception e) {
            }
        }
    }
}


