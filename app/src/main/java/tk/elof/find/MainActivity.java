package tk.elof.find;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    User user = new User();
    Activity activity = this;

    public class CustomAdapter extends ArrayAdapter<Contact> {

        public CustomAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public CustomAdapter(Context context, int resource, Contact[] contacts) {
            super(context, resource, contacts);
        }


        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = convertView;

            if(view == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                view = vi.inflate(R.layout.contact_list_item, null);
            }

            Contact c = getItem(i);

            if(c != null) {
                TextView name = (TextView) view.findViewById(R.id.Name);
                TextView position = (TextView) view.findViewById(R.id.Position);

                name.setText(c.getName());
                position.setText(c.getPosition());
            }

            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        Intent intent = getIntent();
        user.token = intent.getStringExtra("token");

        user.intent = "list";

        DownloadListTask task = new DownloadListTask();
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class DownloadListTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.w("APP", "background-task");
            try {
                String link = "http://apktest.site90.com/"
                        + "?intent=" + user.intent
                        + "&token=" + user.token
                        + "&user=" + user.user
                        + "&pass=" + user.pass
                        + "&mail=" + user.mail
                        + "&number=" + user.number
                        + "&query=" + user.query
                        + "&pos=" + user.pos
                        + "&id=" + user.id
                        + "&edit=" + user.edit
                        + "&add=" + user.add;

                Log.w("APP", link);


                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                Log.w("DOWN", sb.toString());

                return sb.toString();
            } catch (Exception e) {
                Log.w("APP", e.toString());
                return "Failure";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            user.result = result;
            taskResult();
            return;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void taskResult() {
        if (Objects.equals(user.result, "Failure")) {
            Log.w("RESULT", "Failure");
            return;
        } else {
            String[] contactIDs = user.result.split("_");

            Contact[] contacts = new Contact[contactIDs.length];

            for(int i = 0; i < contactIDs.length; i++) {
                contacts[i] = new Contact(contactIDs[i], user);
            }

            CustomAdapter adapter = new CustomAdapter(this, R.layout.contact_list_item, contacts);

            listView.setAdapter(adapter);

        }
    }
}
