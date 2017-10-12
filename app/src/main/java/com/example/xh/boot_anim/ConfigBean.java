package com.example.xh.boot_anim;

/**
 * Created by xh on 2017/10/9.
 */

public class ConfigBean {
    private int reX;
    private int reY;
    private int frames;
    private char play_type;
    private int recycle;

    public ConfigBean(int reX, int reY, int frames, char play_type, int recycle) {
        this.reX = reX;
        this.reY = reY;
        this.frames = frames;
        this.play_type = play_type;
        this.recycle = recycle;
    }

    public int getReX() {
        return reX;
    }

    public int getReY() {
        return reY;
    }

    public int getFrames() {
        return frames;
    }

    public char getPlay_type() {
        return play_type;
    }

    public int getRecycle() {
        return recycle;
    }
}
