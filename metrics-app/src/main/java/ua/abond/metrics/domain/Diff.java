package ua.abond.metrics.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@AllArgsConstructor
public class Diff<S> {

    @Id
    private String hash;
    private String ownerId;
    private LocalDateTime creationDate;
    private Set<S> added;
    private Set<S> removed;

    public boolean isEmpty() {
        return getAdded().isEmpty() && getRemoved().isEmpty();
    }

}
