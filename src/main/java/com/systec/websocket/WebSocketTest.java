package com.systec.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;

/**
 * Created by wuhuan on 2017/7/26.
 */
public class WebSocketTest extends WebSocketServer {

    public WebSocketTest(InetSocketAddress address, int decodercount, List<Draft> drafts) {
        super(address, decodercount, drafts);
    }

    public WebSocketTest(InetSocketAddress address) {
        this(address, DECODERS, (List)null);
    }

    public static void main(String[] args) {
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8090);
        WebSocketTest server = new WebSocketTest(address);
        server.start();
        try {
            String ip = server.getAddress().getHostName();
            int port = server.getPort();
            print(String.format("服务已启动: %s:%d", ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }

        InputStreamReader in = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(in);

        while (true) {
            try {
                String msg = reader.readLine();
                server.broadcastMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        String address = webSocket.getRemoteSocketAddress().getAddress().getHostAddress();
        String message = String.format("(%s) <加入>", address);
        broadcastMessage(message);
        print(message);

    }

    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        String address = webSocket.getRemoteSocketAddress().getAddress().getHostAddress();
        String message = String.format("(%s) <离开>", address);
        broadcastMessage(message);
        print(message);
    }

    public void onMessage(WebSocket webSocket, String msg) {
        String address = webSocket.getRemoteSocketAddress().getAddress().getHostAddress();
        String message = String.format("(%s) %s", address, msg);
        broadcastMessage(message);
        print(message);
    }

    public void onError(WebSocket webSocket, Exception e) {
        if (null != webSocket) {
            if (!webSocket.isClosed()) {
                webSocket.close(0);
            }
        }
        e.printStackTrace();
    }

    public void onStart() {
    }

    /**
     * 广播收到消息
     *
     * @param msg
     */
    private void broadcastMessage(String msg) {
        Collection<WebSocket> connections = connections();
        synchronized (connections) {
            for (WebSocket client : connections) {
                client.send(msg);
            }
        }
    }

    private static void print(String msg) {
        System.out.println(String.format("[%d] %s", System.currentTimeMillis(), msg));
    }
}
