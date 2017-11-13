package ua.abond.metrics.util.date;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import lombok.Getter;

@Getter
public class LocalDateTimeInterval {

    private final LocalDateTime from;
    private final LocalDateTime to;

    public LocalDateTimeInterval(LocalDateTime from, LocalDateTime to) {
        this.from = Objects.requireNonNull(from, "From date cannot be null");
        this.to = Objects.requireNonNull(to, "To date cannot be null");
    }

    public long duration(ChronoUnit chronoUnit) {
        Objects.requireNonNull(chronoUnit, "ChronoUnit cannot be null");
        return chronoUnit.between(from, to);
    }

}
