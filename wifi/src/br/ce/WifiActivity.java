package br.ce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class WifiActivity extends Activity implements OnClickListener {
	public static WifiManager wifiManager;
	public static ScanResult scanResult;
	DataBase dataBase;
	Button bt_cadastrarSinais;
	Button bt_localizar;
	TextView et_local;
	TextView et_maisforte;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		TextView texto = (TextView) findViewById(R.id.texto);
		bt_cadastrarSinais = (Button) findViewById(R.id.bt_cadastrarSinais);
		bt_localizar = (Button) findViewById(R.id.bt_localizar);
		et_local = (TextView) findViewById(R.id.et_local);
		et_maisforte = (TextView) findViewById(R.id.et_maisforte);
		
		bt_cadastrarSinais.setOnClickListener(this);
		bt_localizar.setOnClickListener(this);
		

		/*
		 * DADOS DA APLICAÇÃO
		 */

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
			List<Localizacao> listLocalizacoesCapturadas = new ArrayList<Localizacao>();

			List<ScanResult> scanResults = wifiManager.getScanResults();

			Localizacao voceEstaAqui = null;
			
			for (ScanResult scanResult : scanResults) {
				Localizacao localizacao = new Localizacao();

				localizacao.setRede(scanResult.SSID);
				localizacao.setSinal(scanResult.level);

				listLocalizacoesCapturadas.add(localizacao);
			}

			voceEstaAqui = new Localizacao();
			voceEstaAqui.setSinal(-500);
			voceEstaAqui.setRede(null);

			for (Localizacao localizacao : listLocalizacoesCapturadas) {

				Log.d("Capturado", localizacao.getRede() + " - " + localizacao.getSinal());
				for (Localizacao localizacaoBd : listLocalizacoesBd) {

					if (localizacao.getRede().equals(localizacaoBd.getRede())) {

						if (voceEstaAqui.getRede() == null) {
							voceEstaAqui.setSinal(localizacao.getSinal());
							voceEstaAqui.setRede(localizacao.getRede());

							voceEstaAqui.setLocalizacao(localizacaoBd
									.getLocalizacao());
						}

						// <0, primeiro é mais fraco q o segundo
						else if (WifiManager
								.compareSignalLevel(voceEstaAqui.getSinal(),
										localizacao.getSinal()) < 0) {
							voceEstaAqui.setSinal(localizacao.getSinal());
							voceEstaAqui.setRede(localizacao.getRede());

							voceEstaAqui.setLocalizacao(localizacaoBd
									.getLocalizacao());
						}

					}
				}
			}
			if(voceEstaAqui == null)
			{
				Log.d("mais forte", "NULO");
				et_maisforte.setText("NULO");
			}else{
			Log.d("mais forte-rede", voceEstaAqui.getRede());
			Log.d("mais forte-sinal", voceEstaAqui.getSinal().toString());
			et_maisforte.setText("Você está na: " + voceEstaAqui.getLocalizacao() + " - Sinal + forte: " + voceEstaAqui.getRede() + "com sinal: " + voceEstaAqui.getSinal().toString());
			}
			
		}
		
	}

}