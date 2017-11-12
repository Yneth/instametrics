package ua.abond.metrics.util.io;

import java.io.IOException;

public interface IOSupplier<A> {

    A get() throws IOException;

}
