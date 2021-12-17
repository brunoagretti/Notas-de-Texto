package com.example.notasdetexto;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;


public class CrearRecordatorioActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private EditText textoRecordatorio;
    private Button botonElegir;
    private Button botonCrear;
    private TextView textoFecha;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private LocalDateTime tiempoRecordatorio;
    private Integer diaRecordatorio;
    private Integer mesRecordatorio;
    private Integer añoRecordatorio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_recordatorio);

        textoRecordatorio = (EditText) findViewById(R.id.editTextRecordatorio);
        textoFecha = (TextView) findViewById(R.id.textoFecha);
        botonElegir = (Button) findViewById(R.id.botonFecha);
        botonCrear = (Button) findViewById(R.id.buttonCrear);

        //Asignación del broadcast
        BroadcastReceiver br = new RecordatorioReceiver();
        IntentFilter filtro = new IntentFilter();
        filtro.addAction(RecordatorioReceiver.RECORDATORIO);
        getApplication().getApplicationContext()
                .registerReceiver(br,filtro);



        botonElegir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = DatePickerDialog.newInstance(CrearRecordatorioActivity.this,
                        LocalDate.now().getYear(), LocalDate.now().getMonthValue()-1, LocalDate.now().getDayOfMonth());
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setTitle("Elegir fecha");
                datePickerDialog.setOkColor(Color.BLACK);
                datePickerDialog.setCancelColor(Color.BLACK);


                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
            }
        });
        botonCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setearAlarma(tiempoRecordatorio, 1, System.currentTimeMillis(), getApplicationContext());
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String mes = numeroMesATexto(monthOfYear);
        textoFecha.setText("El recordatorio se programó para el " + dayOfMonth + " de " + mes + " de " + year);
        this.añoRecordatorio=year;
        this.mesRecordatorio=monthOfYear;
        this.diaRecordatorio=dayOfMonth;

        timePickerDialog = TimePickerDialog.newInstance(CrearRecordatorioActivity.this, true);
        timePickerDialog.setThemeDark(false);
        timePickerDialog.setOkColor(Color.BLACK);
        timePickerDialog.setCancelColor(Color.BLACK);
        timePickerDialog.setTitle("Elegir hora");
        timePickerDialog.show(getFragmentManager(),"timePickerDialog");
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        textoFecha.append(" a las " + agregarCero(hourOfDay) + ":" + agregarCero(minute));
        tiempoRecordatorio = LocalDateTime.of(añoRecordatorio,mesRecordatorio,diaRecordatorio,hourOfDay,minute,second);
        botonCrear.setEnabled(true);
    }

    private String numeroMesATexto(Integer mes){
        String mesTexto = "";
        if (mes <= 11 && mes >= 0){
            switch (mes){
                case 0:
                    mesTexto = "Enero";
                    break;
                case 1:
                    mesTexto = "Febrero";
                    break;
                case 2:
                    mesTexto = "Marzo";
                    break;
                case 3:
                    mesTexto = "Abril";
                    break;
                case 4:
                    mesTexto = "Mayo";
                    break;
                case 5:
                    mesTexto = "Junio";
                    break;
                case 6:
                    mesTexto = "Julio";
                    break;
                case 7:
                    mesTexto = "Agosto";
                    break;
                case 8:
                    mesTexto = "Septiembre";
                    break;
                case 9:
                    mesTexto = "Octubre";
                    break;
                case 10:
                    mesTexto = "Noviembre";
                    break;
                case 11:
                    mesTexto = "Diciembre";
                    break;
            }
        }
        return mesTexto;
    }

    private String agregarCero(Integer cifra){
        if(cifra.toString().length()==1){
            return "0" + cifra;
        }
        return cifra.toString();
    }

    private void setearAlarma(LocalDateTime fechaHora, Integer id, Long timestamp, Context contexto){
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(contexto, RecordatorioReceiver.class);
        alarmIntent.putExtra("TEXTO",textoRecordatorio.getText().toString());
        Log.println(Log.DEBUG,"textoRecordatorio.getText()",textoRecordatorio.getText().toString());
        alarmIntent.setAction(RecordatorioReceiver.RECORDATORIO);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(contexto,id ,alarmIntent, PendingIntent.FLAG_ONE_SHOT);

        Calendar tiempoNotificacion = Calendar.getInstance();
        tiempoNotificacion.set(Calendar.HOUR_OF_DAY, fechaHora.getHour());
        tiempoNotificacion.set(Calendar.MINUTE, fechaHora.getMinute());
        tiempoNotificacion.set(Calendar.SECOND, 0);
        tiempoNotificacion.set(Calendar.YEAR, fechaHora.getYear());
        tiempoNotificacion.set(Calendar.MONTH, fechaHora.getMonth().getValue());
        tiempoNotificacion.set(Calendar.DAY_OF_MONTH,fechaHora.getDayOfMonth());

        alarmIntent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
        alarm.set(AlarmManager.RTC_WAKEUP, tiempoNotificacion.getTimeInMillis(), pendingIntent);

        Toast.makeText(getApplicationContext(), "Se ha creado el recordatorio  para el " +
                diaRecordatorio + " de " + numeroMesATexto(mesRecordatorio) + " de " + añoRecordatorio + " a las " +
                agregarCero(fechaHora.getHour()) + ":" + agregarCero(fechaHora.getMinute()), Toast.LENGTH_LONG).show();

    }
}