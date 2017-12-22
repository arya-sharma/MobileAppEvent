package io.oneinfinity.eventmanagement;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static io.oneinfinity.eventmanagement.R.color.colorPrimary;

/**
 * Created by ujjwal on 12/21/2017.
 */

public class EventActivity extends AppCompatActivity {

    JSONArray eventArray;
    EventTask task;
    LinearLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_layout);
        rootView = findViewById(R.id.event_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        task = new EventTask();
        task.execute((Void) null);
    }

    private void createUI() {

        Log.w("Events", eventArray.toString());
        for (int i = 0; i < eventArray.length(); i++) {
            try {
                JSONObject event = eventArray.getJSONObject(i);
                Button eventUI = new Button(this);
                eventUI.setBackgroundColor(Color.parseColor("#2196f3"));
                eventUI.setText(event.getString("eventName"));
                eventUI.setTag(event.getString("_id"));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.topMargin = 40;
                eventUI.setLayoutParams(params);
                rootView.addView(eventUI);

                eventUI.setOnClickListener(
                        new Button.OnClickListener() {
                            public void onClick(View v) {
                                String eventId = v.getTag().toString();
                                Intent mainIntent = new Intent(EventActivity.this, MainActivity.class);
                                new EventModel(eventId);
                                EventActivity.this.startActivity(mainIntent);
                            }
                        });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



    }

    public class EventTask extends AsyncTask<Void, Void, Boolean> {

               @Override
        protected Boolean doInBackground(Void... params) {

            EventService service = new EventService();
            EventActivity.this.eventArray = service.execute();

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if(success) {
                EventActivity.this.createUI();
            }

        }

        @Override
        protected void onCancelled() {

        }
    }
}
