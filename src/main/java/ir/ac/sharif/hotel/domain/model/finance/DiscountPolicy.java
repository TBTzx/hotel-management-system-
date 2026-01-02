package ir.ac.sharif.hotel.domain.model.finance;

/**
 * تخفیف تا سقف ۴۰٪.
 */
public interface DiscountPolicy {
    double discountRate(int pastReservationsCount);

    static DiscountPolicy loyaltyUpTo40() {
        return past -> {
            if (past >= 10) return 0.40;
            if (past >= 5) return 0.25;
            if (past >= 2) return 0.10;
            return 0.0;
        };
    }
}
