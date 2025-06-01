package net.javaguides;

import net.javaguides.backtest.Backtester;


import java.io.File;

public class App {
    public static void main(String[] args) {
        File dataDir = new File("data");
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            System.err.println("ERROR: data/ not found");
            System.exit(1);
        }

        File outDir = new File("output");
        if (!outDir.exists()) outDir.mkdirs();

        File[] files = dataDir.listFiles((d, n) -> n.toLowerCase().endsWith(".csv"));
        if (files == null || files.length == 0) {
            System.err.println("No CSVs in data/");
            System.exit(1);
        }

        for (File f : files) {
            String name = f.getName().replaceFirst("(?i)\\.csv$", "");
            System.out.println("Processing " + name);
            try {
                Backtester.Backtest(f.getPath(), name);
            } catch (Exception e) {
                System.err.println("Error on " + name + ": " + e.getMessage());
            }
        }
    }
}
