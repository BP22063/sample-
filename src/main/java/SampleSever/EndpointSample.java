package SampleSever;

import com.google.gson.Gson;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/sample")
public class EndpointSample {
    private static Set<Session> establishedSessions
            = Collections.synchronizedSet(new HashSet<Session>());

    private int privateIncrementTest = 0;
    private static int staticIncrementTest = 0;

    static Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session, EndpointConfig ec) {
        establishedSessions.add(session);
        System.out.println("[WebSocketServerSample] onOpen:" + session.getId());
    }


    @OnMessage
    public void onMessage(final String message, final Session session) throws IOException {
        System.out.println("[WebSocketServerSample] onMessage from (session: " + session.getId() + ") msg: " + message);
        this.privateIncrementTest++;
        EndpointSample.staticIncrementTest++;

        // 変換：String -> SampleMessage
        SampleMessage receivedMessage = gson.fromJson(message, SampleMessage.class);
        // 変換：SampleMessage -> String
        // receivedMessageはSampleMessageのインスタンスなので
        // receivedMessage.id などとして要素にアクセス可能。
        System.out.println(gson.toJson(receivedMessage));
        // 特定のグループに対しての送信（この例だと全体）
        sendBroadcastMessage(message);
        // 送信してきた人に返信
        //sendMessage(session, message);
        System.out.println("[WebSocketServerSample]:privateIncrementTest:" + this.privateIncrementTest);
        System.out.println("[WebSocketServerSample]:staticInrementTest  :" + EndpointSample.staticIncrementTest);

    }


    @OnClose
    public void onClose(Session session) {
        System.out.println("[WebSocketServerSample] onClose:" + session.getId());
        establishedSessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("[WebSocketServerSample] onError:" + session.getId());
    }

    public void sendMessage(Session session, String message) {
        System.out.println("[WebSocketServerSample] sendMessage(): " + message);
        try {
            // 同期送信（sync）
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendBroadcastMessage(String message) {
        System.out.println("[WebSocketServerSample] sendBroadcastMessage(): " + message);
        establishedSessions.forEach(session -> {
            // 非同期送信（async）
            session.getAsyncRemote().sendText(message);
        });
    }
}
