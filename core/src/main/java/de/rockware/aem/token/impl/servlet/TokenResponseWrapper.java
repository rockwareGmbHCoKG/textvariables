package de.rockware.aem.token.impl.servlet;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Response Wrapper for our token replacer feature.
 */
public class TokenResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream byteArrayOutputStream;
    private ServletOutputStream output;
    private PrintWriter writer;

    /**
     * C'tor.
     * @param response  original response
     */
    public TokenResponseWrapper(HttpServletResponse response) {
        super(response);
        byteArrayOutputStream = new ByteArrayOutputStream(response.getBufferSize());
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (writer != null) {
            throw new IllegalStateException(
                    "getWriter() has already been called on this response.");
        }

        if (output == null) {
            output = new ServletOutputStream() {
                @Override
                public void write(int b) throws IOException {
                    byteArrayOutputStream.write(b);
                }

                @Override
                public void flush() throws IOException {
                    byteArrayOutputStream.flush();
                }

                @Override
                public void close() throws IOException {
                    byteArrayOutputStream.close();
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setWriteListener(WriteListener arg0) {
                }
            };
        }

        return output;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (output != null) {
            throw new IllegalStateException(
                    "getOutputStream() has already been called on this response.");
        }

        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(byteArrayOutputStream,
                    getCharacterEncoding()));
        }

        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        super.flushBuffer();

        if (writer != null) {
            writer.flush();
        } else if (output != null) {
            output.flush();
        }
    }

    /**
     * Get response as Byte array.
     * @return              byte array
     * @throws IOException  if response cannot be extracted.
     */
    public byte[] getResponseAsBytes() throws IOException {
        if (writer != null) {
            writer.close();
        } else if (output != null) {
            output.close();
        }

        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Get the original response as String.
     * @return              String with the original resonse.
     * @throws IOException  if something goes wrong
     */
    public String getResponseAsString() throws IOException {
        byte[] bytes = getResponseAsBytes();
        if (Objects.isNull(bytes)) {
            bytes = new byte[0];
        }
        String encoding = getCharacterEncoding();
        if (StringUtils.isEmpty(encoding)) {
            encoding = StandardCharsets.UTF_8.toString();
        }
        return new String(bytes, encoding);
    }

    /**
     * Get response as Byte array encoded using UTF-8 charset.
     * @return              byte array
     * @throws IOException  if response cannot be extracted.
     */
    public byte[] getResponseAsUtf8Bytes() throws IOException {
        String response = getResponseAsString();
        if (StringUtils.isEmpty(response)) {
            return new byte[0];
        }
        return response.getBytes(StandardCharsets.UTF_8);
    }

}