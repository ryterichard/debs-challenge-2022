
# Table of Contents

1.  [Problem Statement](#org0535f46)
2.  [Proposed Solution](#org2d7c81c)
3.  [Expectations](#org0c58290)
4.  [Experimental Plan](#orgaa2e633)
5.  [Success Indicators](#org2339cda)
    1.  [Deliverables are:](#org50e4375)
        1.  [Query 1: Quantitative Indicators Per Symbol](#org40f9948)
        2.  [Query 2: Breakout Patterns via Crossovers](#orgd7c968c)
        3.  [Reply to subscriptions](#orga179053)
6.  [Task Assignments](#org5bc6588)
    1.  [Initial Proof of Concept](#orge583ef9)
    2.  [Optimizing](#org9e5be43)
    3.  [Task Distribution](#orgc17e470)
        1.  [Xavier](#org3fd0102)
        2.  [Quang](#orgf7a36f8)
        3.  [Quan](#org116660e)
        4.  [Shekhar](#orga9e0028)
        5.  [Ryte](#org2384c06)


<a id="org0535f46"></a>

# Problem Statement

“The 2022 DEBS Grand Challenge focuses on real-time complex event processing of real-world high-volume tick data provided by Infront Financial Technology (<https://www.infrontfinance.com/>). In the data set, about 5000+ financial instruments are being traded on three major exchanges over the course of a week. The goal of the challenge is to efficiently compute specific trend indicators and detect patterns resembling those used by real-life traders to decide on buying or selling on the financial markets”
(<https://2022.debs.org/call-for-grand-challenge-solutions/>)


<a id="org2d7c81c"></a>

# Proposed Solution

Given the problem of analyzing market data in real-time, a streaming system is the obvious choice. We will be using Flink for the data analysis and Kafka for the PubSub part of the problem. For data visualization, we will look into solutions such as Python or Javascript due to the vast array of pre-built libraries for visualization. 


<a id="org0c58290"></a>

# Expectations

A real-time data analysis and visualization system that could be used in the real world. The primary metric, EMA, could easily be expanded or replaced with different ones, making our system modular, extensible, and horizontally scalable. 


<a id="orgaa2e633"></a>

# Experimental Plan

The DEBs challenge provides a test dataset along with a benchmarking platform to test our performance. The DEBS Challenge platform also provides VMs for deployment of our code and testing there.

Per DEBS, “The data set contains 289 million tick data events covering 5504 equities and indices that are traded on three European exchanges: Paris (FR), Amsterdam (NL), and Frankfurt/Xetra (ETR). All tick data events for security types equities (e.g., Royal Dutch Shell, Siemens Healthineers) and indices are contained as they have been captured on these days. However, some event notifications appear to come with no payload. This is due to the fact that this challenge requires only a small subset of attributes to be evaluated; other attributes have been eliminated from the data set to minimize its overall size while keeping the amount of events to process unchanged.”

This data set was captured by Infront Financial Technology for a complete week from 11/8/2021-11/14/2021.


<a id="org2339cda"></a>

# Success Indicators

The project’s success should first be measured by correctness. Later, we want to challenge ourselves by making the throughput higher and latency lower. Latency would be measured by how long our system takes to process a single window of data. Throughput would be the size of the window the system can process.


<a id="org50e4375"></a>

## Deliverables are:


<a id="org40f9948"></a>

### Query 1: Quantitative Indicators Per Symbol

Implementation of an operator that calculates the EMA for a given symbol


<a id="orgd7c968c"></a>

### Query 2: Breakout Patterns via Crossovers

Implementation of an operator that uses Query 1 to track 2 EMAs for a given symbol and detect patterns (bullish/bearish) in crossovers of EMA values


<a id="orga179053"></a>

### Reply to subscriptions

Everytime a bullish or bearish breakout pattern is detected for a given symbol, reply to the subscriber and notify


<a id="org5bc6588"></a>

# Task Assignments

Here is a tentative checklist of assignments for this project.
We will be meeting on at least a weekly basis physically.

There are two main parts to this project:


<a id="orge583ef9"></a>

## Initial Proof of Concept

-   Getting VMs working
-   Example code running
-   EMA calculation through Flink
-   Breakout pattern detection
-   TODO PubSub Kafka handle symbol subscriptions and notify of breakout patterns
    -   publisher: ???
    -   subscriber: ???
-   Testing and Benchmarking


<a id="org9e5be43"></a>

## Optimizing

-   Work on optimizing query 1
-   Work on optimizing query 2
-   Sample data visualization drafts
-   EMA visualization
-   Breakout visualization
-   Documentation
-   Testing and Benchmarking
-   Short Paper draft


<a id="orgc17e470"></a>

## Task Distribution

Here is an initial task distribution


<a id="org3fd0102"></a>

### Xavier

EMA calculation through Flink


<a id="orgf7a36f8"></a>

### Quang

Breakout pattern detection


<a id="org116660e"></a>

### Quan

Breakout pattern detection


<a id="orga9e0028"></a>

### Shekhar

EMA calculation through Flink


<a id="org2384c06"></a>

### Ryte

Example code running

