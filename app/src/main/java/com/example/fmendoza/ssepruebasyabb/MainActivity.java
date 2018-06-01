package com.example.fmendoza.ssepruebasyabb;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.annotation.WorkerThread;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.here.oksse.OkSse;
import com.here.oksse.ServerSentEvent;
import com.here.oksse.ServerSentEvent.Listener;
import com.tylerjroach.eventsource.EventSource;
import com.tylerjroach.eventsource.EventSourceHandler;
import com.tylerjroach.eventsource.MessageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        startEventSource();
    }


    private SSEHandler sseHandler = new SSEHandler();
    EventSource eventSource = null;

   private  Listener listener = new Listener() {
       @Override
       public void onOpen(ServerSentEvent sse, Response response) {

       }

       @RequiresApi(api = Build.VERSION_CODES.O)
       @Override
       public void onMessage(ServerSentEvent sse, String id, String event, String message) {
           System.out.println("Mensaje: " + message);

           // Spawning an Android notification
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               createNotification(event, message);
           }
       }

       @Override
       public void onComment(ServerSentEvent sse, String comment) {

       }

       @Override
       public boolean onRetryTime(ServerSentEvent sse, long milliseconds) {
           return false;
       }

       @Override
       public boolean onRetryError(ServerSentEvent sse, Throwable throwable, Response response) {
           return false;
       }

       @Override
       public void onClosed(ServerSentEvent sse) {

       }

       @Override
       public Request onPreRetry(ServerSentEvent sse, Request originalRequest) {
           return null;
       }
   };

    private void startEventSource() {
  /*      Map<String, String> headers = new HashMap<String, String>();
        eventSource = new EventSource.Builder("http://34.239.25.138:9197/cranes/7fbc08a4-0820-4a2d-b633-2e327acdd40c/event/suscribe")
                .eventHandler(sseHandler)
                .headers(headers)
                .build();
        eventSource.connect();*/
        Request request = new Request.Builder().url("http://34.239.25.138:9197/cranes/7fbc08a4-0820-4a2d-b633-2e327acdd40c/event/suscribe").build();
        OkSse okSse = new OkSse();
        ServerSentEvent sse = okSse.newServerSentEvent(request, listener);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private final void createNotification(String title, String text) {

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String id = "my_channel_01";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(id, "algo",importance);
        mChannel.enableLights(true);
        mNotificationManager.createNotificationChannel(mChannel);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(MainActivity.this)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setChannelId(id);

        // Sets an ID for the notification
        Random rand = new Random();
        int mNotificationId = rand.nextInt(51);

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }


    private void stopEventSource() {
       /* if (eventSource != null)
            eventSource.close();
        sseHandler = null;*/
    }




private class SSEHandler implements EventSourceHandler {

    public SSEHandler() {
    }

    @Override
    public void onConnect() {
        Log.v("SSE Connected", "True");
    }

    @Override
    public void onMessage(String event, MessageEvent message) {
        Log.v("SSE Message", event);
        //Log.v("SSE Message: ", message.lastEventId);
        Log.v("SSE Message: ", message.data);
    }

    @Override
    public void onComment(String comment) {
        //comments only received if exposeComments turned on
        Log.v("SSE Comment", comment);
    }

    @Override
    public void onError(Throwable t) {
        //ignore ssl NPE on eventSource.close()
    }

    @Override
    public void onClosed(boolean willReconnect) {
        Log.v("SSE Closed", "reconnect? " + willReconnect);
    }
    }

}