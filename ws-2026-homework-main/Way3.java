import java.util.concurrent.atomic.AtomicReference;

// Homework way 3: with compare-and-set (an Atomic class, putIfAbsent or a VarHandle)
public class Way3 implements Claimer {

    private final AtomicReference<String> zpsWinner = new AtomicReference<>();

    // GIẢI THÍCH: Bản gốc sai vì kiểm tra (đọc) và ghi là hai bước tách rời. compareAndSet(null, who) gộp
    // chúng thành MỘT thao tác nguyên tử của CPU: "nếu giá trị đang là null thì đặt thành who, và cho tôi
    // biết có đặt được không". Nhiều thread cùng gọi thì phần cứng chỉ cho đúng một thread đổi null thành tên,
    // những thread còn lại nhận false. Đây chính là "first-write fence": lần ghi đầu tiên thành công đóng cửa,
    // nên không cần khoá hay hàng đợi mà vẫn chỉ có một người thắng.
    public boolean claim(String who) {
        Claimer.pause();            // giữ nguyên: dù chờ bao lâu, quyết định thắng/thua vẫn chỉ nằm ở CAS bên dưới
        return zpsWinner.compareAndSet(null, who);
    }

    public String winner() { return zpsWinner.get(); }
}