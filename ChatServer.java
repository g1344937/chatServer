/*
  学生証番号:344937
  氏名:中澤和寿
 */

import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer{
    private ServerSocket server;
    private List clients = new ArrayList();//全ハンドラーを保持するリスト

    public void listen(){
	try{
	    int num = 1;// クライアントの番号
	    /*サーバの起動*/
	    server = new ServerSocket(18080);// ポートを指定してサーバを立てる
	    System.out.println("Chatサーバをポート18080で起動しました.");
	    while(true){
		Socket socket = server.accept();// クライアントの接続を待つ
		ChatClientHandler handler = new ChatClientHandler(socket, clients);
		clients.add(handler);
		System.out.println("クライアント"+ num +"が接続してきました.");
		num++;
		handler.start();/* startメソッドの呼び出しで、
				   runメソッドが平行して実行されて、
				   handlerに残りの処理を任せられる*/
		// handler.close()を呼び出さないようにする
	    }
	}catch(IOException e){// ネットワークは入出力があるのでIOExceptionが必要
	    e.printStackTrace();
	}
    }

    public static void main(String[] args){
	ChatServer chat = new ChatServer();
	chat.listen();
    }
}