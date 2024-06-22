package com.yangyang5214.mergefit;


import com.yangyang5214.mergefit.fit.FitDecode;
import com.yangyang5214.mergefit.fit.FitMerge;
import com.yangyang5214.mergefit.fit.FitSession;
import com.yangyang5214.mergefit.fit.FitStat;
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) {
        Options options = new Options();

        Option merge = new Option("m", "merge", true, "Merge fits. Usage: merge <result.fit> input-1.fit input-2.fit");
        options.addOption(merge);

        Option stat = new Option("s", "stat", true, "Gen stat info.Usage: stat <result.fit>");
        options.addOption(stat);

        CommandLine cmd;
        CommandLineParser parser = new DefaultParser();
        HelpFormatter helper = new HelpFormatter();

        if (args.length == 0) {
            helper.printHelp("Usage:", options);
            return;
        }

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("m")) {
                merge(Arrays.copyOfRange(args, 1, args.length));
            } else if (cmd.hasOption("s")) {
                stat(Arrays.copyOfRange(args, 1, args.length));
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            helper.printHelp("Usage:", options);
        }
    }

    public static void stat(String[] args) {
        String fieFile = args[0];
        new FitStat().Parse(fieFile);
    }

    public static void merge(String[] args) {
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