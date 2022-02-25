package app.datatypes;

import java.util.HashMap;
import java.util.Map;

public enum SecurityType {
    EQUITY(0),
    INDEX(1);

    SecurityType(int eventCode) {
        this.value = eventCode;
    }

    private final int value;
    private final static Map map = new HashMap();

    static {
        for (SecurityType e : SecurityType.values()) {
            map.put(e.value, e);
        }
    }

    public static SecurityType valueOf(int eventType) {
        return (SecurityType) map.get(eventType);
    }

    public int getValue() {
        return value;
    }
}
