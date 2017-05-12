package com.cp.bean;

import java.util.List;

/**
 * Created by john on 2017/3/10.
 */
public class TheaterArchiveCustom  extends  TheaterArchive {

    private List<Theater> theaterList;

    public List<Theater> getTheaterList() {
        return theaterList;
    }

    public void setTheaterList(List<Theater> theaterList) {
        this.theaterList = theaterList;
    }

    @Override
    public String toString() {
        return "TheaterArchiveCustom{" +
                "theaterList=" + theaterList +
                '}';
    }
}
