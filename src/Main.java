public class Main {
    public static void main(String[] args) {
        for (int i = 0; i < 200; i++) {
            int num = (int) (Math.random() * 100)+1;
            System.out.println(num);
        }

    }

    public static boolean isLetterDigit(String str) {
        String regex = "^[a-z0-9A-Z]+$";
        return str.matches(regex);
    }
}