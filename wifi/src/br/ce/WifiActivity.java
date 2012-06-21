package br.ce;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WifiActivity extends Activity implements OnClickListener {
	public static WifiManager wifiManager;
	public static ScanResult scanResult;
	public static String SHARED_PREFERENCES = "wifi";
	public static String URLGenerate = "http://silobocarvalho.s156.eatj.com/IndoorLocation/main?acao=generate";
	public static String URLAdd = "http://silobocarvalho.s156.eatj.com/IndoorLocation/main?acao=add";
	DataBase dataBase;
	Editor editor;
	Button bt_cadastrarSinais;
	Button bt_zerarBanco;
	Button bt_localizar;
	Button bt_generate;
	Button bt_generateId;
	TextView et_local;
	TextView et_maisforte;
	
	
	
	static final int caseGenerateId = 1;
	Dialog dialogLogin;
	Long idApp;
	String nomeApp = "Indefinido";
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		TextView texto = (TextView) findViewById(R.id.texto);
		bt_cadastrarSinais = (Button) findViewById(R.id.bt_cadastrarSinais);
		bt_zerarBanco = (Button) findViewById(R.id.bt_zerarBanco);
		bt_localizar = (Button) findViewById(R.id.bt_localizar);
		bt_generate = (Button) findViewById(R.id.bt_generate);
		bt_generateId = (Button) findViewById(R.id.bt_generateId);
		et_local = (TextView) findViewById(R.id.et_local);
		et_maisforte = (TextView) findViewById(R.id.et_maisforte);

		bt_cadastrarSinais.setOnClickListener(this);
		bt_localizar.setOnClickListener(this);
		bt_zerarBanco.setOnClickListener(this);
		bt_generate.setOnClickListener(this);
		
		/*
		 * DADOS DA APLICAÇÃO
		 */

		SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES, 0);
		editor = preferences.edit();
		idApp = preferences.getLong("idApp", -1);
		
		
		
		dataBase = new DataBase(this);

		List<Localizacao> localizacoes = new ArrayList<Localizacao>();
		Log.d("TESTE", String.valueOf(wifiManager.getWifiState()));

		if (wifiManager.getWifiState() == wifiManager.WIFI_STATE_DISABLED) {
			texto.setText("ATIVE O WIFI");
			et_local.setEnabled(false);
			bt_cadastrarSinais.setEnabled(false);
			bt_localizar.setEnabled(false);
		}
	}

	@Override
	protected void onPause() {
		Log.d("abili", "entrou onpause, kill app");
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	
	final Handler handlerHttp = new Handler() {
		public void handleMessage(Message msg) {
			String response = msg.getData().getString("response");
			Log.d("pow", response);
			try{
			Long id = Long.parseLong(response.trim());
			editor.putLong("idApp", id);
			editor.commit();
			idApp = id;
			Log.d("appId", "Essa App possui um id: " + id);
			}catch(Exception e)
			{
				Log.d("ErroHTML", "A pagina devolvida pelo servidor é invalida");
			}
		}
	};
	
	
	
	public void onClick(View v) {

		if (v == bt_cadastrarSinais) {
			String nomeLocal = et_local.getText().toString();

			Log.d("Abili", "Cadastrar Sinais");
			Log.d("nomeLocal", nomeLocal);

			List<ScanResult> scanResults = wifiManager.getScanResults();

			for (ScanResult scanResult : scanResults) {
				Localizacao localizacao = new Localizacao();

				localizacao.setRede(scanResult.SSID);
				localizacao.setSinal(scanResult.level);
				localizacao.setLocalizacao(nomeLocal);

				dataBase.addLocalizacao(localizacao);
			}

		} else if (v == bt_localizar) {
			Log.d("Abili", "Botao Localizar");

			List<Localizacao> listLocalizacoesBd = dataBase.getAllLocalizacao();
			Localizacao local = new Localizacao();
			local.setRede(null);
			List<ScanResult> scanResults = wifiManager.getScanResults();
			
			getLocal(listLocalizacoesBd, scanResults);

			/*for (int i = 0; i < scanResults.size(); i++) {
				for (int j = 0; j < scanResults.size() - 1; j++) {
					if (WifiManager.compareSignalLevel(
							scanResults.get(j).level,
							scanResults.get(j + 1).level) < 0)
					// <0 o segundo é mais forte, >0 o primeiro é mais forte
					{
						ScanResult temp = scanResults.get(j);
						scanResults.set(j, scanResults.get(j + 1));
						scanResults.set(j + 1, temp);
					}
				}
			}
			

			for (int i = 0; i < scanResults.size(); i++)
			{
				for(int k=0; k<listLocalizacoesBd.size(); k++)
				{
					if(scanResults.get(i).SSID.equals(listLocalizacoesBd.get(k).getRede()))
					{
						local.setLocalizacao(listLocalizacoesBd.get(k).getLocalizacao());
						local.setRede(listLocalizacoesBd.get(k).getRede());
						local.setSinal(scanResults.get(i).level);
						break;
					}
				}
			}*/
			if(local.getRede() == null)
			{
				Log.d("Falhou", "Vc não pôde ser localizado");
			}
			et_maisforte.setText("Local: " + local.getLocalizacao() + " \nRede: " + local.getRede() + " \nSinal: " + local.getSinal());
			
			String urlId = URLAdd + "&id=" + idApp;
			HttpThread httpThread = new HttpThread(handlerHttp, urlId, nomeApp, local.getLocalizacao());
			httpThread.start();

		}else if(v == bt_zerarBanco)
		{
			dataBase.dropTable();
			
		}else if(v == bt_generate)
		{
			dialogLogin = new Dialog(this);

			dialogLogin.setContentView(R.layout.login);
			dialogLogin.setTitle("Generate Id");
			
			dialogLogin.show();
			
			bt_generateId = (Button) dialogLogin.findViewById(R.id.bt_generateId);
			bt_generateId.setOnClickListener(this);
			    
		}else if(v == bt_generateId)
		{
			TextView et_nome = (TextView) dialogLogin.findViewById(R.id.et_nome);
		    TextView et_local = (TextView) dialogLogin.findViewById(R.id.et_localGenerateId);
			
		    Log.d("pow", "antes de chamar http");
		    
		    nomeApp = et_nome.getText().toString();
			HttpThread httpThread = new HttpThread(handlerHttp, URLGenerate, et_nome.getText().toString(), et_local.getText().toString());
			httpThread.start();
		    /*Colocar uma confirmação de aguardo da Thread antes de mostrar o Toast*/
			
			dialogLogin.dismiss();
			
			Toast toast = Toast.makeText(this, "Id Criado com Sucesso!", Toast.LENGTH_LONG);
			toast.show();
		}

	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		
		switch (id) {
		case caseGenerateId:
			
			break;

		default:
			break;
		}
		
		return super.onCreateDialog(id);
	}
	
	private Localizacao getLocal(List<Localizacao> bd, List<ScanResult> results) {
		Localizacao local = new Localizacao();
		Map<Localizacao, Integer> map = new HashMap<Localizacao, Integer>();
		Integer aux = 0;
		for(ScanResult sr : results){
			for(Localizacao l : bd) {
				if(aux == 0) {
					local = l;
					aux = 1;
				}
				else {
					Integer dif1 = (l.getSinal() * (-1)) - (sr.level *(-1));
					if(dif1 < 0) {
						dif1 = dif1*(-1);
					}
					Integer dif2 = (local.getSinal() * (-1)) - (sr.level *(-1));
					if(dif2 < 0) {
						dif2 = dif2*(-1);
					}
					if(dif1 < dif2) {
						local = l;
					}
				}
			}
			if(map.containsKey(local)) {
				map.put(local, map.get(local) +1);
			}
			else {
				map.put(local, 1);
			}
		}
		aux = 0;
		for(Localizacao l : map.keySet()){
			if(aux == 0){
				local = l;
			}
			else {
				if(map.get(l) > map.get(local)) {
					local = l;
				}
			}
		}
		return local;
	}
}