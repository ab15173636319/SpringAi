package ai.s_ai.verification;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import ai.s_ai.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class DeviceIdInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String deviceId = request.getHeader("deviceId");
        if (deviceId == null || deviceId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(objectMapper.writeValueAsString(ResultUtil.fail("非法访问")));
            return false;
        }
        return true;
    }

}
