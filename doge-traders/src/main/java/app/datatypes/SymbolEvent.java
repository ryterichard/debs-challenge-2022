package app.datatypes;

import com.google.protobuf.Timestamp;
import de.tum.i13.bandency.SecurityType;

import java.io.Serializable;

public class SymbolEvent implements Comparable<SymbolEvent>, Serializable {
    public String symbol;
    public SecurityType securityType;
    public float lastTradePrice;
    public long timeStamp;

    public SymbolEvent(String symbol, SecurityType securityType, float lastTradePrice, Timestamp timeStamp) {
        this.symbol = symbol;
        this.securityType = securityType;
        this.lastTradePrice = lastTradePrice;
        this.timeStamp = timeStamp.getSeconds() * 1000 + timeStamp.getNanos() / 1000000;
    }


    public SymbolEvent(){}

    public String toString() {
        return this.timeStamp + "," +
                this.symbol + "," +
                this.lastTradePrice + "," +
                this.securityType + ",";
    }

//    public static SymbolEvent fromString(String line) {
//        String[] tokens = line.split(",");
//        if (tokens.length < 24) {
//            throw new RuntimeException("Invalid symbol event record: " + line);
//        }
//
//        SymbolEvent sEvent = new SymbolEvent();
//        SimpleDateFormat sdf = new SimpleDateFormat("d/MM/yyyy mm:ss.s");
//        try {
//            sEvent.symbol = tokens[0];
//            if (tokens[1].equals("I")) {
//                sEvent.securityType = SecurityType.INDEX;
//            } else {
//                sEvent.securityType = SecurityType.EQUITY;
//            }
//            sEvent.lastTradePrice = Float.parseFloat(tokens[21]);
//            try {
//                sEvent.timeStamp = sdf.parse(tokens[2] + " " + tokens[3]).getTime() / 1000;
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
//
//        return sEvent;
//    }


    @Override
    public int compareTo(SymbolEvent other) {
        if (other == null) {
            return 1;
        }
        return Long.compare(this.timeStamp,other.timeStamp);
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
