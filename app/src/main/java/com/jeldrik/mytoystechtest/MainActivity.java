package com.jeldrik.mytoystechtest;

import android.app.FragmentManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    WebView myWebView;
    String url="https://www.mytoys.de";
    String sJsonArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        myWebView=(WebView)findViewById(R.id.myWebView);


        if(savedInstanceState!=null){
            myWebView.restoreState(savedInstanceState);
            sJsonArray=savedInstanceState.getString("sJsonArray","");
            Log.e("MainActivity","JSON: "+sJsonArray);
            Fragment menuFragment=new MenuFragment();
            Bundle b=new Bundle();
            b.putString("jsonArray",sJsonArray);
            menuFragment.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, menuFragment).commit();
        }
        else {
            fetchData();
            myWebView.loadUrl(url);
            myWebView.getSettings().setJavaScriptEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openLink(String url){
        myWebView.loadUrl(url);
    }

    private void fetchData(){
        final RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://mytoysiostestcase1.herokuapp.com/api/navigation";

// Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                       // Log.e("MainActivity","Response is: "+ response);
                        //TODO add loading screen to drawer befor starting volley request and swap it with fragment here.
                        try {
                            JSONArray jsonArray = response.getJSONArray("navigationEntries");

                            sJsonArray=jsonArray.toString();
                            Bundle b=new Bundle();
                            b.putString("jsonArray",sJsonArray);

                            Fragment menuFragment=new MenuFragment();
                            menuFragment.setArguments(b);
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, menuFragment).commit();
                            queue.stop();

                        }catch(JSONException e) {
                            Log.e("MainActivity", "Could not parse JSON from Server "+e);
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MainActivity","That didn't work! "+error);
                queue.stop();
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("x-api-key",getResources().getString(R.string.API_KEY));
                return params;
            }
        };
// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("sJsonArray",sJsonArray);
        myWebView.saveState(outState);
    }
}
