package com.sujanlama.yipl_android_list_me;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.sujanlama.yipl_android_list_me.RecylerViewAdapter.PostAdapter;
import com.sujanlama.yipl_android_list_me.RecylerViewAdapter.RecyclerItemClickListener;
import com.sujanlama.yipl_android_list_me.helper.PostHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private ArrayList<PostHelper> list;
    DatabaseForPost info;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RelativeLayout relativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        info = new DatabaseForPost(MainActivity.this);
        mProgressDialog = new ProgressDialog(this);
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);
        relativeLayout = findViewById(R.id.relative);
        snackbar1();
        mRecyclerView = findViewById(R.id.postrecycler);
        mRecyclerView.setHasFixedSize(true);


        list = new ArrayList<>();
        list = info.getPosts();

        if (!list.isEmpty()) {
            showList(list);
        } else {
            if (isOnline()) {
                getJsonValue();
            } else {
                Toast.makeText(MainActivity.this, "Please Connect to Internet", Toast.LENGTH_LONG).show();
            }
        }
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isOnline()) {
                    info.deletedatafromTable();
                    getJsonValue();
                    Toast.makeText(MainActivity.this, "Data refreshed", Toast.LENGTH_LONG).show();

                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    Toast.makeText(MainActivity.this, "Please Connect to Internet", Toast.LENGTH_LONG).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }


            }
        });
    }

    private void getJsonValue() {
        String tag_req = "req_post";
        mProgressDialog.setMessage("Fetching Data...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, AppConfig.listingapi, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                mProgressDialog.dismiss();
                DatabaseForPost info = new DatabaseForPost(MainActivity.this);
                Log.i("response", response.toString());

                try {
                    ArrayList<PostHelper> list = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        PostHelper helper = new PostHelper();
                        JSONObject object = response.getJSONObject(i);
                        helper.setUserId(object.optString("userId"));
                        helper.setId(object.optString("id"));
                        helper.setTitle(object.optString("title"));
                        helper.setBody(object.optString("body"));

                        ContentValues cv = new ContentValues();
                        cv.put("userId", object.optString("userId"));
                        cv.put("id", object.optString("id"));
                        cv.put("title", object.optString("title"));
                        cv.put("body", object.optString("body"));

                        info.insertPosts(cv);

                        list.add(helper);
                    }

                    showList(list);

                } catch (JSONException e) {
                    mProgressDialog.dismiss();
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Log.i("Volley Error", error.toString());

            }
        });

        AppController.getInstance().addToRequestQueue(request, tag_req);

    }

    private void showList(final ArrayList<PostHelper> list) {

       /* mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(new PostAdapter(list, this));


       */


        int duration = getResources().getInteger(R.integer.scroll_duration);
        mRecyclerView.setLayoutManager(new ScrollingLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false, duration));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(new PostAdapter(list, this));
        FastScroller fastScroller = (FastScroller) findViewById(R.id.fastscroller);
        fastScroller.setRecyclerView(mRecyclerView);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String id = list.get(position).getId();
                Intent i = new Intent(MainActivity.this, ViewCommentActivity.class);
                i.putExtra("id", id);

                startActivity(i);
            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.refresh) {
            if (isOnline()) {
                info.deletedatafromTable();
                getJsonValue();
            } else {
                Toast.makeText(MainActivity.this, "Please Connect to Internet", Toast.LENGTH_LONG).show();
            }
        }else if(id==R.id.info){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("App developed for Internship Challenge by Sujan Lama");
            builder.setMessage("Hope to get it :D");
            builder.setCancelable(true);
            builder.show();
        }
        return true;
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            finishAffinity();
            super.onBackPressed();
            return;
        }
    }

    private void snackbar1() {
        final Snackbar snackbar = Snackbar.make(relativeLayout, "Click posts to view Comment", Snackbar.LENGTH_SHORT);

        snackbar.setActionTextColor(Color.GREEN);
        snackbar.setDuration(Snackbar.LENGTH_SHORT);

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.BLACK);
        TextView textView = (TextView) sbView
                .findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        snackbar.show();

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}
