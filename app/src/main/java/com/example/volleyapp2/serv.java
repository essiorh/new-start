package com.example.volleyapp2;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;

public class serv extends Service {
    private static final int NOTIFY_ID = 1; // Уникальный индификатор вашего уведомления в пределах класса
    public void onCreate() {
        super.onCreate();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // Создаем экземпляр менеджера уведомлений
        int icon = android.R.drawable.stat_notify_more; // Иконка для уведомления, я решил воспользоваться стандартной иконкой для Email
        CharSequence tickerText = "Почему надо сходить на GLO"; // Подробнее под кодом
        long when = System.currentTimeMillis(); // Выясним системное время
        Intent notificationIntent = new Intent(this, PageArt.class); // Создаем экземпляр Intent
        //Intent intent		= Intent.parseIntent().ge

        //uri		= intent.getStringExtra("uri");

        notificationIntent.putExtra("title", "Почему надо сходить на GLO");
        notificationIntent.putExtra("uri","http://live.goodline.info/blog/Good_Line/2814.html");


        Notification notification = new Notification(icon, tickerText, when); // Создаем экземпляр уведомления, и передаем ему наши параметры
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0); // Подробное описание смотреть в UPD к статье
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.note); // Создаем экземпляр RemoteViews указывая использовать разметку нашего уведомления

        contentView.setImageViewResource(R.id.image, R.drawable.reklama_goodline_priznana_nezakonnoy_thumb_main); // Привязываем нашу картинку к ImageView в разметке уведомления
        contentView.setTextViewText(R.id.text, "Почему надо сходить на GLO"); // Привязываем текст к TextView в нашей разметке

        notification.contentIntent = contentIntent; // Присваиваем contentIntent нашему уведомлению
        notification.contentView = contentView; // Присваиваем contentView нашему уведомлению
        mNotificationManager.notify(NOTIFY_ID, notification); // Выводим уведомление в строку
    }
        public IBinder onBind(Intent intent) {
        return new Binder();
    }

    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void onDestroy() {
        super.onDestroy();
    }


}