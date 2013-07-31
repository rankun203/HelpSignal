package com.example.chenjiantest;

/**
 * Created by chenjian on 13-7-30.
 */
public class PersonInfo {
    private String name;
    private int falg;           //1-survivor, 2-searcher

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFalg() {
        return falg;
    }

    public void setFalg(int falg) {
        this.falg = falg;
    }
}
