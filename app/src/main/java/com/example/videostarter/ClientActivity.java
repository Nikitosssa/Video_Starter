package com.example.videostarter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientActivity extends AppCompatActivity {
//192.168.100.111
    private Button toConnectButton;
    private EditText ipEditText;
    private TextView messageTextView;
    private String ip = "",
            oldMessage = "";

    public static String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        toConnectButton = findViewById(R.id.set_ip_button);
        ipEditText = findViewById(R.id.server_ip);
        messageTextView = findViewById(R.id.sent_message);
        message = oldMessage;

//        String yourFilePath = getFilesDir() + "/" + "hello.txt";
//        File yourFile = new File( yourFilePath );


        //message = messageTextView.getText().toString();
        connectNetwork();
    }

    public void onResume() {
        super.onResume();
        connectNetwork();
    }


    public void onSetIpClick(View view){
        ip = ipEditText.getText().toString();
//        writeFileOnInternalStorage(this, "last_ip_value", ip);
        if (!ip.equals("")){
            toConnectButton.setVisibility(View.GONE);
            ipEditText.setVisibility(View.GONE);
        }
    }

//    public void writeFileOnInternalStorage(Context mcoContext, String sFileName, String sBody){
//        File dir = new File(mcoContext.getFilesDir(), "mydir");
//        if(!dir.exists()){
//            dir.mkdir();
//        }
//
//        try {
//            File gpxfile = new File(dir, sFileName);
//            FileWriter writer = new FileWriter(gpxfile);
//            writer.append(sBody);
//            writer.flush();
//            writer.close();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    public void connectNetwork(){
        class ClientThread extends Thread{
            public void run(){
                try {
                    while(true) {
                        if (!ip.equals("")) {
                            sleep(500);
                            Socket socket = new Socket(InetAddress.getByName(ip), 1770);
                            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            message = input.readLine();
                            if (!message.equals(oldMessage)) {
                                oldMessage = message;
                                runOnUiThread(() -> {
                                    messageTextView.setText(message);
                                    toVideoPlayerActivity();
                                });
                            }
                            socket.close();
                        }
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        ClientThread clientThread = new ClientThread();
        clientThread.start();
    }

    private void toVideoPlayerActivity(){
        Intent videoPlayer = new Intent(this, VideoPlayerActivity.class);
        startActivity(videoPlayer);
    }
}