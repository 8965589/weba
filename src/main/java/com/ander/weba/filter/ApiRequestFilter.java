package com.ander.weba.filter;

import com.ander.weba.config.AccessConfig;
import com.ander.weba.service.HttpFilterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by zhouhh2 on 2016/10/24.
 */
@Component
public class ApiRequestFilter extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    //@Value("${spring.application.name}")
    @Value("weba")
    private String appName;

    @Autowired
    private AccessConfig accessConfig;

    @Autowired
    private HttpFilterService httpFilterService;

    //@Autowired
    //private TSysLogService sysLogService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        logger.debug("Step into [{}] api filter......", appName);
        setResponseAccessControl(response);
        if (!accessConfig.isLogEnabled() || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        httpFilterService.addRequest(request, response);
        response.setHeader("taskId", httpFilterService.getTaskId());
        handleResponse(request, response, filterChain);
        httpFilterService.clearRequest();
    }

    private void handleResponse(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        PrintWriter out = null;
        try {
            filterChain.doFilter(httpFilterService.getRequestWrapper(), httpFilterService.getResponseWrapper());
            out = response.getWriter();
            out.write(httpFilterService.getResponse());
        } finally {
            out.flush();
            out.close();
            //saveSysLog(httpFilterService, request);
        }
    }

    /**
     * 插入日志的操作
     *
     * @param httpFilterService
     * @param request
     * @throws IOException private void saveSysLog(HttpFilterService httpFilterService, HttpServletRequest request) throws IOException {
     *                     TSysLog sysLog = new TSysLog();
     *                     sysLog.setUuid(CommonUtils.getUuid());
     *                     //sysLog.setUid(httpFilterService.getRequestParam().get("uid"));
     *                     sysLog.setTaskid(httpFilterService.getTaskId());
     *                     sysLog.setSrcApp(appName);
     *                     sysLog.setResponseStatus(httpFilterService.getResponseStatus() + "");
     *                     sysLog.setRequestMethod(request.getMethod());
     *                     sysLog.setResponseCode(httpFilterService.getResponseStatus() + "");
     *                     if (StringUtils.isNotEmpty(httpFilterService.getStrRequestParam())) {
     *                     sysLog.setRequestUrl(request.getRequestURL().toString() + "?" + httpFilterService.getStrRequestParam());
     *                     } else {
     *                     sysLog.setRequestUrl(request.getRequestURL().toString());
     *                     }
     *                     sysLog.setRequestHeader(objectMapper.writeValueAsString(httpFilterService.getRequestHeader()));
     *                     sysLog.setRequestBody(httpFilterService.getRequestBody());
     *                     sysLog.setIp(httpFilterService.getClientIp());
     *                     sysLog.setCrTime(new Date());
     *                     sysLog.setResponse(httpFilterService.getResponse());
     *                     sysLogService.insertSelective(sysLog);
     *                     }
     */

    private void setResponseAccessControl(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", accessConfig.getOrigin());
        response.setHeader("Access-Control-Allow-Methods", accessConfig.getMethod());
        response.setHeader("Access-Control-Max-Age", String.valueOf(accessConfig.getMaxAge()));
        response.setHeader("Access-Control-Allow-Headers", accessConfig.getHeader());
    }

}


