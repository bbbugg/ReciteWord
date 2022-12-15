public class User {
    private static long nextId=1;
    private final long userId;
    private String username;
    private String userpassword;
    private String name;
    private String phone;
    private String age;
    private String sex;

    {
        userId=nextId;
        nextId++;
    }
    public User(String username, String password, String name, String phone, String age, String sex) {
        this.username=username;
        this.userpassword =password;
        this.name=name;
        this.phone=phone;
        this.age=age;
        this.sex=sex;
    }
    public void ChangeUser(String username, String password, String name, String phone, String age, String sex){
        this.username=username;
        this.userpassword =password;
        this.name=name;
        this.phone=phone;
        this.age=age;
        this.sex=sex;
    }
    public long GetUserId(){
        return this.userId;
    }
}

