package com.chocco.huy.qlsk;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NuocTrongCoThe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuoc_trong_co_the);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        assert actionBar!=null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        final TextView txtCanNang=(TextView)findViewById(R.id.txtCanNang);
        final TextView txtChieuCao=(TextView)findViewById(R.id.txtChieuCao);
        final TextView txtTuoi=(TextView)findViewById(R.id.txtTuoi);
        final LinearLayout linearLayout=(LinearLayout)findViewById(R.id.linear_nuoctrongcothe);
        Button btnTinh=(Button)findViewById(R.id.btnTinh);
        btnTinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtCanNang.getText().toString().length()>0&&txtChieuCao.getText().toString().length()>0
                        &&txtTuoi.getText().toString().length()>0)
                {
                    NuocTrongCoTheResultDialogFragment nuocTrongCoTheResultDialogFragment=new NuocTrongCoTheResultDialogFragment(
                            Float.parseFloat(txtCanNang.getText().toString()),Float.parseFloat(txtChieuCao.getText().toString()));
                    nuocTrongCoTheResultDialogFragment.show(getSupportFragmentManager(),"NUOC TRONG CO THE");
                }
                else
                    Snackbar.make(linearLayout,"Bạn vui lòng nhập đầy đủ thông tin",Snackbar.LENGTH_SHORT).show();
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
            NuocTrongCoTheInfoDialogFragment nuocTrongCoTheInfoDialogFragment=new NuocTrongCoTheInfoDialogFragment();
            nuocTrongCoTheInfoDialogFragment.show(getSupportFragmentManager(),"NUOC TRONG CO THE");
        }
        if(id==android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }


}
