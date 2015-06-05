
package com.example.volleyapp2.background;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.example.volleyapp2.adapters.Article;
import com.example.volleyapp2.activities.ActivityMain;
import com.example.volleyapp2.R;
import com.example.volleyapp2.methods.VolleyApplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

    public class MyService extends IntentService {
        NotificationManager nm;
        private SharedPreferences sharedPreferences;
        final String SAVED_FIRST_NEW = "saved_first_new";
        private ArrayList<Article> artList=new ArrayList<>();


        public MyService() {

            super(MyService.class.getName());
        }
        public void onCreate() {
            super.onCreate();
            nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        @Override
        public IBinder onBind(Intent arg0)
        {
            return null;
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String thisZapros = "http://live.goodline.info/guest";
            RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest request = new StringRequest(Request.Method.GET, thisZapros, future, future);
            VolleyApplication.getInstance().getRequestQueue().add(request);
            String response=null;
            boolean stateNotif=false;
            try {
                response=future.get(20, TimeUnit.SECONDS);
                Document doc = Jsoup.parse(response);
                Elements metaElems = doc.getElementsByTag("article");
                String title = metaElems.first().select("h2").text();
                String url;
                if (!metaElems.first().getElementsByClass("preview").isEmpty())
                    url = metaElems.first().select("img").attr("src");
                else
                    url = "";
                String dt = metaElems.first().select("time").text();
                String uri = metaElems.first().select("h2").select("a").attr("href");
                Article art = new Article(url, title, dt, uri);
                artList.add(art);
                stateNotif=true;
            } catch (Exception e)
            {
                Log.d("mylog",e.getLocalizedMessage());
            }

            String stredf=null;
            if (stateNotif) {
                stredf = artList.get(0).getDat();
                sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                String savedText = sharedPreferences.getString(SAVED_FIRST_NEW, "");
                if (!savedText.equals(stredf)) {
                    sendNotif();
                    sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SAVED_FIRST_NEW, stredf);
                    editor.commit();
                }
            }
        }

        void sendNotif() {
            Notification notif = new Notification(R.drawable.ic_launcher, "Text in status bar",
                    System.currentTimeMillis());
            Intent intent = new Intent(this, ActivityMain.class);
            intent.setAction("show_news");
            intent.putExtra("title", artList.get(0).getTitle());
            intent.putExtra("uri", artList.get(0).getUri());
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0); // Подробное описание смотреть в UPD к статье
            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.note);
            notif.setLatestEventInfo(this, "Notification's title", "Notification's text", pIntent);
            notif.flags |= Notification.FLAG_AUTO_CANCEL;
            Bitmap bitmap=null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(artList.get(0).getUrl()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null)
                contentView.setImageViewBitmap(R.id.image, bitmap);
            else
                contentView.setImageViewResource(R.id.image, R.drawable.reklama_goodline_priznana_nezakonnoy_thumb_main);

            contentView.setTextViewText(R.id.text, artList.get(0).getTitle());

            notif.contentIntent = contentIntent;
            notif.contentView = contentView;
            nm.notify(1, notif);
        }


    }

