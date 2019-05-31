package com.demo.dictionary.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.demo.dictionary.R;
import com.demo.dictionary.Words;
import com.demo.dictionary.WordsAdapter;
import com.demo.dictionary.helperClasses.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class WordsList extends Fragment {


    private RecyclerView recyclerView;
    private WordsAdapter wordsAdapter;
    private ArrayList<Words> listOfWords;
    private DatabaseHelper databaseHelper;
    private SearchView searchView;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_list_of_words, container, false);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            try {
                listOfWords = new ArrayList<>(databaseHelper.getList());
                wordsAdapter = new WordsAdapter(listOfWords, view.getContext());
                recyclerView.setAdapter(wordsAdapter);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        databaseHelper = new DatabaseHelper(view.getContext());

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {

                    listOfWords = new ArrayList<>(databaseHelper.getListByEnglishWord(newText));
                    if (newText.length() == 0) {
                        wordsAdapter = new WordsAdapter(listOfWords, view.getContext());
                        recyclerView.setAdapter(wordsAdapter);
                    } else {
                        executeTask(newText);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        try {
            listOfWords = new ArrayList<>(databaseHelper.getList());
            wordsAdapter = new WordsAdapter(listOfWords, view.getContext());
            recyclerView.setAdapter(wordsAdapter);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void executeTask(String word) {

        WordsList.DictionaryAsyncTask task = new WordsList.DictionaryAsyncTask();
        task.execute("https://lt-translate-test.herokuapp.com/?langpair=en-de&query=" + word);
    }

    private class DictionaryAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            HttpURLConnection httpURLConnection;
            StringBuilder builder = new StringBuilder();


            try {
                URL url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int symbol = reader.read();

                while (symbol != -1) {
                    builder.append((char) symbol);
                    symbol = reader.read();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return builder.toString();

        }

        @Override
        protected void onPostExecute(String content) {
            super.onPostExecute(content);

            try {
                JSONArray jsonArray = new JSONArray(content);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String german = jsonObject.getString("l1_text");
                    String english = jsonObject.getString("l2_text");

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
                    String strDate = mdformat.format(calendar.getTime());

                    listOfWords.add(new Words(mdformat.parse(strDate), english, german));
                    wordsAdapter = new WordsAdapter(listOfWords, view.getContext());
                    recyclerView.setAdapter(wordsAdapter);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
