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
    private boolean isLastEventOfKeyOfBatch;

    public SymbolEvent(String symbol, SecurityType securityType, float lastTradePrice, Timestamp timeStamp, long bid, boolean last) {
        this.symbol = symbol;
        this.securityType = securityType;
        this.lastTradePrice = lastTradePrice;
        this.timestamp = Timestamps.toMillis(timeStamp);
        this.batchID = bid;
        this.isLastEventOfKeyOfBatch = last;
    }

    public SymbolEvent(String symbol, long bid, boolean last) {
        this.symbol = symbol;
        this.batchID = bid;
        this.isLastEventOfKeyOfBatch = last;
    }


    public SymbolEvent(){}

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
        return Long.compare(this.timestamp,other.timestamp);
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
