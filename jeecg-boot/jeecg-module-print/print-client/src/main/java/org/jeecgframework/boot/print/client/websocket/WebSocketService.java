package org.jeecgframework.boot.print.client.websocket;//package org.jeecgframework.boot.printerclient.websocket;
//
//import cn.hutool.json.JSONUtil;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.stereotype.Service;
//import org.springframework.web.socket.*;
//import org.springframework.web.socket.client.WebSocketConnectionManager;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.io.IOException;
//
//@Service
//public class WebSocketService implements InitializingBean {
//
//
//
//    MyWebSocketHandler myWebSocketHandler;
//
//    String uri;
//
//
//    public void sendMessage(String message) throws IOException {
//        myWebSocketHandler.session.sendMessage(new TextMessage(message));
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        connect(uri);
//    }
//
//    private void connect(String uri) {
//        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
//        myWebSocketHandler = new MyWebSocketHandler();
//        WebSocketConnectionManager webSocketConnectionManager = new WebSocketConnectionManager(standardWebSocketClient, myWebSocketHandler, uri);
//        webSocketConnectionManager.start();
//    }
//
//    public static class MyWebSocketHandler extends TextWebSocketHandler {
//
//        private WebSocketSession session;
//        @Override
//        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//            this.session = session;
//            super.afterConnectionEstablished(session);
//        }
//
//        @Override
//        protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
//            //启用禁用打印机
//            String payload = textMessage.getPayload();
//            Message message = JSONUtil.toBean(payload, Message.class);
//            if (message.getType() == )
//        }
//
//        @Override
//        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
//            super.handleTransportError(session, exception);
//        }
//
//        @Override
//        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//            super.afterConnectionClosed(session, status);
//        }
//    }
//
//
//
//
//}
