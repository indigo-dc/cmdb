package pl.cyfronet.fid.cmdbproxy.web.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(2)
public class ResettableRequestStreamFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ResettableStreamHttpServletRequest resettableRequest =
                new ResettableStreamHttpServletRequest(request);

        filterChain.doFilter(resettableRequest, response);
    }

    private static class ResettableStreamHttpServletRequest extends HttpServletRequestWrapper {

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
                rawData = IOUtils.toByteArray(this.request.getReader(), Charset.defaultCharset());
            }
            servletStream.stream = new ByteArrayInputStream(rawData);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            readRawData();
            return new BufferedReader(new InputStreamReader(servletStream));
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
}
