package com.demo.dictionary.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.demo.dictionary.R;
import com.demo.dictionary.helperClasses.DatabaseHelper;

import java.io.IOException;
import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class NewWord extends Fragment {

    private View view;
    private Button addWordButton;
    private MediaRecorder mediaRecorder;
    private ImageView record;
    private Chronometer counter;
    private EditText englishWord;
    private EditText germanWord;
    private FrameLayout frameLayout;
    private Handler handler;
    private static final int permissionCode = 1;
    private static DatabaseHelper databaseHelper;
    private String audio_path;
    private boolean recordClicked;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_add_words, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        record = view.findViewById(R.id.record);
        counter = view.findViewById(R.id.timer);
        englishWord = view.findViewById(R.id.englishWord);
        germanWord = view.findViewById(R.id.germanWord);
        frameLayout = view.findViewById(R.id.frameLayout);
        addWordButton = view.findViewById(R.id.addWord);

        handler = new Handler();
        databaseHelper = new DatabaseHelper(view.getContext());

        hideKeyboard();
        addWordClick();
        recordClick();


    }

    public void hideKeyboard() {

        frameLayout.setOnClickListener(v -> {
            InputMethodManager methodManager = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(INPUT_METHOD_SERVICE);
            assert methodManager != null;
            methodManager.hideSoftInputFromWindow(englishWord.getWindowToken(), 0);
            methodManager.hideSoftInputFromWindow(germanWord.getWindowToken(), 0);
        });

    }

    public void addWordClick() {

        addWordButton.setOnClickListener(v -> {
            String englishWordString = englishWord.getText().toString();
            String germanWordString = germanWord.getText().toString();

            if (!TextUtils.isEmpty(englishWordString) && !TextUtils.isEmpty(germanWordString)) {
                new Thread(() -> {
                    databaseHelper.insertWord(englishWordString, germanWordString);
                    handler.post(() -> {
                        Toast.makeText(view.getContext(), "Data is saved!", Toast.LENGTH_LONG).show();
                        englishWord.getText().clear();
                        germanWord.getText().clear();
                    });

                }).start();
            } else {
                Toast.makeText(view.getContext(), "Empty fields", Toast.LENGTH_LONG).show();

            }
        });


    }


    public void recordClick() {

        record.setOnClickListener(v -> {
            if (checkPermission()) {

                if (!recordClicked) {
                    audio_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                            + germanWord.getText().toString() + "AudioRecording.3gp";

                    setMediaRecorder();

                    try {

                        record.playSoundEffect(0);
                        mediaRecorder.prepare();
                        mediaRecorder.start();

                        recordClicked = true;
                        record.setImageResource(R.drawable.stopbutton);

                        counter.setVisibility(View.VISIBLE);
                        counter.setBase(SystemClock.elapsedRealtime());
                        counter.start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    record.setImageResource(R.drawable.microphone);
                    mediaRecorder.stop();
                    recordClicked = false;
                    counter.stop();

                    counter.setVisibility(View.INVISIBLE);

                    Toast.makeText(view.getContext(), "Recorded successfully!",
                            Toast.LENGTH_LONG).show();


                }
            } else {
                ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                }, permissionCode);
            }
        });

    }

    private void setMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(audio_path);
    }

    private boolean checkPermission() {
        int writePermission = ContextCompat.checkSelfPermission(view.getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int recordPermission = ContextCompat.checkSelfPermission(view.getContext(),
                Manifest.permission.RECORD_AUDIO);

        return writePermission == PackageManager.PERMISSION_GRANTED &&
                recordPermission == PackageManager.PERMISSION_GRANTED;

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case permissionCode:
                if (grantResults.length > 0) {
                    boolean storagePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean recordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (!storagePermission || !recordPermission) {
                        Toast.makeText(view.getContext(), "Permission Denied",
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}
