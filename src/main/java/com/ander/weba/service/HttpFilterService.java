package com.ander.weba.service;

import com.ander.weba.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Service
public class HttpFilterService {
    private static ThreadLocal<RequestManager> managerThreadLocal = new ThreadLocal<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void addRequest(HttpServletRequest request, HttpServletResponse response) {
        RequestManager requestManager = new RequestManager(request, response);
        requestManager.setTaskId(CommonUtils.getUuid());
        managerThreadLocal.set(requestManager);
    }

    public void clearRequest() {
        managerThreadLocal.remove();
    }

    public String getClientIp() {
        HttpServletRequest request = managerThreadLocal.get().getRequest();
        if (request == null) {
            return null;
        }
        return getIp(request);
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public RequestWrapper getRequestWrapper() throws IOException {
        if (managerThreadLocal.get().getRequestWrapper() != null) {
            return managerThreadLocal.get().getRequestWrapper();
        }
        managerThreadLocal.get().setRequestWrapper(new RequestWrapper(managerThreadLocal.get().getRequest()));
        return managerThreadLocal.get().getRequestWrapper();
    }

    public String getRequestBody() {
        return managerThreadLocal.get().getRequestWrapper().getBody();
    }

    public ResponseWrapper getResponseWrapper() {
        if (managerThreadLocal.get().getResponseWrapper() != null) {
            return managerThreadLocal.get().getResponseWrapper();
        }
        managerThreadLocal.get().setResponseWrapper(new ResponseWrapper(managerThreadLocal.get().getResponse()));
        return managerThreadLocal.get().getResponseWrapper();
    }

    public String getResponse() {
        return managerThreadLocal.get().getResponseWrapper().getTextContent();
    }

    public int getResponseStatus() {
        return managerThreadLocal.get().getResponseWrapper().getStatus();
    }

    public String getTaskId() {
        if (StringUtils.isBlank(managerThreadLocal.get().getTaskId())) {
            return CommonUtils.getUuid();
        }
        return managerThreadLocal.get().getTaskId();
    }

    public Map<String, String> getRequestParam() {
        String param = managerThreadLocal.get().getRequest().getQueryString();
        if (StringUtils.isBlank(param)) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        String[] tmpParam = StringUtils.split(param, "&");
        for (String perParam : tmpParam) {
            String[] tmpPerParam = StringUtils.split(perParam, "=");
            map.put(tmpPerParam[0], tmpPerParam[1]);
        }
        return map;
    }

    public String getStrRequestParam() {
        return managerThreadLocal.get().getRequest().getQueryString();
    }

    public Map<String, String> getRequestHeader() {
        Enumeration headerNames = managerThreadLocal.get().getRequest().getHeaderNames();
        Map<String, String> map = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = managerThreadLocal.get().getRequest().getHeader(key);
            map.put(key, value);
        }
        return map;
    }
}

class RequestManager {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ResponseWrapper responseWrapper;
    private RequestWrapper requestWrapper;
    private String taskId;

    public RequestManager(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public ResponseWrapper getResponseWrapper() {
        return responseWrapper;
    }

    public void setResponseWrapper(ResponseWrapper responseWrapper) {
        this.responseWrapper = responseWrapper;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public RequestWrapper getRequestWrapper() {
        return requestWrapper;
    }

    public void setRequestWrapper(RequestWrapper requestWrapper) {
        this.requestWrapper = requestWrapper;
    }
}

class ResponseWrapper extends HttpServletResponseWrapper {

    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private PrintWriter printWriter = new PrintWriter(outputStream);


    public ResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return printWriter;
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener listener) {

            }

            @Override
            public void write(int bytes) {
                outputStream.write(bytes);
            }
        };
    }

    public void flush() {
        try {
            printWriter.flush();
            printWriter.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ByteArrayOutputStream getByteArrayOutputStream() {
        return outputStream;
    }


    public String getTextContent() {
        flush();
        return outputStream.toString();
    }
}

class RequestWrapper extends HttpServletRequestWrapper {
    private final String body;

    public String getBodyString(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = request.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            close(inputStream, reader);
        }
        return sb.toString();
    }

    private void close(InputStream inputStream, BufferedReader reader) throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
        if (reader != null) {
            reader.close();
        }
    }

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        body = getBodyString(request);
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }

            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    public String getBody() {
        return this.body;
    }
}
