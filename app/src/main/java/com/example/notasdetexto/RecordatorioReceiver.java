package com.example.notasdetexto;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

public class RecordatorioReceiver extends BroadcastReceiver {

    public static String RECORDATORIO = "com.example.tp3.RECORDATORIO";
    public static String TEXTO = "TEXTO";

    @Override
    public void onReceive(Context context, Intent intent) {

        // Acá debería armarse la notificación

        Intent service1 = new Intent(context, ServicioNotificacion.class);
        service1.putExtra("TEXTO",intent.getStringExtra("TEXTO"));
        service1.setData(Uri.parse("custom://" + System.currentTimeMillis()));
        ContextCompat.startForegroundService(context,service1);


        Toast.makeText(context, "LLEGUE! " + intent.getStringExtra(TEXTO), Toast.LENGTH_LONG).show();

    }
}
