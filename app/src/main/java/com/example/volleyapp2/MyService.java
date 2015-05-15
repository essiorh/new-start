package com.example.volleyapp2;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.concurrent.TimeUnit;

    public class MyService extends IntentService {
        NotificationManager nm;
        public MyService() {
            super("myname");
        }

        public void onCreate() {
            super.onCreate();
            nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        @Override
        protected void onHandleIntent(Intent intent) {

            sendNotif();
        }
        void sendNotif() {
            // 1-€ часть
            Notification notif = new Notification(R.drawable.ic_launcher, "Text in status bar",
                    System.currentTimeMillis());

            // 3-€ часть
            Intent intent = new Intent(this, PageArt.class);
            intent.putExtra("title", "ѕочему надо сходить на GLO");
            intent.putExtra("uri","http://live.goodline.info/blog/Good_Line/2814.html");
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0); // ѕодробное описание смотреть в UPD к статье
            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.note);

            // 2-€ часть
            notif.setLatestEventInfo(this, "Notification's title", "Notification's text", pIntent);

            // ставим флаг, чтобы уведомление пропало после нажати€
            notif.flags |= Notification.FLAG_AUTO_CANCEL;
            contentView.setImageViewResource(R.id.image, R.drawable.reklama_goodline_priznana_nezakonnoy_thumb_main); // ѕрив€зываем нашу картинку к ImageView в разметке уведомлени€
            contentView.setTextViewText(R.id.text, "ѕочему надо сходить на GLO"); // ѕрив€зываем текст к TextView в нашей разметке

            notif.contentIntent = contentIntent; // ѕрисваиваем contentIntent нашему уведомлению
            notif.contentView = contentView; // ѕрисваиваем contentView нашему уведомлению
            // отправл€ем
            nm.notify(1, notif);

        }
        public void onDestroy() {
            super.onDestroy();
        }
    }

