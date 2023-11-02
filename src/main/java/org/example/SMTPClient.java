package org.example;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.*;

import static java.lang.System.*;

public class SMTPClient {

    private static int smtpPort=465;
    private static String smtpServer;
    private static String id;
    private static String pw;
    private static String mailServer;

    public void makeSmtpHost(String ident){
        int index = ident.indexOf("@");
        mailServer = ident.substring(index+1);

        smtpServer = "smtp."+mailServer;
    }

    public static void sendMail() throws Exception{
        Scanner in = new Scanner(System.in);

        System.out.print("Mail Receiver: ");
        String receiver = in.nextLine();
        System.out.print("Mail Subject: ");
        String subject = in.nextLine();
        System.out.print("Mail Message: ");
        String message = in.nextLine();


        SSLSocketFactory sslSocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket sslsocket = (SSLSocket) sslSocketfactory.createSocket(smtpServer, smtpPort);

        OutputStream outToServer = sslsocket.getOutputStream();
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));

        String isConnect = inFromServer.readLine();
        if(isConnect.startsWith("220")){
            System.out.println();
            System.out.println("S> " + isConnect);
            System.out.println("SMTP Server connection Accept");
        }
        else{
            System.out.println("SMTP Server connection Failed");
            sslsocket.close();
            exit(1);
        }

        // 1. EHLO
        String ehlo = "EHLO "+mailServer+"\r\n";
        outToServer.write(ehlo.getBytes());

        do{
            isConnect = inFromServer.readLine();
            if (!isConnect.startsWith("250")){
                sslsocket.close();
                System.out.println();
                System.out.println("S> " + isConnect);
                System.out.println("EHLO FAILED");
            }
        } while (isConnect.contains("-"));
        System.out.println();
        System.out.println("S> " + isConnect);


        // 2. AUTH LOGIN
        String auth = "AUTH LOGIN\r\n";
        System.out.print(auth);
        outToServer.write(auth.getBytes());

        isConnect = inFromServer.readLine();
        if(!isConnect.startsWith("334")){
            sslsocket.close();
            System.out.println();
            System.out.println("S> " + isConnect);
            System.out.println("AUTH Failed");
        }
        else{
            System.out.println();
            System.out.println("S> " + isConnect);
            System.out.println("AUTH Accept");
        }


        // 3. 메일 계정 입력
        String mailId = DatatypeConverter.printBase64Binary(id.getBytes())+"\r\n";
        outToServer.write(mailId.getBytes());

        isConnect = inFromServer.readLine();
        if(!isConnect.startsWith("334")){
            sslsocket.close();
            System.out.println();
            System.out.println("S> " + isConnect);
            System.out.println("ID Failed");
        }
        else{
            System.out.println();
            System.out.println("S> " + isConnect);
            System.out.println("ID Accept");
        }


        // 4. 메일 비밀번호 입력
        String mailPw = DatatypeConverter.printBase64Binary(pw.getBytes())+"\r\n";
        outToServer.write(mailPw.getBytes());

        isConnect=inFromServer.readLine();
        if(!isConnect.startsWith("235")){
            sslsocket.close();
            System.out.println();
            System.out.println("S> " + isConnect);
            System.out.println("PW Failed");
        }
        else{
            System.out.println();
            System.out.println("S> " + isConnect);
            System.out.println("PW Accept");
        }

        // 5. MAIL FROM
        String send = "MAIL FROM: <"+id+">\r\n";
        outToServer.write(send.getBytes());

        isConnect = inFromServer.readLine();
        if(!isConnect.startsWith("250")){
            sslsocket.close();
            System.out.println();
            System.out.println("S> " + isConnect);
            System.out.println("MAIL FROM Failed");
        }
        else{
            System.out.println();
            System.out.println("S> " + isConnect);
            System.out.println("MAIL FROM Accept");
        }


        // 5. RCPT TO
        String receive = "RCPT TO:<"+receiver+">\r\n";
        outToServer.write(receive.getBytes());

        isConnect = inFromServer.readLine();
        if(!isConnect.startsWith("250")){
            sslsocket.close();
            System.out.println();
            System.out.println("S> " + isConnect);
            System.out.println("RCPT TO Failed");
        }
        else{
            System.out.println();
            System.out.println("S> " + isConnect);
            System.out.println("RCPT TO Accept");
        }


        String data = "DATA\r\n";
        outToServer.write(data.getBytes());

        isConnect = inFromServer.readLine();
        if(!isConnect.startsWith("354")){
            sslsocket.close();
            System.out.println();
            System.out.println("S> " + isConnect);
            System.out.print("DATA Failed");
        }
        else{
            System.out.println();
            System.out.println("S> " + isConnect);
            System.out.println("DATA Accept");
        }


        String mailTo = "to:<"+receiver+">\r\n";
        String mailFrom = "from:<"+id+">\r\n";
        String mailSub = "subject: "+subject+"\r\n";
        String temp = "\n";
        message+="\r\n";
        String end = ".\r\n";


        outToServer.write(mailTo.getBytes());
        outToServer.write(mailFrom.getBytes());
        outToServer.write(mailSub.getBytes());
        outToServer.write(temp.getBytes());
        outToServer.write(message.getBytes());
        outToServer.write(end.getBytes());

        isConnect = inFromServer.readLine();
        if(!isConnect.startsWith("250")){
            sslsocket.close();
            System.out.println();
            System.out.println("S> " + isConnect);
            System.out.println("Send Mail Failed");
        }
        else{
            System.out.println();
            System.out.println("S> " + isConnect);
            System.out.println("Send Mail Success");
        }


        String quit = "QUIT\r\n";
        outToServer.write(quit.getBytes());

        isConnect = inFromServer.readLine();
        if(!isConnect.startsWith("221")){
            sslsocket.close();
            System.out.println();
            System.out.println("S> " + isConnect);
            System.out.println("QUIT Failed");
        }
        else{
            System.out.println();
            System.out.println("S> " + isConnect);
            System.out.println("QUIT Accept");
        }

        sslsocket.close();
    }


    public static void main(String argc[]) throws Exception{
        SMTPClient client = new SMTPClient();

        Scanner in = new Scanner(System.in);

        System.out.print("id: ");
        client.id = in.next();
        System.out.print("pw: ");
        client.pw = in.next();

        client.makeSmtpHost(client.id.toString());

        client.sendMail();
    }
}