package com.example.videostarter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashSet;

public class AdminActivity extends AppCompatActivity {

    private TextView ipTextView;
    private EditText mesEditText;
    private String message,
                   ip;
    private Boolean end = true;
    private ListView videoList;
    private ArrayList<String> videoNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mesEditText = findViewById(R.id.mes);
        message = mesEditText.getText().toString();

        ipTextView = findViewById(R.id.ip_value);
        ip = ipTextView.getText().toString();

        videoList = findViewById(R.id.list_video);

        try {
            ip = getLocalIpAddress();
            ipTextView.setText(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                    android.R.layout.simple_list_item_activated_1, videoNames);

        videoList.setAdapter(adapter);

        videoList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //TextView selectedView = (TextView) view;
                //selectedView.setTextColor(Color.GREEN);
                message = videoNames.get(position);
            }
        });

        String[] projection = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE
        };

// content:// style URI for the "primary" external storage volume
        Uri videos = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

// Make the query.
        Cursor cur = getContentResolver().query(videos,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );

        Log.i("ListingImages"," query count=" + cur.getCount());
        if (cur.moveToFirst()) {
            String name;

            int dateColumn = cur.getColumnIndex(
                    MediaStore.Video.Media.TITLE);
            do {
                // Get the field values
                name = cur.getString(dateColumn);
                if (name.contains("test")){videoNames.add(name);}
                // Do something with the values.
                Log.i("ListingVideos", "  name =" + name);
            } while (cur.moveToNext());
        }
    }

    private String getLocalIpAddress() throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        assert wifiManager != null;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onOpenServerClick(View view){
        Log.e("TAG", "-----------------------------------------");
        end = !end;
        Log.e("TAG", "onOpenServerClick: end = "+end.toString());
        if (!end) {ipTextView.setTextColor(Color.GREEN);}
        else {ipTextView.setTextColor(Color.RED);}
        class ServerThread extends Thread {
            public void run(){
                try {
                    ServerSocket serverSocket = new ServerSocket(1770);
                    while(true) {
                        Socket socket = serverSocket.accept();
                        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                        output.println(message);
                        Log.e("TAG", "Message = " + message);
                        socket.close();
                        if (end) {
                            Log.e("TAG", "run: ServerBreaked" );break; }
                    }
                    serverSocket.close();
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        ServerThread serverThread = new ServerThread();
        serverThread.start();
    }

    public void onSetMessageClick(View view){
        message = mesEditText.getText().toString();
    }
}