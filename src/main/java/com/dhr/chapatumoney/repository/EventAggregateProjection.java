package com.dhr.chapatumoney.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface EventAggregateProjection {
    UUID getEventId();
    BigDecimal getMinPrice();
    Integer getTotalDisponibles();
}
