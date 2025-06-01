package net.javaguides.indicators;

public class EMA {
    private final int period;
    private final double multiplier;
    private Double lastEma = null;

    public EMA(int period) {
        this.period = period;
        this.multiplier = 2.0 / (period + 1);  // basic smoothing factor
    }

    // updates EMA with next price point
    public Double next(double price) {
        if (lastEma == null) {
            lastEma = price;  // init on first price
        } else {
            // applying EMA formula
            lastEma = lastEma + (price - lastEma) * multiplier;
        }
        return lastEma;
    }
}