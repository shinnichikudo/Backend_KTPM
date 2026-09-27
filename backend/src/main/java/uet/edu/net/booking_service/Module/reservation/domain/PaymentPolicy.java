package uet.edu.net.booking_service.Module.reservation.domain;

public class PaymentPolicy {
    public boolean isValidLuhn (String cardNumber) {
        cardNumber = cardNumber.replaceAll("\\s+", ""); // Loai bo khoang trang
        if (!cardNumber.matches("\\d+")) {
            return false; // Chi cho phep so
        }
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0;i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1; // Cong don cac chu so
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;

    }
}