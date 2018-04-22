/*
 * Zoo Database authorization and authentication system.  Gives a user three 
 * attempts to log in with proper credentials, otherwise the user is kicked from
 * the program. Then displays only the information appropriate for that specific
 * users role in the zoo.
 */
package zoodb;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.*;

public class ZooDB {
    
    public static void main(String[] args) throws IOException {
        Scanner getInput         = new Scanner(System.in);
        String userInput                             = "";
        UserInfo[] userList;
        UserInfo userData;
        
        //WELCOME to ZooDB [aesthetics]
        welcomeScreen();
        
        while(true) {
            //PARSE -> (organize credentials.txt information into an array of userInfo objects.)
            userList = parseData();
            //VALIDATE & DISPLAY -> (gets user entries, compares to credential list and displays data)
            userData = checkCredentials(userList); //authentication
            if (!userData.getUserRole().equals("")) //check to continue
            {
            displayRoleData(userData); //authorization and display
            }
        }
    }
    
    public static UserInfo[] parseData()throws IOException{
        int VALID_CREDS                              = 7; // !!!-- CHANGE NUMBER HERE IF CREDENTIALS.TXT IS MODIFIED --!!!
        UserInfo[] userList  = new UserInfo[VALID_CREDS]; 
        FileInputStream credentialsFile           = null;
        Scanner credentialsData                   = null;
        String userName                             = "";
        String userPass                             = "";
        String userRole                             = "";
        int i                                        = 0; //iterable for userList array
        
        //import file
        credentialsFile = new FileInputStream("../credentials.txt");
        credentialsData = new Scanner(credentialsFile);
        
        while (credentialsData.hasNext()){  //continue through entire file
            userName = credentialsData.next(); //username 
            credentialsData.next(); //skip MD5
            credentialsData.useDelimiter("\""); //set delimter to " to allow for spaces in password.
            credentialsData.next(); //skip over first "
            userPass = credentialsData.next(); //password
            credentialsData.reset(); //delimiter back to spaces
            credentialsData.next(); //skip over first space from reset
            userRole = credentialsData.next(); //role
            
            userList[i] = new UserInfo();  //create new userinfo object for [i] array location
            userList[i].setUserData(userName, userPass, userRole); //update object w/ obtained information
            i++; 
        }
        return userList;
    }
    
