package com.example.volleyapp2.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
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
import com.example.volleyapp2.activities.ActivityPageArt;
import com.example.volleyapp2.R;
import com.example.volleyapp2.methods.VolleyApplication;
import com.squareup.picasso.Picasso;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


/**
 * A placeholder fragment containing a simple view.
 * @see android.support.v4.app.Fragment
 * @author ilia
 */
public class PageArtFragment extends Fragment {

    private final String QUERY = ".topic-content.text";
    private TextView htmlTextView;
    private TextView titleView;
    private String uri;

    /**
     * default constructor
     */
    public PageArtFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_art, container, false);
        htmlTextView = (TextView) view.findViewById(R.id.html_text);
        htmlTextView.setClickable(true);
        htmlTextView.setMovementMethod(new LinkMovementMethod());
        titleView = (TextView) view.findViewById(R.id.title_text);

        Intent intent = getActivity().getIntent();
        uri = intent.getStringExtra(MainActivityFragment.URI);
        titleView.setText(intent.getStringExtra(MainActivityFragment.TITLE));
        ActivityPageArt rod = (ActivityPageArt) getActivity();
        if (rod != null) {
            rod.setTitle(intent.getStringExtra(MainActivityFragment.TITLE));
        }
        leadArticleToHTMLTextView();
        return view;
    }

    /**
     * This method loads article to html text view
     */
    private void leadArticleToHTMLTextView() {
        StringRequest getReq = new StringRequest(Request.Method.GET
                , uri
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String data) {
                Document doc = Jsoup.parse(data);
                String text = doc.select(".topic-content.text").html();
                Spanned spanned = Html.fromHtml(text,
                        new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                LevelListDrawable d = new LevelListDrawable();
                                Drawable empty = getResources().getDrawable(R.drawable.abc_btn_check_material);
                                d.addLevel(0, 0, empty);
                                d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
                                new ImageGetterAsyncTask(getActivity(), source, d).execute(htmlTextView);
                                return d;
                            }
                        }, null);
                htmlTextView.setText(spanned);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        VolleyApplication.getInstance().getRequestQueue().add(getReq);
    }

    /**
     * This class extends AsyncTask and downloads for you an image in background
     * @see android.os.AsyncTask
     * @author ilia
     */
    class ImageGetterAsyncTask extends AsyncTask<TextView, Void, Bitmap>
    {
        private LevelListDrawable levelListDrawable;
        private Context context;
        private String source;
        private TextView mTextView;

        /**
         * Constructor for {@link #ImageGetterAsyncTask(Context, String, LevelListDrawable)}
         * @param context this is your context
         * @param source source of your
         * @param levelListDrawable level your future drawable
         */
        public ImageGetterAsyncTask(Context context, String source, LevelListDrawable levelListDrawable) {
            this.context = context;
            this.source = source;
            this.levelListDrawable = levelListDrawable;
        }

        @Override
        protected Bitmap doInBackground(TextView... params) {
            mTextView = params[0];
            try {
                return Picasso.with(context).load(source).get();
            } catch (Exception e) {
                return null;
            }
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        @Override
        protected void onPostExecute(final Bitmap bitmap) {
            try {
                Drawable d = new BitmapDrawable(getResources(), bitmap);
                Point size = new Point();
                getActivity().getWindowManager().getDefaultDisplay().getSize(size);
                int multiplier = size.x / bitmap.getWidth();
                levelListDrawable.addLevel(1, 1, d);
                levelListDrawable.setBounds(0, 0, bitmap.getWidth() * multiplier, bitmap.getHeight() * multiplier);
                levelListDrawable.setLevel(1);
                mTextView.setText(mTextView.getText());
            } catch (Exception e) {
            }
        }
    }
}


