package com.jijesoft.core.plugin.stateless;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class BufferedHttpResponseWrapper extends HttpServletResponseWrapper {

	private final BufferedServletOutputStream bufferedServletOut = new BufferedServletOutputStream();

	private Integer errorCode = null;
	private String errorMsg = null;
	private String location = null;
	private ServletOutputStream outputStream = null;
	private PrintWriter printWriter = null;

	public BufferedHttpResponseWrapper(HttpServletResponse response) {
		super(response);
	}

	@Override
	public void flushBuffer() throws IOException {
		if (this.outputStream != null) {
			this.outputStream.flush();
		} else if (this.printWriter != null) {
			this.printWriter.flush();
		}
	}

	public byte[] getBuffer() {
		return this.bufferedServletOut.getBuffer();
	}

	@Override
	public int getBufferSize() {
		return this.bufferedServletOut.getBuffer().length;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (this.printWriter != null) {
			throw new IllegalStateException(
					"The Servlet API forbids calling getOutputStream( ) after" //$NON-NLS-1$
							+ " getWriter( ) has been called"); //$NON-NLS-1$
		}

		if (this.outputStream == null) {
			this.outputStream = this.bufferedServletOut;
		}
		return this.outputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (this.outputStream != null) {
			throw new IllegalStateException(
					"The Servlet API forbids calling getWriter( ) after" //$NON-NLS-1$
							+ " getOutputStream( ) has been called"); //$NON-NLS-1$
		}

		if (this.printWriter == null) {
			// Create writer with the response encoding.
			Writer writer = new BufferedWriter(new OutputStreamWriter(
					this.bufferedServletOut, this.getResponse()
							.getCharacterEncoding()));

			this.printWriter = new PrintWriter(writer);
		}
		return this.printWriter;
	}

	public boolean performSend() throws IOException {
		if (errorCode != null && errorMsg != null) {
			super.sendError(errorCode.intValue(), errorMsg);
			return true;
		} else if (errorCode != null) {
			super.sendError(errorCode.intValue());
			return true;
		} else if (location != null) {
			super.sendRedirect(location);
			return true;
		}

		return false;
	}

	@Override
	public void reset() {
		this.bufferedServletOut.reset();
	}

	@Override
	public void resetBuffer() {
		this.bufferedServletOut.reset();
	}

	@Override
	public void sendError(int sc) throws IOException {
		if (location == null) {
			errorCode = new Integer(sc);

		}
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		if (location == null) {
			errorCode = new Integer(sc);
			errorMsg = msg;
		}
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		if (errorCode == null) {
			this.location = location;
		}
	}

	@Override
	public void setBufferSize(int size) {
		this.bufferedServletOut.setBufferSize(size);
	}

}
