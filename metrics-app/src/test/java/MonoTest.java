import org.junit.Test;

import reactor.core.publisher.Mono;

public class MonoTest {

    @Test
    public void testExpand() {
        Mono.justOrEmpty("a").expand(obj -> obj.equals("a") ? Mono.just("b") : Mono.empty())
                .subscribe(System.out::println);
    }

}
