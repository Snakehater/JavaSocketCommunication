public class AccountObject {
    String username = "";
    String password = "";
    AccountObject(String username, String password){
        this.username = username;
        this.password = password;
    }
    @Override
    public boolean equals(Object v) {
        boolean retVal = false;

        if (v instanceof AccountObject) {
            AccountObject ptr = (AccountObject) v;
            retVal = ptr.username.equals(this.username) && ptr.password.equals(this.password);
        }

        System.out.println(retVal);

        return retVal;
    }
}
