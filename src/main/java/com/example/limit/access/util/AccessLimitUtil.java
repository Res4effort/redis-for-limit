package com.example.limit.access.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class AccessLimitUtil {

    public static String getUniqueId(HttpServletRequest request) {
        return getIp(request);
    }

    private static String getIp(HttpServletRequest req) {
        try {
            String ip = req.getHeader("X-Forwarded-For");
            if (StringUtils.isNotBlank(ip)) {
                String[] ips = StringUtils.split(ip, ',');
                if (ips != null) {
                    for (String tmpIp : ips) {
                        if (StringUtils.isBlank(tmpIp))
                            continue;
                        tmpIp = tmpIp.trim();
                        if (isIPAddress(tmpIp) && !tmpIp.startsWith("10.") && !tmpIp.startsWith("192.168.")
                                && !"127.0.0.1".equals(tmpIp)) {
                            return tmpIp.trim();
                        }
                    }
                }
            }
            ip = req.getHeader("x-real-ip");
            if (isIPAddress(ip)) {
                return ip;
            }
            ip = req.getRemoteAddr();
            if (ip.indexOf('.') == -1) {
                ip = "127.0.0.1";
            }
            return ip;
        } catch (Exception e) {
            log.error("Error {} occurred when transform ip address.", e.getLocalizedMessage());
            return "";
        }
    }

    private static boolean isIPAddress(String addr) {
        if (StringUtils.isEmpty(addr))
            return false;
        String[] ips = StringUtils.split(addr, '.');
        if (ips.length != 4)
            return false;
        try {
            for(String ip : ips){
                Integer ipInt = Integer.parseInt(ip);
                if(ipInt < 0 || ipInt > 255){
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("It is not a ip address :{}", e);
        }
        return true;
    }
}
