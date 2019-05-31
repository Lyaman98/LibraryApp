package com.demo.dictionary.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demo.dictionary.R;
import com.demo.dictionary.Words;
import com.demo.dictionary.WordsAdapter;
import com.demo.dictionary.helperClasses.DatabaseHelper;

import java.text.ParseException;
import java.util.ArrayList;

public class TodayWordsList extends Fragment {

    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private ArrayList<Words> wordsArrayList;
    private WordsAdapter wordsAdapter;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_today_list_of_words, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        this.view = view;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        databaseHelper = new DatabaseHelper(view.getContext());
        try {
            wordsArrayList = new ArrayList<>(databaseHelper.getListByDate());
            wordsAdapter = new WordsAdapter(wordsArrayList, view.getContext());
            recyclerView.setAdapter(wordsAdapter);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            try {
                wordsArrayList = new ArrayList<>(databaseHelper.getListByDate());
                wordsAdapter = new WordsAdapter(wordsArrayList, view.getContext());
                recyclerView.setAdapter(wordsAdapter);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


}