    public static UserInfo getUserCredentials(UserInfo userCredentials, String screenText){
        Scanner scnr             = new Scanner(System.in);
        String userName                              = "";
        String userPass                              = "";
        JTextField userField          = new JTextField();
        JTextField passField          = new JTextField();
        JPanel loginPanel =                  new JPanel();
        int loginPress                                = 0;
        
        //setup login screen
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS)); //formatting
        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(userField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passField);

        // login 'screen' - [aesthetics]
        loginPress = JOptionPane.showConfirmDialog(null, loginPanel, 
                      screenText, JOptionPane.OK_CANCEL_OPTION);
        
        if (loginPress == JOptionPane.CANCEL_OPTION) { //quit if button pressed
            userCredentials.setUserData("back_to_welcome", "", "");
        }
        if (loginPress == JOptionPane.CLOSED_OPTION){ //closed
            System.exit(0);
        }
        if (loginPress == JOptionPane.OK_OPTION) {   //update user entered information
            userName = userField.getText(); 
            userPass = passField.getText(); 
            
        userCredentials.setUserData(userName, userPass, "none"); //update userCred object w/ entries - (role is none still)
        }
        return userCredentials;
    }
    
    public static UserInfo checkCredentials(UserInfo[] userList){
        UserInfo userCredentials         = new UserInfo();
        String screenText     = "Enter your credentials.";
        int invalidTries                              = 0;
        int i                                         = 0; //iterable
        boolean keepGoing                          = true;
        UserInfo userData; //will store verified user credentials
        
        keepGoing = true;
        while(keepGoing){
            //Gets user input and compares to array of UserInfo objects to check validity.
            userCredentials = getUserCredentials(userCredentials, screenText); 
            for (i = 0; i < userList.length; i++){
                if (userCredentials.getUserName().equals(userList[i].getUserName())){  //username check
                    if (userCredentials.getUserPass().equals(userList[i].getUserPass())){ //password check
                        userData = userList[i];    //sets userData to verified credential object in array
                        userCredentials.setUserData("", "", ""); //CLEAR userCredentials to use if logout choosen later
                        keepGoing = false;
                        return userData;
                    }
                }
            }
            if (userCredentials.getUserName().equals("back_to_welcome")) { //return to welcome if cancel clicked
                keepGoing = false;
                welcomeScreen();
            }
            else {
                invalidTries ++; //if here -> attempt invalid.
                screenText = "Invalid Entry.. Try again."; //Invalid [aesthetics]
                if (invalidTries == 3){ //quit if 3 fails
                    quitNow();
                }
            }
        }
        return userCredentials;
    }
    
    public static void displayRoleData(UserInfo userData) throws IOException{
        FileInputStream roleFile  = null;
        Scanner roleData          = null;
        
        roleFile = new FileInputStream("../" + userData.getUserRole() + ".txt"); //import based on user role
        roleData = new Scanner(roleFile);
        roleData.useDelimiter("\\Z");  //set .next() to pick up entire file string
        
        //PRINT Authorized Data
        displayScreen(roleData.next(), userData);
        roleData.close();
    }
    
    public static void quitProgram(){ //quitting with nice goodbye message
        final JDialog dialog = new JDialog();
        dialog.setAlwaysOnTop(true); //so window pops to top of screen

        JOptionPane.showMessageDialog(dialog, "Goodbye!",
                                              "ZooDB", JOptionPane.WARNING_MESSAGE);
        System.exit(0);
        
    }
    
    public static void quitNow(){  //quits with mean goodbye message
        final JDialog dialog = new JDialog();
        dialog.setAlwaysOnTop(true); //so window pops to top of screen

        JOptionPane.showMessageDialog(dialog, "Too many invalid attempts.. Shutting down!",
                                              "ZooDB", JOptionPane.WARNING_MESSAGE);
        System.exit(0);
    }
    
    public static void welcomeScreen(){ 
        final JDialog welcomeDialog          = new JDialog();
        int buttonPress                                  = 0;
        String[] options =  new String[]{"Continue", "Quit"};
        String welcomeText      = "         ZooDB - Welcome"; //spaces for formatting
        
        //welcome GUI [aestetics]
        buttonPress = JOptionPane.showOptionDialog(welcomeDialog, welcomeText,"ZooDB",
                     JOptionPane.YES_NO_OPTION, JOptionPane.OK_CANCEL_OPTION,
                     null, options, options[0]);
        
        if (buttonPress == JOptionPane.NO_OPTION) {//quit if button pressed
            quitProgram();
        }
        if (buttonPress == JOptionPane.CLOSED_OPTION){ //closed
            System.exit(0);
        }
    }
    
    public static void displayScreen(String roleData, UserInfo userData){
        final JDialog welcomeDialog          = new JDialog();
        int buttonPress                                  = 0;
        String[] options    = new String[]{"Logout", "Quit"};
        
        //displaying data GUI [aestetics]
        buttonPress = JOptionPane.showOptionDialog(welcomeDialog, roleData,"ZooDB",
                     JOptionPane.YES_NO_OPTION, JOptionPane.OK_CANCEL_OPTION,
                     null, options, options[0]);
        
        if (buttonPress == JOptionPane.NO_OPTION) { //quit if button pressed
            quitProgram();
        }
        if (buttonPress == JOptionPane.CLOSED_OPTION) { //closed
            System.exit(0);
        }
        if (buttonPress == JOptionPane.YES_OPTION){ //back to welcome
            userData.setUserData("", "", ""); //reset userData for next login 
        }
    }
}