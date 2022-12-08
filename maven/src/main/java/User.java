public class User {
    private static long nextId=1;
    private final long userId;
    private String username;
    private String password;
    private String name;
    private String phone;
    private int age;
    private int sex;

    {
        userId=nextId;
        nextId++;
    }
    public User(String username, String password, String name, String phone, int age, int sex) {
        this.username=username;
        this.password=password;
        this.name=name;
        this.phone=phone;
        this.age=age;
        this.sex=sex;
    }
    public void ChangeUser(String username, String password, String name, String phone, int age, int sex){
        this.username=username;
        this.password=password;
        this.name=name;
        this.phone=phone;
        this.age=age;
        this.sex=sex;
    }
    public long GetUserId(){
        return this.userId;
    }
}

