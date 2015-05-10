package com.example.volleyapp2;

import android.content.Intent;
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
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class PopitkaFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private ArticleAdapter mAdapter;
    private SwipeRefreshLayout  swipeRefreshLayout;
    private List<Article> Articles=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view	= inflater.inflate(R.layout.fragment_popitka, container, false);
        ListView listView=(ListView)view.findViewById(R.id.list1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent i = new Intent(getActivity(), PageArt.class);
                i.putExtra("position", position);
                startActivity(i);

            }
        });
        listView.setOnScrollListener(new InfiniteScrollListener(10) {

            @Override
            public void loadMore(int page, int totalItemsCount) {
                fetch(true);
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new ArticleAdapter(getActivity());
        ListView listView = (ListView) getView().findViewById(R.id.list1);
        listView.setAdapter(mAdapter);
        fetch(true);
    }
    private void fetch(boolean DownOrUp) {
        String thisZapros=getString(R.string.adress);
if(Articles.size()!=0) {
    int kratn=Articles.size()/10;
thisZapros+=String.format("/page%d/",kratn);
}
    StringRequest request = new StringRequest(Request.Method.GET, thisZapros,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String data) {
                    Document doc = Jsoup.parse(data);

                    Elements metaElems = doc.getElementsByTag("article");
                    for (Element thisArt : metaElems) {
                        String title = thisArt.select("h2").text();
                        String url = thisArt.select("img").attr("src");
                        String dt = thisArt.select("time").text();
                        Article art = new Article(url, title, dt);
                        Articles.add(art);
                    }
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
