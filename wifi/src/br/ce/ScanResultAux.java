package br.ce;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.net.wifi.ScanResult;


public class ScanResultAux {
	private Map<String, Integer> localizacoes;
	
	public String getLocation() {
		localizacoes = new HashMap<String, Integer>();
		List<ScanResult> listScans = WifiActivity.wifiManager.getScanResults();
		
		for(ScanResult sr : listScans) {
			String rede = sr.SSID;
			Integer sinal = sr.level;
			// Pega todos os sinais da rede no banco de dados
			List<Localizacao> listaLocalizacao = null;
			String local = getSinal(listaLocalizacao, sinal);
			if(localizacoes.containsKey(local)) {
				localizacoes.put(local, localizacoes.get(local) + 1);
			}
		}
		return getLocalizacao();
	}
	
	private String getSinal(List<Localizacao> listaLocalizacao, Integer sinal) {
		Localizacao local = new Localizacao();
		for(Localizacao l : listaLocalizacao) {
			Integer lSinal = l.getSinal() - sinal;
			Integer atualSinal = local.getSinal() - sinal;
			if(lSinal < 0) {
				lSinal = lSinal * (-1);
			}
			if(atualSinal < 0) {
				atualSinal = atualSinal * (-1);
			}
			if(lSinal < atualSinal) {
				local = l;
			}
			
		}
		return local.getLocalizacao();
	}
	
	// Pega a localização que mais foi encontrada em todas as redes
	private String getLocalizacao() {
		String local = "";
		Integer sinal = 9999999;
		for(String s : localizacoes.keySet()) {
			if(localizacoes.get(s) < sinal) {
				sinal = localizacoes.get(s);
				local = s;
			}
		}
		return local;
	}

	public Map<String, Integer> getLocalizacoes() {
		return localizacoes;
	}

	public void setLocalizacoes(Map<String, Integer> localizacoes) {
		this.localizacoes = localizacoes;
	}
	

}
