package br.ce;

import java.util.List;

import android.net.wifi.ScanResult;
import android.util.Log;

public class Threads extends Thread{

	@Override
	public void run() {
		
        List<ScanResult> listScans = WifiActivity.wifiManager.getScanResults();
        
        for (ScanResult scanResult : listScans) {
			Log.d(scanResult.SSID + "- Level", String.valueOf(scanResult.level));
			if(WifiActivity.scanResult == null || 
					WifiActivity.wifiManager.compareSignalLevel
					(WifiActivity.scanResult.level, scanResult.level) < 0) /*
				Se o segundo for mais forte, atribui. return Returns < 0 if the first signal 
				is weaker than the second signal, if the two signals have the same strength,
				 and >0 if the first signal is stronger than the second signal.
				*/
			{
				WifiActivity.scanResult = scanResult;
			}
		}
       
	}

}
