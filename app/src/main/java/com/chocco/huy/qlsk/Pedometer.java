package com.chocco.huy.qlsk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.zhaoxiaodan.miband.ActionCallback;
import com.zhaoxiaodan.miband.MiBand;
import com.zhaoxiaodan.miband.listeners.NotifyListener;
import com.zhaoxiaodan.miband.listeners.RealtimeStepsNotifyListener;
import com.zhaoxiaodan.miband.model.VibrationMode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class Pedometer extends AppCompatActivity //implements SensorEventListener
{
    public static int numSteps;
    private static final String TAG = "==[mibandtest]==";
//    public static MiBand miband = PedometerService.miband;
    ArrayList<Integer> totalStep;
    ArrayList<Integer> index;
    Chart chart;
    ArrayList<BarEntry> barEntries;
    Button btnDoBuocChan;
    Button btnRung;
    public static Button btnKetThuc;
    TextView txtThongBaoDo;
    TextView txtSteps;
    SeekBar muctieu;
    TextView txtMuctieu;
    TextView txtPhantram;
    int solan;
    float seekvalue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        totalStep=new ArrayList<>();
        index=new ArrayList<>();
        barEntries=new ArrayList<>();
        setContentView(R.layout.activity_pedometer);
        chart=(Chart)findViewById(R.id.chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        //assert actionBar!=null;
        actionBar.setDisplayHomeAsUpEnabled(true);
       // actionBar.setDisplayShowHomeEnabled(true);
        txtSteps=(TextView)findViewById(R.id.txtSteps);
        muctieu = (SeekBar) findViewById(R.id.sbMuctieu);
        txtMuctieu = (TextView) findViewById(R.id.txtMuctieu);
        txtPhantram = (TextView) findViewById(R.id.txtPhantram);
        SharedPreferences pre=getSharedPreferences("target", MODE_PRIVATE);
        final SharedPreferences.Editor edit=pre.edit();

        muctieu.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtMuctieu.setText(String.valueOf(progress)+" Bước");
                seekvalue = (float) (progress*1.0);
                edit.putInt("mytarget", progress);
                edit.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
       // threshold=10;

       // acceleration=0.00f;

//        miband = new MiBand(this);

       // final ArrayAdapter adapter1 = new ArrayAdapter<String>(this, R.layout.activity_pedometer, new ArrayList<String>());


        final TextView txtThongBaoDo=(TextView)findViewById(R.id.txtThongBaoDo);
        btnRung=(Button)findViewById(R.id.btnRung);
       // btnRung.setEnabled(false);
        btnRung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!PedometerService.startus)
                {
                    Toast.makeText(Pedometer.this,"Bạn cần kết nối với thiết bị trước để sử dụng tính năng này !",Toast.LENGTH_SHORT).show();
                    Intent toConnect = new Intent(Pedometer.this,ConnectDevice.class);
                    startActivity(toConnect);
                } else
                PedometerService.miband.startVibration(VibrationMode.VIBRATION_10_TIMES_WITH_LED);
            }
        });
        btnDoBuocChan=(Button)findViewById(R.id.btnDoBuocChan);
       // btnDoBuocChan.setEnabled(false);

        btnDoBuocChan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!PedometerService.startus)
                {
                    Toast.makeText(Pedometer.this,"Bạn cần kết nối với thiết bị trước để sử dụng tính năng này !",Toast.LENGTH_SHORT).show();
                    Intent toConnect = new Intent(Pedometer.this,ConnectDevice.class);
                    startActivity(toConnect);
                } else
                {
                    txtThongBaoDo.setText("Đang nhận số liệu số bước chân ...");
                    realtimestep();
                }
            }
        });
      btnKetThuc=(Button)findViewById(R.id.btnKetThuc);
        btnKetThuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            finishandrecord();
            }
        });

    }

    public void realtimestep()
    {
        PedometerService.stepprocess = true;
        PedometerService.miband.setRealtimeStepsNotifyListener(new RealtimeStepsNotifyListener() {
            @Override
            public void onNotify(final int steps) {
                PedometerService.numSteps++;
                Log.d(TAG, "RealtimeStepsNotifyListener:" + steps);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtSteps.setText(PedometerService.numSteps +"");
                        if(seekvalue!=0)
                        {
                            double x = Math.round((PedometerService.numSteps/seekvalue*100)*10)/10.0;
                            txtPhantram.setText("Mục tiêu đã hoàn thành được: "+String.valueOf(x)+"%");
                        }
                    }
                });
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bmi, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
           return true ;
        }
        if(id==android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        SharedPreferences pre=getSharedPreferences ("target",MODE_PRIVATE);
        muctieu.setProgress(pre.getInt("mytarget",0));
        super.onResume();
        txtSteps.setText(PedometerService.numSteps +"");
        if(PedometerService.stepprocess && PedometerService.startus)
        {
            realtimestep();
        }
        SharedPreferences sharedPreferences=getSharedPreferences("buocchan",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        int a=sharedPreferences.getInt("SOBUOCCHAN",0);
        String date=sharedPreferences.getString("THOIGIAN","");
        int b=sharedPreferences.getInt("SOLAN",0);
        if(b>0)
        {
            solan=b;
            editor.putInt("LAN"+solan,a);
            editor.commit();
            for(int j=0;j<solan;j++)
            {
                int i=j+1;
                int bc=sharedPreferences.getInt("LAN"+i,0);
                barEntries.add(new BarEntry(i,bc));
            }

            TextView txtLichSu=(TextView)findViewById(R.id.txtLichSu);
            txtLichSu.setText(date +" Số bước chân : "+a);
        }

      //  Toast.makeText(getApplication(),a+" "+date+" "+solan,Toast.LENGTH_LONG).show();

        BarDataSet barDataSet=new BarDataSet(barEntries,"Số bước chân");
        chart.setData(new BarData(barDataSet));
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        Description description=new Description();
        description.setText("Theo dõi bước đi");
        chart.setDescription(description);
        chart.animateY(1000);
    }
    public void finishandrecord()
    {
        ++solan;
        PedometerService.stepprocess = false;
        SharedPreferences sharedPreferences=getSharedPreferences("buocchan",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("SOBUOCCHAN",PedometerService.numSteps);
        editor.putInt("SOLAN",solan);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String stringDate = dateFormat.format(date);
        editor.putString("THOIGIAN",stringDate);
        editor.commit();
        BuocChanInfoDialogFragment buocChanInfoDialogFragment=new BuocChanInfoDialogFragment(PedometerService.numSteps);
        buocChanInfoDialogFragment.show(getSupportFragmentManager(),"THONG BAO");
    }
//    /*@Override
//    protected void onPause() {
//        super.onPause();
//        sensorManager.unregisterListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER));
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
//        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        if(sensor!=null)
//        {
//        sensorManager.registerListener(this,sensor
//                ,SensorManager.SENSOR_DELAY_UI);}
//        else
//            Toast.makeText(this,"Sensor not found ! ",Toast.LENGTH_LONG).show();
//
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//
//        float x=event.values[0];
//        float y=event.values[0];
//        float z=event.values[0];
//
//        currentY=y;
//
//        if(Math.abs(currentY-previousY)>threshold)
//        {
//            numSteps++;
//            txtStep.setText(String.valueOf(numSteps));
//        }
//        previousY=y;
//        txtX.setText("X="+String.valueOf(x));
//        txtY.setText("Y="+String.valueOf(y));
//        txtZ.setText("Z="+String.valueOf(z));
//
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//    }*/
}
