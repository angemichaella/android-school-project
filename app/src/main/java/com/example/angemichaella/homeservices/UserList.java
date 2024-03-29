package com.example.angemichaella.homeservices;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ArrayAdapter;


import java.util.List;



public class UserList extends ArrayAdapter<User>{


    private Activity context;
    List<User> users;

    public UserList(Activity context, List<User> users){
        super(context, R.layout.activity_user_list_layout, users);
        this.context = context;
        this.users = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.activity_user_list_layout, null, true);

        User user = users.get(position);

        if (user.getType() != "Admin") {
            TextView textViewUsername = (TextView) listViewItem.findViewById(R.id.textViewUsername);
            TextView textViewUsertype = (TextView) listViewItem.findViewById(R.id.textViewUsertype);


            textViewUsername.setText(user.getUsername());

            if (user.getType().equals("HomeOwner")){
                textViewUsertype.setText("Home Owner");
            }
            else if (user.getType().equals("ServiceProvider")) {
                textViewUsertype.setText("Service Provider");
            }

        }
        return listViewItem;
    }
}



