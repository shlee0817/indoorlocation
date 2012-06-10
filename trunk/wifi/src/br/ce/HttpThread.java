package br.ce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class HttpThread extends Thread{

	Handler handler;
	String url;
	String nome;
	String local;
	
	public HttpThread(Handler h, String url, String nome, String local)
	{
		this.handler = h;
		this.url = url;
		this.nome = nome;
		this.local = local;
	}
	
	@Override
	public void run() {
		
HttpClient httpClient = new DefaultHttpClient();
		
		url = url.concat("&nome=" + nome + "&local=" + local);
		
		HttpGet httpGet = new HttpGet(url);

		HttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
			e.toString();
		}

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		} catch (IllegalStateException e) {
			Log.d("abili", e.toString());
		} catch (IOException e) {
			Log.d("abili", e.toString());
		}
		
		//	Ler dados da resposta
		
		StringBuffer sb = new StringBuffer("");

		String line = "";

		String NL = System.getProperty("line.separator");

		try {
			while ((line = in.readLine()) != null) {

				sb.append(line + NL);

			}
		} catch (IOException e) {e.printStackTrace();}

		try {
			in.close();
		} catch (IOException e) {e.printStackTrace();}
		
		String page = sb.toString();
		
		Message m = handler.obtainMessage();
		Bundle b = new Bundle();
		b.putString("response", page);
		
		m.setData(b);
		
		handler.sendMessage(m);
		
	}
}
