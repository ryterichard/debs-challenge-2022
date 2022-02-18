
# Table of Contents

1.  [Problem Statement](#org72f3ac2)
2.  [Proposed Solution](#orge62dfd0)
3.  [Expectations](#orgbc99a15)
4.  [Experimental Plan](#org20bd619)
5.  [Success Indicators](#org3d82ee5)
    1.  [Deliverables are:](#org4014417)
        1.  [Query 1: Quantitative Indicators Per Symbol](#org84e49a1)
        2.  [Query 2: Breakout Patterns via Crossovers](#org8a4ec2d)
        3.  [Reply to subscriptions](#org292b3d5)
6.  [Task Assignments](#orgd4a1c52)
    1.  [Initial Proof of Concept](#orge3eb846)
    2.  [Optimizing](#org841a073)
    3.  [Task Distribution](#org0d2a2ec)
        1.  [Xavier](#org512dd31)
        2.  [Quang](#org04774dc)
        3.  [Quan](#org77f3310)
        4.  [Shekhar](#org4eab2e3)
        5.  [Ryte](#orgcdd9786)
7.  [Q&A](#org32b90a1)
    1.  [Which flink operators are you going to use to implement windows?](#orgf801c3e)
    2.  [What state do you need to keep and for how long?](#org7e44ab4)
    3.  [Are you going to compute the window aggregate incrementally or on trigger?](#org6e1cc2a)
    4.  [Can you share any partial results across windows?](#org353b26f)
    5.  [How are you going to parallelize the computation? Can you even parallelize the computation?](#org25c6201)


<a id="org72f3ac2"></a>

# Problem Statement

“The 2022 DEBS Grand Challenge focuses on real-time complex event processing of real-world high-volume tick data provided by Infront Financial Technology (<https://www.infrontfinance.com/>). In the data set, about 5000+ financial instruments are being traded on three major exchanges over the course of a week. The goal of the challenge is to efficiently compute specific trend indicators and detect patterns resembling those used by real-life traders to decide on buying or selling on the financial markets”
(<https://2022.debs.org/call-for-grand-challenge-solutions/>)


<a id="orge62dfd0"></a>

# Proposed Solution

Given the problem of analyzing market data in real-time, a streaming system is the obvious choice. We will be using Flink for the data analysis and Kafka for the PubSub part of the problem. For data visualization, we will look into solutions such as Python or Javascript due to the vast array of pre-built libraries for visualization. 


<a id="orgbc99a15"></a>

# Expectations

A real-time data analysis and visualization system that could be used in the real world. The primary metric, EMA, could easily be expanded or replaced with different ones, making our system modular, extensible, and horizontally scalable. 


<a id="org20bd619"></a>

# Experimental Plan

The DEBS challenge provides a test dataset along with a benchmarking platform to test our performance. The DEBS Challenge platform also provides VMs for deployment of our code and testing there.

Per DEBS, “The data set contains 289 million tick data events covering 5504 equities and indices that are traded on three European exchanges: Paris (FR), Amsterdam (NL), and Frankfurt/Xetra (ETR). All tick data events for security types equities (e.g., Royal Dutch Shell, Siemens Healthineers) and indices are contained as they have been captured on these days. However, some event notifications appear to come with no payload. This is due to the fact that this challenge requires only a small subset of attributes to be evaluated; other attributes have been eliminated from the data set to minimize its overall size while keeping the amount of events to process unchanged.”

This data set was captured by Infront Financial Technology for a complete week from 11/8/2021-11/14/2021.


<a id="org3d82ee5"></a>

# Success Indicators

The project’s success should first be measured by correctness. Later, we want to challenge ourselves by making the throughput higher and latency lower. Latency would be measured by how long our system takes to process a single window of data. Throughput would be the size of the window the system can process.


<a id="org4014417"></a>

## Deliverables are:


<a id="org84e49a1"></a>

### Query 1: Quantitative Indicators Per Symbol

Implementation of an operator that calculates the EMA for a given symbol


<a id="org8a4ec2d"></a>

### Query 2: Breakout Patterns via Crossovers

Implementation of an operator that uses Query 1 to track 2 EMAs for a given symbol and detect patterns (bullish/bearish) in crossovers of EMA values


<a id="org292b3d5"></a>

### Reply to subscriptions

Everytime a bullish or bearish breakout pattern is detected for a given symbol, reply to the subscriber and notify


<a id="orgd4a1c52"></a>

# Task Assignments

Here is a tentative checklist of assignments for this project.
We will be meeting on at least a weekly basis physically.

There are two main parts to this project:


<a id="orge3eb846"></a>

## Initial Proof of Concept

-   Getting VMs working
-   Example code running
-   Ask for sample correct output
-   EMA calculation through Flink
-   Breakout pattern detection
-   Subscription handling, filter the query 2 breakout events to be only for the symbols that are subscribed to
-   Testing and Benchmarking


<a id="org841a073"></a>

## Optimizing

-   Work on optimizing query 1
-   Work on optimizing query 2
-   Sample data visualization drafts
-   EMA visualization
-   Breakout visualization
-   Documentation
-   Testing and Benchmarking
-   Short Paper draft


<a id="org0d2a2ec"></a>

## Task Distribution

Here is an initial task distribution


<a id="org512dd31"></a>

### Xavier

-   EMA calculation through Flink
-   Ask organizers for sample input and output


<a id="org04774dc"></a>

### Quang

Breakout pattern detection


<a id="org77f3310"></a>

### Quan

Breakout pattern detection


<a id="org4eab2e3"></a>

### Shekhar

EMA calculation through Flink


<a id="orgcdd9786"></a>

### Ryte

Example code running


<a id="org32b90a1"></a>

# Q&A


<a id="orgf801c3e"></a>

## Which flink operators are you going to use to implement windows?

-   `keyBy()` &rarr; we will partition the data by ticker, which is unique
-   `window()` &rarr; takes in a keyed stream and allows tumbling windows, our exact applicaiton. We also need to operate on the entire window once it is completed and retain until trigger.
-   `windowApply` &rarr; takes in a window and returns a data stream, we want to use this to find the EMA

    var windowedStream = dataStream
    		.keyBy(value -> value.tickerID)
    		.window(TumblingEventTimeWindows.of(Time.seconds(300)));  // 300 = 5mins
    
    
    windowedStream.apply(new WindowFunction<Tuple2<String,Integer>, Integer, Tuple, Window>() {
    				public void apply (Tuple tuple,
    													 Window window,
    													 Iterable<Tuple2<String, Integer>> values,
    													 Collector<Integer> out) throws Exception {
    						int sum = 0;
    						for (value t: values) {
    								sum += t.f1;
    						}
    						out.collect (new Integer(sum));
    				}
    		});


<a id="org7e44ab4"></a>

## What state do you need to keep and for how long?

-   EMAs for every ticker/window, we keep this until the next window's EMA is calculated.
-   We need to keep two EMAs for each ticker.
-   We must do this EMA calculation for all stocks.


<a id="org6e1cc2a"></a>

## Are you going to compute the window aggregate incrementally or on trigger?

Due to EMA calculation, you must wait until window is complete. 


<a id="org353b26f"></a>

## Can you share any partial results across windows?

No, EMA calculations are unique to a window, but you use the last window's EMA value to calculate the current.


<a id="org25c6201"></a>

## How are you going to parallelize the computation? Can you even parallelize the computation?

Because the ticker's EMA values are independent of each other, these calculations can be parallelized. Flink allows any keys to be parallelized. Because we are keying by ticker, we are parallelizing by them as well.

Since we have 5,000 unique tickers, we *could* have as much parallelization as 5,000 at once. However, it may be better to "categorize" or group tickers in a different way so the parallel processes are running batches of tickers. This is because each thread causes lots of overhead and only calculating one ticker. 

