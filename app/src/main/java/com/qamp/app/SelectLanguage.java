package com.qamp.app;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qamp.app.Utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class SelectLanguage extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    LinearLayout setLanguageBtn;
    List<Language> personUtilsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilss.setLanguage(SelectLanguage.this);
        setContentView(R.layout.activity_select_language);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        AppUtils.setActivityStyle(this, toolbar);
        AppUtils.setStatusBarColor(SelectLanguage.this, R.color.colorAccent);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        setRecycler();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Utilss.setLanguage(SelectLanguage.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setRecycler() {
        recyclerView = (RecyclerView) findViewById(R.id.language_recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        personUtilsList = new ArrayList<>();

        //Adding Data into ArrayList
        personUtilsList.add(new Language(getString(R.string.Default), ""));
        personUtilsList.add(new Language(getString(R.string.English), "en"));
        personUtilsList.add(new Language(getString(R.string.Hindi), "hi"));
        personUtilsList.add(new Language(getString(R.string.Urdu), "ur"));
        personUtilsList.add(new Language(getString(R.string.Punjabi), "pa"));
        personUtilsList.add(new Language(getString(R.string.Gujarati), "gu"));
        mAdapter = new LanguageAdapter(this, personUtilsList);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        SelectLanguage.this.finish();
        QampUiHelper.launchEditProfile(SelectLanguage.this, 0, 0, false);
    }


}