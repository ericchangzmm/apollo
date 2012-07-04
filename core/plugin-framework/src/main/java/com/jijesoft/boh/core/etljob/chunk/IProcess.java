package com.jijesoft.boh.core.etljob.chunk;

import org.springframework.batch.item.ItemProcessor;

/**
 * Interface for item transformation
 * 
 * @author Adam.Mei
 *
 * @param <I> input
 * @param <O> output
 */
public interface IProcess<I, O> extends ItemProcessor<I, O> {

}
