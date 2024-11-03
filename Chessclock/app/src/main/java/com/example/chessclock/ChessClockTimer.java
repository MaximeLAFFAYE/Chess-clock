package com.example.chessclock;

import android.os.CountDownTimer;

public class ChessClockTimer {

    //-----------------[ Attributes ]-----------------
    private long timeLeftInMillis;
    private long defaultTimeInMillis;
    private long incrementMillis;
    private CountDownTimer timer;
    private boolean ranOutOfTime = false;
    //------------------------------------------------


    //-----------------[ Methods ]-----------------
    /**
     * Initializes a new ChessClockTimer with the specified time and increment.
     *
     * @param timeInMillis the initial time in milliseconds for the timer.
     * @param incrementMillis the increment in milliseconds to add after each pause.
     */
    public ChessClockTimer(long timeInMillis, long incrementMillis) {
        this.timeLeftInMillis = timeInMillis;
        this.incrementMillis = incrementMillis;
    }


    /**
     * Sets a new initial time for the timer.
     *
     * @param timeInMillis the new initial time in milliseconds.
     */
    public void setTime(long timeInMillis) {
        this.timeLeftInMillis = timeInMillis;
        this.defaultTimeInMillis = timeInMillis;
    }


    /**
     * Sets the increment time that will be added to the timer after each pause.
     *
     * @param incrementMillis the increment time in milliseconds.
     */
    public void setIncrement(long incrementMillis) {
        this.incrementMillis = incrementMillis;
    }


    /**
     * Starts or restarts the countdown timer. Cancels any existing timer and starts a new one.
     */
    public void start() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
            }

            public void onFinish() {
                ranOutOfTime = true;
                timeLeftInMillis = 0;
            }
        }.start();
    }


    /**
     * Pauses the timer and, optionally, adds the increment to the remaining time.
     *
     * @param increment true if the increment should be added, false otherwise.
     */
    public void pause(boolean increment) {
        if(increment){
            // adding increment time
            timeLeftInMillis += incrementMillis;
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    /**
     * Resets the timer to its default initial time and stops it.
     */
    public void reset() {
        pause(false);
        ranOutOfTime = false;
        timeLeftInMillis = defaultTimeInMillis;
    }


    /**
     * Formats the remaining time as a string in the format MM:SS.
     *
     * @return a formatted string representing the remaining time.
     */
    public String getFormattedTime() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }


    /**
     * Checks if the timer has run out of time.
     *
     * @return true if the timer has reached zero, false otherwise.
     */
    public boolean isRanOutOfTime() {
        return ranOutOfTime;
    }
}
