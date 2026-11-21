import java.util.concurrent.*;

// Homework way 2: with a queue (or a single-thread executor)
public class Way2 implements Claimer {

    // Chỉ một thread duy nhất (daemon để JVM thoát được) sở hữu cuộc đua
    private final ExecutorService owner = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "claim-owner");
        t.setDaemon(true);
        return t;
    });

    private volatile String zpsWinner;   // chỉ thread owner ghi; volatile để winner() đọc từ thread khác vẫn thấy

    // GIẢI THÍCH: Mọi lời gọi claim() chỉ đẩy một tác vụ vào hàng đợi của executor một thread, rồi chờ
    // kết quả bằng Future.get(). Executor chạy từng tác vụ lần lượt, xong tác vụ này mới tới tác vụ kế tiếp,
    // nên "đọc -> chờ -> ghi" của hai thread không bao giờ xen kẽ nhau. Tác vụ đầu tiên trong hàng đợi thấy null
    // và ghi tên; đó là "first-write fence": các tác vụ sau đều thấy tên đã có và trả về false.
    // Thread gọi vẫn nhận đúng đáp án của mình vì get() trả về kết quả của chính tác vụ nó đã gửi.
    public boolean claim(String who) {
        Future<Boolean> result = owner.submit(() -> {
            String seen = zpsWinner;
            Claimer.pause();        // giữ nguyên: chờ ở trong thread duy nhất nên không ai chen vào được
            if (seen != null) return false;
            zpsWinner = who;
            return true;
        });
        try {
            return result.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    public String winner() { return zpsWinner; }
}