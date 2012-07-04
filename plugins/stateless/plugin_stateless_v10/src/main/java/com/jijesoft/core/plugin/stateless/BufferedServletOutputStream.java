package com.jijesoft.core.plugin.stateless;

import java.io.ByteArrayOutputStream;

import javax.servlet.ServletOutputStream;

public class BufferedServletOutputStream extends ServletOutputStream {

	private ByteArrayOutputStream bos = new ByteArrayOutputStream();

	/**
	 * @return the contents of the buffer.
	 */
	public byte[] getBuffer() {
		return this.bos.toByteArray();
	}

	// BufferedHttpResponseWrapper calls this method
	public void reset() {
		this.bos.reset();
	}

	// BufferedHttpResponseWrapper calls this method
	public void setBufferSize(int size) {
		// no way to resize an existing ByteArrayOutputStream
		this.bos = new ByteArrayOutputStream(size);
	}

	/**
	 * This method must be defined for custom servlet output streams.
	 */
	@Override
	public void write(int data) {
		this.bos.write(data);
	}

}
