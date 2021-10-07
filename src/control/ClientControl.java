/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import model.IPAddress;
import model.ObjectWrapper;
import view.LoginFrm;
import view.SignUp;

/**
 *
 * @author MSI GF63
 */
public class ClientControl {

    private Socket mysocket;
    private ClientListening myListening;
    private ArrayList<ObjectWrapper> myfunction;
    private IPAddress serverAdress = new IPAddress("localhost", 8888);

    public ClientControl(LoginFrm lgfrm) {
      
        myfunction = new ArrayList<>();
    }

    public ClientControl(LoginFrm lgfrm, IPAddress serverAddress) {
        
        this.serverAdress = serverAddress;
        myfunction = new ArrayList<>();
    }

    public ArrayList<ObjectWrapper> getMyfunction() {
        return myfunction;
    }
    
     public boolean openConnection(){        
        try {
            mysocket = new Socket(serverAdress.getHost(), serverAdress.getPort());  
            myListening = new ClientListening();
            myListening.start();
          
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
     
     
      public boolean closeConnection(){
         try {
             if(myListening != null)
                 myListening.stop();
             if(mysocket !=null) {
                 mysocket.close();
             }
            myfunction.clear();             
         } catch (Exception e) {
             e.printStackTrace();
             return false;
         }
         return true;
    }
     
    public boolean sendData(Object obj){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(mysocket.getOutputStream());
            oos.writeObject(obj);           
             
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    

    class ClientListening extends Thread {

        public ClientListening() {
        }

        public void run() {
            try {
                while (true) {
                    ObjectInputStream ois = new ObjectInputStream(mysocket.getInputStream());
                    Object obj = ois.readObject();
                    if (obj instanceof ObjectWrapper) {
                        ObjectWrapper data = (ObjectWrapper) obj;
                        for (ObjectWrapper fto : myfunction) {
                            if (fto.getPerformative() == data.getPerformative()) {
                                switch (data.getPerformative()) {
                                    case ObjectWrapper.REPLY_LOGIN_USER -> {
                                        LoginFrm lgfrm = (LoginFrm) fto.getData();
                                        lgfrm.receivedDataProcessing(data);
                                    }
                                    case ObjectWrapper.REPLY_SIGNUP_USER -> {
                                        SignUp sgufrm = (SignUp)fto.getData();
                                        sgufrm.receivedDataProcessing(data);
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
               
            }
        }

    }

}
