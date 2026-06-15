package es.udc.fi.dc.fd.rest.dtos;

import java.math.BigDecimal;

public class RankingEntryDto {
    private Long userId;
    private String displayName;
    private BigDecimal value;

    public RankingEntryDto(Long userId, String displayName, BigDecimal value) {
        this.userId = userId;
        this.displayName = displayName;
        this.value = value;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}