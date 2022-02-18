
# Table of Contents

1.  [Problem Statement](#org3c11547)
2.  [Proposed Solution](#org533e9be)
3.  [Expectations](#org5b7ca2e)
4.  [Experimental Plan](#org4a8415e)
5.  [Success Indicators](#org58ee57d)
    1.  [Deliverables are:](#orga74f86e)
        1.  [Query 1: Quantitative Indicators Per Symbol](#org24f77e3)
        2.  [Query 2: Breakout Patterns via Crossovers](#org08c6896)
        3.  [Reply to subscriptions](#orgb22a201)
6.  [Task Assignments](#orgb119809)
    1.  [Initial Proof of Concept](#orgf374aaa)
    2.  [Optimizing](#org00fdac6)
    3.  [Task Distribution](#org7cacdef)
        1.  [Xavier](#orged295ed)
        2.  [Quang](#org0fabe78)
        3.  [Quan](#orgcae9e31)
        4.  [Shekhar](#org6d92083)
        5.  [Ryte](#orgc59c66e)
7.  [Q&A](#orgdbdafcc)
    1.  [Which flink operators are you going to use to implement windows?](#org7fc22e6)
    2.  [What state do you need to keep and for how long?](#orge8d167c)
    3.  [Are you going to compute the window aggregate incrementally or on trigger?](#orga80045d)
    4.  [Can you share any partial results across windows?](#org1075e82)
    5.  [How are you going to parallelize the computation? Can you even parallelize the computation?](#orgec4dc42)


<a id="org3c11547"></a>

# Problem Statement

“The 2022 DEBS Grand Challenge focuses on real-time complex event processing of real-world high-volume tick data provided by Infront Financial Technology (<https://www.infrontfinance.com/>). In the data set, about 5000+ financial instruments are being traded on three major exchanges over the course of a week. The goal of the challenge is to efficiently compute specific trend indicators and detect patterns resembling those used by real-life traders to decide on buying or selling on the financial markets”
(<https://2022.debs.org/call-for-grand-challenge-solutions/>)


<a id="org533e9be"></a>

# Proposed Solution

Given the problem of analyzing market data in real-time, a streaming system is the obvious choice. We will be using Flink for the data analysis and Kafka for the PubSub part of the problem. For data visualization, we will look into solutions such as Python or Javascript due to the vast array of pre-built libraries for visualization. 


<a id="org5b7ca2e"></a>

# Expectations

A real-time data analysis and visualization system that could be used in the real world. The primary metric, EMA, could easily be expanded or replaced with different ones, making our system modular, extensible, and horizontally scalable. 


<a id="org4a8415e"></a>

# Experimental Plan

The DEBS challenge provides a test dataset along with a benchmarking platform to test our performance. The DEBS Challenge platform also provides VMs for deployment of our code and testing there.

Per DEBS, “The data set contains 289 million tick data events covering 5504 equities and indices that are traded on three European exchanges: Paris (FR), Amsterdam (NL), and Frankfurt/Xetra (ETR). All tick data events for security types equities (e.g., Royal Dutch Shell, Siemens Healthineers) and indices are contained as they have been captured on these days. However, some event notifications appear to come with no payload. This is due to the fact that this challenge requires only a small subset of attributes to be evaluated; other attributes have been eliminated from the data set to minimize its overall size while keeping the amount of events to process unchanged.”

This data set was captured by Infront Financial Technology for a complete week from 11/8/2021-11/14/2021.


<a id="org58ee57d"></a>

# Success Indicators

The project’s success should first be measured by correctness. Later, we want to challenge ourselves by making the throughput higher and latency lower. Latency would be measured by how long our system takes to process a single window of data. Throughput would be the size of the window the system can process.


<a id="orga74f86e"></a>

## Deliverables are:


<a id="org24f77e3"></a>

### Query 1: Quantitative Indicators Per Symbol

Implementation of an operator that calculates the EMA for a given symbol


<a id="org08c6896"></a>

### Query 2: Breakout Patterns via Crossovers

Implementation of an operator that uses Query 1 to track 2 EMAs for a given symbol and detect patterns (bullish/bearish) in crossovers of EMA values


<a id="orgb22a201"></a>

### Reply to subscriptions

Everytime a bullish or bearish breakout pattern is detected for a given symbol, reply to the subscriber and notify


<a id="orgb119809"></a>

# Task Assignments

Here is a tentative checklist of assignments for this project.
We will be meeting on at least a weekly basis physically.

There are two main parts to this project:


<a id="orgf374aaa"></a>

## Initial Proof of Concept

-   Getting VMs working
-   Example code running
-   Ask for sample correct output
-   EMA calculation through Flink
-   Breakout pattern detection
-   Subscription handling, filter the query 2 breakout events to be only for the symbols that are subscribed to
-   Testing and Benchmarking


<a id="org00fdac6"></a>

## Optimizing

-   Work on optimizing query 1
-   Work on optimizing query 2
-   Sample data visualization drafts
-   EMA visualization
-   Breakout visualization
-   Documentation
-   Testing and Benchmarking
-   Short Paper draft


<a id="org7cacdef"></a>

## Task Distribution

Here is an initial task distribution


<a id="orged295ed"></a>

### Xavier

-   EMA calculation through Flink
-   Ask organizers for sample input and output


<a id="org0fabe78"></a>

### Quang

Breakout pattern detection


<a id="orgcae9e31"></a>

### Quan

Breakout pattern detection


<a id="org6d92083"></a>

### Shekhar

EMA calculation through Flink


<a id="orgc59c66e"></a>

### Ryte

Example code running


<a id="orgdbdafcc"></a>

# Q&A


<a id="org7fc22e6"></a>

## Which flink operators are you going to use to implement windows?

-   `keyBy()` &rarr; we will partition the data by symbol, which is unique
-   `window()` &rarr; takes in a keyed stream and allows tumbling windows, our exact applicaiton. We also need to operate on the entire window once it is completed and retain until trigger.

    var windowedStream = dataStream
    		.keyBy(value -> value.symbolID)
    		.window(TumblingEventTimeWindows.of(Time.seconds(300)));  // 300 = 5mins
    
    
    // some function that incrementally updates the latest price seen for the window
    // if at a trigger/window end, calculate the EMA and throw away last price seen and update previous EMA
    // ??? unknown function ??? We are not sure which function we will be using here, yet.


<a id="orge8d167c"></a>

## What state do you need to keep and for how long?

-   EMAs for every symbol/window, we keep this until the next window's EMA is calculated.
-   We need to keep two EMAs for each symbol and the latest price seen in a window for a symbol until that EMA is calculated.
-   We must do this EMA calculation for all stocks.


<a id="orga80045d"></a>

## Are you going to compute the window aggregate incrementally or on trigger?

There are two things to update:

-   We want to update the last price observed for a symbol on addition to a window incrementally iff it is later than the latest event-time seen for the window (*not* system-time, when you receive the event).
-   For the actual EMA calculation, you must wait until window is complete (triggered), the last price is actually seen for the window.


<a id="org1075e82"></a>

## Can you share any partial results across windows?

No, EMA calculations are unique to a window, but you use the last window's EMA value to calculate the current.


<a id="orgec4dc42"></a>

## How are you going to parallelize the computation? Can you even parallelize the computation?

Because the symbol's EMA values are independent of each other, these calculations can be parallelized. Flink allows any keys to be parallelized. Because we are keying by symbol, we are parallelizing by them as well.

Since we have 5,000 unique symbols, we *could* have as much parallelization as 5,000 at once. However, it may be better to "categorize" or group symbols in a different way so the parallel processes are running batches of symbols. This is because each thread causes lots of overhead and only calculating one symbol. 

