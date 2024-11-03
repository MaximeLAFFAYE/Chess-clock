package com.example.chessclock;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.os.Looper;
public class MainActivity extends AppCompatActivity {

    //-----------------[ Attributes ]-----------------
    private ChessClockTimer clock1;
    private ChessClockTimer clock2;
    private boolean isClock1Running = false;
    private boolean isPaused = true;
    private boolean isFirstClick = true;

    private int haltedColor = Color.parseColor("#FF727778");

    private int tickingColor = Color.parseColor("#FF0E7FB3");

    private int outOfTimeColor = Color.parseColor("#FFC30505");

    private TextView clock1Display;
    private TextView clock2Display;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateTimeTask;

    //------------------------------------------------



    //-----------------[ Methods ]-----------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init the clocks
        clock1 = new ChessClockTimer(300000, 0);
        clock2 = new ChessClockTimer(300000, 0);

        clock1Display = findViewById(R.id.clock1);
        clock2Display = findViewById(R.id.clock2);

        // top bar btns
        Button resetButton = findViewById(R.id.reset);
        Button settingsButton = findViewById(R.id.settings);
        Button pauseButton = findViewById(R.id.pause);
        Button resumeButton = findViewById(R.id.resume);

        resetButton.setOnClickListener(v -> resetClocks());
        settingsButton.setOnClickListener(v -> showSettingsDialog());
        pauseButton.setOnClickListener(v -> pauseClocks());
        resumeButton.setOnClickListener(v -> resumeClocks());

        // clock btns
        clock1Display.setOnClickListener(v -> handleClockClick(true));
        clock2Display.setOnClickListener(v -> handleClockClick(false));

        updateClockDisplays();

        updateTimeTask = new Runnable() {
            @Override
            public void run() {
                updateClockDisplays();
                handler.postDelayed(this, 100); // updating every 100 0.1 second
            }
        };

    }


    /***
     * Starting the handler to update the view.
     */
    private void startUpdatingTime() {
        handler.post(updateTimeTask);
    }


    /***
     * Stopping the handler to update the view.
     */
    private void stopUpdatingTime() {
        handler.removeCallbacks(updateTimeTask);
    }


    /**
     * Handles the click event on the clock displays.
     * Starts or switches the active clock based on the state.
     *
     * @param isClock1 true if the first clock was clicked, false if the second clock was clicked.
     */
    private void handleClockClick(boolean isClock1) {
        if (isFirstClick) {
            if (isClock1) {
                clock2.start();
                isClock1Running = false;
            } else {
                clock1.start();
                isClock1Running = true;
            }
            isPaused = false;
            isFirstClick = false;
        } else {
            if (!isPaused) {
                switchClock(isClock1);
            }
        }
        startUpdatingTime();
        updateClockDisplays();
    }

    /**
     * Switches the active clock between the two clocks.
     * Pauses the currently running clock and starts the other clock.
     *
     * @param startClock1 true to start the first clock, false to start the second clock.
     */
    private void switchClock(boolean startClock1) {
        if (startClock1) {
            clock2.start();
            clock1.pause(true);
        } else {
            clock1.start();
            clock2.pause(true);
        }
        startUpdatingTime();
        isClock1Running = !startClock1;
    }


    /**
     * Resets both clocks to their initial time and increments.
     * Stops any running clock, resets the state, and updates the display.
     */
    private void resetClocks() {
        clock1.reset();
        clock2.reset();
        isClock1Running = false;
        isPaused = true;
        isFirstClick = true;

        stopUpdatingTime();
        updateClockDisplays();
    }


    /**
     * Pauses both clocks, setting the game in a paused state.
     * Stops any ongoing time updates and allows the clocks to be resumed later.
     */
    private void pauseClocks() {
        isPaused = true;
        clock1.pause(false);
        clock2.pause(false);
        isFirstClick = true;

        stopUpdatingTime();
        updateClockDisplays();
    }


    /**
     * Resumes the currently active clock.
     * Starts the clock that was previously running, updates the display, and resumes time updates.
     */
    private void resumeClocks() {
        isPaused = false;
        if (isClock1Running) {
            clock1.start();
        } else {
            clock2.start();
        }

        isFirstClick = false;
        startUpdatingTime();
        updateClockDisplays();
    }


    /**
     * Displays a dialog for selecting a preset time and increment for the clocks.
     * Updates the clocks with the selected time and resets them to apply the new settings.
     */
    private void showSettingsDialog() {
        String[] options = {"5 minutes", "10 minutes", "1 minute", "5 | 5 (increasement)", "1 | 1 (increasement)"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Time");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    setTimeForClocks(300000, 300000, 0); // 5 minutes
                    break;
                case 1:
                    setTimeForClocks(600000, 600000, 0); // 10 minutes
                    break;
                case 2:
                    setTimeForClocks(60000, 60000, 0); // 1 minute
                    break;
                case 3:
                    setTimeForClocks(300000, 300000, 5000); // 5 minutes w 5 secondes increasement
                    break;
                case 4:
                    setTimeForClocks(60000, 60000, 1000); // 1 minute w 1 second increasement
                    break;
            }
            resetClocks();
        });
        builder.show();
    }


    /**
     * Sets the specified time and increment for both clocks.
     *
     * @param time1 the initial time for the first clock in milliseconds.
     * @param time2 the initial time for the second clock in milliseconds.
     * @param increment the increment in milliseconds to be added after each move.
     */
    private void setTimeForClocks(long time1, long time2, long increment) {
        clock1.setTime(time1);
        clock1.setIncrement(increment);
        clock2.setTime(time2);
        clock2.setIncrement(increment);
    }


    /**
     * Updates the display of both clocks, reflecting the current time, background color, and enabled state.
     * Adjusts the clock displays based on the active and inactive clocks.
     */
    private void updateClockDisplays() {

        if(!isFirstClick){
            clock1Display.setBackgroundColor(isClock1Running ? tickingColor : haltedColor);
            clock2Display.setBackgroundColor(isClock1Running ? haltedColor : tickingColor);
        }else{
            clock1Display.setBackgroundColor(haltedColor);
            clock2Display.setBackgroundColor(haltedColor);
        }

        clock2Display.setEnabled(!isClock1Running);
        clock1Display.setEnabled(isClock1Running);

        clock1Display.setText(clock1.getFormattedTime());
        clock2Display.setText(clock2.getFormattedTime());
    }
    //------------------------------------------------
}
