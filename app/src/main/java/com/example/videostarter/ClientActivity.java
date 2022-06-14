package com.example.videostarter;

import androidx.appcompat.app.AppCompatActivity;

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
    private String ip,
                   message,
                   oldMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        ipEditText = findViewById(R.id.server_ip);
        messageTextView = findViewById(R.id.sent_message);

        message = messageTextView.getText().toString();
    }

    public void onSetIpClick(View view) throws UnknownHostException {
        ip = ipEditText.getText().toString();


        Log.e("TAG", "-----------------------------------------");
        class ClientThread extends Thread{
            public void run(){
                try {
                    while(true) {
                        sleep(500);
                        Log.e("TAG", " thread starts ");
                        Socket socket = new Socket(InetAddress.getByName(ip), 1770);
                        //InputStream in = socket.getInputStream();
                        Log.e("TAG", " connection passed ");
                        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        Log.e("TAG", " bufferreader created ");
                        message = input.readLine();
                        if (!message.equals(oldMessage)){
                            oldMessage = message;
                            runOnUiThread(() -> {
                                messageTextView.setText(message);
                            });
                        }
                        Log.e("TAG", "onSetIpClick: geted " + message);
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
}