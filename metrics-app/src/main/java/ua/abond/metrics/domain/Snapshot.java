package ua.abond.metrics.domain;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface Snapshot<T extends Snapshot, S> {

    String getOwnerId();

    List<S> getSources();

    LocalDateTime getCreationDate();

    default Diff<S> diff(Snapshot<T, S> that) {
        Set<S> added = Util.diff(this.getSources(), that.getSources());
        Set<S> removed = Util.diff(that.getSources(), this.getSources());
        return new Diff<>(UUID.randomUUID().toString(), getOwnerId(), LocalDateTime.now(), added, removed);
    }

    @UtilityClass
    class Util {

        static <S> Set<S> diff(List<S> left, List<S> right) {
            return left.stream()
                .filter(notContains(right))
                .collect(Collectors.toSet());
        }

        static <T> Predicate<T> notContains(List<T> set) {
            return ((Predicate<T>) set::contains).negate();
        }

    }

}
