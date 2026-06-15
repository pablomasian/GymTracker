package es.udc.fi.dc.fd.rest.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExerciseProgressDto {
    private LocalDateTime fecha;
    private BigDecimal maxWeight;
    private int totalReps;
    private int totalSets;

    public ExerciseProgressDto(LocalDateTime fecha, BigDecimal maxWeight, int totalReps, int totalSets) {
        this.fecha = fecha;
        this.maxWeight = maxWeight;
        this.totalReps = totalReps;
        this.totalSets = totalSets;
    }

    // Getters y Setters
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    
    public BigDecimal getMaxWeight() { return maxWeight; }
    public void setMaxWeight(BigDecimal maxWeight) { this.maxWeight = maxWeight; }
    
    public int getTotalReps() { return totalReps; }
    public void setTotalReps(int totalReps) { this.totalReps = totalReps; }
    
    public int getTotalSets() { return totalSets; }
    public void setTotalSets(int totalSets) { this.totalSets = totalSets; }
}
