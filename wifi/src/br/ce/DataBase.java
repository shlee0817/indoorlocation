package br.ce;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBase extends SQLiteOpenHelper{

    protected static int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "wifi";
    private static final String TABLE_NAME = "redes";
   
    private static final String KEY_ID = "id";
    private static final String nome = "nome";
    private static final String sinal = "sinal";
    private static final String localizacao = "localizacao";
	
	public DataBase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("Abili", "entrou on create DB");
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY NOT NULL," + nome + " TEXT NOT NULL,"
                + sinal + " TEXT NOT NULL," + localizacao + " TEXT NOT NULL " + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("Abili", "entrou on UPGRADE DB");
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
 
        // Create tables again
        onCreate(db);
    }
	
	public void dropTable()
	{
		for (Localizacao elemento: this.getAllLocalizacao()) {
			this.deleteLocalizacao(elemento);
		}
	}
	
	public void addLocalizacao(Localizacao localizacao)
	{
		    SQLiteDatabase db = this.getWritableDatabase();
		 
		    ContentValues values = new ContentValues();
		    values.put("nome", localizacao.getRede());
		    values.put("sinal", localizacao.getSinal());
		    values.put("localizacao", localizacao.getLocalizacao());
		 
		    // Inserting Row
		    db.insert("redes", null, values);
		    db.close(); // Closing database connection
		    Log.d("SUCESSO", "adicionou ao banco");
	}
	
	public List<Localizacao> getAllLocalizacao(){
	    String nomeRede;
	    String local;
	    Integer sinal;
	    Integer id;
	    List<Localizacao> listLocalizacoes = new ArrayList<Localizacao>();
	    
	    String selectQuery = "SELECT  * FROM " + TABLE_NAME;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);

	 Log.d("numero_linhas", String.valueOf(cursor.getCount()));

	    if (cursor.moveToFirst()) {
	        do {
	            id = cursor.getInt(0);
	            nomeRede = cursor.getString(1);
	            sinal = cursor.getInt(2);
	            local = cursor.getString(3);
	            
	            Localizacao localizacao = new Localizacao();
	            
	            localizacao.setPk(id);
	            localizacao.setRede(nomeRede);
	            localizacao.setSinal(sinal);
	            localizacao.setLocalizacao(local);
	            
	            listLocalizacoes.add(localizacao);
	            
	            Log.d("Id", id.toString());
	            Log.d("nome", nomeRede);
	            Log.d("sinal", sinal.toString());
	            Log.d("local", local);
	        } while (cursor.moveToNext());
	    }
	    
	    return listLocalizacoes;
	}
	
	public void deleteLocalizacao(Localizacao localizacao) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_NAME, KEY_ID + " = ?",
	            new String[] { String.valueOf(localizacao.getPk()) });
	    db.close();
	}

}
