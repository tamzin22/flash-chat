package com.otwomaevelyne.flashchatnewfirebase;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter  extends BaseAdapter {

    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private String mDisplayname;
    private ArrayList<DataSnapshot>mSnapshotList;

    //listener to notify new message from firebase
    public ChildEventListener mListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            //to append new item to the array list
            mSnapshotList.add(dataSnapshot);
            //to automatically refresh upon getting new message
            notifyDataSetChanged();

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //constructor for configuring the chatlist adapter
    public ChatListAdapter(Activity activity,DatabaseReference ref,String name){

     mActivity = activity;
     mDisplayname = name;
     mDatabaseReference = ref.child("messages");
     mDatabaseReference.addChildEventListener(mListener);

     mSnapshotList = new ArrayList<>();

    }
    //inner class o hold all the views in a single chat row
    static class ViewHolder{

        TextView mAuthorName;
        TextView body;
        //to style messages programmatically
        LinearLayout.LayoutParams params;
    }

    @Override
    //returns number of items in the list
    public int getCount() {

        return mSnapshotList.size();
    }

    @Override
    public InstantMessage getItem(int i) {

        //to get a particular snapsot from the arraylist
        DataSnapshot snapshot = mSnapshotList.get(i);

        return snapshot.getValue(InstantMessage.class);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    //returns the item at a certain index in the list
    public View getView(int i, View view, ViewGroup viewGroup) {

        //check if there is an existing row that can be reused
        if (view == null){
            //create a view from a layout xml file if there is no existing row
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //create a new row from xml file and store the data of the row in a viewholder
            view = inflater.inflate(R.layout.chat_msg_row,viewGroup,false);

            final ViewHolder holder = new ViewHolder();
            //link the fields of the viewholderto the views on the chatmessage row
            holder.mAuthorName = view.findViewById(R.id.author);
            holder.params = (LinearLayout.LayoutParams) holder.mAuthorName.getLayoutParams();

            //give the adapter a way of storing data for a short time and reuse later
            view.setTag(holder);

        }
        //to ensure display the correct message and author
        final InstantMessage message = getItem(i);
        //to get the view holder that was temporarily saved
        //getTag() helps to recycle views
        final ViewHolder holder = (ViewHolder) view.getTag();

        //retrieve current author in the list from instant message
        String author = message.getAuthor();
        //set the text of the author name textview with new information
        holder.mAuthorName.setText(author);

        String msg = message.getMessage();
        holder.body.setText(msg);

        return view;
    }
    //to stop ChildListen when there no even

    public void cleanup(){

        mDatabaseReference.removeEventListener(mListener);
    }

}