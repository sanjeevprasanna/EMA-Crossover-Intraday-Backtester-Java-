EMA Crossover Backtester (Java)

This project simulates a backtesting strategy on stock market data using a 5-minute EMA crossover strategy. Written in Java, it allows you to test the performance of a trading logic based on technical indicators (EMA 35 & EMA 70), and outputs the results into CSV files for analysis.

⸻

Strategy Logic

Entry Conditions:
	•	Long (Buy): When EMA 35 crosses above EMA 70 (Bullish crossover), after 10:00 AM
	•	Short (Sell): When EMA 35 crosses below EMA 70 (Bearish crossover), after 10:00 AM

Exit Conditions:
	•	Stop Loss: 1% adverse price movement
	•	Take Profit: 2.5% favorable price movement
	•	Time-based Exit: Force exit at 3:20 PM if neither SL nor TP hit

⸻

Output Files

1. output/orderinfo2.csv

Detailed log of each trade:

```TradeId, Stock, Type, EntryDate, EntryTime, EntryPrice,EntryEMA35, EntryEMA70, ExitDate, ExitTime, ExitPrice,ExitEMA35, ExitEMA70,DidCrossoverHappen, StopLossPrice,TakeProfitPrice, Profit%, ExitReason ```

 Additional Notes:
	•	ExitReason: One of SL (Stop Loss), TP (Take Profit), or TIME (Time-based exit)
	•	EMA prices and crossover condition are recorded at both entry and exit

⸻

Setup Instructions

Prerequisites:
	•	Java 17+
	•	Maven

Build & Run:

```mvn clean compile exec:java```

⸻

 Sample Trade Record

TradeId	Stock	Type	EntryTime	EntryPrice	ExitPrice	Profit%	ExitReason	DidCrossoverHappen
102	HDFCBANK	L	10:25	1520.00	1560.00	2.63	TP	Bullish

⸻
