package app.datatypes;

import de.tum.i13.bandency.CrossoverEvent;
import de.tum.i13.bandency.Indicator;

import java.util.List;

public class SymbolResult {

    private String symbolEvent;
    private Long batchId;
    private Integer lookupSymbolCount;
    private Long benchmarkId;
    private Boolean isLastBatch;
    private Indicator indicator;
    private List<CrossoverEvent> crossoverEventList;

    public SymbolResult(String symbolEvent, Long batchId, Integer lookupSymbolCount, Long benchmarkId, Boolean isLastBatch,
                        Indicator indicator, List<CrossoverEvent> crossoverEventList) {
        this.symbolEvent = symbolEvent;
        this.batchId = batchId;
        this.lookupSymbolCount = lookupSymbolCount;
        this.benchmarkId = benchmarkId;
        this.isLastBatch = isLastBatch;
        this.indicator = indicator;
        this.crossoverEventList = crossoverEventList;
    }

    @Override
    public String toString() {
        return "BatchResult{" +
                "BatchId=" + batchId +
                ", indicator=" + indicator +
                ", crossoverEventList=" + crossoverEventList +
                '}';
    }

    public String getSymbolEvent() {
        return symbolEvent;
    }

    public void setSymbolEvent(String symbolEvent) {
        this.symbolEvent = symbolEvent;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
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

    public Integer getLookupSymbolCount() {
        return lookupSymbolCount;
    }

    public void setLookupSymbolCount(Integer lookupSymbolCount) {
        this.lookupSymbolCount = lookupSymbolCount;
    }

    public Long getBenchmarkId() {
        return benchmarkId;
    }

    public void setBenchmarkId(Long benchmarkId) {
        this.benchmarkId = benchmarkId;
    }

    public Boolean getLastBatch() {
        return isLastBatch;
    }

    public void setLastBatch(Boolean lastBatch) {
        isLastBatch = lastBatch;
    }
}
