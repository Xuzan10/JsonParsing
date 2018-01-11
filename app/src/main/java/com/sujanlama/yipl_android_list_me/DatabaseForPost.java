package com.sujanlama.yipl_android_list_me;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sujanlama.yipl_android_list_me.helper.CommentHelper;
import com.sujanlama.yipl_android_list_me.helper.PostHelper;

import java.util.ArrayList;

/**
 * Created by user on 12/15/2017.
 */

public class DatabaseForPost extends SQLiteOpenHelper {

    static String name = "postdb";
    static int version = 1;
    String createPostTable = "Create table if not exists `posts` (`id` TEXT , `userId` TEXT,`title` TEXT, `body` TEXT)";
    String createCommentTable = "Create table if not exists `comment` (`id` TEXT , `postId` TEXT,`name` TEXT,`email` TEXT, `body` TEXT)";


    public DatabaseForPost(Context context) {
        super(context, name, null, version);
        getWritableDatabase().execSQL(createPostTable);
        getWritableDatabase().execSQL(createCommentTable);
    }

    public void insertPosts(ContentValues cv) {
        getWritableDatabase().insert("posts", null, cv);
    }

    public void insertComments(ContentValues cv) {
        getWritableDatabase().insert("comment", null, cv);
    }


    public void deletedatafromTable() {
        String sql = "Delete from posts ";
        getWritableDatabase().execSQL(sql);
    }

    public void deleteallcomment(String id) {
        String sql = "Delete from comment where postId="+id;
        getWritableDatabase().execSQL(sql);
    }

    public ArrayList<CommentHelper> getComments(String id) {
        String sql = "Select * from comment where postId="+id;
        Cursor cursor = getWritableDatabase().rawQuery(sql, null);
        ArrayList<CommentHelper> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            CommentHelper helper = new CommentHelper();
            helper.setPostId(cursor.getString(cursor.getColumnIndex("postId")));
            helper.setId(cursor.getString(cursor.getColumnIndex("id")));
            helper.setName(cursor.getString(cursor.getColumnIndex("name")));
            helper.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            helper.setBody(cursor.getString(cursor.getColumnIndex("body")));
            list.add(helper);

        }
        cursor.close();
        return list;
    }

    public CommentHelper getCommentbyId(String id) {
        String sql = "Select * from comment where postId=" + id;
        Cursor cursor = getWritableDatabase().rawQuery(sql, null);
        CommentHelper helper = new CommentHelper();
        while (cursor.moveToNext()) {
            helper.setPostId(cursor.getString(cursor.getColumnIndex("postId")));
            helper.setId(cursor.getString(cursor.getColumnIndex("id")));
            helper.setName(cursor.getString(cursor.getColumnIndex("name")));
            helper.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            helper.setBody(cursor.getString(cursor.getColumnIndex("body")));
        }
        cursor.close();
        return helper;
    }


    public ArrayList<PostHelper> getPosts() {
        String sql = "Select * from posts";
        Cursor cursor = getWritableDatabase().rawQuery(sql, null);
        ArrayList<PostHelper> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            PostHelper helper = new PostHelper();
            helper.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
            helper.setId(cursor.getString(cursor.getColumnIndex("id")));
            helper.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            helper.setBody(cursor.getString(cursor.getColumnIndex("body")));
            list.add(helper);

        }
        cursor.close();
        return list;
    }

    public PostHelper getPostbyId(String id) {
        String sql = "Select * from posts where id=" + id;
        Cursor cursor = getWritableDatabase().rawQuery(sql, null);
        PostHelper helper = new PostHelper();
        while (cursor.moveToNext()) {
            helper.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
            helper.setId(cursor.getString(cursor.getColumnIndex("id")));
            helper.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            helper.setBody(cursor.getString(cursor.getColumnIndex("body")));
        }
        cursor.close();
        return helper;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
