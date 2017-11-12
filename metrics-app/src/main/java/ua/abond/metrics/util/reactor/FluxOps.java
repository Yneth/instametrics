package ua.abond.metrics.util.reactor;

import lombok.experimental.UtilityClass;
import reactor.core.publisher.Flux;

@UtilityClass
public class FluxOps {

    public <T> Flux<T> emptyOrIterable(Iterable<T> iterable) {
        return iterable == null ? Flux.empty() : Flux.fromIterable(iterable);
    }

}
