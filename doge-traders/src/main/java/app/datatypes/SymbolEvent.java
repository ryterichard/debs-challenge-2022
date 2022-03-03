package app.datatypes;

import java.io.Serializable;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.google.protobuf.Timestamp;

public class SymbolEvent implements Comparable<SymbolEvent>, Serializable {
    public String symbol;
    public SecurityType securityType;
    public long lastTradePrice;
    public long timeStamp;

    public SymbolEvent(String symbol, SecurityType securityType, long lastTradePrice, long timeStamp) {
        this.symbol = symbol;
        this.securityType = securityType;
        this.lastTradePrice = lastTradePrice;
        this.timeStamp = timeStamp;
    }

    public SymbolEvent(){}

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.timeStamp).append(",");
        sb.append(this.symbol).append(",");
        sb.append(this.lastTradePrice).append(",");
        sb.append(this.securityType).append(",");
        return sb.toString();
    }

    public static SymbolEvent fromString(String line) {
        String[] tokens = line.split(",");
        if (tokens.length < 24) {
            throw new RuntimeException("Invalid symbol event record: " + line);
        }

        SymbolEvent sEvent = new SymbolEvent();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy MM:ss.s");
        try {
            sEvent.symbol = tokens[0];
            if (tokens[1].equals("I")) {
                sEvent.securityType = SecurityType.INDEX;
            } else {
                sEvent.securityType = SecurityType.EQUITY;
            }
            sEvent.lastTradePrice = Long.parseLong(tokens[21]);
            try {
                sEvent.timeStamp = sdf.parse(tokens[2] + " " + tokens[3]).getTime() / 1000;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return sEvent;
    }


    @Override
    public int compareTo(SymbolEvent other) {
        if (other == null) {
            return 1;
        }
        return Long.compare(this.timeStamp, other.timeStamp);
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
