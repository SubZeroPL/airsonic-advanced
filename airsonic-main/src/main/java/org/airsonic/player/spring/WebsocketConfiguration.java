package org.airsonic.player.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {
    public static final String UNDERLYING_SERVLET_REQUEST = "servletRequest";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");

        // this ensures publish order is serial at the cost of no parallelization and
        // performance - if performance is bad, this should be turned off
        config.setPreservePublishOrder(true);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/airsonic").addInterceptors(new ServletRequestCaptureHandshakeInterceptor()).withSockJS();
    }

    public static class ServletRequestCaptureHandshakeInterceptor implements HandshakeInterceptor {
        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

            // Set ip attribute to WebSocket session
            attributes.put("ip", request.getRemoteAddress());

            // Set servlet request attribute to WebSocket session
            if (request instanceof ServletServerHttpRequest) {
                ServletServerHttpRequest servletServerRequest = (ServletServerHttpRequest) request;
                HttpServletRequest servletRequest = servletServerRequest.getServletRequest();
                attributes.put(UNDERLYING_SERVLET_REQUEST, servletRequest);
            }

            return true;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                Exception exception) {
        }
    }
}