package com.jotangi.tomsworld.util;

import java.io.IOException;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
/**
 * Created by carolyn on 2017/11/9.
 */

public class SocketMsgSender {
    public static String SendMsg(String requestMSG , String socketIP ,int socketPort)throws UnknownHostException, IOException,Exception
    {
//		Log.d("SocketMsgSender","new socketing");
        String callbackMSG ="";
        InetSocketAddress  iisa;
        requestMSG = strBase64.base64StrEn(requestMSG);
        Socket socket=null;
//		Log.d("socket","new socketing");
        socket=new Socket();
        iisa = new InetSocketAddress(socketIP, socketPort);
        //Log.d("iisa","new socketing");
        //try {
        socket.connect(iisa, 5000);
        if(socket.isConnected()){
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())), true);
            out.println(requestMSG);
            //Log.d("out.println","sucess");

            // receive response
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            //String response = br.readLine();
            //Log.d("BufferedReader","sucess");
            String text = "";
            String response = "";
            while ((text = br.readLine()) != null) {
                response += text;
                //Log.d("BufferedReader+text",text);
            }

            //Log.d("response",response);
            callbackMSG = strBase64.base64StrDe(response);

            //Log.d("callbackMSG",callbackMSG);
        }


        else{
            throw new Exception();
//	    			Toast t = Toast.makeText(this , "指令無回應，請檢查網路或設定!", Toast.LENGTH_SHORT);
//	    			t.show();
        }
        //}
//		catch (IOException e) {
//			throw new IOException();
//		}catch (Exception e){
//			throw new Exception();
//		}

        return callbackMSG;
    }
}
