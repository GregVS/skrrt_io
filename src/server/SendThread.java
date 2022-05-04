package server;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Gregory on 8/13/17.
 */
public class SendThread extends Thread {

    public static LinkedBlockingQueue<MsgPair> messages = new LinkedBlockingQueue<>();

    public void run() {
        while(true) {
            try {
                MsgPair msg = messages.take();
                if(msg == null || !msg.getConn().isOpen()) continue;
                msg.getConn().send(msg.getMsg());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
