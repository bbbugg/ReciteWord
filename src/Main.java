public class Main {
    public static void main(String[] args) {
        System.out.println("查询用户单词成功!\n\t未选\t未背\t认识\t不认识\nCET4:"+3+"\t"+3+"\t"+3425+"\t"+989);

    }

    public static boolean isLetterDigit(String str) {
        String regex = "^[a-z0-9A-Z]+$";
        return str.matches(regex);
    }
}