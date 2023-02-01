package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final Button[][] buttons = new Button[3][3];

    private boolean player1Turn = true;

    private int roundCount;

    private int player1Points;
    private int player2Points;

    private TextView text_view_p1;
    private TextView text_view_p2, muteTv;
    private TextView playerTurn;
    private Dialog dialog;
    SwitchCompat mute;
    MediaPlayer player;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_TicTacToe);
        setContentView(R.layout.activity_main);

        dialog = new Dialog(this);

        text_view_p1 = findViewById(R.id.text_view_p1);
        text_view_p2 = findViewById(R.id.text_view_p2);
        playerTurn = findViewById(R.id.playerTurn);
        mute = dialog.findViewById(R.id.mute_mute);
        muteTv = dialog.findViewById(R.id.mute_tv);


        for (int i = 0;i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_"+i+j;
                int resID = getResources().getIdentifier(buttonID,"id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);
            }
        }
        Button buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(v -> moreDialog());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onClick(View v) {
        if (!((Button) v).getText().toString().equals("")) {
            return;
        }

        if (player1Turn) {
            ((Button) v).setText("X");
            playerTurn.setText(getResources().getString(R.string.player_O_turn));
        } else {
            ((Button) v).setText("O");
            playerTurn.setText(getResources().getString(R.string.player_X_turn));
        }

        roundCount++;

        if (checkForWin()) {
            if (player1Turn) {
                player1Wins();
                player_x_WinsSound();
            } else  {
                player2Wins();
                player_o_WinsSound();
            }
        } else if (roundCount == 9) {
            draw();
            drawSound();
        } else {
            player1Turn = !player1Turn;
        }

        turnSound();

    }

    private  boolean checkForWin() {
        String[][] field = new String[3][3];

        for (int i =0 ; i < 3 ; i++) {
            for (int j = 0 ; j < 3 ; j++) {
                field[i][j] = buttons[i][j].getText().toString();

            }
        }

        for (int i =0 ; i < 3 ; i++) {
            if (field[i][0].equals(field[i][1])
                    && field[i][0].equals(field[i][2])
                    && !field[i][0].equals("")) {
                return true;
            }
        }

        for (int i =0 ; i < 3 ; i++) {
            if (field[0][i].equals(field[1][i])
                    && field[0][i].equals(field[2][i])
                    && !field[0][i].equals("")) {
                return true;
            }
        }

        if (field[0][0].equals(field[1][1])
                && field[0][0].equals(field[2][2])
                && !field[0][0].equals("")) {
            return true;
        }

        return field[0][2].equals(field[1][1])
                && field[0][2].equals(field[2][0])
                && !field[0][2].equals("");
    }

    private void player1Wins() {
        player1Points++;
        showToast(getResources().getString(R.string.player_X_wins));
        playerTurn.setText(getResources().getString(R.string.player_X_turn));
        updatePointsText();
        resetBoard();
    }

    private void player2Wins() {
        player2Points++;
        showToast(getResources().getString(R.string.player_O_wins));
        playerTurn.setText(getResources().getString(R.string.player_X_turn));
        updatePointsText();
        resetBoard();
    }

    private void draw() {
        showToast(getResources().getString(R.string.draw));
        resetBoard();
        playerTurn.setText(getResources().getString(R.string.player_X_turn));
    }

    @SuppressLint("SetTextI18n")
    private void updatePointsText() {
        text_view_p1.setText("Player X \n"+player1Points);
        text_view_p2.setText("Player O \n"+player2Points);
    }

    private void resetBoard() {
        for (int i =0 ; i < 3; i++){
            for (int j = 0 ; j < 3 ; j++){
                buttons[i][j].setText("");
            }
        }

        roundCount = 0;
        player1Turn = true;
        playerTurn.setText(getResources().getString(R.string.player_X_turn));
    }

    private void resetGame() {
        player1Points = 0;
        player2Points = 0;
        updatePointsText();
        playerTurn.setText(getResources().getString(R.string.player_X_turn));
        text_view_p1.setText("Player X \n"+0);
        text_view_p2.setText("Player O \n"+0);
        resetBoard();
    }

    @Override
    public void onBackPressed() {
        exitDialog();
    }

    private void exitDialog() {
        dialog.setContentView(R.layout.exit_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imageClose = dialog.findViewById(R.id.image_close);
        Button btnQuit = dialog.findViewById(R.id.btn_quit);
        Button btnNo = dialog.findViewById(R.id.btn_no);

        imageClose.setOnClickListener(v -> dialog.dismiss());

        btnNo.setOnClickListener(v -> dialog.dismiss());

        btnQuit.setOnClickListener(v -> finish());
        dialog.show();
    }

    private void moreDialog() {
        dialog.setContentView(R.layout.more);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageButton refresh = dialog.findViewById(R.id.button_refresh);
        ImageButton resetBoard = dialog.findViewById(R.id.resetBoardBtn);
        SwitchCompat mute = dialog.findViewById(R.id.mute_mute);
        muteTv = dialog.findViewById(R.id.mute_tv);

        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        SharedPreferences muteTvText = getSharedPreferences("muteText", MODE_PRIVATE);

        mute.setChecked(sharedPreferences.getBoolean("value", true));
        muteTv.setText(muteTvText.getString("string", "Mute"));

        mute.setOnClickListener(v -> {
            if (mute.isChecked()) {
                SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                SharedPreferences.Editor textEditor = getSharedPreferences("muteText", MODE_PRIVATE).edit();
                editor.putBoolean("value", true);
                textEditor.putString("String","Mute" );
                editor.apply();
                textEditor.apply();
                mute.setChecked(true);
                mute.setThumbDrawable(getDrawable(R.drawable.ic_unmute));
                muteTv.setText(getResources().getString(R.string.mute));
                AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
            } else {
                SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                SharedPreferences.Editor textEditor = getSharedPreferences("muteText", MODE_PRIVATE).edit();
                textEditor.putString("String","Mute" );
                editor.putBoolean("value", false);
                editor.apply();
                textEditor.apply();
                mute.setChecked(false);
                mute.setThumbDrawable(getDrawable(R.drawable.ic_mute));
                muteTv.setText(getResources().getString(R.string.un_mute));
                AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
            }
        });
        refresh.setOnClickListener(v -> resetGameDialog());
        dialog.show();

        resetBoard.setOnClickListener(v -> {
            resetBoard();
            dialog.dismiss();
        });
    }

    private void resetGameDialog() {
        dialog.setContentView(R.layout.reset_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imageView = dialog.findViewById(R.id.imageView2);
        Button reset = dialog.findViewById(R.id.btn_reset);
        Button cancel = dialog.findViewById(R.id.btn_cancel);

        imageView.setOnClickListener(v -> dialog.dismiss());

        cancel.setOnClickListener(v -> dialog.dismiss());

        reset.setOnClickListener(v -> {
            resetGame();
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("roundCount", roundCount);
        outState.putInt("player1Points", player1Points);
        outState.putInt("player2Points", player2Points);
        outState.putBoolean("player1Turn", player1Turn);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        roundCount = savedInstanceState.getInt("roundCount");
        player1Points = savedInstanceState.getInt("player1points");
        player2Points = savedInstanceState.getInt("player2Points");
        player1Turn = savedInstanceState.getBoolean("player1Turn");
    }

    private void showToast( String winner) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, findViewById(R.id.toast_root));

        TextView toastText = layout.findViewById(R.id.Toast_Text);
        ImageView toastImage = layout.findViewById(R.id.toast_image);

        toastText.setText(winner);
        toastImage.setImageResource(R.drawable.ic_toast_icon);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

    }

    private void turnSound() {
            if (playerTurn.getText().toString().equals(getResources().getString(R.string.player_X_turn))) {
                if (player == null) {
                    player = MediaPlayer.create(this, R.raw.player_x_turn);
                    player.setOnCompletionListener(mp -> stopPlayerSound());
                }
                player.start();
            } else {
                if (player == null) {
                    player = MediaPlayer.create(this, R.raw.player_o_turn);
                    player.setOnCompletionListener(mp -> stopPlayerSound());
                }
                player.start();
            }
    }

    private void player_x_WinsSound() {
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.player_x_wins);
            player.setOnCompletionListener(mp -> stopPlayerSound());
        }
        player.start();
    }

    private void player_o_WinsSound() {
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.player_o_wins);
            player.setOnCompletionListener(mp -> stopPlayerSound());
        }
        player.start();
    }

    private void drawSound() {
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.draw);
            player.setOnCompletionListener(mp -> stopPlayerSound());
        }
        player.start();
    }
    private void stopPlayerSound() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

}
