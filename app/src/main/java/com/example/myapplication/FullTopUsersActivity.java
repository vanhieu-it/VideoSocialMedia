package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.myapplication.Adapters.TopUsersAdapter;
import com.example.myapplication.Entities.User;

import java.util.ArrayList;
import java.util.Collections;

public class FullTopUsersActivity extends AppCompatActivity {
    ListView lvTopUsers;
    ArrayList<User> listTopUsers = new ArrayList<>();
    TopUsersAdapter topUsersAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_top_users);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setControl();
        String endpoint = String.format("https://video-vds.herokuapp.com/channel/%s/top", StatisticInChannelActivity.channelId);
        listTopUsers = StatisticInChannelActivity.getTopUser(endpoint);
        Collections.sort(listTopUsers);
        setEvent();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void setControl() {
        lvTopUsers = findViewById(R.id.listTopUsers);
    }

    private void setEvent() {
        topUsersAdapter = new TopUsersAdapter(FullTopUsersActivity.this, listTopUsers, R.layout.list_top_users);
        lvTopUsers.setAdapter(topUsersAdapter);
    }
}