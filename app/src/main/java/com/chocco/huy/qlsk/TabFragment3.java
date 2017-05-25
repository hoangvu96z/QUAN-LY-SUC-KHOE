package com.chocco.huy.qlsk;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.zhaoxiaodan.miband.model.VibrationMode;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;


public class TabFragment3 extends Fragment {
    public static Ringtone ringtone;
    private static final int MY_NOTIFICATION_ID = 12345;
    TimePicker alarmTimePicker;
    public  static PendingIntent pendingIntent;
    public  static AlarmManager alarmManager;
    public static int on=0;
    Button baothuc;
    public static Switch vib;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View views=inflater.inflate(R.layout.tab_fragment_3,container,false);
        alarmTimePicker = (TimePicker) views.findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
      baothuc = (Button) views.findViewById(R.id.btnBaoThuc);
        vib = (Switch) views.findViewById(R.id.swVib);
        vib.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(vib.isChecked())
                {
                    if(!PedometerService.startus)
                    {
                        vib.setChecked(false);
                        Toast.makeText(getActivity(),"Để sử dụng chức năng này bạn cần kết nối với thiết bị trước",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getActivity(),ConnectDevice.class);
                        startActivity(i);
                    }
                }
            }
        });


        baothuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time;
               if(baothuc.getText()=="Bật báo thức")
               {
                   on=0;
               }
               else if(baothuc.getText()=="Tắt báo thức") on=1;
                if(on==0)
                {
                    baothuc.setText("Tắt báo thức");
                    baothuc.setBackgroundColor(Color.RED);
                    Toast.makeText(getActivity(), "Báo thức đã bật", Toast.LENGTH_SHORT).show();
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
                    calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
                    Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);

                    time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
                    if(System.currentTimeMillis()>time)
                    {
                        if (calendar.AM_PM == 0)
                            time = time + (1000*60*60*12);
                        else
                            time = time + (1000*60*60*24);
                    }
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent);
                } else if(on==1)
                {
                    baothuc.setText("Bật báo thức");
                    baothuc.setBackgroundColor(Color.BLUE);
                    alarmManager.cancel(pendingIntent);
                    Toast.makeText(getActivity(), "Báo thức đã tắt", Toast.LENGTH_SHORT).show();
                    AlarmReceiver.ringtone.stop();
                }
            }
        });
        return views;
    }
//    private final AlarmReceiver alarm = new AlarmReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
//            mBuilder.setSmallIcon(R.drawable.heart);
//            mBuilder.setContentTitle("Báo thức !");
//            mBuilder.setContentText("Nhấn vào đây để trở lại ứng dụng");
//            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            Intent resultIntent = new Intent(context, MainActivity.class);
//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//            stackBuilder.addParentStack(MainActivity.class);
//// Adds the Intent that starts the Activity to the top of the stack
//            stackBuilder.addNextIntent(resultIntent);
//            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//            mBuilder.setContentIntent(resultPendingIntent);
//            mNotificationManager.notify(MY_NOTIFICATION_ID, mBuilder.build());
//
//            Toast.makeText(context, "Alarm! Wake up! Wake up!", Toast.LENGTH_LONG).show();
//            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//            if (alarmUri == null)
//            {
//                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            }
//            ringtone = RingtoneManager.getRingtone(context, alarmUri);
//            ringtone.play();
//            if(TabFragment3.vib.isChecked()) {
//                PedometerService.miband.startVibration(VibrationMode.VIBRATION_10_TIMES_WITH_LED);
//            }
//            displayAlert();
//
//        }
//    };
    private void displayAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to exit?").setCancelable(
                false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       
                        dialog.cancel();
                    }
                }).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
//    public void OnToggleClicked()
//    {
//        long time;
//        if (tog.isChecked())
//        {
//            Toast.makeText(getActivity(), "ALARM ON", Toast.LENGTH_SHORT).show();
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
//            calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
//            Intent intent = new Intent(getActivity(), AlarmReceiver.class);
//            pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
//
//            time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
//            if(System.currentTimeMillis()>time)
//            {
//                if (calendar.AM_PM == 0)
//                    time = time + (1000*60*60*12);
//                else
//                    time = time + (1000*60*60*24);
//            }
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent);
//        }
//        else
//        {
//            alarmManager.cancel(pendingIntent);
//            Toast.makeText(getActivity(), "ALARM OFF", Toast.LENGTH_SHORT).show();
//        }
//    }
}
