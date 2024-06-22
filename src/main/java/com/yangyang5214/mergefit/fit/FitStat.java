package com.yangyang5214.mergefit.fit;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileOutputStream;

public class FitStat {

    public void Parse(String fitFile) {
        FitSession session = new FitDecode().Encode(fitFile);

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(session.getSession());
        System.out.println(json);

        try (FileOutputStream fileOutputStream = new FileOutputStream("session.json")) {
            byte[] bytes = json.getBytes();
            fileOutputStream.write(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
