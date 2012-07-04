package com.jijesoft.boh.core.etljob.chunk;

import org.springframework.batch.item.ItemWriter;
/**
 * Basic interface for generic output operations
 * 
 * @author Adam.Mei
 *
 * @param <T>
 */
public interface IWriter<T> extends ItemWriter<T> {

}
