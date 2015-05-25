package com.example.volleyapp2;

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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class PopitkaFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private ArticleAdapter mAdapter;
    private SwipeRefreshLayout  swipeRefreshLayout;
    private List<Article> Articles=new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private PendingIntent pendingIntent;
    final String SAVED_FIRST_NEW = "saved_first_new";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view	= inflater.inflate(R.layout.fragment_popitka, container, false);
        ListView listView=(ListView)view.findViewById(R.id.list1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent i = new Intent(getActivity(), PageArt.class);
                Article currentNews = mAdapter.getItem(position);

                i.putExtra("uri", currentNews.getUri());
                i.putExtra("title", currentNews.getTitle());
                i.putExtra("url", currentNews.getUrl());
                startActivity(i);

            }
        });
        listView.setOnScrollListener(new InfiniteScrollListener(10) {

            @Override
            public void loadMore(int page, int totalItemsCount) {
                sharedPreferences = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                String savedText = sharedPreferences.getString(SAVED_FIRST_NEW, "");
                if (!savedText.equals(mAdapter.getItem(0).getDat())) {
                    sharedPreferences = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SAVED_FIRST_NEW, mAdapter.getItem(0).getDat());
                    editor.commit();
                }
                fetch();
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        Intent intent=getActivity().getIntent();
        String title=intent.getAction();
        if (!title.equals(null)) {
            if (title.equals("show_news")){
                Intent i = new Intent(getActivity(), PageArt.class);
                i.putExtra("uri", intent.getStringExtra("uri"));
                i.putExtra("title", intent.getStringExtra("title"));
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

    private void upDate() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getString(R.string.adress),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        List<Article> parsedNewsList = myParser(response);
                        Articles=parsedNewsList;
                        mAdapter.swapArticleRecords(Articles);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        VolleyApplication.getInstance().getRequestQueue().add(stringRequest);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new ArticleAdapter(getActivity());
        ListView listView = (ListView) getView().findViewById(R.id.list1);
        listView.setAdapter(mAdapter);
        fetch();

        Intent myIntent = new Intent(getActivity(), MyReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, SystemClock.elapsedRealtime(),10000, pendingIntent);

    }

    private void fetch() {
        String thisZapros = getString(R.string.adress);
        if (Articles.size() >9) {
            int kratn = Articles.size() / 10+1;
            thisZapros += String.format("/page%d/", kratn);
        }
        
        StringRequest request = new StringRequest(Request.Method.GET, thisZapros,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String data) {
                        List<Article> NewArts=myParser(data);

                        Articles.addAll(NewArts);

                        mAdapter.swapArticleRecords(Articles);
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

    private List<Article> myParser(String data) {
        Document doc = Jsoup.parse(data);
        List<Article> NewArts=new ArrayList<>();
        Elements metaElems = doc.getElementsByTag("article");
        for (Element thisArt : metaElems) {
            String title = thisArt.select("h2").text();
            String url;
            if (!thisArt.getElementsByClass("preview").isEmpty())
                url = thisArt.select("img").attr("src");
            else
                url="";
            String dt = thisArt.select("time").text();
            String uri = thisArt.select("h2").select("a").attr("href");
            Article art = new Article(url, title, dt, uri);
            NewArts.add(art);
        }
        return NewArts;
    }

    public abstract class InfiniteScrollListener implements AbsListView.OnScrollListener {
        private int bufferItemCount = 10;
        private int currentPage = 0;
        private int itemCount = 0;
        private boolean isLoading = true;

        public InfiniteScrollListener(int bufferItemCount) {
            this.bufferItemCount = bufferItemCount;
        }

        public abstract void loadMore(int page, int totalItemsCount);

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // Do Nothing
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            if (totalItemCount < itemCount) {
                this.itemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.isLoading = true; }
            }

            if (isLoading && (totalItemCount > itemCount)) {
                isLoading = false;
                itemCount = totalItemCount;
                currentPage++;
            }

            if (!isLoading && (totalItemCount - visibleItemCount)<=(firstVisibleItem + bufferItemCount)) {
                loadMore(currentPage + 1, totalItemCount);
                isLoading = true;
            }
        }
    }
}
