package com.jijesoft.boh.core.etljob.chunk;

import org.springframework.batch.item.ItemReader;
/**
 * Strategy interface for providing the data.
 * 
 * @author Adam.Mei
 *
 * @param <T>
 */
public interface IReader<T> extends ItemReader<T> {

}
