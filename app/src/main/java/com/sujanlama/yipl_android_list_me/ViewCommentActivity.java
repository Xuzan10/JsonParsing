package com.sujanlama.yipl_android_list_me;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.sujanlama.yipl_android_list_me.RecylerViewAdapter.ViewCommentAdapter;
import com.sujanlama.yipl_android_list_me.helper.CommentHelper;
import com.sujanlama.yipl_android_list_me.helper.PostHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewCommentActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    String id;
    private TextView cid, title, body;
    ArrayList<CommentHelper> commentHelpers;
    private SwipeRefreshLayout swipeRefreshLayout;
    DatabaseForPost info;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        id = getIntent().getStringExtra("id");
        mProgressDialog = new ProgressDialog(this);
        mRecyclerView = findViewById(R.id.commentrecyler);
        mRecyclerView.setHasFixedSize(true);
        info = new DatabaseForPost(ViewCommentActivity.this);

        cid = findViewById(R.id.id);
        title = findViewById(R.id.title);
        body = findViewById(R.id.body);
        linearLayout = findViewById(R.id.linear);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);


        PostHelper helper = info.getPostbyId(id);
        cid.setText(helper.getId());
        title.setText(helper.getTitle());
        body.setText(helper.getBody());

        commentHelpers = new ArrayList<>();

        commentHelpers = info.getComments(id);
        if (!commentHelpers.isEmpty()) {
            showList(commentHelpers);
        } else {
            if (isOnline()) {
                getJsonValue();
            } else {
                Toast.makeText(ViewCommentActivity.this, "Please Connect to Internet", Toast.LENGTH_LONG).show();

            }
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isOnline()) {
                    info.deleteallcomment(id);
                    getJsonValue();
                    Toast.makeText(ViewCommentActivity.this, "Comment refreshed", Toast.LENGTH_LONG).show();

                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    Toast.makeText(ViewCommentActivity.this, "Please Connect to Internet", Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);

                }
            }
        });
    }

    private void getJsonValue() {

        String tag_req = "req_post";
        mProgressDialog.setMessage("Fetching Data...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String url = AppConfig.listingapi + id + "/comments";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                mProgressDialog.dismiss();
                try {

                    ArrayList<CommentHelper> list = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        CommentHelper helper = new CommentHelper();
                        JSONObject object = response.getJSONObject(i);
                        helper.setPostId(object.optString("postId"));
                        helper.setId(object.optString("id"));
                        helper.setName(object.optString("name"));
                        helper.setEmail(object.optString("email"));
                        helper.setBody(object.optString("body"));

                        ContentValues cv = new ContentValues();
                        cv.put("postId", object.optString("postId"));
                        cv.put("id", object.optString("id"));
                        cv.put("name", object.optString("name"));
                        cv.put("email", object.optString("email"));
                        cv.put("body", object.optString("body"));

                        info.insertComments(cv);


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
                Toast.makeText(ViewCommentActivity.this, "Comment Cannot be loaded", Toast.LENGTH_LONG).show();
            }
        });
        AppController.getInstance().addToRequestQueue(request, tag_req);
    }

    private void showList(ArrayList<CommentHelper> list) {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(ViewCommentActivity.this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setNestedScrollingEnabled(false);

        mRecyclerView.setAdapter(new ViewCommentAdapter(list, this));

        linearLayout.requestFocus();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemid = item.getItemId();
        if (itemid == android.R.id.home) {
            onBackPressed();
        } else if (itemid == R.id.refresh) {
            if (isOnline()) {
                info.deleteallcomment(id);
                getJsonValue();
                Toast.makeText(ViewCommentActivity.this, "Comment refreshed", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(ViewCommentActivity.this, "Please Connect to Internet", Toast.LENGTH_LONG).show();

            }
        }
        return true;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
