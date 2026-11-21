// Homework way 1: with a lock (synchronized, a Lock or a Semaphore)
public class Way1 implements Claimer {

    private String zpsWinner;

    // GIẢI THÍCH: Bản gốc sai vì "đọc -> chờ -> ghi" không phải một khối liền: nhiều thread cùng đọc
    // được null trong lúc chờ nên đều thắng. Với synchronized, tại một thời điểm chỉ một thread được
    // vào claim(); các thread khác phải đợi. Thread vào đầu tiên đọc null, ghi tên mình, rồi mới nhả khoá.
    // Khoá này là "first-write fence": từ khi lần ghi đầu tiên xong, mọi thread vào sau đều thấy giá trị
    // đã có nên trả về false. Vậy kiểm tra và ghi trở thành một bước không thể chen ngang, chỉ có đúng 1 người thắng.
    public synchronized boolean claim(String who) {
        String seen = zpsWinner;
        Claimer.pause();            // giữ nguyên: bây giờ khoảng chờ nằm trong khoá nên không còn ai chen vào được
        if (seen != null) return false;
        zpsWinner = who;
        return true;
    }

    // synchronized cùng monitor để luôn đọc được giá trị mới nhất
    public synchronized String winner() { return zpsWinner; }
}