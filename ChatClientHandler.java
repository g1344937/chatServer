/*
  学生証番号:344937
  氏名:中澤和寿
 */

import java.io.*;
import java.net.*;
import java.util.*;

class ChatClientHandler extends Thread{
    Socket socket; // クライアントを表すソケット
    BufferedReader in;
    BufferedWriter out;
    List clients;
    String name;

    /*コンストラクタ*/
    ChatClientHandler(Socket socket, List clients){
	this.socket = socket;
	this.clients = clients;
	this.name = "undifined" + (clients.size() + 1);
    }
    /*runメソッド*/
    public void run(){
	try{
	    open();
	    while(true){
		String message = receive();
		String[] commands = message.split(" ");
		/*POSTコマンド*/
		if(commands[0].equalsIgnoreCase("post")){
		    post(commands[1]);
		}/*BYEコマンド*/
		else if(commands[0].equalsIgnoreCase("bye")){
		    bye();
		    break;/*接続を閉じに行く*/
		}/*HELPコマンド*/
		else if(commands[0].equalsIgnoreCase("help")){
		    help();
		}
		/*WHOAMIコマンド*/
		else if(commands[0].equalsIgnoreCase("whoami")){
		    whoami();
		}
		/*NAMEコマンド*/
		else if(commands[0].equalsIgnoreCase("name")){
		    name(commands[1]);
		}
		/*USERSコマンド*/
		else if(commands[0].equalsIgnoreCase("users")){
		    users();
		}
		/*空文を入力した場合は接続を閉じる*/
		if(message.equals("")){
		    break;
		}
	    }
	}catch(IOException e){
	    e.printStackTrace();
	}finally{
	    close();
	}
    }

    /*getメソッド*/
    public String getClientsName(){//このクライアントの名前を返す
	return this.name;
    }

    /*コマンドで呼ぶメソッド*/
    public void post(String message) throws IOException{/*他のクライアントに
							  メッセージを投稿する*/
	List names = new ArrayList();
	for(int i=0; i<clients.size(); i++){
	    ChatClientHandler handler = (ChatClientHandler)clients.get(i);
	    if(handler != this){//相手側に自分の名前とメッセージを表示させる
		names.add(handler.getClientsName());
		handler.send("["+ this.name +"]"+ message);
	    }
	}
	/*自分側に送った相手の名前を表示する*/
	Collections.sort(names);
	String returnMessage = "";
	for(int i = 0; i < names.size(); i++){
	    returnMessage = returnMessage + names.get(i) + ",";
	}
	this.send(returnMessage);
    }

    public void bye() throws IOException{//サーバから切断する
	send("bye " + this.name + "");//切断することを表示
    }

    public void help() throws IOException{//コマンドの一覧を表示する
	send("help, name, whoami, users, bye, post");
    }

    public void whoami() throws IOException{//このクライアントの名前を表示
	send(this.name);
    }

    public void name(String newName) throws IOException{/*このクライアントの
							  名前を変える*/
	/*他のクライアントを一覧にし、その名前を確認する*/
	List names = new ArrayList();
	for(int i=0; i<clients.size(); i++){
	    ChatClientHandler handler = (ChatClientHandler)clients.get(i);
	    if(handler != this){
		names.add(handler.getClientsName());
	    }
	}
	Collections.sort(names);
	for(int i = 0; i < names.size(); i++){
	    if(newName.equals(names.get(i))){//もし名前が使われていたら
		send("この名前は使われています");//エラー文を出し
		return;//このメソッドを終了
	    }
	}
	this.name = newName;//使われていない名前なら、その名前をつける
    }

    public void users() throws IOException{//参加している人の名前を表示する
	List names = new ArrayList();
	for(int i=0; i<clients.size(); i++){
	    ChatClientHandler handler = (ChatClientHandler)clients.get(i);
	    names.add(handler.getClientsName());
	}
	Collections.sort(names);
	String returnMessage = "";
	for(int i = 0; i < names.size(); i++){
	    returnMessage = returnMessage + names.get(i) + ",";
	}
	this.send(returnMessage);
    }

    /*通常のメソッド*/
    void open() throws IOException{/* クライアントとのデータのやり取りを行う	
				      ストリームを開く */
	/*クライアントから送られたデータの出力*/
	InputStream socketIn = socket.getInputStream();// 入力ストリーム取得
	in = new BufferedReader(new InputStreamReader(socketIn));
	/*クライアントにデータを送り返す*/
	OutputStream socketOut = socket.getOutputStream();//出力ストリーム取得
	out = new BufferedWriter(new OutputStreamWriter(socketOut));
	/*
	  InputStreamReaderでバイナリデータストリームを文字ストリーム(Reader)
	  に変換
	  Bufferedでラップすることで行ごとに読めるようになる
	  出力ストリームも同様に変換してる
	*/
    }
    String receive() throws IOException{//クライアントからデータを受け取る
	String message = in.readLine();
	System.out.println(message);// 読み込んだ文字をこちら側に表示
	return message;
    }
    void send(String message) throws IOException{//クライアントにデータを送信する
	out.write(message);// 相手側に書き出す
	out.write("\r\n");/* 相手側のカーソルを改行(ネットワークの改行コードは
			     \r\nである */
	out.flush(); // writeの後に必要
    }
    void close(){//クライアントとの接続を閉じる
	if(in != null){
	    try{
		in.close();
	    }catch(IOException e){
	    }
	}
	if(out != null){
	    try{
		out.close();
	    }catch(IOException e){
	    }
	}
	if(socket != null){
	    try{
		socket.close();
	    }catch(IOException e){
	    }
	}
    }
}