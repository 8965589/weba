package com.ander.weba.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Date 2018/11/22 15:06.
 * <p>
 * 和 fileserver 放在 同一个 tomcat 下 就可以了  项目 叫 weba 不要变动
 */
@RestController
@RequestMapping("/ue")
public class UEConfigController {
    private static Logger logger = LogManager.getLogger(UEConfigController.class);


    @RequestMapping("/conf")
    public Object conf(HttpServletRequest request, @RequestParam(required = false) String action) throws Exception {
        if (StringUtils.equals(action, "config")) {
            String uirPre = "http://" + request.getServerName() + ":" + request.getServerPort() + "/fileserver/files/weba/";

            ObjectMapper mapper = new ObjectMapper();
            File file = ResourceUtils.getFile("classpath:static/ue/jsp/config.json");
            ObjectNode objectNode = mapper.readValue(file, ObjectNode.class);
            objectNode.put("imageUrlPrefix", uirPre);
            objectNode.put("fileUrlPrefix", uirPre);
            objectNode.put("scrawlUrlPrefix", uirPre);
            objectNode.put("snapscreenUrlPrefix", uirPre);
            objectNode.put("catcherUrlPrefix", uirPre);
            objectNode.put("videoUrlPrefix", uirPre);
            objectNode.put("imageManagerUrlPrefix", uirPre);
            objectNode.put("fileManagerUrlPrefix", uirPre);
            return objectNode;
        } else if ("uploadimage".equals(action) || "uploadvideo".equals(action) || "uploadfile".equals(action)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String appPath = request.getSession().getServletContext().getRealPath(File.separator);
            logger.info("获取的项目全路径是:" + appPath);
            String uploadPath = appPath.substring(0, appPath.indexOf("webapps") + 7) + "/" + "fileserver/files/weba/";
            String date = sdf.format(new Date());
            String path = uploadPath + date;
            File temFile = new File(path);
            if (!temFile.exists()) {
                temFile.mkdir();
            }
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            MultipartFile upfile = mRequest.getFile("upfile");
            if (upfile.isEmpty()) {
                return "上传错误";
            }
            String localFileName = "weba" + System.currentTimeMillis() + upfile.getOriginalFilename();
            try {
                String filePath = path + "/" + localFileName;
                upfile.transferTo(new File(filePath));
            } catch (Exception e) {
            }
            String imageUrl = date + "/" + localFileName;
            String prefix = upfile.getOriginalFilename().substring(upfile.getOriginalFilename().lastIndexOf(".") + 1);
            Map<String, Object> result = new HashMap();
            result.put("original", upfile.getOriginalFilename());
            result.put("name", upfile.getOriginalFilename());
            result.put("url", imageUrl);
            result.put("size", upfile.getSize());
            result.put("type", "." + prefix);
            result.put("state", "SUCCESS");
            return result;
        } else {
            return null;
        }

    }

}
