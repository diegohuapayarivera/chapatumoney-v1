package com.dhr.chapatumoney.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AdminStatsResponse {
    private long totalUsers;
    private long totalOrganizers;
    private long totalEvents;
    private long totalTicketsSold;
    private BigDecimal totalRevenue;
    private BigDecimal totalPlatformCommission;
}
