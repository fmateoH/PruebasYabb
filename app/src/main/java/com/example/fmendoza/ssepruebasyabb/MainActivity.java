package com.example.fmendoza.ssepruebasyabb;

import android.os.StrictMode;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.here.oksse.*;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        startEventSource();
    }


private ServerSentEvent sse;

    private void startEventSource() {
        String path = "http://34.239.25.138:9197/cranes/7fbc08a4-0820-4a2d-b633-2e327acdd40c/event/suscribe";
        Request request = new Request.Builder().url(path).build();
        OkSse okSse = new OkSse();
        sse = okSse.newServerSentEvent(request, new ServerSentEvent.Listener() {

            @Override
            public Request onPreRetry(ServerSentEvent sse, Request originalRequest) {
                return null;
            }

            @Override
            public void onOpen(ServerSentEvent sse, Response response) {
                Log.v("SSE Connected", "True");
            }

            @Override
            public void onMessage(ServerSentEvent sse, String id, String event, String message) {
                // When a message is received
                Log.v("Event received", event);
                Log.v("Message received", message);
                JSONObject json = null;
                try {
                    json = new JSONObject(message);
                    Log.v("Folio received", (String) json.get("folio"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @WorkerThread
            @Override
            public void onComment(ServerSentEvent sse, String comment) {
                // When a comment is received
                Log.v("SSE Comment", comment);
            }

            @WorkerThread
            @Override
            public boolean onRetryTime(ServerSentEvent sse, long milliseconds) {
                return true; // True to use the new retry time received by SSE
            }

            @WorkerThread
            @Override
            public boolean onRetryError(ServerSentEvent sse, Throwable throwable, Response response) {
                return true; // True to retry, false otherwise
            }

            @WorkerThread
            @Override
            public void onClosed(ServerSentEvent sse) {
                // Channel closed
                Log.v("SSE Closed", "reconnect? ");
            }

            ;
        });
    }

    private void stopEventSource() {
        if (sse != null)
            sse.close();
    }

}