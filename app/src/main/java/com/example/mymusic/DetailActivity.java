package com.example.mymusic;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailActivity extends Activity {
    Context context;
    static final int VIEW_MODE_LISTVIEW = 0;
    static final int VIEW_MODE_GRIDVIEW = 1;
    ListView listView;
    GridView gridView;
    private ViewStub stubGrid;
    private ViewStub stubList;
    private int currentViewMode = 0;
    ListAdapter adapter;
    // Songs list
    public ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);
        stubList = (ViewStub) findViewById(R.id.stub_list);
        stubGrid = (ViewStub) findViewById(R.id.stub_grid);
        //readFile(lyricArray);
        stubList.inflate();
        stubGrid.inflate();
        listView = (ListView) findViewById(R.id.listviewID);
        gridView = (GridView) findViewById(R.id.mygridview);

        context = getApplicationContext();
        songsListData = new ArrayList<HashMap<String, String>>();
        //get songListData from MainActivity
        songsListData = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("musiclist");

        SharedPreferences sharedPreferences = getSharedPreferences("ViewMode", MODE_PRIVATE);
        currentViewMode = sharedPreferences.getInt("currentViewMode", VIEW_MODE_LISTVIEW);//Default is view listview
        //Register item lick
        listView.setOnItemClickListener(onItemClick);
        gridView.setOnItemClickListener(onItemClick);
        switchView();

    }

    /**
     * Switch Listview and Gridview
     */
    private void switchView() {

        if (VIEW_MODE_LISTVIEW == currentViewMode) {
            //Display listview
            stubList.setVisibility(View.VISIBLE);
            //Hide gridview
            stubGrid.setVisibility(View.GONE);
        } else {
            //Hide listview
            stubList.setVisibility(View.GONE);
            //Display gridview
            stubGrid.setVisibility(View.VISIBLE);
        }
        setAdapters();
    }

    private void setAdapters() {
        if (VIEW_MODE_LISTVIEW == currentViewMode) {
            adapter = new SimpleAdapter(this, songsListData,
                    R.layout.listview_row, new String[]{"songTitle"}, new int[]{
                    R.id.nameTextViewID});

            listView.setAdapter(adapter);
        } else {
            adapter = new SimpleAdapter(this, songsListData,
                    R.layout.gridview_items, new String[]{"songTitle"}, new int[]{
                    R.id.txtTitle});

            gridView.setAdapter(adapter);

        }
    }

    AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Do any thing when user click to item
            int songIndex = position;

            // Starting new intent
            Intent in = new Intent(getApplicationContext(),
                    MainActivity.class);
            // Sending songIndex to PlayerActivity
            in.putExtra("songIndex", songIndex);
            setResult(100, in);
            // Closing PlayListView
            finish();

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_menu_1:
                if (VIEW_MODE_LISTVIEW == currentViewMode) {
                    currentViewMode = VIEW_MODE_GRIDVIEW;
                } else {
                    currentViewMode = VIEW_MODE_LISTVIEW;
                }
                //Switch view
                switchView();
                //Save view mode in share reference
                SharedPreferences sharedPreferences = getSharedPreferences("ViewMode", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("currentViewMode", currentViewMode);
                editor.commit();

                break;
        }
        return true;
    }


}