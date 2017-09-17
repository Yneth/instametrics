package ua.abond.instaret.util.io;

import java.io.IOException;

public interface IOSupplier<A> {

    A get() throws IOException;
}
