/*
 * Object which holds information on a user
 * Includes -> USERNAME, PASSWORD, and ROLE
 */
package zoodb;

public class UserInfo {
    String userName;
    String userPass;
    String userRole;

    public UserInfo(){ //initialize as empty
        userName = "";
        userPass = "";
        userRole = "";
    }
    
       //update class object variables with given input
    public void setUserData(String name, String pass, String role){
        this.userName = name;
        this.userPass = pass;
        this.userRole = role;
    }
    
    public String getUserName(){ //return username 
        return this.userName;
    }
    
    public String getUserPass(){ //return password
        return this.userPass;
    }
    
    public String getUserRole(){ //return role
        return this.userRole;
    }
}
