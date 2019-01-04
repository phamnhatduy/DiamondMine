package com.example.phamn.diamondmine;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

public class PlayActivity extends AppCompatActivity {
    TextView tvScore, tvBest;
    Button btnPause;
    ImageView iv00, iv01, iv02, iv03, iv04, iv05, iv06,
            iv10, iv11, iv12, iv13, iv14, iv15, iv16,
            iv20, iv21, iv22, iv23, iv24, iv25, iv26,
            iv30, iv31, iv32, iv33, iv34, iv35, iv36,
            iv40, iv41, iv42, iv43, iv44, iv45, iv46,
            iv50, iv51, iv52, iv53, iv54, iv55, iv56,
            iv60, iv61, iv62, iv63, iv64, iv65, iv66,
            iv70, iv71, iv72, iv73, iv74, iv75, iv76,
            iv80, iv81, iv82, iv83, iv84, iv85, iv86;
    ProgressBar progressBar;
    boolean chose = false;      // đã chọn ô nào chưa
    int rowPrev, colPrev, rowNext, colNext;
//    int[][] A ={{2,12,1,2,4,1,2},
//                {3,2,2,1,1,2,2},
//                {2,3,2,1,2,1,4},
//                {2,2,1,2,3,2,1},
//                {1,2,3,1,2,12,1},
//                {3,11,1,3,1,5,4},
//                {1,1,2,11,3,3,2},
//                {1,2,1,1,3,1,4},
//                {3,1,1,2,1,2,1}};
    int[][] A = new int[9][7];
    int width = 7;
    int height = 9;
    int numberColor = 8;
    int[][] B = new int[height][width];
    int slideDirection; // hướng trượt
    int sumBonus, bonus, bestScore, combo;
    int wImage; // chiều rộng của ô
    boolean isCombo;    // đang trong combo
    boolean isCheck = false;    // đang kiểm tra các ô
    boolean music, sound;
    MediaPlayer soundtrack;
    MediaPlayer soundClick;
    MediaPlayer soundWin;
    CheckBox checkBoxMusic, checkBoxSound;
    CountDownTimer count, count1, count2, count3, countDownTime;
    int maxTime;
    int remainTime;
    Effect effect = new Effect();
    Random random = new Random();
    DataBase dataBase = new DataBase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Intent intent = getIntent();
        soundtrack = MediaPlayer.create(this,R.raw.soundtrack);
        soundClick = MediaPlayer.create(this, R.raw.clickone);
        soundWin = MediaPlayer.create(this, R.raw.bubbles_win);
        music = intent.getBooleanExtra("music", false);
        sound = intent.getBooleanExtra("sound", false);
        updateSound();
        mapping();
        autoChangeSize();
        onClickImageView();
        loadImageView();
        newGame();

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogPause();
                countDownTime.cancel();
            }
        });

    }

    private void updateSound(){
        if(music){
            soundtrack = MediaPlayer.create(this,R.raw.soundtrack);
            soundtrack.setLooping(true);
            soundtrack.start();
        }
        else
            soundtrack.stop();
    }

    private void countTimePlay(int currentTime){
        countDownTime = new CountDownTimer(currentTime, 500) {
            @Override
            public void onTick(long l) {
                remainTime -= 500;
                progressBar.setProgress(remainTime);
                if(remainTime == 0) {
                    onFinish();
                    countDownTime.cancel();
                }
            }

            @Override
            public void onFinish() {
                Toast.makeText(PlayActivity.this, "Game Over", Toast.LENGTH_SHORT).show();
                gameOver();
            }
        };
        countDownTime.start();
    }

    private void newGame() {
        randomImage();
//        A = new int[][]{{2,2,1,2,2,1,1},
//                        {3,2,7,3,3,4,2},
//                        {4,5,6,1,2,7,1},
//                        {2,2,3,5,4,3,6},
//                        {1,1,5,6,2,7,2},
//                        {7,7,8,3,1,5,4},
//                        {1,3,2,4,4,2,6},
//                        {6,6,3,1,3,1,4},
//                        {1,1,2,4,5,2,1}};
//        loadImageView();
        isCombo = false;
        sumBonus = 0;
        combo = 0;
        bonus = 0;
        tvScore.setText("" + sumBonus);
        progressBar.setProgress(maxTime);
        bestScore = getHighScore();
        maxTime = 60000; // 60s
        remainTime = maxTime;
        progressBar.setMax(maxTime);
        progressBar.setProgress(maxTime);

        countTimePlay(maxTime);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                getImageView(i, j).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.effect_appear));
            }
        }
    }

    private int getHighScore(){
        String hS = dataBase.getMax();
        if(hS != null){
            tvBest.setText("Best: "+ hS);
            return Integer.parseInt(hS);
        }
        else
            tvBest.setText("0");
        return 0;
    }

    private void gameOver(){
        if(sumBonus > bestScore){
            dataBase.saveGame(sumBonus, DateFormat.getDateTimeInstance().format(new Date()));
            showDialogHighScore();
        }
        else {
            showDialogGameOver();
        }
    }

    public void showDialogPause() {
        final Dialog dialogPause = new Dialog(this);
        dialogPause.setContentView(R.layout.pause_dialog);
        dialogPause.show();
        dialogPause.setCanceledOnTouchOutside(false);

        checkBoxMusic = (CheckBox)dialogPause.findViewById(R.id.checkbox_music);
        checkBoxSound = (CheckBox)dialogPause.findViewById(R.id.checkbox_sound);

        checkBoxMusic.setChecked(music);
        checkBoxSound.setChecked(sound);

        Button btnResume = (Button)dialogPause.findViewById(R.id.button_resume);
        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                music = checkBoxMusic.isChecked();
                sound = checkBoxSound.isChecked();
                updateSound();
                dialogPause.cancel();
                countTimePlay(remainTime);
            }
        });

        Button btnNewGame = (Button)dialogPause.findViewById(R.id.button_new_game);
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newGame();
                dialogPause.cancel();
            }
        });

        Button btnQuit = (Button)dialogPause.findViewById(R.id.button_quit);
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                music = checkBoxMusic.isChecked();
                sound = checkBoxSound.isChecked();
                showDialogConfirm("Are you sure want to quit?");
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
                Intent intent = new Intent(PlayActivity.this, MainActivity.class);
                intent.putExtra("music", music);
                intent.putExtra("sound", sound);
                startActivity(intent);
            }
        });
    }

    public void showDialogHighScore() {
        final Dialog dialogHighScore = new Dialog(this);
        dialogHighScore.setContentView(R.layout.high_score_dialog);
        dialogHighScore.show();
        dialogHighScore.setCanceledOnTouchOutside(false);

        TextView tvHighScore = (TextView)dialogHighScore.findViewById(R.id.textview_new_high_score);
        tvHighScore.setText("" + sumBonus);

        Button btnNewGame = (Button)dialogHighScore.findViewById(R.id.button_new_game);
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newGame();
                dialogHighScore.cancel();
            }
        });

        Button btnQuit = (Button)dialogHighScore.findViewById(R.id.button_quit);
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // lưu điểm, thời gian, ma trận
                dialogHighScore.cancel();
                finish();
                Intent intent = new Intent(PlayActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showDialogGameOver() {
        final Dialog dialogGameOver = new Dialog(this);
        dialogGameOver.setContentView(R.layout.game_over_dialog);
        dialogGameOver.show();
        dialogGameOver.setCanceledOnTouchOutside(false);

        TextView tvScore = (TextView)dialogGameOver.findViewById(R.id.textview_score);
        tvScore.setText("Your score: " + sumBonus);

        Button btnPlayAgain = (Button)dialogGameOver.findViewById(R.id.button_play_again);
        btnPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newGame();
                dialogGameOver.cancel();
            }
        });

        Button btnQuit = (Button)dialogGameOver.findViewById(R.id.button_quit);
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // lưu điểm, thời gian, ma trận
                dialogGameOver.cancel();
                finish();
                Intent intent = new Intent(PlayActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onClickImageView(){
        setOnClickImageView(iv00,0,0);
        setOnClickImageView(iv01,0,1);
        setOnClickImageView(iv02,0,2);
        setOnClickImageView(iv03,0,3);
        setOnClickImageView(iv04,0,4);
        setOnClickImageView(iv05,0,5);
        setOnClickImageView(iv06,0,6);

        setOnClickImageView(iv10,1,0);
        setOnClickImageView(iv11,1,1);
        setOnClickImageView(iv12,1,2);
        setOnClickImageView(iv13,1,3);
        setOnClickImageView(iv14,1,4);
        setOnClickImageView(iv15,1,5);
        setOnClickImageView(iv16,1,6);

        setOnClickImageView(iv20,2,0);
        setOnClickImageView(iv21,2,1);
        setOnClickImageView(iv22,2,2);
        setOnClickImageView(iv23,2,3);
        setOnClickImageView(iv24,2,4);
        setOnClickImageView(iv25,2,5);
        setOnClickImageView(iv26,2,6);

        setOnClickImageView(iv30,3,0);
        setOnClickImageView(iv31,3,1);
        setOnClickImageView(iv32,3,2);
        setOnClickImageView(iv33,3,3);
        setOnClickImageView(iv34,3,4);
        setOnClickImageView(iv35,3,5);
        setOnClickImageView(iv36,3,6);

        setOnClickImageView(iv40,4,0);
        setOnClickImageView(iv41,4,1);
        setOnClickImageView(iv42,4,2);
        setOnClickImageView(iv43,4,3);
        setOnClickImageView(iv44,4,4);
        setOnClickImageView(iv45,4,5);
        setOnClickImageView(iv46,4,6);

        setOnClickImageView(iv50,5,0);
        setOnClickImageView(iv51,5,1);
        setOnClickImageView(iv52,5,2);
        setOnClickImageView(iv53,5,3);
        setOnClickImageView(iv54,5,4);
        setOnClickImageView(iv55,5,5);
        setOnClickImageView(iv56,5,6);

        setOnClickImageView(iv60,6,0);
        setOnClickImageView(iv61,6,1);
        setOnClickImageView(iv62,6,2);
        setOnClickImageView(iv63,6,3);
        setOnClickImageView(iv64,6,4);
        setOnClickImageView(iv65,6,5);
        setOnClickImageView(iv66,6,6);

        setOnClickImageView(iv70,7,0);
        setOnClickImageView(iv71,7,1);
        setOnClickImageView(iv72,7,2);
        setOnClickImageView(iv73,7,3);
        setOnClickImageView(iv74,7,4);
        setOnClickImageView(iv75,7,5);
        setOnClickImageView(iv76,7,6);

        setOnClickImageView(iv80,8,0);
        setOnClickImageView(iv81,8,1);
        setOnClickImageView(iv82,8,2);
        setOnClickImageView(iv83,8,3);
        setOnClickImageView(iv84,8,4);
        setOnClickImageView(iv85,8,5);
        setOnClickImageView(iv86,8,6);
    }

    private void setOnClickImageView(final ImageView iv, final int row, final int col) {
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCheck) {  // nếu ko có ô đang kiểm tra thì mới được click
                    if (!chose) {     // chua chon cai nao
                        if(sound)
                            soundClick.start();
                        chose = true;
                        rowPrev = row; // cap nhat lai row, col truoc
                        colPrev = col;
                        effect.alphaImage(getImageView(rowPrev, colPrev));
                    } else {  // da chon 1 cai
                        if(sound)
                            soundClick.start();
                        getImageView(rowPrev, colPrev).clearAnimation();
                        isCheck = true; // đang kiểm tra
                        rowNext = row;  // cap nhat lai row, col sau
                        colNext = col;
                        int i = col - colPrev;
                        int j = row - rowPrev;

                        if (i > 1 || i < -1 || j > 1 || j < -1 ||
                                (i == 0 && j == 0) ||
                                (i == -1 && j == -1) ||
                                (i == 1 && j == 1) ||
                                (i == 1 && j == -1) ||
                                (i == -1 && j == 1)) {
                            chose = false;
                            isCheck = false;    // không có kiểm tra ô nào
                            return;
                        }
                        if (i == 1 && j == 0) {  // cung hang khac cot styleSilde 12
                            slideDirection = 12;
                        }
                        if (i == -1 && j == 0) { // cung hang khac cot
                            slideDirection = 21;
                        }
                        if (i == 0 && j == 1) { // cung cot khac hang
                            slideDirection = 13;
                        }
                        if (i == 0 && j == -1) { // cung cot khac hang
                            slideDirection = 31;
                        }
                        chose = false;

                        effect.swapImage(getImageView(rowPrev, colPrev), getImageView(rowNext, colNext),
                                getImage(A[rowPrev][colPrev]), getImage(A[rowNext][colNext]));
                        count3 = new CountDownTimer(500, 500) {
                            @Override
                            public void onTick(long l) {
                            }

                            @Override
                            public void onFinish() {
                                checkScore(rowPrev, colPrev, row, col);
                            }
                        };
                        count3.start();
                    }

                }
            }
        });
    }

    private void checkScore(final int row1, final int col1, final int row2, final int col2) {
        boolean f = false;  // không có ghi điểm
        // swap image of Next and Previous
        if(!isCombo && bonus == 0) {  // không phải là combo và chưa có điểm tức là lần duyệt đầu tiên thì 2 ô chọn sẽ đổi vị trí
            int t = A[row1][col1];
            A[row1][col1] = A[row2][col2];
            A[row2][col2] = t;

            getImageView(row1,col1).setImageResource(getImage(A[row1][col1]));  // cập nhật hình ảnh 2 ô
            getImageView(row2,col2).setImageResource(getImage(A[row2][col2]));

            for (int i = 0; i < height; i++) {    // duyệt qua tất cả các phần tử
                for (int j = 0; j < width; j++) {
                    B[i][j] = A[i][j];  // cập nhật lại B
                }
            }
        }
        bonus = 0;
        for (int i = 0; i < height; i++) {    // duyệt qua tất cả các phần tử
            for (int j = 0; j < width; j++) {
                //if(B[i][j] != 0)    // ô chưa xét
                markMatrix(i, j, 3);
            }
        }
        if(isCombo) {   // có ô ăn được thì tính là combo
            isCombo = false;
            combo += 1;
            if(sound)
                soundWin.start();
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 7; j++) {
                    if (B[i][j] == 0) {
                        //effect.alphaImage(getImageView(i, j));
                        getImageView(i, j).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.effect_destroy));
                    }
                    A[i][j] = B[i][j];      // cập nhật lại A
                }
            }
            count = new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    loadImageView();
                    reOrganize(row1, col1, row2, col2);   // cho các ô rơi xuống sau khi ăn
                }
            };
            count.start();
        }
        else {  // isCombo = false;
            if(bonus == 0 && combo == 0) {    // ko có ô nào ăn được sau khi chọn thì trả 2 ô đã chọn về vị trí cũ
                /// hiệu ứng chuyển 2 ô trở lại
                effect.swapImage(getImageView(rowPrev, colPrev), getImageView(rowNext, colNext),
                        getImage(A[rowPrev][colPrev]), getImage(A[rowNext][colNext]));
                int t = A[row1][col1];
                A[row1][col1] = A[row2][col2];
                A[row2][col2] = t;

            }
            if(bonus > 0){
                bonus = 0;
            }
            isCheck = false;    // việc kiểm tra các ô hoàn tất
            combo = 0;
        }
    }

    private void markMatrix(final int row, final int col, int type){// đánh dấu các ô sé ăn
        // type = 1 xét theo chiều ngang
        // type = 2 xét theo chiều dọc
        // type = 3 xét cả 2 chiều
        if(type == 1 || type == 3) {    // xet theo chieu ngang
            if (checkHorizontal(row, col) != null) {    // có ô ăn được theo hàng ngang
                isCombo = true;
                int[] horizontal;
                horizontal = checkHorizontal(row, col).clone();
                //sumBonus += horizontal[width];
                if (horizontal[width + 1] > 0) {   // nếu bên trái có ô ăn được
                    for (int i = 1; i <= horizontal[width + 1]; i++) {   // duyệt qua tất cả các ô bên trái
                        if (checkVertical(row, col - i) != null)    // duyệt theo chiều dọc thấy có nhánh
                            markMatrix(row, col - i, 2);
                    }
                }
                if (horizontal[width + 2] > 0) {    // nếu bên phải có ô ăn được
                    for (int j = 1; j <= horizontal[width + 2]; j++) {   // duyệt qua tất cả các ô bên phải
                        if (checkVertical(row, col + j) != null)    // duyệt theo chiều dọc xem có nhánh không
                            markMatrix(row, col + j, 2);
                    }
                }
            }
        }
        if(type == 2 || type == 3) {
            if (checkVertical(row, col) != null) {
                isCombo = true;
                int[] vertical;
                vertical = checkVertical(row, col).clone();
                if(vertical[height] == 4) { // hàng dọc có 4 ô ăn được thì ra bom hàng dọc
                   if(slideDirection == 12 || slideDirection == 21) {
                       if(colPrev == col){
                           if(B[rowPrev][colPrev] < 10)
                               B[rowPrev][colPrev] = getColorImage(A[row][colPrev]) * 10 + 1;
                       }
                       if(colNext == col){
                           if(B[rowNext][colNext] < 10)
                               B[rowNext][colNext] = getColorImage(A[row][colNext]) * 10 + 1;
                       }
                   }
                    if (slideDirection == 13 || slideDirection == 31) {
                        if(rowPrev == row){
                            if(B[rowPrev][colPrev] < 10)
                                B[rowPrev][colPrev] = getColorImage(A[row][colPrev]) * 10 + 2;
                        }
                        if(rowNext == row){
                            if(B[rowNext][colNext] < 10)
                                B[rowNext][colNext] = getColorImage(A[row][colNext]) * 10 + 2;
                        }
                    }
                }
                if (vertical[height + 1] > 0) {   // nếu bên trên có ô ăn được
                    for (int i = 1; i <= vertical[height + 1]; i++) {     // duyệt qua tất cả các ô bên bên
                        if (checkHorizontal(row - i, col) != null)  // duyệt theo chiều ngang xem có nhánh không
                            markMatrix(row - i, col, 1);
                    }
                }
                if (vertical[height + 2] > 0) {
                    for (int j = 1; j <= vertical[height + 2]; j++) {
                        if(checkHorizontal(row + j, col) != null)
                            markMatrix(row + j, col, 1);
                    }
                }
            }
        }
        return;
    }

    private int[] checkHorizontal(int row, int col){ // kiểm tra theo hàng ngang
        int boom = -1;
        int[] tempt = new int[width + 3];
        // mảng này có phần tử thứ 0 đến width-1 là hàng ngang của ma trận ban đầu
        // phần tử thứ width lưu số ô ăn được theo hàng ngang từ điểm đó
        // phần tử thứ width+1 lưu lại số ô ăn được qua bên trái và width+2 lưu lại số ô ăn được qua bên phải
        for (int m = 0; m < width; m++)
            tempt[m] = A[row][m];
        tempt[width] = 1;
        tempt[width + 1] = 0;
        tempt[width + 2] = 0;
        if(col > 0){    // không phải là ô đầu tiên
            for(int t = col; t > 0; t--){     // duyệt qua trái
                if (getColorImage(A[row][t]) == getColorImage(A[row][t - 1])){ // nếu 2 ô liền nhau giống nhau
                    tempt[width] += 1;
                    tempt[width + 1] += 1;
                    if(boom != tempt[t - 1])
                        boom = tempt[t - 1]; // lưu lại màu cũ
                    tempt[t - 1] = 0;   // đánh dấu ô đã ăn
                }
                else {
                    break;
                }
            }
        }
        if(col < width - 1){    // không phải là ô cuối cùng
            for(int s = col; s < width - 1; s++){     // duyệt qua phải
                if (getColorImage(A[row][s]) == getColorImage(A[row][s + 1])){ // nếu 2 ô liền nhau giống nhau
                    tempt[width] += 1;
                    tempt[width + 2] += 1;
                    if(boom != tempt[s + 1])
                        boom = tempt[s + 1]; // lưu lại màu cũ
                    tempt[s + 1] = 0;   // đánh dấu ô đã ăn
                }
                else {
                    break;
                }
            }
        }
        if(tempt[width] < 3)
            return null;
        else {
            for(int i = col; i < tempt[width + 2]; i++){
                if(A[row][i] > 10)  // có bom
                    boomLine(row, i);
            }
            for(int i = col; i >= col - tempt[width + 1]; i--){
                if(A[row][i] > 10)
                    boomLine(row, i);
            }
            if(tempt[width] == 4) {
                // đổi ô màu thành ô bom
                if (slideDirection == 12 || slideDirection == 21) {
                    boom = boom * 10 + 1;
                }
                if (slideDirection == 13 || slideDirection == 31) {
                    boom = boom * 10 + 2;
                }
                if(combo > 0) {
                    for (int i = 0; i < width; i++) {
                        if (tempt[i] == 0) {
                            if (boom != -1) {   // ko phải chung hàng với ô chọn và chưa có bom
                                tempt[i] = boom;
                                boom = -1;
                            }
                            if (B[row][i] != 0)
                                B[row][i] = tempt[i];   // cập nhật lại mảng B
                        }
                    }
                }
                if(combo == 0) {
                    if (slideDirection == 12 || slideDirection == 21) {
                        if (colPrev == col) {
                            if (B[rowPrev][colPrev] < 10) {
                                B[rowPrev][colPrev] = getColorImage(A[row][colPrev]) * 10 + 1;
                            }
                        }
                        if (colNext == col) {
                            if (B[rowNext][colNext] < 10) {
                                B[rowNext][colNext] = getColorImage(A[row][colNext]) * 10 + 1;
                            }
                        }
                    }
                    if (slideDirection == 13 || slideDirection == 31) {
                        if (rowPrev == row) {
                            if (B[rowPrev][colPrev] < 10) {
                                B[rowPrev][colPrev] = getColorImage(A[row][colPrev]) * 10 + 2;
                            }
                        }
                        if (rowNext == row) {
                            if (B[rowNext][colNext] < 10) {
                                B[rowNext][colNext] = getColorImage(A[row][colNext]) * 10 + 2;
                            }
                        }
                    }
                    for (int i = 0; i < width; i++) {
                        if (tempt[i] == 0 && B[row][i] < 10) {
                            if(B[row][i] != 0)
                                B[row][i] = tempt[i];   // cập nhật lại mảng B
                        }
                    }
                }
            }
            if(tempt[width] != 4) {
                for (int i = 0; i < width; i++)
                    if (tempt[i] == 0) {
                        if(B[row][i] != 0)
                            B[row][i] = tempt[i];   // cập nhật lại mảng B
                    }
            }
            return tempt;
        }
    }

    private int[] checkVertical(int row, int col){ // kiểm tra theo hàng dọc
        int boom = -1;
        int[] tempt = new int[height + 3];
        // mảng này có phần tử thứ 0 đến height-1 là hàng dọc của ma trận ban đầu
        // phần tử thứ height lưu số ô ăn được theo hàng dọc từ điểm đó
        // phần tử thứ height+1 lưu lại số ô ăn được lên phía trên và height+2 lưu lại số ô ăn được xuống phía dưới
        for (int m = 0; m < height; m++)
            tempt[m] = A[m][col];
        tempt[height] = 1;
        tempt[height + 1] = 0;
        tempt[height + 2] = 0;
        if(row > 0){    // không phải là ô ở hàng đầu tiên
            for(int t = row; t > 0; t--){     // duyệt lên trên
                if(getColorImage(A[t][col]) == getColorImage(A[t - 1][col])){ // nếu 2 ô liền nhau giống nhau
                    tempt[height] += 1;     // số ô ăn được tăng lên 1
                    tempt[height + 1] += 1; // số ô ăn được bên trên tăng 1
                    if(boom != tempt[t - 1])
                        boom = tempt[t - 1]; // lưu lại màu cũ
                    tempt[t - 1] = 0;   // đánh dấu ô đã ăn
                }
                else {
                    break;
                }
            }
        }
        if(row < height - 1){   // không phải là ô ở hàng cuối cùng
            for(int s = row; s < height - 1; s++){     // duyệt xuống dưới
                if(getColorImage(A[s][col]) == getColorImage(A[s + 1][col])){ // nếu 2 ô liền nhau giống nhau
                    tempt[height] += 1;
                    tempt[height + 2] += 1;
                    if(boom != tempt[s + 1])
                        boom = tempt[s + 1]; // lưu lại màu cũ
                    tempt[s + 1] = 0;   // đánh dấu ô đã ăn
                }
                else {
                    break;
                }
            }
        }
        if(tempt[height] < 3)
            return null;
        else {
            for(int i = row; i < tempt[height + 2]; i++){
                if(A[i][col] > 10)  // có bom
                    boomLine(i, col);
            }
            for(int i = row; i >= row - tempt[height + 1]; i--){
                if(A[i][col] > 10)
                    boomLine(i, col);
            }
            if(tempt[height] == 4){
                // đổi ô màu thành ô bom
                if (slideDirection == 12 || slideDirection == 21) {
                    boom = boom * 10 + 1;
                }
                if (slideDirection == 13 || slideDirection == 31) {
                    boom = boom * 10 + 2;
                }
                if(combo > 0){
                    for(int i = 0; i < height; i++){
                        if(tempt[i] == 0){
                            if(boom != -1){ // ko phải chung hàng với ô chọn và chưa có bom
                                tempt[i] = boom;
                                boom = -1;
                            }
                            if(B[i][col] != 0)
                                B[i][col] = tempt[i];   // cập nhật lại mảng B
                        }
                    }
                }
                if(combo == 0) {
                    if (slideDirection == 12 || slideDirection == 21) {
                        if (colPrev == col) {
                            if (B[rowPrev][colPrev] < 10) {
                                B[rowPrev][colPrev] = getColorImage(A[row][colPrev]) * 10 + 1;
                            }
                        }
                        if (colNext == col) {
                            if (B[rowNext][colNext] < 10) {
                                B[rowNext][colNext] = getColorImage(A[row][colNext]) * 10 + 1;
                            }
                        }
                    }
                    if (slideDirection == 13 || slideDirection == 31) {
                        if (rowPrev == row) {
                            if (B[rowPrev][colPrev] < 10) {
                                B[rowPrev][colPrev] = getColorImage(A[row][colPrev]) * 10 + 2;
                            }
                        }
                        if (rowNext == row) {
                            if (B[rowNext][colNext] < 10) {
                                B[rowNext][colNext] = getColorImage(A[row][colNext]) * 10 + 2;
                            }
                        }
                    }
                    for (int i = 0; i < height; i++) {
                        if (tempt[i] == 0 && B[i][col] < 10) {
                            if(B[i][col] != 0)
                                B[i][col] = tempt[i];   // cập nhật lại mảng B
                        }
                    }
                }
            }
            if(tempt[height] != 4) {
                for (int i = 0; i < height; i++)
                    if (tempt[i] == 0) {
                        if(B[i][col] != 0)
                            B[i][col] = tempt[i];   // cập nhật lại mảng B
                    }
            }
            return tempt;
        }
    }

    private void reOrganize(final int row1, final int col1, final int row2, final int col2){
        boolean f = true;
        for (int i = 0; i < width; i++) {   // column
            for (int j = height - 1; j >= 0; j--) { //row
                if (A[j][i] == 0) {
                    if (j > 0) {
                        for (int k = j; k > 0; k--) {    // xét từ ô đó lên trên
                            A[k][i] = A[k - 1][i];  // dịch xuống 1 ô
                            effect.dropImage(getImageView(k, i), 200, -wImage);    // hiệu ứng ô rơi xuống
                            getImageView(k, i).setImageResource(getImage(A[k][i]));  // cập nhật lại ô hình
                        }
                    }
                    bonus += 1;
                    A[0][i] = 1 + random.nextInt(numberColor);
                    effect.dropImage(getImageView(0, i), 200, -wImage);
                    getImageView(0, i).setImageResource(getImage(A[0][i]));  // cập nhật lại ô hình
                }
            }
        }
        for (int i = 0; i < width; i++) {   // duyệt lại xem còn ô trống không
            if(f) {
                for (int j = 0; j < height - 1; j++) {
                    if (A[j][i] != 0 && A[j + 1][i] == 0) {   // nếu vẫn còn ô trống
                        f = false;
                        break;
                    }
                }
            }
            else
                break;
        }
        if (f) {    // không có ô trống
            count1 = new CountDownTimer(500, 500) {
                @Override
                public void onTick(long l) {

                }
                @Override
                public void onFinish() {
                    for (int i = 0; i < 9; i++)
                        for (int j = 0; j < 7; j++) {
                            getImageView(i, j).clearAnimation();    // bỏ hiệu ứng tất cả các ô
                        }
                    for(int i = 0; i < height; i++) {    // cập nhật lại B
                        for (int j = 0; j < width; j++) {
                            if(B[i][j] != A[i][j])
                                B[i][j] = A[i][j];
                        }
                    }
                    //Toast.makeText(PlayActivity.this, ""+ bonus + "," + combo, Toast.LENGTH_SHORT).show();
                    sumBonus += bonus;
                    tvScore.setText("" + sumBonus);
                    if(!checkAbleScore()){
                        Toast.makeText(PlayActivity.this, "Create new game!", Toast.LENGTH_SHORT).show();
                        isCheck = false;    // việc kiểm tra các ô hoàn tất
                        combo = 0;
                        bonus = 0;
                        randomImage();
                    }
                    else {
                        checkScore(row1, col1, row2, col2);    // duyệt lại
                    }
                }
            };
            count1.start();
        }
        else {  // vẫn còn ô trống
            count2 = new CountDownTimer(200, 100) {
                @Override
                public void onTick(long l) {

                }
                @Override
                public void onFinish() {
                    reOrganize(row1, col1, row2, col2);   // cho rơi tiếp
                }
            };
            count2.start();
        }
    }

    private void mapping(){
        tvScore = (TextView) findViewById(R.id.tv_score);
        tvBest = (TextView) findViewById(R.id.tv_best);
        btnPause = (Button) findViewById(R.id.button_Pause);

        iv00 = (ImageView) findViewById(R.id.iv_00);
        iv01 = (ImageView) findViewById(R.id.iv_01);
        iv02 = (ImageView) findViewById(R.id.iv_02);
        iv03 = (ImageView) findViewById(R.id.iv_03);
        iv04 = (ImageView) findViewById(R.id.iv_04);
        iv05 = (ImageView) findViewById(R.id.iv_05);
        iv06 = (ImageView) findViewById(R.id.iv_06);

        iv10 = (ImageView) findViewById(R.id.iv_10);
        iv11 = (ImageView) findViewById(R.id.iv_11);
        iv12 = (ImageView) findViewById(R.id.iv_12);
        iv13 = (ImageView) findViewById(R.id.iv_13);
        iv14 = (ImageView) findViewById(R.id.iv_14);
        iv15 = (ImageView) findViewById(R.id.iv_15);
        iv16 = (ImageView) findViewById(R.id.iv_16);

        iv20 = (ImageView) findViewById(R.id.iv_20);
        iv21 = (ImageView) findViewById(R.id.iv_21);
        iv22 = (ImageView) findViewById(R.id.iv_22);
        iv23 = (ImageView) findViewById(R.id.iv_23);
        iv24 = (ImageView) findViewById(R.id.iv_24);
        iv25 = (ImageView) findViewById(R.id.iv_25);
        iv26 = (ImageView) findViewById(R.id.iv_26);

        iv30 = (ImageView) findViewById(R.id.iv_30);
        iv31 = (ImageView) findViewById(R.id.iv_31);
        iv32 = (ImageView) findViewById(R.id.iv_32);
        iv33 = (ImageView) findViewById(R.id.iv_33);
        iv34 = (ImageView) findViewById(R.id.iv_34);
        iv35 = (ImageView) findViewById(R.id.iv_35);
        iv36 = (ImageView) findViewById(R.id.iv_36);

        iv40 = (ImageView) findViewById(R.id.iv_40);
        iv41 = (ImageView) findViewById(R.id.iv_41);
        iv42 = (ImageView) findViewById(R.id.iv_42);
        iv43 = (ImageView) findViewById(R.id.iv_43);
        iv44 = (ImageView) findViewById(R.id.iv_44);
        iv45 = (ImageView) findViewById(R.id.iv_45);
        iv46 = (ImageView) findViewById(R.id.iv_46);

        iv50 = (ImageView) findViewById(R.id.iv_50);
        iv51 = (ImageView) findViewById(R.id.iv_51);
        iv52 = (ImageView) findViewById(R.id.iv_52);
        iv53 = (ImageView) findViewById(R.id.iv_53);
        iv54 = (ImageView) findViewById(R.id.iv_54);
        iv55 = (ImageView) findViewById(R.id.iv_55);
        iv56 = (ImageView) findViewById(R.id.iv_56);

        iv60 = (ImageView) findViewById(R.id.iv_60);
        iv61 = (ImageView) findViewById(R.id.iv_61);
        iv62 = (ImageView) findViewById(R.id.iv_62);
        iv63 = (ImageView) findViewById(R.id.iv_63);
        iv64 = (ImageView) findViewById(R.id.iv_64);
        iv65 = (ImageView) findViewById(R.id.iv_65);
        iv66 = (ImageView) findViewById(R.id.iv_66);

        iv70 = (ImageView) findViewById(R.id.iv_70);
        iv71 = (ImageView) findViewById(R.id.iv_71);
        iv72 = (ImageView) findViewById(R.id.iv_72);
        iv73 = (ImageView) findViewById(R.id.iv_73);
        iv74 = (ImageView) findViewById(R.id.iv_74);
        iv75 = (ImageView) findViewById(R.id.iv_75);
        iv76 = (ImageView) findViewById(R.id.iv_76);

        iv80 = (ImageView) findViewById(R.id.iv_80);
        iv81 = (ImageView) findViewById(R.id.iv_81);
        iv82 = (ImageView) findViewById(R.id.iv_82);
        iv83 = (ImageView) findViewById(R.id.iv_83);
        iv84 = (ImageView) findViewById(R.id.iv_84);
        iv85 = (ImageView) findViewById(R.id.iv_85);
        iv86 = (ImageView) findViewById(R.id.iv_86);

        progressBar = (ProgressBar) findViewById(R.id.progressBar_Time);
    }

    private void loadImageView(){
        iv00.setImageResource(getImage(A[0][0]));
        iv01.setImageResource(getImage(A[0][1]));
        iv02.setImageResource(getImage(A[0][2]));
        iv03.setImageResource(getImage(A[0][3]));
        iv04.setImageResource(getImage(A[0][4]));
        iv05.setImageResource(getImage(A[0][5]));
        iv06.setImageResource(getImage(A[0][6]));

        iv10.setImageResource(getImage(A[1][0]));
        iv11.setImageResource(getImage(A[1][1]));
        iv12.setImageResource(getImage(A[1][2]));
        iv13.setImageResource(getImage(A[1][3]));
        iv14.setImageResource(getImage(A[1][4]));
        iv15.setImageResource(getImage(A[1][5]));
        iv16.setImageResource(getImage(A[1][6]));

        iv20.setImageResource(getImage(A[2][0]));
        iv21.setImageResource(getImage(A[2][1]));
        iv22.setImageResource(getImage(A[2][2]));
        iv23.setImageResource(getImage(A[2][3]));
        iv24.setImageResource(getImage(A[2][4]));
        iv25.setImageResource(getImage(A[2][5]));
        iv26.setImageResource(getImage(A[2][6]));

        iv30.setImageResource(getImage(A[3][0]));
        iv31.setImageResource(getImage(A[3][1]));
        iv32.setImageResource(getImage(A[3][2]));
        iv33.setImageResource(getImage(A[3][3]));
        iv34.setImageResource(getImage(A[3][4]));
        iv35.setImageResource(getImage(A[3][5]));
        iv36.setImageResource(getImage(A[3][6]));

        iv40.setImageResource(getImage(A[4][0]));
        iv41.setImageResource(getImage(A[4][1]));
        iv42.setImageResource(getImage(A[4][2]));
        iv43.setImageResource(getImage(A[4][3]));
        iv44.setImageResource(getImage(A[4][4]));
        iv45.setImageResource(getImage(A[4][5]));
        iv46.setImageResource(getImage(A[4][6]));

        iv50.setImageResource(getImage(A[5][0]));
        iv51.setImageResource(getImage(A[5][1]));
        iv52.setImageResource(getImage(A[5][2]));
        iv53.setImageResource(getImage(A[5][3]));
        iv54.setImageResource(getImage(A[5][4]));
        iv55.setImageResource(getImage(A[5][5]));
        iv56.setImageResource(getImage(A[5][6]));

        iv60.setImageResource(getImage(A[6][0]));
        iv61.setImageResource(getImage(A[6][1]));
        iv62.setImageResource(getImage(A[6][2]));
        iv63.setImageResource(getImage(A[6][3]));
        iv64.setImageResource(getImage(A[6][4]));
        iv65.setImageResource(getImage(A[6][5]));
        iv66.setImageResource(getImage(A[6][6]));

        iv70.setImageResource(getImage(A[7][0]));
        iv71.setImageResource(getImage(A[7][1]));
        iv72.setImageResource(getImage(A[7][2]));
        iv73.setImageResource(getImage(A[7][3]));
        iv74.setImageResource(getImage(A[7][4]));
        iv75.setImageResource(getImage(A[7][5]));
        iv76.setImageResource(getImage(A[7][6]));

        iv80.setImageResource(getImage(A[8][0]));
        iv81.setImageResource(getImage(A[8][1]));
        iv82.setImageResource(getImage(A[8][2]));
        iv83.setImageResource(getImage(A[8][3]));
        iv84.setImageResource(getImage(A[8][4]));
        iv85.setImageResource(getImage(A[8][5]));
        iv86.setImageResource(getImage(A[8][6]));
    }

    private int getImage(int image){
        switch (image){
            case 0:
                return R.drawable.empty;
            case 1:
                return R.drawable.blue;
            case 11:
                return R.drawable.blue1;
            case 12:
                return R.drawable.blue2;
            case 2:
                return R.drawable.green;
            case 21:
                return R.drawable.green1;
            case 22:
                return R.drawable.green2;
            case 3:
                return R.drawable.red;
            case 31:
                return R.drawable.red1;
            case 32:
                return R.drawable.red2;
            case 4:
                return R.drawable.yellow;
            case 41:
                return R.drawable.yellow1;
            case 42:
                return R.drawable.yellow2;
            case 5:
                return R.drawable.brown;
            case 51:
                return R.drawable.brown1;
            case 52:
                return R.drawable.brown2;
            case 6:
                return R.drawable.purple;
            case 61:
                return R.drawable.purple1;
            case 62:
                return R.drawable.purple2;
            case 7:
                return R.drawable.orange;
            case 71:
                return R.drawable.orange1;
            case 72:
                return R.drawable.orange2;
            case  8:
                return R.drawable.pink;
            case  81:
                return R.drawable.pink1;
            case  82:
                return R.drawable.pink2;
        }
        return R.drawable.empty;
    }

    private ImageView getImageView(int row, int col){
        if (row == 0) {
            if (col == 0)
                return iv00;
            if (col == 1)
                return iv01;
            if (col == 2)
                return iv02;
            if (col == 3)
                return iv03;
            if (col == 4)
                return iv04;
            if (col == 5)
                return iv05;
            if (col == 6)
                return iv06;
        }
        if (row == 1) {
            if (col == 0)
                return iv10;
            if (col == 1)
                return iv11;
            if (col == 2)
                return iv12;
            if (col == 3)
                return iv13;
            if (col == 4)
                return iv14;
            if (col == 5)
                return iv15;
            if (col == 6)
                return iv16;
        }
        if (row == 2) {
            if (col == 0)
                return iv20;
            if (col == 1)
                return iv21;
            if (col == 2)
                return iv22;
            if (col == 3)
                return iv23;
            if (col == 4)
                return iv24;
            if (col == 5)
                return iv25;
            if (col == 6)
                return iv26;
        }
        if (row == 3) {
            if (col == 0)
                return iv30;
            if (col == 1)
                return iv31;
            if (col == 2)
                return iv32;
            if (col == 3)
                return iv33;
            if (col == 4)
                return iv34;
            if (col == 5)
                return iv35;
            if (col == 6)
                return iv36;
        }
        if (row == 4) {
            if (col == 0)
                return iv40;
            if (col == 1)
                return iv41;
            if (col == 2)
                return iv42;
            if (col == 3)
                return iv43;
            if (col == 4)
                return iv44;
            if (col == 5)
                return iv45;
            if (col == 6)
                return iv46;
        }
        if (row == 5) {
            if (col == 0)
                return iv50;
            if (col == 1)
                return iv51;
            if (col == 2)
                return iv52;
            if (col == 3)
                return iv53;
            if (col == 4)
                return iv54;
            if (col == 5)
                return iv55;
            if (col == 6)
                return iv56;
        }
        if (row == 6) {
            if (col == 0)
                return iv60;
            if (col == 1)
                return iv61;
            if (col == 2)
                return iv62;
            if (col == 3)
                return iv63;
            if (col == 4)
                return iv64;
            if (col == 5)
                return iv65;
            if (col == 6)
                return iv66;
        }
        if (row == 7) {
            if (col == 0)
                return iv70;
            if (col == 1)
                return iv71;
            if (col == 2)
                return iv72;
            if (col == 3)
                return iv73;
            if (col == 4)
                return iv74;
            if (col == 5)
                return iv75;
            if (col == 6)
                return iv76;
        }
        if (row == 8) {
            if (col == 0)
                return iv80;
            if (col == 1)
                return iv81;
            if (col == 2)
                return iv82;
            if (col == 3)
                return iv83;
            if (col == 4)
                return iv84;
            if (col == 5)
                return iv85;
            if (col == 6)
                return iv86;
        }
        return null;
    }

    private void randomImage(){
        Random random = new Random();
        int i, j;
        for(i = 0; i < height; i++){
            for(j = 0; j < width; j++){
                if((i % 2 == 0 && j % 2 == 0) || (i % 2 == 1 && j % 2 == 1)){
                    A[i][j] = 1 + random.nextInt(numberColor);    // ngẫu nhiên từ 1 đến 7
                }
            }
        }
        for(i = 0; i < height; i++){
            for(j = 0; j < width; j++){
                if((i % 2 == 1 && j % 2 == 0) || (i % 2 == 0 && j % 2 == 1)){
                    if(i % 2 == 0) {    // hàng chẵn 0, 2, 4, 6
                        if (i == 0) {
                            do {
                                A[i][j] = 1 + random.nextInt(numberColor);
                            } while (A[i][j] == A[i + 1][j] || A[i][j] == A[i][j + 1]);
                        } else {  // i > 0
                            do {
                                A[i][j] = 1 + random.nextInt(numberColor);
                            } while (A[i][j] == A[i - 1][j] || A[i][j] == A[i][j - 1]);
                        }
                    }
                    else {  // hàng lẻ
                        if (j == 0) {
                            do {
                                A[i][j] = 1 + random.nextInt(numberColor);
                            } while (A[i][j] == A[i + 1][j] || A[i][j] == A[i][j + 1]);
                        } else {  // j > 0
                            do {
                                A[i][j] = 1 + random.nextInt(numberColor);
                            } while (A[i][j] == A[i - 1][j] || A[i][j] == A[i][j - 1]);
                        }
                    }
                }
            }
        }
        // kiểm tra xem có ô ăn được không nếu không có thì random lại
        if(!checkAbleScore())
            randomImage();
        // cập nhật lại hình ảnh tất cả các ô
        loadImageView();
    }

    private int getColorImage(int x){
        if(x > 10){
            return x / 10;
        }
        return x;
    }

    private void boomLine(int x, int y){
        if(A[x][y] > 10) {  // la bom
            B[x][y] = 0;
            if (A[x][y] % 10 == 1) { // bom hang ngang
                for (int i = 0; i < width; i++) {
                    if (A[x][i] < 10) {
                        if(B[x][i] != 0)
                            B[x][i] = 0;
                    } else {
                        if(B[x][i] != 0) {
                            boomLine(x, i);
                        }
                    }
                }
            }
            if (A[x][y] % 10 == 2) { // bom hang doc
                for (int j = 0; j < height; j++) {
                    if (A[j][y] < 10) {
                        if(B[j][y] != 0)
                            B[j][y] = 0;
                    } else {
                        if(B[j][y] != 0) {
                            //B[j][y] = 0;
                            boomLine(j, y);
                        }
                    }
                }
            }
        }
    }
    
    private boolean checkAbleScore(){
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                // xet theo hang ngang
                if(i < height - 1 && j < width - 2){
                    if(A[i][j] == A[i][j + 2] && A[i][j] == A[i + 1][j + 1])
                        return true;
                }
                if(i > 0 && j < width- 2){
                    if(     (A[i][j] == A[i][j + 2] && A[i][j] == A[i - 1][j + 1]) ||
                            (A[i][j] == A[i][j + 1] && A[i][j] == A[i - 1][j + 2]) ||
                            (A[i][j] == A[i - 1][j + 1] && A[i][j] == A[i - 1][j + 2]))
                        return true;
                }
                if(j < width - 3){
                    if(     (A[i][j] == A[i][j + 2] && A[i][j] == A[i][j + 3]) || 
                            (A[i][j] == A[i][j + 1] && A[i][j] == A[i][j + 3]))
                        return true;
                }
                if(i < height - 1 && j < width - 2){
                    if(     (A[i][j] == A[i][j + 1] && A[i][j] == A[i + 1][j + 2]) ||
                            (A[i][j] == A[i + 1][j + 1] && A[i][j] == A[i + 1][j + 2]))
                        return true;
                }
                // xet theo hang doc
                if(i < height - 2 && j < width - 1){
                    if(     (A[i][j] == A[i + 2][j] && A[i][j] == A[i + 1][j + 1]) || 
                            (A[i][j] == A[i + 1][j] && A[i][j] == A[i + 2][j + 1]) ||
                            (A[i][j] == A[i + 1][j + 1] && A[i][j] == A[i + 2][j + 1]))
                        return true;
                }
                if(i < height - 3){
                    if(     (A[i][j] == A[i + 2][j] && A[i][j] == A[i + 3][j])||
                            (A[i][j] == A[i + 1][j] && A[i][j] == A[i + 3][j]))
                        return true;
                }
                if(i > 0 && i < height - 1 && j < width - 1){
                    if(     (A[i][j] == A[i - 1][j + 1] && A[i][j] == A[i + 1][j + 1]) || 
                            (A[i][j] == A[i - 1][j + 1] && A[i][j] == A[i + 1][j]))
                        return true;
                }
                if(i > 1 && j < width - 1){
                    if(A[i][j] == A[i - 1][j + 1] && A[i][j] == A[i - 2][j + 1])
                        return true;
                }
            }                
        }
        return false;
    }

    private void autoChangeSize(){
        //Toast.makeText(this, "" + getScreenHeight() +","+ getScreenWidth(), Toast.LENGTH_LONG).show();
        int w = getScreenWidth() - 40;
        wImage = w / 7 - 12;
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                GridLayout.LayoutParams params = (GridLayout.LayoutParams) getImageView(i,j).getLayoutParams();
                params.width = wImage;
                params.height = wImage;
                params.leftMargin = 3;
                params.rightMargin = 3;
                params.topMargin = 3;
                params.bottomMargin = 3;
                getImageView(i,j).setLayoutParams(params);
            }
        }
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        countDownTime.cancel();
        showDialogPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundtrack.stop();
    }
}
