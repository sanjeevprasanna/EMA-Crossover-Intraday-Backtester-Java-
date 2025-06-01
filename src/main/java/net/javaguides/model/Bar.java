package net.javaguides.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Bar {
    private LocalDateTime time;
    private double open, high, low, close, volume;

    // Format: "13-07-17 09:15" -> dd-MM-yy HH:mm
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");

    // Constructor - takes raw CSV fields and parses them
    public Bar(String[] f) {
        // expected format: [timestamp, open, high, low, close, volume]
        this.time   = LocalDateTime.parse(f[0], FMT);
        this.open   = Double.parseDouble(f[1]);
        this.high   = Double.parseDouble(f[2]);
        this.low    = Double.parseDouble(f[3]);
        this.close  = Double.parseDouble(f[4]);
        this.volume = Double.parseDouble(f[5]);
    }

    // Getter methods
    public LocalDateTime getTime() { return time; }
    public double getOpen()        { return open; }
    public double getHigh()        { return high; }
    public double getLow()         { return low; }
    public double getClose()       { return close; }
}