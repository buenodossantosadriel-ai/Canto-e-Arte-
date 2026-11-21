package br.com.cantoearte.petshop;

import android.app.*;
import android.content.*;
import java.util.*;

public class NotificationScheduler {
    public static void agendar(Context c) {
        SharedPreferences p=c.getSharedPreferences("lembretes",Context.MODE_PRIVATE);
        if(!p.getBoolean("ativo",true)) { cancelar(c); return; }
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,p.getInt("hora",8));
        cal.set(Calendar.MINUTE,p.getInt("minuto",0));
        cal.set(Calendar.SECOND,0); cal.set(Calendar.MILLISECOND,0);
        if(cal.getTimeInMillis()<=System.currentTimeMillis()) cal.add(Calendar.DAY_OF_YEAR,1);
        Intent i=new Intent(c,NotificationReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(c,7001,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pi);
    }
    public static void cancelar(Context c) {
        Intent i=new Intent(c,NotificationReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(c,7001,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        ((AlarmManager)c.getSystemService(Context.ALARM_SERVICE)).cancel(pi);
    }
}