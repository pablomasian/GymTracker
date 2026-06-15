package es.udc.fi.dc.fd.rest.dtos;

import java.math.BigDecimal;

public class RoutineStatsDto {
    private Long routineId;
    private String routineName;
    private BigDecimal totalWeight;

    public RoutineStatsDto(Long routineId, String routineName, BigDecimal totalWeight) {
        this.routineId = routineId;
        this.routineName = routineName;
        this.totalWeight = totalWeight;
    }

    public Long getRoutineId() {
        return routineId;
    }

    public void setRoutineId(Long routineId) {
        this.routineId = routineId;
    }

    public String getRoutineName() {
        return routineName;
    }

    public void setRoutineName(String routineName) {
        this.routineName = routineName;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }
}