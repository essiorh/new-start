package com.example.volleyapp2;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * A placeholder fragment containing a simple view.
 */
public class PageArtFragment extends Fragment {
    public PageArtFragment() {
    }
    TextView htmlTextView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view			= inflater.inflate(R.layout.fragment_page_art, container, false);
        htmlTextView = (TextView) view.findViewById(R.id.html_text);


        Intent intent		= getActivity().getIntent();
        String urik		= intent.getStringExtra("uri");
        String newsTitle	= intent.getStringExtra("title");
        PageArt rod=(PageArt)getActivity();
        if(rod != null)
            rod.setTitle(newsTitle);
        fetch(urik);
        return view;
    }

    private void fetch(String uri) {
        StringRequest request = new StringRequest(Request.Method.GET, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String data) {
                        Document doc = Jsoup.parse(data);
                        htmlTextView.setText(Html.fromHtml(doc.getElementsByClass("wraps").toString(), new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                LevelListDrawable d = new LevelListDrawable();

                                return d;
                            }
                        },null));

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

}

