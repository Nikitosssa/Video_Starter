package com.example.videostarter;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private TextView ipTextView;
    private EditText mesEditText;
    private String message,
                   ip;
    private Boolean end = true;
    private ListView videoList;
    private ArrayList<String> videoNames;

    public AdminActivity() {
    }

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
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ipTextView.setText(ip);

        File directory = new File("/storage");
        findVideos(directory, videoNames);

        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, videoNames);

        videoList.setAdapter(adapter);
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

    public void findVideos(File dir, ArrayList<String> list){
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) findVideos(file, list);
            else if(file.getAbsolutePath().contains(".mp4")) list.add(file.getName());
        }
    }

    public ArrayList<String> getAllMedia() {
        HashSet<String> videoItemHashSet = new HashSet<>();
        String[] projection = { MediaStore.Video.VideoColumns.DATA ,MediaStore.Video.Media.DISPLAY_NAME};
        Cursor cursor = this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        try {
            cursor.moveToFirst();
            do{
                videoItemHashSet.add((cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))));
            }while(cursor.moveToNext());

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<String> downloadedList = new ArrayList<>(videoItemHashSet);
        for (String s: downloadedList) {
            Log.e("TAG", "getAllMedia: " + s );
        }
        return downloadedList;
    }
}