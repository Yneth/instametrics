package ua.abond.instaret.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(onConstructor = @__(@NonNull))
public class LocalDateTimeInterval {

    private final LocalDateTime from;
    private final LocalDateTime to;

    public long duration(ChronoUnit chronoUnit) {
        Objects.requireNonNull(chronoUnit, "ChronoUnit cannot be null");
        return chronoUnit.between(from, to);
    }

}
