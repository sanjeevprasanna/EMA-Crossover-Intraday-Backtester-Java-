package net.javaguides.backtest;

import com.opencsv.CSVReader;
import net.javaguides.indicators.EMA;
import net.javaguides.model.Bar;
import net.javaguides.model.Trade;
import net.javaguides.model.Trade.Side;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Backtester {

    private static final double STOP_LOSS_PCT   = 1.0;
    private static final double TAKE_PROFIT_PCT = 2.5;
    private static final LocalTime EXIT_TIME    = LocalTime.of(15, 20);
    private static final LocalTime ENTRY_START  = LocalTime.of(10, 0);
    private static final int EMA_FAST_PERIOD    = 35;
    private static final int EMA_SLOW_PERIOD    = 70;

    public static void Backtest(String csvPath, String stockName) throws Exception {
        List<Bar> bars = new ArrayList<>();
        try (CSVReader r = new CSVReader(new FileReader(csvPath))) {
            r.readNext();
            String[] row;
            while ((row = r.readNext()) != null)
                bars.add(new Bar(row));
        }

        EMA emaFast = new EMA(EMA_FAST_PERIOD), emaSlow = new EMA(EMA_SLOW_PERIOD);
        Double prevFast = null, prevSlow = null;

        List<Trade> allTrades  = new ArrayList<>();
        List<Trade> openTrades = new ArrayList<>();
        Set<LocalDate> days    = new HashSet<>();

        for (Bar b : bars) {
            days.add(b.getTime().toLocalDate());
            double price = b.getClose();
            Double f = emaFast.next(price), s = emaSlow.next(price);

            List<Trade> finished = new ArrayList<>();
            for (Trade t : openTrades) {
                t.updateCurrentProfit(price);

                double sl = t.getEntryPrice() * (t.getSide() == Side.LONG ? 0.99 : 1.01);
                double tp = t.getEntryPrice() * (t.getSide() == Side.LONG ? 1.025 : 0.975);

                boolean slHit   = (t.getSide()==Side.LONG  && b.getLow()  <= sl) ||
                                  (t.getSide()==Side.SHORT && b.getHigh() >= sl);
                boolean tpHit   = (t.getSide()==Side.LONG  && b.getHigh() >= tp) ||
                                  (t.getSide()==Side.SHORT && b.getLow()  <= tp);
                boolean timeOut = b.getTime().toLocalTime().compareTo(EXIT_TIME) >= 0;

                if (slHit || tpHit || timeOut) {
                    double exitPrice = b.getClose();
                    String reason = "TIME";

                    if (slHit && tpHit) {
                        double slProfit = (t.getSide() == Side.LONG) ? (sl / t.getEntryPrice() - 1) * 100 : (t.getEntryPrice() / sl - 1) * 100;
                        double tpProfit = (t.getSide() == Side.LONG) ? (tp / t.getEntryPrice() - 1) * 100 : (t.getEntryPrice() / tp - 1) * 100;
                        if (slProfit < tpProfit) {
                            exitPrice = sl;
                            reason = "SL";
                        } else {
                            exitPrice = tp;
                            reason = "TP";
                        }
                    } else if (slHit) {
                        exitPrice = sl;
                        reason = "SL";
                    } else if (tpHit) {
                        exitPrice = tp;
                        reason = "TP";
                    }

                    t.setExitEmaFast(f);
                    t.setExitEmaSlow(s);
                    t.setExitEmaCrossover(f > s ? "YES" : "NO");
                    t.setExitReason(reason);
                    t.markExit(exitPrice, b.getTime());
                    finished.add(t);
                }
            }
            openTrades.removeAll(finished);

            if (f != null && s != null && prevFast != null && prevSlow != null) {
                LocalTime barTime = b.getTime().toLocalTime();
                if (barTime.compareTo(ENTRY_START) >= 0 && barTime.compareTo(EXIT_TIME) < 0) {
                    if (prevFast <= prevSlow && f > s) {
                        Trade t = new Trade(Side.LONG, price, b.getTime());
                        t.setEntryEmaFast(f);
                        t.setEntryEmaSlow(s);
                        double sl = price * 0.99;
                        double tp = price * 1.025;
                        t.setStopLossPrice(sl);
                        t.setTakeProfitPrice(tp);
                        allTrades.add(t);
                        openTrades.add(t);
                    } else if (prevFast >= prevSlow && f < s) {
                        Trade t = new Trade(Side.SHORT, price, b.getTime());
                        t.setEntryEmaFast(f);
                        t.setEntryEmaSlow(s);
                        double sl = price * 1.01;
                        double tp = price * 0.975;
                        t.setStopLossPrice(sl);
                        t.setTakeProfitPrice(tp);
                        allTrades.add(t);
                        openTrades.add(t);
                    }
                }
            }

            prevFast = f;
            prevSlow = s;
        }

        boolean fileExists = Files.exists(Paths.get("output/orderinfo2.csv"));
        try (PrintWriter w = new PrintWriter(new FileWriter("output/orderinfo2.csv", true))) {
            if (!fileExists) {
                w.println("TradeId,Stock,Type,EntryDate,EntryTime,EntryPrice,EntryEMA35,EntryEMA70,ExitDate,ExitTime,ExitPrice,ExitEMA35,ExitEMA70,StopLossPrice,TakeProfitPrice,Profit%,ExitReason");
            }

            int tradeId = 0;
            for (Trade t : allTrades) {
                if (!t.isExited()) continue;
                String[] entryDateTime = t.getEntryTime().toString().split("T");
                String[] exitDateTime = t.getExitTime().toString().split("T");
                String tradeType = t.getSide() == Side.LONG ? "L" : "S";
                w.printf("%d,%s,%s,%s,%s,%.2f,%.2f,%.2f,%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%s,%n",
                    tradeId++, stockName, tradeType,
                    entryDateTime[0], entryDateTime[1], t.getEntryPrice(),
                    t.getEntryEmaFast(), t.getEntryEmaSlow(),
                    exitDateTime[0], exitDateTime[1], t.getExitPrice(),
                    t.getExitEmaFast(), t.getExitEmaSlow(),
                    t.getStopLossPrice(), t.getTakeProfitPrice(),
                    t.getPnlPercent(), t.getExitReason()   
                );
            }
        }
        return;
    }
}