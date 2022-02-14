
# Table of Contents

1.  [Problem Statement](#org757a20d)
2.  [Proposed Solution](#orgef88520)
3.  [Expectations](#org1cf6d3e)
4.  [Experimental Plan](#org5de66ad)
5.  [Success Indicators](#org4838f1d)
    1.  [Deliverables are:](#orgbc206f9)
        1.  [Query 1: Quantitative Indicators Per Symbol](#orgf11dedb)
        2.  [Query 2: Breakout Patterns via Crossovers](#org1f55128)
        3.  [Reply to subscriptions](#org8278188)
6.  [Task Assignments](#org763187a)
    1.  [Initial Proof of Concept](#org28e92de)
    2.  [Optimizing](#orge1bc008)
    3.  [Task Distribution](#org7583b8f)


<a id="org757a20d"></a>

# Problem Statement

“The 2022 DEBS Grand Challenge focuses on real-time complex event processing of real-world high-volume tick data provided by Infront Financial Technology (<https://www.infrontfinance.com/>). In the data set, about 5000+ financial instruments are being traded on three major exchanges over the course of a week. The goal of the challenge is to efficiently compute specific trend indicators and detect patterns resembling those used by real-life traders to decide on buying or selling on the financial markets”
(<https://2022.debs.org/call-for-grand-challenge-solutions/>)


<a id="orgef88520"></a>

# Proposed Solution

Given the problem of analyzing market data in real-time, a streaming system is the obvious choice. We will be using Flink for the data analysis and Kafka for the PubSub part of the problem. For data visualization, we will look into solutions such as Python or Javascript due to the vast array of pre-built libraries for visualization. 


<a id="org1cf6d3e"></a>

# Expectations

A real-time data analysis and visualization system that could be used in the real world. The primary metric, EMA, could easily be expanded or replaced with different ones, making our system modular, extensible, and horizontally scalable. 


<a id="org5de66ad"></a>

# Experimental Plan

The DEBs challenge provides a test dataset along with a benchmarking platform to test our performance. The DEBS Challenge platform also provides VMs for deployment of our code and testing there.

Per DEBS, “The data set contains 289 million tick data events covering 5504 equities and indices that are traded on three European exchanges: Paris (FR), Amsterdam (NL), and Frankfurt/Xetra (ETR). All tick data events for security types equities (e.g., Royal Dutch Shell, Siemens Healthineers) and indices are contained as they have been captured on these days. However, some event notifications appear to come with no payload. This is due to the fact that this challenge requires only a small subset of attributes to be evaluated; other attributes have been eliminated from the data set to minimize its overall size while keeping the amount of events to process unchanged.”

This data set was captured by Infront Financial Technology for a complete week from 11/8/2021-11/14/2021.


<a id="org4838f1d"></a>

# Success Indicators

The project’s success should first be measured by correctness. Later, we want to challenge ourselves by making the throughput higher and latency lower. Latency would be measured by how long our system takes to process a single window of data. Throughput would be the size of the window the system can process.


<a id="orgbc206f9"></a>

## Deliverables are:


<a id="orgf11dedb"></a>

### Query 1: Quantitative Indicators Per Symbol

Implementation of an operator that calculates the EMA for a given symbol


<a id="org1f55128"></a>

### Query 2: Breakout Patterns via Crossovers

Implementation of an operator that uses Query 1 to track 2 EMAs for a given symbol and detect patterns (bullish/bearish) in crossovers of EMA values


<a id="org8278188"></a>

### Reply to subscriptions

Everytime a bullish or bearish breakout pattern is detected for a given symbol, reply to the subscriber and notify


<a id="org763187a"></a>

# Task Assignments

Here is a tentative checklist of assignments for this project.
We will be meeting on at least a weekly basis physically.

There are two main parts to this project:


<a id="org28e92de"></a>

## Initial Proof of Concept

-   Getting VMs working
-   Example code running
-   EMA calculation through Flink
-   Breakout pattern detection
-   TODO PubSub Kafka handle symbol subscriptions and notify of breakout patterns
    -   publisher: ???
    -   subscriber: ???
-   Testing and Benchmarking


<a id="orge1bc008"></a>

## Optimizing

-   Work on optimizing query 1
-   Work on optimizing query 2
-   Sample data visualization drafts
-   EMA visualization
-   Breakout visualization
-   Documentation
-   Testing and Benchmarking
-   Short Paper draft


<a id="org7583b8f"></a>

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

