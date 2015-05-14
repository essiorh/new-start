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
    private static final int NOTIFY_ID = 1; // ���������� ����������� ������ ����������� � �������� ������
    public void onCreate() {
        super.onCreate();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // ������� ��������� ��������� �����������
        int icon = android.R.drawable.stat_notify_more; // ������ ��� �����������, � ����� ��������������� ����������� ������� ��� Email
        CharSequence tickerText = "������ ���� ������� �� GLO"; // ��������� ��� �����
        long when = System.currentTimeMillis(); // ������� ��������� �����
        Intent notificationIntent = new Intent(this, PageArt.class); // ������� ��������� Intent
        //Intent intent		= Intent.parseIntent().ge

        //uri		= intent.getStringExtra("uri");

        notificationIntent.putExtra("title", "������ ���� ������� �� GLO");
        notificationIntent.putExtra("uri","http://live.goodline.info/blog/Good_Line/2814.html");


        Notification notification = new Notification(icon, tickerText, when); // ������� ��������� �����������, � �������� ��� ���� ���������
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0); // ��������� �������� �������� � UPD � ������
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.note); // ������� ��������� RemoteViews �������� ������������ �������� ������ �����������

        contentView.setImageViewResource(R.id.image, R.drawable.reklama_goodline_priznana_nezakonnoy_thumb_main); // ����������� ���� �������� � ImageView � �������� �����������
        contentView.setTextViewText(R.id.text, "������ ���� ������� �� GLO"); // ����������� ����� � TextView � ����� ��������

        notification.contentIntent = contentIntent; // ����������� contentIntent ������ �����������
        notification.contentView = contentView; // ����������� contentView ������ �����������
        mNotificationManager.notify(NOTIFY_ID, notification); // ������� ����������� � ������
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