package pl.cyfronet.fid.cmdb.web;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;

public class ResettableStreamHttpServletRequest extends HttpServletRequestWrapper {

    private byte[] rawData;
    private HttpServletRequest request;
    private ResettableServletInputStream servletStream;

    public ResettableStreamHttpServletRequest(HttpServletRequest request) {
        super(request);
        this.request = request;
        this.servletStream = new ResettableServletInputStream();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        readRawData();
        return servletStream;
    }

    private void readRawData() throws IOException {
        if (rawData == null) {
            rawData = IOUtils.toByteArray(request.getReader(), Charset.defaultCharset());
        }
        servletStream.stream = new ByteArrayInputStream(rawData);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        readRawData();
        return new BufferedReader(new InputStreamReader(servletStream));
    }



    @Override
    public String getHeader(String name) {
        if ("Content-Length".equalsIgnoreCase(name)) {
            try {
                readRawData();
            } catch (IOException e) { }
            return "" + rawData.length;
        }
        return super.getHeader(name);
    }

    public void resetInputStream(byte[] rawData) {
        this.rawData = rawData;
        servletStream.stream = new ByteArrayInputStream(rawData);
    }

    private class ResettableServletInputStream extends ServletInputStream {

        private InputStream stream;

        @Override
        public int read() throws IOException {
            return stream.read();
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
        }
    }
}