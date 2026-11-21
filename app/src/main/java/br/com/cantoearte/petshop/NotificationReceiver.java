package br.com.cantoearte.petshop;

import android.app.*;
import android.content.*;
import android.os.Build;
import java.util.*;
import java.text.*;

public class NotificationReceiver extends BroadcastReceiver {
    static final String CHANNEL = "canto_arte_lembretes";
    @Override public void onReceive(Context context, Intent intent) {
        criarCanal(context);
        SharedPreferences p=context.getSharedPreferences("lembretes",Context.MODE_PRIVATE);
        LocalDb db=new LocalDb(context);
        String hoje=new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(new Date());
        String iso=new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(new Date());
        int agenda=0,vacinas=0,contas=0,retornos=0,estoque=0;
        if(p.getBoolean("agenda",true)) for(String[] r:db.listar("Agendamento")) if(contemData(r[2],hoje,iso)) agenda++;
        if(p.getBoolean("vacinas",true)) for(String[] r:db.listar("Pet")) if(contemData(r[2],hoje,iso) && (r[2].toLowerCase(Locale.ROOT).contains("vacina"))) vacinas++;
        if(p.getBoolean("contas",true)) for(String[] r:db.listar("Conta")) if(contemData(r[2],hoje,iso)) contas++;
        if(p.getBoolean("retornos",true)) for(String[] r:db.listar("Retorno")) if(contemData(r[2],hoje,iso)) retornos++;
        if(p.getBoolean("estoque",true)) for(String[] r:db.listar("Produto")) {
            String d=r[2]==null?"":r[2].toLowerCase(Locale.ROOT);
            if(d.contains("estoque baixo") || d.matches("(?s).*\\b(0|1|2)\\s*(un|unidade| unidades).*")) estoque++;
        }
        if(p.getBoolean("resumo",true)) {
            int total=agenda+vacinas+contas+retornos+estoque;
            if(total>0) {
                String msg=agenda+" agendamento(s), "+vacinas+" vacina(s), "+retornos+" retorno(s), "+contas+" conta(s) e "+estoque+" alerta(s) de estoque.";
                Notification.Builder n=Build.VERSION.SDK_INT>=26?new Notification.Builder(context,CHANNEL):new Notification.Builder(context);
                n.setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("Canto e Arte • Lembretes de hoje").setContentText(msg).setStyle(new Notification.BigTextStyle().bigText(msg)).setAutoCancel(true);
                ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1001,n.build());
            }
        }
        NotificationScheduler.agendar(context);
    }
    private boolean contemData(String s,String br,String iso){return s!=null&&(s.contains(br)||s.contains(iso));}
    static void criarCanal(Context c) {
        if(Build.VERSION.SDK_INT>=26) {
            NotificationChannel ch=new NotificationChannel(CHANNEL,"Lembretes do Canto e Arte",NotificationManager.IMPORTANCE_DEFAULT);
            ch.setDescription("Avisos locais de agenda, estoque, contas, retornos e vacinas.");
            ((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(ch);
        }
    }
}