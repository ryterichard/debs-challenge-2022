package app.datatypes;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import de.tum.i13.bandency.SecurityType;

import java.io.Serializable;

public class SymbolEvent implements Comparable<SymbolEvent>, Serializable {
    private String symbol;
    private SecurityType securityType;
    private float lastTradePrice;
    private long timestamp;
    private long batchID;
    private int lookupSymbolCount;
    private long benchmarkId;
    private boolean isLastEventOfKeyOfBatch;
    private boolean isLastBatch;

    public SymbolEvent(String symbol, SecurityType securityType, float lastTradePrice, Timestamp timeStamp, long bid, boolean lastBatchEvent) {
        this.symbol = symbol;
        this.securityType = securityType;
        this.lastTradePrice = lastTradePrice;
        this.timestamp = Timestamps.toMillis(timeStamp);
        this.batchID = bid;
        this.isLastEventOfKeyOfBatch = lastBatchEvent;
    }

    public SymbolEvent(String symbol, long bid, int lookupSymbolCount, long benchmarkId, boolean last, boolean isLastBatch) {
        this.symbol = symbol;
        this.batchID = bid;
        this.lookupSymbolCount = lookupSymbolCount;
        this.benchmarkId = benchmarkId;
        this.isLastEventOfKeyOfBatch = last;
        this.isLastBatch = isLastBatch;
    }


    public SymbolEvent() {
    }

    public String toString() {
        return this.timestamp + "," +
                this.symbol + "," +
                this.lastTradePrice + "," +
                this.securityType + "," +
                this.batchID;
    }


    @Override
    public int compareTo(SymbolEvent other) {
        if (other == null) {
            return 1;
        }
        return Long.compare(this.timestamp, other.timestamp);
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public SecurityType getSecurityType() {
        return securityType;
    }

    public void setSecurityType(SecurityType securityType) {
        this.securityType = securityType;
    }

    public float getLastTradePrice() {
        return lastTradePrice;
    }

    public void setLastTradePrice(float lastTradePrice) {
        this.lastTradePrice = lastTradePrice;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getBatchID() {
        return batchID;
    }

    public void setBatchID(long batchID) {
        this.batchID = batchID;
    }

    public boolean isLastEventOfKeyOfBatch() {
        return isLastEventOfKeyOfBatch;
    }

    public void setLastEventOfKeyOfBatch(boolean lastEventOfKeyOfBatch) {
        isLastEventOfKeyOfBatch = lastEventOfKeyOfBatch;
    }

    public int getLookupSymbolCount() {
        return lookupSymbolCount;
    }

    public void setLookupSymbolCount(int lookupSymbolCount) {
        this.lookupSymbolCount = lookupSymbolCount;
    }

    public long getBenchmarkId() {
        return benchmarkId;
    }

    public void setBenchmarkId(long benchmarkId) {
        this.benchmarkId = benchmarkId;
    }

    public boolean isLastBatch() {
        return isLastBatch;
    }

    public void setLastBatch(boolean lastBatch) {
        isLastBatch = lastBatch;
    }

    @Override
    public int hashCode() {
        return this.symbol.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof SymbolEvent &&
                this.symbol.equals(((SymbolEvent) other).symbol);
    }

}
