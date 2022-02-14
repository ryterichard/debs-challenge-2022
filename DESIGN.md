
# Table of Contents

1.  [Problem Statement](#orged9ed91)
2.  [Proposed Solution](#org2a66394)
3.  [Expectations](#orgd5bde20)
4.  [Experimental Plan](#orgb1a2996)
5.  [Success Indicators](#org9e20eb0)
    1.  [Deliverables are:](#org6be5090)
        1.  [Query 1: Quantitative Indicators Per Symbol](#orgf105e74)
        2.  [Query 2: Breakout Patterns via Crossovers](#orgc1684e9)
        3.  [Reply to subscriptions](#org4aee02e)
6.  [Task Assignments](#orgb155a2b)
    1.  [Initial Proof of Concept](#org6974bfa)
    2.  [Optimizing](#org651a517)
    3.  [Task Distribution](#orgbcd54cb)


<a id="orged9ed91"></a>

# Problem Statement

“The 2022 DEBS Grand Challenge focuses on real-time complex event processing of real-world high-volume tick data provided by Infront Financial Technology (<https://www.infrontfinance.com/>). In the data set, about 5000+ financial instruments are being traded on three major exchanges over the course of a week. The goal of the challenge is to efficiently compute specific trend indicators and detect patterns resembling those used by real-life traders to decide on buying or selling on the financial markets”
(<https://2022.debs.org/call-for-grand-challenge-solutions/>)


<a id="org2a66394"></a>

# Proposed Solution

Given the problem of analyzing market data in real-time, a streaming system is the obvious choice. We will be using Flink for the data analysis and Kafka for the PubSub part of the problem. For data visualization, we will look into solutions such as Python or Javascript due to the vast array of pre-built libraries for visualization. 


<a id="orgd5bde20"></a>

# Expectations

A real-time data analysis and visualization system that could be used in the real world. The primary metric, EMA, could easily be expanded or replaced with different ones, making our system modular, extensible, and horizontally scalable. 


<a id="orgb1a2996"></a>

# Experimental Plan

The DEBs challenge provides a test dataset along with a benchmarking platform to test our performance. The DEBS Challenge platform also provides VMs for deployment of our code and testing there.

Per DEBS, “The data set contains 289 million tick data events covering 5504 equities and indices that are traded on three European exchanges: Paris (FR), Amsterdam (NL), and Frankfurt/Xetra (ETR). All tick data events for security types equities (e.g., Royal Dutch Shell, Siemens Healthineers) and indices are contained as they have been captured on these days. However, some event notifications appear to come with no payload. This is due to the fact that this challenge requires only a small subset of attributes to be evaluated; other attributes have been eliminated from the data set to minimize its overall size while keeping the amount of events to process unchanged.”

This data set was captured by Infront Financial Technology for a complete week from 11/8/2021-11/14/2021.


<a id="org9e20eb0"></a>

# Success Indicators

The project’s success should first be measured by correctness. Later, we want to challenge ourselves by making the throughput higher and latency lower. Latency would be measured by how long our system takes to process a single window of data. Throughput would be the size of the window the system can process.


<a id="org6be5090"></a>

## Deliverables are:


<a id="orgf105e74"></a>

### Query 1: Quantitative Indicators Per Symbol

Implementation of an operator that calculates the EMA for a given symbol


<a id="orgc1684e9"></a>

### Query 2: Breakout Patterns via Crossovers

Implementation of an operator that uses Query 1 to track 2 EMAs for a given symbol and detect patterns (bullish/bearish) in crossovers of EMA values


<a id="org4aee02e"></a>

### Reply to subscriptions

Everytime a bullish or bearish breakout pattern is detected for a given symbol, reply to the subscriber and notify


<a id="orgb155a2b"></a>

# Task Assignments

Here is a tentative checklist of assignments for this project.
We will be meeting on at least a weekly basis physically.

There are two main parts to this project:


<a id="org6974bfa"></a>

## Initial Proof of Concept

-   Getting VMs working
-   Example code running
-   EMA calculation through Flink
-   Breakout pattern detection
-   TODO PubSub Kafka handle symbol subscriptions and notify of breakout patterns
    -   publisher: ???
    -   subscriber: ???
-   Testing and Benchmarking


<a id="org651a517"></a>

## Optimizing

-   Work on optimizing query 1
-   Work on optimizing query 2
-   Sample data visualization drafts
-   EMA visualization
-   Breakout visualization
-   Documentation
-   Testing and Benchmarking
-   Short Paper draft


<a id="orgbcd54cb"></a>

## Task Distribution

Here is an initial task distribution

-   Xavier
    EMA calculation through Flink
-   Quang
    Breakout pattern detection
-   Quan
    Breakout pattern detection
-   Shekhar
    EMA calculation through Flink
-   Ryte
    Example code running

