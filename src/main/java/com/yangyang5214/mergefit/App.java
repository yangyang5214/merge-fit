package com.yangyang5214.mergefit;


import com.garmin.fit.SessionMesg;
import com.yangyang5214.mergefit.fit.FitDecode;
import com.yangyang5214.mergefit.fit.FitMerge;
import com.yangyang5214.mergefit.fit.FitSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -jar merge-fit.jar <result.fit> <input-1.fit> <input-2.fit>");
            return;
        }

        String[] inputs = Arrays.copyOfRange(args, 1, args.length);


        List<FitSession> sessions = new ArrayList<>();

        for (String input : inputs) {
            FitDecode decode = new FitDecode();
            sessions.add(decode.Encode(input));
        }

        new FitMerge(args[0], sessions).Merge();

        System.out.println("Gen merge fit file success: " + args[0]);
    }
}