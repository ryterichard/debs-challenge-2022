package app.datatypes;

import de.tum.i13.bandency.CrossoverEvent;
import de.tum.i13.bandency.Indicator;

import java.util.List;

public class BatchResult {

    private Long BatchId;
    private Indicator indicator;
    private List<CrossoverEvent> crossoverEventList;

    public BatchResult(Long batchId, Indicator indicator, List<CrossoverEvent> crossoverEventList) {
        BatchId = batchId;
        this.indicator = indicator;
        this.crossoverEventList = crossoverEventList;
    }

    @Override
    public String toString() {
        return "BatchResult{" +
                "BatchId=" + BatchId +
                ", indicator=" + indicator +
                ", crossoverEventList=" + crossoverEventList +
                '}';
    }

    public Long getBatchId() {
        return BatchId;
    }

    public void setBatchId(Long batchId) {
        BatchId = batchId;
    }

    public Indicator getIndicator() {
        return indicator;
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    public List<CrossoverEvent> getCrossoverEventList() {
        return crossoverEventList;
    }

    public void setCrossoverEventList(List<CrossoverEvent> crossoverEventList) {
        this.crossoverEventList = crossoverEventList;
    }
}
