package com.example.phamn.diamondmine;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    Button btnPlay, btnNewGame, btnBack, btnOption, btnExit;
    CheckBox checkBoxMusic, checkBoxSound;
    DataBase dataBase = new DataBase(this);
    boolean music = true;
    boolean sound = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        music = intent.getBooleanExtra("music", true);
        sound = intent.getBooleanExtra("sound", true);
        mediaPlayer = MediaPlayer.create(this, R.raw.soundtrack);
        if(music) {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);   // vòng lặp vô hạn
        }

        btnPlay = (Button) findViewById(R.id.button_play);
        btnExit = (Button) findViewById(R.id.button_exitGame);
        btnOption = (Button) findViewById(R.id.button_option);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogPlay();
            }
        });

        btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogOption();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogConfirm("Are you sure want to Exit?");
            }
        });
    }

    public void showDialogPlay() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.play_dialog);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        btnNewGame = (Button)dialog.findViewById(R.id.button_new_game);
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                intent.putExtra("music", music);
                intent.putExtra("sound", sound);
                startActivity(intent);
                finish();
                dialog.cancel();
            }
        });
        btnBack = (Button)dialog.findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    public void showDialogOption() {
        final Dialog dialogOption = new Dialog(this);
        dialogOption.setContentView(R.layout.option_dialog);
        dialogOption.show();
        dialogOption.setCanceledOnTouchOutside(false);

        checkBoxMusic = (CheckBox)dialogOption.findViewById(R.id.checkbox_music);
        checkBoxSound = (CheckBox)dialogOption.findViewById(R.id.checkbox_sound);

        checkBoxMusic.setChecked(music);
        checkBoxSound.setChecked(sound);

        Button btnHelp = (Button)dialogOption.findViewById(R.id.button_help);
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogNotice("Select two consecutive cells in rows or columns to make three or more consecutive cells of same color");
            }
        });

        Button btnResetScore = (Button)dialogOption.findViewById(R.id.button_reset_score);
        btnResetScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataBase.removeData(MainActivity.this);
                showDialogNotice("High score was successfully removed!");
            }
        });

        Button btnBack = (Button)dialogOption.findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                music = checkBoxMusic.isChecked();
                sound = checkBoxSound.isChecked();
                if(music){
                    if(!mediaPlayer.isPlaying()) {
                        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.soundtrack);
                        mediaPlayer.start();
                        mediaPlayer.setLooping(true);   // vòng lặp vô hạn
                    }
                }
                else
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                dialogOption.cancel();
            }
        });
    }

    public void showDialogNotice(String notice) {
        final Dialog dialogNotice = new Dialog(this);
        dialogNotice.setContentView(R.layout.notice_dialog);
        dialogNotice.show();
        dialogNotice.setCanceledOnTouchOutside(false);

        TextView tvNotice = (TextView)dialogNotice.findViewById(R.id.textview_notice);
        tvNotice.setText(notice);

        Button btnOk = (Button)dialogNotice.findViewById(R.id.button_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogNotice.cancel();
            }
        });
    }

    public void showDialogConfirm(String content) {
        final Dialog dialogConfirm = new Dialog(this);
        dialogConfirm.setContentView(R.layout.confirm_dialog);
        dialogConfirm.show();
        dialogConfirm.setCanceledOnTouchOutside(false);

        TextView tv = (TextView)dialogConfirm.findViewById(R.id.textview_confirm);
        tv.setText(content);

        Button btnNo = (Button)dialogConfirm.findViewById(R.id.button_no);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirm.cancel();
            }
        });

        Button btnYes = (Button)dialogConfirm.findViewById(R.id.button_yes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirm.cancel();
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }
}
