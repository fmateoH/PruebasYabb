package com.example.fmendoza.ssepruebasyabb;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.tylerjroach.eventsource.EventSource;
import com.tylerjroach.eventsource.EventSourceHandler;
import com.tylerjroach.eventsource.MessageEvent;

import java.util.HashMap;
import java.util.Map;

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


    private void startEventSource() {
        Map<String, String> headers = new HashMap<String, String>();
        eventSource = new EventSource.Builder("http://34.239.25.138:9197/cranes/7fbc08a4-0820-4a2d-b633-2e327acdd40c/event/suscribe")
                .eventHandler(sseHandler)
                .headers(headers)
                .build();
        eventSource.connect();
    }

    private void stopEventSource() {
        if (eventSource != null)
            eventSource.close();
        sseHandler = null;
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