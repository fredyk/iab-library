package com.txstockdata.iab_library;


/**
 * © 2016 Jhon Fredy Magdalena Vila
 */
public interface Function<T> {
    void call(T result) throws StockDataException;

}
