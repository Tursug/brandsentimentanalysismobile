package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

//import com.opencsv.CSVReader;

public class MainActivity extends AppCompatActivity {

    private int STORAGE_PERMISSION_CODE = 1;
    public static final int ACTIVITY_CHOOSE_FILE = 1;
    SetNumbers set_numbers = new SetNumbers();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {


            case ACTIVITY_CHOOSE_FILE: {
                if (resultCode == RESULT_OK){

                    Uri uri = data.getData();
                    Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    returnCursor.moveToFirst();
                    File csv_file = new File(uri.getPath());
                    File csv_file_a = new File(uri.toString());

                    String file = csv_file.getParent();

                    File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File file1 = new File(csv_file.getParent());
                    File file2 = new File(file1, returnCursor.getString(nameIndex));
                    File file3 = new File(downloadsDir, returnCursor.getString(nameIndex));

                    BufferedReader br = null;

                    try {
                        String sCurrentLine;
                        //String cvsSplitBy = ",";
                        //br = new BufferedReader(new FileReader(uri.getPath()+"/"+returnCursor.getString(nameIndex)));
                        br = new BufferedReader(new FileReader(file3));
                        //SetNumbers set_numbers = new SetNumbers();

                        int positive_count = 0;
                        int negative_count = 0;
                        int neutral_count = 0;

                        while ((sCurrentLine = br.readLine()) != null) {
                            //((TextView)findViewById(R.id.textView2)).setText(sCurrentLine);

                            //String[] rates = sCurrentLine.split(cvsSplitBy);

                            int sentence_positive = 0;
                            int sentence_negative = 0;

                            String sentiment = sCurrentLine;
                            String[] words = sentiment.split(" ");
                            //int num[] ={Integer.parseInt(rates[0]), Integer.parseInt(rates[1]), Integer.parseInt(rates[2])};
                            /*
                            for(int number: num){
                            if(number == 20)
                            {
                                set_numbers.setNeuCount(number);
                            }else if(number == 30)
                            {
                                set_numbers.setNegCount(number);
                            }else{
                                set_numbers.setPosCount(number);
                            }}*/
                            StringBuffer sbuffer = new StringBuffer();

                            InputStream pr = this.getResources().openRawResource(R.raw.positive_raw);
                            InputStream nr = this.getResources().openRawResource(R.raw.negative_raw);

                            BufferedReader pr_reader = new BufferedReader(new InputStreamReader(pr));
                            BufferedReader nr_reader = new BufferedReader(new InputStreamReader(nr));

                            String line_pos;
                            String line_neg;

                            while ((line_pos = pr_reader.readLine())!= null){
                                 //line = pr_reader.readLine();
                                for (int i=0; i<words.length; i++) {
                                    if (line_pos.equals(words[i].toLowerCase())) {
                                        //Log.d("positive",words[0].toString());
                                        sentence_positive = sentence_positive + 1;
                                    }
                                }
                            }

                            while ((line_neg = nr_reader.readLine())!= null){
                                //line = pr_reader.readLine();
                                for (int i=0; i<words.length; i++) {
                                    if (line_neg.equals(words[i].toLowerCase())) {
                                        //Log.d("positive",words[0].toString());
                                        sentence_negative = sentence_negative +1 ;
                                    }
                                }
                            }

                            if (sentence_positive > sentence_negative){
                                positive_count = positive_count + 1;
                            }else if(sentence_negative > sentence_positive){
                                negative_count = negative_count + 1;
                            }else{
                                neutral_count = neutral_count + 1;
                            }

                        }

                        set_numbers.setPosCount(positive_count);
                        //Log.d("positive",Integer.toString(positive_count));
                        set_numbers.setNegCount(negative_count);
                        //Log.d("negative",Integer.toString(negative_count));
                        set_numbers.setNeuCount(neutral_count);
                        //Log.d("Neutral",Integer.toString(neutral_count));

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (br != null)br.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    openActivity2();
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button select_file_button = findViewById(R.id.button2);

        select_file_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    requestStoragePermission();
                }
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/csv");
                    startActivityForResult(Intent.createChooser(intent, "Open CSV"), ACTIVITY_CHOOSE_FILE);
            }
        });
    }

    private void requestStoragePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            new AlertDialog.Builder(this).setTitle("Permission Required")
                    .setMessage("Read External Storage")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults){
        if (requestCode==STORAGE_PERMISSION_CODE){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                Toast.makeText(this, "PERMISSION GRANDED", Toast.LENGTH_SHORT).show();
            }else
                {
                    Toast.makeText(this,"PERMISSION DENIED", Toast.LENGTH_SHORT);
                }
        }
    }


    public void showPie(){
        PieChart piechart = findViewById(R.id.pieChart);
        piechart.setUsePercentValues(true);

        int pos = set_numbers.getPosCount();
        int neg = set_numbers.getNegCount();
        int neu = set_numbers.getNeuCount();

        List<PieEntry> value = new ArrayList<>();

        value.add(new PieEntry(pos, "Positive"));
        value.add(new PieEntry(neg, "Negative"));
        value.add(new PieEntry(neu, "Neutral"));

        PieDataSet pieDataSet = new PieDataSet(value, "Sentiment");
        PieData pieData = new PieData(pieDataSet);
    }

    public void openActivity2(){

        int pos = set_numbers.getPosCount();
        int neg = set_numbers.getNegCount();
        int neu = set_numbers.getNeuCount();

        Intent intent = new Intent(MainActivity.this, Main2Activity.class);

        intent.putExtra("positive", pos);
        intent.putExtra("negative", neg);
        intent.putExtra("neutral", neu);

        startActivity(intent);
    }
}

