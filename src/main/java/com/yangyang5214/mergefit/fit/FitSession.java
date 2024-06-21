package com.yangyang5214.mergefit.fit;

import com.garmin.fit.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class FitSession {

    private FileIdMesg fileIdMesg;

    private List<RecordMesg> records;

    private SessionMesg session;

    private List<EventMesg> events;

    private List<LapMesg> laps;

    private ActivityMesg activity;

    public FitSession() {
        records = new ArrayList<>();
        events = new ArrayList<>();
        laps = new ArrayList<>();
    }
}

