package ir.ac.sharif.hotel.domain.model.reservation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * جریمه لغو بر اساس تعداد روزهای باقی‌مانده تا تاریخ شروع رزرو.
 * PDF مقدار دقیق درصدها را تعیین نکرده؛ این یک پیاده‌سازی ساده/قابل تغییر است.
 */
public interface PenaltyPolicy {
    double penaltyRate(LocalDate today, LocalDate startDate);

    static PenaltyPolicy defaultPolicy() {
        return (today, start) -> {
            long daysLeft = ChronoUnit.DAYS.between(today, start);
            if (daysLeft >= 7) return 0.0;
            if (daysLeft >= 3) return 0.2;
            return 0.5;
        };
    }
}
