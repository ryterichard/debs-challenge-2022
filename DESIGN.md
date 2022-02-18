
# Table of Contents

1.  [Problem Statement](#orged6cd05)
2.  [Proposed Solution](#orgc0f09b5)
3.  [Expectations](#orgdf20a57)
4.  [Experimental Plan](#orgb4317e2)
5.  [Success Indicators](#org27c42fc)
    1.  [Deliverables are:](#org1fda4b6)
        1.  [Query 1: Quantitative Indicators Per Symbol](#org52e999b)
        2.  [Query 2: Breakout Patterns via Crossovers](#orgbbb9ef6)
        3.  [Reply to subscriptions](#org263b124)
6.  [Task Assignments](#org09959f9)
    1.  [Initial Proof of Concept](#org649cdd2)
    2.  [Optimizing](#org94f579c)
    3.  [Task Distribution](#org633a672)
        1.  [Xavier](#orgd59d242)
        2.  [Quang](#orge9c088c)
        3.  [Quan](#orgb421d47)
        4.  [Shekhar](#orgc065aba)
        5.  [Ryte](#orgf63e4ab)
7.  [Q&A](#orge6ae7e9)
    1.  [Which flink operators are you going to use to implement windows?](#org96f14af)
    2.  [What state do you need to keep and for how long?](#org3cfcc4c)
    3.  [Are you going to compute the window aggregate incrementally or on trigger?](#org31dbd6d)
    4.  [Can you share any partial results across windows?](#org103aa8d)
    5.  [How are you going to parallelize the computation? Can you even parallelize the computation?](#orga945979)


<a id="orged6cd05"></a>

# Problem Statement

“The 2022 DEBS Grand Challenge focuses on real-time complex event processing of real-world high-volume tick data provided by Infront Financial Technology (<https://www.infrontfinance.com/>). In the data set, about 5000+ financial instruments are being traded on three major exchanges over the course of a week. The goal of the challenge is to efficiently compute specific trend indicators and detect patterns resembling those used by real-life traders to decide on buying or selling on the financial markets”
(<https://2022.debs.org/call-for-grand-challenge-solutions/>)


<a id="orgc0f09b5"></a>

# Proposed Solution

Given the problem of analyzing market data in real-time, a streaming system is the obvious choice. We will be using Flink for the data analysis and Kafka for the PubSub part of the problem. For data visualization, we will look into solutions such as Python or Javascript due to the vast array of pre-built libraries for visualization. 


<a id="orgdf20a57"></a>

# Expectations

A real-time data analysis and visualization system that could be used in the real world. The primary metric, EMA, could easily be expanded or replaced with different ones, making our system modular, extensible, and horizontally scalable. 


<a id="orgb4317e2"></a>

# Experimental Plan

The DEBS challenge provides a test dataset along with a benchmarking platform to test our performance. The DEBS Challenge platform also provides VMs for deployment of our code and testing there.

Per DEBS, “The data set contains 289 million tick data events covering 5504 equities and indices that are traded on three European exchanges: Paris (FR), Amsterdam (NL), and Frankfurt/Xetra (ETR). All tick data events for security types equities (e.g., Royal Dutch Shell, Siemens Healthineers) and indices are contained as they have been captured on these days. However, some event notifications appear to come with no payload. This is due to the fact that this challenge requires only a small subset of attributes to be evaluated; other attributes have been eliminated from the data set to minimize its overall size while keeping the amount of events to process unchanged.”

This data set was captured by Infront Financial Technology for a complete week from 11/8/2021-11/14/2021.


<a id="org27c42fc"></a>

# Success Indicators

The project’s success should first be measured by correctness. Later, we want to challenge ourselves by making the throughput higher and latency lower. Latency would be measured by how long our system takes to process a single window of data. Throughput would be the size of the window the system can process.


<a id="org1fda4b6"></a>

## Deliverables are:


<a id="org52e999b"></a>

### Query 1: Quantitative Indicators Per Symbol

Implementation of an operator that calculates the EMA for a given symbol


<a id="orgbbb9ef6"></a>

### Query 2: Breakout Patterns via Crossovers

Implementation of an operator that uses Query 1 to track 2 EMAs for a given symbol and detect patterns (bullish/bearish) in crossovers of EMA values


<a id="org263b124"></a>

### Reply to subscriptions

Everytime a bullish or bearish breakout pattern is detected for a given symbol, reply to the subscriber and notify


<a id="org09959f9"></a>

# Task Assignments

Here is a tentative checklist of assignments for this project.
We will be meeting on at least a weekly basis physically.

There are two main parts to this project:


<a id="org649cdd2"></a>

## Initial Proof of Concept

-   Getting VMs working
-   Example code running
-   Ask for sample correct output
-   EMA calculation through Flink
-   Breakout pattern detection
-   Subscription handling, filter the query 2 breakout events to be only for the symbols that are subscribed to
-   Testing and Benchmarking


<a id="org94f579c"></a>

## Optimizing

-   Work on optimizing query 1
-   Work on optimizing query 2
-   Sample data visualization drafts
-   EMA visualization
-   Breakout visualization
-   Documentation
-   Testing and Benchmarking
-   Short Paper draft


<a id="org633a672"></a>

## Task Distribution

Here is an initial task distribution


<a id="orgd59d242"></a>

### Xavier

EMA calculation through Flink
Ask organizers for sample input and output


<a id="orge9c088c"></a>

### Quang

Breakout pattern detection


<a id="orgb421d47"></a>

### Quan

Breakout pattern detection


<a id="orgc065aba"></a>

### Shekhar

EMA calculation through Flink


<a id="orgf63e4ab"></a>

### Ryte

Example code running


<a id="orge6ae7e9"></a>

# Q&A


<a id="org96f14af"></a>

## Which flink operators are you going to use to implement windows?

`keyBy()` &rarr; we will partition the data by ticker, which is unique
`window()` &rarr; takes in a keyed stream and allows tumbling windows, our exact applicaiton. We also need to operate on the entire window once it is completed and retain until trigger.
`windowApply` &rarr; takes in a window and returns a data stream, we want to use this to find the EMA

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


<a id="org3cfcc4c"></a>

## What state do you need to keep and for how long?

-   EMAs for every ticker/window, we keep this until the next window's EMA is calculated.
-   We need to keep two EMAs for each ticker.
-   We must do this EMA calculation for all stocks.


<a id="org31dbd6d"></a>

## Are you going to compute the window aggregate incrementally or on trigger?

Due to EMA calculation, you must wait until window is complete. 


<a id="org103aa8d"></a>

## Can you share any partial results across windows?

No, EMA calculations are unique to a window, but you use the last window's EMA value to calculate the current.


<a id="orga945979"></a>

## How are you going to parallelize the computation? Can you even parallelize the computation?

Because the ticker's EMA values are independent of each other, these calculations can be parallelized. Flink allows any keys to be parallelized. Because we are keying by ticker, we are parallelizing by them as well.

