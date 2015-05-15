
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
            // 1-я часть
            Notification notif = new Notification(R.drawable.ic_launcher, "Text in status bar",
                    System.currentTimeMillis());

            // 3-я часть
            Intent intent = new Intent(this, PageArt.class);
            intent.putExtra("title", "Почему надо сходить на GLO");
            intent.putExtra("uri","http://live.goodline.info/blog/Good_Line/2814.html");
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0); // Подробное описание смотреть в UPD к статье
            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.note);

            // 2-я часть
            notif.setLatestEventInfo(this, "Notification's title", "Notification's text", pIntent);

            // ставим флаг, чтобы уведомление пропало после нажатия
            notif.flags |= Notification.FLAG_AUTO_CANCEL;
            contentView.setImageViewResource(R.id.image, R.drawable.reklama_goodline_priznana_nezakonnoy_thumb_main); // Привязываем нашу картинку к ImageView в разметке уведомления
            contentView.setTextViewText(R.id.text, "Почему надо сходить на GLO"); // Привязываем текст к TextView в нашей разметке

            notif.contentIntent = contentIntent; // Присваиваем contentIntent нашему уведомлению
            notif.contentView = contentView; // Присваиваем contentView нашему уведомлению
            // отправляем
            nm.notify(1, notif);

        }
        public void onDestroy() {
            super.onDestroy();
        }
    }

