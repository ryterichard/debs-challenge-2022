package app.functions;

import de.tum.i13.bandency.Indicator;
import org.apache.flink.api.common.functions.FilterFunction;

public class SymbolFilter implements FilterFunction<Indicator> {
    @Override
    public boolean filter(Indicator indicator) throws Exception {
        return false;
    }
}
