package com.demo.dictionary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.demo.dictionary.helperClasses.DatabaseHelper;

import java.io.File;
import java.util.List;

public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.WordsViewHolder> {

    private List<Words> wordsList;
    private Context context;
    private DatabaseHelper databaseHelper;
    private MediaPlayer mediaPlayer;

    public WordsAdapter(List<Words> wordsList, Context context) {
        this.wordsList = wordsList;
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public WordsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.fragment_list_of_words, viewGroup, false);
        return new WordsAdapter.WordsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordsViewHolder wordsViewHolder, int i) {

        Words word = wordsList.get(i);
        wordsViewHolder.english.setText(word.getEnglish());
        wordsViewHolder.german.setText(word.getGerman());

        File file = getFile(word.getGerman());

        if (file.exists()) {
            wordsViewHolder.play.setVisibility(View.VISIBLE);
        } else {
            wordsViewHolder.play.setVisibility(View.INVISIBLE);
        }

        if (word.isAdded()) {


            wordsViewHolder.action.setImageResource(R.drawable.minus);
            wordsViewHolder.edit.setImageResource(R.drawable.edit);

            wordsViewHolder.edit.setOnClickListener(v -> {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Edit Word");

                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                EditText germanWordEdit = new EditText(context);
                EditText englishWordEdit = new EditText(context);
                layout.addView(germanWordEdit);
                layout.addView(englishWordEdit);

                alertDialog.setView(layout);

                germanWordEdit.setText(wordsViewHolder.german.getText());
                englishWordEdit.setText(wordsViewHolder.english.getText());

                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save", (dialog, which) -> {

                    databaseHelper.updateWord(wordsViewHolder.german.getText().toString(),
                            englishWordEdit.getText().toString(),
                            germanWordEdit.getText().toString());

                    wordsViewHolder.german.setText(germanWordEdit.getText());
                    wordsViewHolder.english.setText(englishWordEdit.getText());

                });

                alertDialog.show();
            });

            wordsViewHolder.action.setOnClickListener(v -> {

                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete?")
                        .setPositiveButton("Yes", (dialogInterface, index) -> {
                            databaseHelper.deleteWord(word);
                            wordsList.remove(word);
                            notifyDataSetChanged();
                            file.delete();
                        })
                        .setNegativeButton("No", null)
                        .show();

            });

            wordsViewHolder.play.setOnClickListener(v -> {

                if (wordsViewHolder.play.getDrawable().getConstantState() ==
                        ContextCompat.getDrawable(context, R.drawable.play).getConstantState()) {
                    wordsViewHolder.play.setImageResource(R.drawable.stop);
                    mediaPlayer = MediaPlayer.create(context, Uri.fromFile(file));
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(mp -> wordsViewHolder.play.setImageResource(R.drawable.play));

                } else {
                    wordsViewHolder.play.setImageResource(R.drawable.play);
                    mediaPlayer.stop();

                }
            });
        } else {
            wordsViewHolder.action.setImageResource(R.drawable.add);

            wordsViewHolder.action.setOnClickListener(v -> {
                databaseHelper.insertWord(word.getEnglish(), word.getGerman());
                wordsViewHolder.action.setImageResource(R.drawable.minus);

            });

        }

    }

    private File getFile(String fileName) {
        return new File(Environment
                .getExternalStorageDirectory()
                .getAbsolutePath() + "/"
                + fileName + "AudioRecording.3gp");
    }

    @Override
    public int getItemCount() {
        return wordsList.size();
    }

    class WordsViewHolder extends RecyclerView.ViewHolder {

        TextView english;
        TextView german;
        ImageView play;
        ImageView action;
        ImageView edit;


        public WordsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.english = itemView.findViewById(R.id.english);
            this.german = itemView.findViewById(R.id.german);
            this.play = itemView.findViewById(R.id.play);
            this.action = itemView.findViewById(R.id.buttonAction);
            this.edit = itemView.findViewById(R.id.buttonEdit);
        }

    }
}
