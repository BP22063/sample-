package SampleClient;

import com.google.gson.Gson;
import jakarta.websocket.*;

@ClientEndpoint
public class WebSocketEndpointSample {
    // 多分あまり行儀が良くない
    static Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("[client] onOpen" + session.getId());
    }

    @OnMessage
    public void onMessage(String message) {

        // 受信した生のメッセージ
        System.out.println("[client] onMessage: " + message);

        // 変換：String -> SampleMessage
        SampleMessage receivedMessage = gson.fromJson(message, SampleMessage.class);

        // 各要素を見てみる
        System.out.println("id:" + receivedMessage.id);
        System.out.println("password:" + receivedMessage.password);
        System.out.println("message:" + receivedMessage.message);

        // 変換：SampleMessage -> String
        //System.out.println(gson.toJson(receivedMessage));
    }

    @OnError
    public void onError(Throwable t) {
        System.out.println("[client] onError");
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("[client] onClose: " + session.getId());
    }
}
