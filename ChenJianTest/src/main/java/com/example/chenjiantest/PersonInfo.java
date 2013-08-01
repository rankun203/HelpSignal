package com.example.chenjiantest;

/**
 * Created by chenjian on 13-7-30.
 */
public class PersonInfo {
    private String name;
    private int flag;           //1-survivor, 2-searcher

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
