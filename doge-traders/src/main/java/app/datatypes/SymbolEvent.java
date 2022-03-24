package app.datatypes;

import com.google.protobuf.Timestamp;
import de.tum.i13.bandency.SecurityType;

import java.io.Serializable;

public class SymbolEvent implements Comparable<SymbolEvent>, Serializable {
    public String symbol;
    public SecurityType securityType;
    public float lastTradePrice;
    public long timestamp;
    public long batchID;
    public boolean isLastEventOfKeyOfBatch;

    public SymbolEvent(String symbol, SecurityType securityType, float lastTradePrice, Timestamp timeStamp, long bid, boolean last) {
        this.symbol = symbol;
        this.securityType = securityType;
        this.lastTradePrice = lastTradePrice;
        this.timestamp = (timeStamp.getSeconds() * 1000) + (timeStamp.getNanos() / 1000000);
        this.batchID = bid;
        this.isLastEventOfKeyOfBatch = last;
    }


    public SymbolEvent(){}

    public String toString() {
        return this.timestamp + "," +
                this.symbol + "," +
                this.lastTradePrice + "," +
                this.securityType + ",";
    }


    @Override
    public int compareTo(SymbolEvent other) {
        if (other == null) {
            return 1;
        }
        return Long.compare(this.timestamp,other.timestamp);
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
