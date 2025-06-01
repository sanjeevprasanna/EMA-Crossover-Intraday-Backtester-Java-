package net.javaguides.model;

import java.time.LocalDateTime;

public class Trade {
    public enum Side { LONG, SHORT }

    private final Side side;
    private final double entryPrice;
    private final LocalDateTime entryTime;

    private double exitPrice;
    private LocalDateTime exitTime;
    private boolean exited = false;
    private double pnlPercent = 0.0;

    private double entryEmaFast;
    private double entryEmaSlow;
    private double stopLossPrice;
    private double takeProfitPrice;

    private double exitEmaFast;
    private double exitEmaSlow;
    private String exitEmaCrossover;
    private String exitReason;

    public Trade(Side side, double entryPrice, LocalDateTime entryTime) {
        this.side = side;
        this.entryPrice = entryPrice;
        this.entryTime = entryTime;
    }

    public void markExit(double exitPrice, LocalDateTime exitTime) {
        this.exitPrice = exitPrice;
        this.exitTime = exitTime;
        this.exited = true;
        this.pnlPercent = (side == Side.LONG)
            ? ((exitPrice / entryPrice) - 1) * 100
            : ((entryPrice / exitPrice) - 1) * 100;
    }

    public void updateCurrentProfit(double currentPrice) {
        this.pnlPercent = (side == Side.LONG)
            ? ((currentPrice / entryPrice) - 1) * 100
            : ((entryPrice / currentPrice) - 1) * 100;
    }

    public boolean isExited() { return exited; }
    public Side getSide() { return side; }
    public double getEntryPrice() { return entryPrice; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public double getExitPrice() { return exitPrice; }
    public LocalDateTime getExitTime() { return exitTime; }
    public double getPnlPercent() { return pnlPercent; }

    public void setEntryEmaFast(double val) { this.entryEmaFast = val; }
    public void setEntryEmaSlow(double val) { this.entryEmaSlow = val; }
    public void setStopLossPrice(double val) { this.stopLossPrice = val; }
    public void setTakeProfitPrice(double val) { this.takeProfitPrice = val; }
    public void setExitEmaFast(double val) { this.exitEmaFast = val; }
    public void setExitEmaSlow(double val) { this.exitEmaSlow = val; }
    public void setExitEmaCrossover(String val) { this.exitEmaCrossover = val; }
    public void setExitReason(String val) { this.exitReason = val; }

    public double getEntryEmaFast() { return entryEmaFast; }
    public double getEntryEmaSlow() { return entryEmaSlow; }
    public double getStopLossPrice() { return stopLossPrice; }
    public double getTakeProfitPrice() { return takeProfitPrice; }
    public double getExitEmaFast() { return exitEmaFast; }
    public double getExitEmaSlow() { return exitEmaSlow; }
    public String getExitEmaCrossover() { return exitEmaCrossover; }
    public String getExitReason() { return exitReason; }
}