/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcp.conference.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import static tcp.conference.chat.TCPConferenceChat.counter;

/**
 *
 * @author shubham
 */
public class TCPConferenceChat {
 static int counter=0;
 static ArrayList outputstreams=new ArrayList();
    /**
     * @param args the command line arguments
     */
    
    Scanner sc=new Scanner(System.in);
    // initialize socket and input output streams
    private Socket socket = null;
    private DataInputStream  input = null;
    private DataInputStream in   =  null;
    private DataOutputStream out = null;
    
    public static void main(String[] args) {
        // TODO code application logic here      
    try{
      ServerSocket server=new ServerSocket(8000);
      System.out.println("Server Started ....");
      while(true){
        counter++;
        Socket serverClient=server.accept();  //server accept the client connection request
        System.out.println(" >> " + "Client No:" + counter + " started!");
        ServerClientThread sct = new ServerClientThread(serverClient,counter); //send  the request to a separate thread
        sct.start();
      }
    }catch(Exception e){
      System.out.println(e);
    }
   }
    public static void writeoutput(String message,int clientno) throws IOException{
        //Send message to all the clients
        for(int i=0;i<counter;i++){
            if(i!=clientno-1){
                try{
                    DataOutputStream outa=(DataOutputStream)outputstreams.get(i);
                    outa.writeUTF(message);
                }catch(Exception e){
                    System.out.println("Client"+(i+1)+" is not available/ disconnected");
                }
                    
            }
        }
    }
}

 
class ServerClientThread extends Thread {
  Socket serverClient;
  int clientNo;
  int squre;
  
  ServerClientThread(Socket inSocket,int counter){
    serverClient = inSocket;
    clientNo=counter;
  }
  
  public void run(){
    try{
      DataInputStream inStream = new DataInputStream(serverClient.getInputStream());
      DataOutputStream outStream = new DataOutputStream(serverClient.getOutputStream());
      TCPConferenceChat.outputstreams.add(outStream);
      System.out.println("Counter:- "+clientNo);
      outStream.writeInt(clientNo);
      outStream.flush();
      System.out.println("Succcesss");
      String clientMessage="", serverMessage="";
      while(!clientMessage.equals("bye")){
        clientMessage=inStream.readUTF();
        System.out.println("From Client-" +clientNo+ ": Message is :"+clientMessage);
//        squre = Integer.parseInt(clientMessage) * Integer.parseInt(clientMessage);
        serverMessage="Client " + clientNo + ":-  " + clientMessage ;
//        outStream.writeUTF(serverMessage);
        TCPConferenceChat.writeoutput(serverMessage,clientNo);
        outStream.flush();
      }
      inStream.close();
      outStream.close();
      serverClient.close();
    }catch(Exception ex){
      System.out.println("Exception:- "+ex);
    }finally{
      System.out.println("Client -" + clientNo + " exit!! ");
      TCPConferenceChat.outputstreams.remove(clientNo-1);
    }
  }
}
