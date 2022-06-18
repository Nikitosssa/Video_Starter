package com.example.videostarter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientActivity extends AppCompatActivity {

    private EditText ipEditText;
    private TextView messageTextView;
    private String ip = "192.168.100.111",
                   oldMessage = "";

    public static String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);


        //ipEditText = findViewById(R.id.server_ip);
        messageTextView = findViewById(R.id.sent_message);
        message = oldMessage;

        //message = messageTextView.getText().toString();
        connectNetwork();
    }

    public void onResume() {
        super.onResume();
        connectNetwork();
    }


    //public void onSetIpClick(View view){
        public void connectNetwork(){
        //ip = ipEditText.getText().toString();

        class ClientThread extends Thread{
            public void run(){
                try {
                    while(true) {
                        sleep(500);
                        Socket socket = new Socket(InetAddress.getByName(ip), 1770);
                        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        message = input.readLine();
                        if (!message.equals(oldMessage)){
                            oldMessage = message;
                            runOnUiThread(() -> {
                                messageTextView.setText(message);
                                toVideoPlayerActivity();
                            });
                        }
                        socket.close();
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