package com.example.volleyapp2.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.volleyapp2.adapters.Article;
import com.example.volleyapp2.adapters.ArticleAdapter;
import com.example.volleyapp2.adapters.InfiniteScrollListener;
import com.example.volleyapp2.background.MyReceiver;
import com.example.volleyapp2.activities.ActivityPageArt;
import com.example.volleyapp2.R;
import com.example.volleyapp2.methods.VolleyApplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    public static final String PREF = "MyPref";
    public static final String DEF_VALUE = "";
    public static final String SHOW_NEWS = "show_news";
    public static final String URI = "uri";
    public static final String TITLE = "title";
    public static final String ARTICLE = "article";
    public static final String H2 = "h2";
    public static final String PREVIEW = "preview";
    public static final String IMG = "img";
    public static final String SRC = "src";
    public static final String TIME = "time";
    public static final String A = "a";
    public static final String HREF = "href";
    public static final String FORMAT = "/page%d/";
    public static final String URL = "url";

    final String SAVED_FIRST_NEW = "saved_first_new";

    private ArticleAdapter mAdapter;
    private SwipeRefreshLayout  swipeRefreshLayout;
    private List<Article> Articles=new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private PendingIntent pendingIntent;
    private         ListView listView;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new ArticleAdapter(getActivity());
        listView = (ListView) getView().findViewById(R.id.list1);
        listView.setAdapter(mAdapter);
        loadNewArtsToAdapter();
        startNotification();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view	= inflater.inflate(R.layout.fragment_popitka, container, false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(getActivity(), ActivityPageArt.class);
                Article currentNews = mAdapter.getItem(position);
                i.putExtra(URI, currentNews.getUri());
                i.putExtra(TITLE, currentNews.getTitle());
                i.putExtra(URL, currentNews.getUrl());
                startActivity(i);
            }
        });
        listView.setOnScrollListener(new InfiniteScrollListener(10) {
            @Override
            public void loadMore(int page, int totalItemsCount) {
                sharedPreferences = getActivity().getSharedPreferences(PREF, Context.MODE_PRIVATE);
                String savedText = sharedPreferences.getString(SAVED_FIRST_NEW, DEF_VALUE);
                if (!savedText.equals(mAdapter.getItem(0).getDat())) {
                    sharedPreferences = getActivity().getSharedPreferences(PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SAVED_FIRST_NEW, mAdapter.getItem(0).getDat());
                    editor.commit();
                }
                loadNewArtsToAdapter();
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        Intent intent=getActivity().getIntent();
        String title=intent.getAction();
        if (!title.equals(null)) {
            if (title.equals(SHOW_NEWS)){
                Intent i = new Intent(getActivity(), ActivityPageArt.class);
                i.putExtra(URI, intent.getStringExtra(URI));
                i.putExtra(TITLE, intent.getStringExtra(TITLE));
                startActivity(i);
            }
        }
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();


    }
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                upDate();
            }
        }, 2000);
        upDate();
    }

    /**
     * This method needs when you swipe your list and want to get new news
     */
    private void upDate() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getString(R.string.adress),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<Article> parsedNewsList = myParser(response);
                        Articles = parsedNewsList;
                        mAdapter.swapArticleRecords(Articles);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        VolleyApplication.getInstance().getRequestQueue().add(stringRequest);
    }




    /**
     * Use this method when you need to start notification with new news
     */
    private void startNotification() {
        Intent myIntent = new Intent(getActivity(), MyReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, SystemClock.elapsedRealtime(), 10000, pendingIntent);
    }
    /**
     * This method loads new articles to your list view
     */
    private void loadNewArtsToAdapter() {
        String thisRequest = getString(R.string.adress);
        if (Articles.size() >9) {
            int multiplicity = Articles.size() / 10+1;
            thisRequest += String.format(FORMAT, multiplicity);
        }
        StringRequest request = new StringRequest(Request.Method.GET, thisRequest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String data) {
                        List<Article> NewArts = myParser(data);

                        Articles.addAll(NewArts);

                        mAdapter.swapArticleRecords(Articles);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }
        );
        VolleyApplication.getInstance().getRequestQueue().add(request);

    }

    /**
     * This method parses HTML string and return article list
     * @param data HTML string for parsing
     * @return article list
     */
    private List<Article> myParser(String data) {
        Document doc = Jsoup.parse(data);
        List<Article> NewArts=new ArrayList<>();
        Elements metaElements = doc.getElementsByTag(ARTICLE);
        for (Element thisArt : metaElements) {
            String title = thisArt.select(H2).text();
            String url;
            if (!thisArt.getElementsByClass(PREVIEW).isEmpty())
                url = thisArt.select(IMG).attr(SRC);
            else
                url= DEF_VALUE;
            String dt = thisArt.select(TIME).text();
            String uri = thisArt.select(H2).select(A).attr(HREF);
            Article art = new Article(url, title, dt, uri);
            NewArts.add(art);
        }
        return NewArts;
    }


}
