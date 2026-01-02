package ir.ac.sharif.hotel.domain.model.finance;
import java.io.Serializable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * برای نمایش هزینه‌ها:
 * - هزینه هر اتاق جدا
 * - هزینه خدمات جانبی جدا
 * - جمع کل + تخفیف
 */
public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<Integer, Long> roomCosts = new LinkedHashMap<>();
    private long servicesCost;
    private double discountRate;
    private long finalPayable;

    public void putRoomCost(int roomId, long cost) { roomCosts.put(roomId, cost); }
    public void setServicesCost(long servicesCost) { this.servicesCost = servicesCost; }
    public void setDiscountRate(double discountRate) { this.discountRate = discountRate; }
    public void setFinalPayable(long finalPayable) { this.finalPayable = finalPayable; }

    public Map<Integer, Long> getRoomCosts() { return Collections.unmodifiableMap(roomCosts); }
    public long getServicesCost() { return servicesCost; }
    public double getDiscountRate() { return discountRate; }
    public long getFinalPayable() { return finalPayable; }
}
