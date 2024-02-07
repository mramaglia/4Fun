package com.example.fansfun.activities;

import com.example.fansfun.entities.Evento;

import java.util.Comparator;
import java.util.Date;

public class DateComparator implements Comparator<Evento> {

    @Override
    public int compare(Evento event1, Evento event2) {
        Date date1 = event1.getData();
        Date date2 = event2.getData();

        return date1.compareTo(date2);

    }
}


