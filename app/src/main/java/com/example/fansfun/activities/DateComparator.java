package com.example.fansfun.activities;

import com.example.fansfun.entities.ListViewEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DateComparator implements Comparator<ListViewEvent> {

    @Override
    public int compare(ListViewEvent event1, ListViewEvent event2) {
        Date date1 = event1.getData();
        Date date2 = event2.getData();

        return date1.compareTo(date2);

    }
}


