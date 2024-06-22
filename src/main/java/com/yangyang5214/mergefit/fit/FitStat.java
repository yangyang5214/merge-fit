package com.yangyang5214.mergefit.fit;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileOutputStream;

public class FitStat {

    private FitSession session;
    private Gson gson;


    public FitStat(String fitFile) {
        this.session = new FitDecode().Encode(fitFile);
        this.gson = new GsonBuilder().create();
    }

    public void Record() {
        String json = gson.toJson(session.getRecords());
        try (FileOutputStream fileOutputStream = new FileOutputStream("record.json")) {
            byte[] bytes = json.getBytes();
            fileOutputStream.write(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void Session() {
        String json = this.gson.toJson(session.getSession());
        System.out.println(json);

        try (FileOutputStream fileOutputStream = new FileOutputStream("session.json")) {
            byte[] bytes = json.getBytes();
            fileOutputStream.write(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
