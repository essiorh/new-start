package com.example.volleyapp2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ImagesActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ImagesFragment())
                    .commit();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.images, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ImagesFragment extends Fragment {
        private ArticleAdapter mAdapter;
        public ImagesFragment() {
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_images, container, false);
        }
        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mAdapter = new ArticleAdapter(getActivity());
            ListView listView = (ListView) getView().findViewById(R.id.list1);
            listView.setAdapter(mAdapter);
            fetch();
        }
        private void fetch() {
            StringRequest request = new StringRequest(Request.Method.GET, "http://live.goodline.info/guest",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String data) {
                            Document doc = Jsoup.parse(data);
                            List<Article> Articles=new ArrayList<>();

                                Elements metaElems = (Elements) doc.getElementsByTag("article");
                                for(Element thisArt :metaElems) {
                                    String title=thisArt.select("h2").text();
                                    String url=thisArt.select("img").attr("src");
                                    String s=thisArt.select("time").attr("datetime").toString();
                                    SimpleDateFormat format = new SimpleDateFormat();
                                    format.applyPattern("dd.MM.yyyy");
                                    Date dt=new Date(System.currentTimeMillis());
                                    try {
                                        dt= format.parse(s);
                                    } catch (ParseException ex) {
                                        System.out.println("Это не должно произойти");
                                    }

                                    Article art=new Article(url,title,dt);

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
    }
}
