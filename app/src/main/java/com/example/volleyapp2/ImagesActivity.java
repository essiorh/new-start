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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

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
            StringRequest request = new StringRequest(Request.Method.GET, "live.goodline.info",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String data) {
                            Document doc = Jsoup.parse(data);
                            try {
                                List<Article> Articles = parse(jsonObject);

                                mAdapter.swapArticleRecords(Articles);
                            }
                            catch(JSONException e) {
                                Toast.makeText(getActivity(), "Unable to parse data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            // Handle error
                        }
                    }
            );
            StringRequest request = new StringRequest(
                    "http://cblunt.github.io/blog-android-volley/response2.json",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                List<Article> Articles = parse(jsonObject);

                                mAdapter.swapArticleRecords(Articles);
                            }
                            catch(JSONException e) {
                                Toast.makeText(getActivity(), "Unable to parse data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(getActivity(), "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            VolleyApplication.getInstance().getRequestQueue().add(request);
        }

        private List<Article> parse(JSONObject json) throws JSONException {
            ArrayList<Article> records = new ArrayList<Article>();

            JSONArray jsonImages = json.getJSONArray("images");

            for(int i =0; i < jsonImages.length(); i++) {
                JSONObject jsonImage = jsonImages.getJSONObject(i);
                String title = jsonImage.getString("title");
                String url = jsonImage.getString("url");
                Date dt =new Date();

                Article record = new Article(url, title,dt);
                records.add(record);
            }

            return records;
        }
    }
}
