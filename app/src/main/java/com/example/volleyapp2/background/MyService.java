
package com.example.volleyapp2.background;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.example.volleyapp2.R;
import com.example.volleyapp2.activities.ActivityMain;
import com.example.volleyapp2.adapters.Article;
import com.example.volleyapp2.methods.VolleyApplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * This service sends notification when new news has been added
 * @see android.app.IntentService
 * @author ilia
 */
public class MyService extends IntentService {
    private static final String ARTICLE = "article";
    private static final String H2 = "h2";
    private static final String PREVIEW = "preview";
    private static final String IMG = "img";
    private static final String SRC = "src";
    private static final String TIME = "time";
    private static final String A = "a";
    private static final String HREF = "href";
    private static final String MYLOG = "mylog";
    private static final String MY_PREF = "MyPref";
    private static final String SHOW_NEWS = "show_news";
    private static final String TITLE = "title";
    private static final String URI = "uri";
    private static final String NOTIFICATION_S_TITLE = "Notification's title";
    private static final String NOTIFICATION_S_TEXT = "Notification's text";
    private static final String TICKER_TEXT = "Text in status bar";
    private static final String thisRequest = "http://live.goodline.info/guest";
    private static final String SAVED_FIRST_NEW = "saved_first_new";

    private NotificationManager nm;
    private SharedPreferences sharedPreferences;
    private ArrayList<Article> artList = new ArrayList<>();

    /**
     * default constructor
     */
    public MyService() {
        super(MyService.class.getName());
    }

    /**
     * create this service
     */
    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * The most work of this service
     *
     * @param intent Sometimes it needs to drive this service
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.GET, thisRequest, future, future);
        VolleyApplication.getInstance().getRequestQueue().add(request);
        String response;
        boolean stateNotify = false;
        try {
            response = future.get(20, TimeUnit.SECONDS);
            Document doc = Jsoup.parse(response);
            Elements metaElms = doc.getElementsByTag(ARTICLE);
            String title = metaElms.first().select(H2).text();
            String url;
            if (!metaElms.first().getElementsByClass(PREVIEW).isEmpty())
                url = metaElms.first().select(IMG).attr(SRC);
            else
                url = "";
            String dt = metaElms.first().select(TIME).text();
            String uri = metaElms.first().select(H2).select(A).attr(HREF);
            Article art = new Article(url, title, dt, uri);
            artList.add(art);
            stateNotify = true;
        } catch (Exception e) {
            Log.d(MYLOG, e.getLocalizedMessage());
        }

        String prevArtInPreference;
        if (stateNotify) {
            prevArtInPreference = artList.get(0).getDat();
            sharedPreferences = getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
            String savedText = sharedPreferences.getString(SAVED_FIRST_NEW, "");
            if (!savedText.equals(prevArtInPreference)) {
                sendNotify();
                sharedPreferences = getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SAVED_FIRST_NEW, prevArtInPreference);
                editor.commit();
            }
        }
    }

    /**
     * THis method needs to send notification
     */
    private void sendNotify() {
        Notification notify = new Notification(R.drawable.ic_launcher, TICKER_TEXT,
                System.currentTimeMillis());

        Intent intent = new Intent(this, ActivityMain.class);
        intent.setAction(SHOW_NEWS);
        intent.putExtra(TITLE, artList.get(0).getTitle());
        intent.putExtra(URI, artList.get(0).getUri());

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notify.setLatestEventInfo(this, NOTIFICATION_S_TITLE, NOTIFICATION_S_TEXT, pIntent);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.note);

        contentView.setImageViewResource(R.id.image, R.drawable.goodline_picture);
        contentView.setTextViewText(R.id.text, artList.get(0).getTitle());

        notify.contentIntent = contentIntent;
        notify.contentView = contentView;
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notify.defaults |= Notification.DEFAULT_SOUND;
        nm.notify(1, notify);
    }
}

