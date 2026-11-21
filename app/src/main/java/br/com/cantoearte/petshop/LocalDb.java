package br.com.cantoearte.petshop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class LocalDb extends SQLiteOpenHelper {
    public LocalDb(Context c) { super(c, "canto_arte.db", null, 1); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE registros (id INTEGER PRIMARY KEY AUTOINCREMENT, tipo TEXT NOT NULL, titulo TEXT NOT NULL, detalhe TEXT, valor REAL DEFAULT 0, criado_em TEXT DEFAULT CURRENT_TIMESTAMP)");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    public long inserir(String tipo, String titulo, String detalhe, double valor) {
        ContentValues v = new ContentValues(); v.put("tipo", tipo); v.put("titulo", titulo); v.put("detalhe", detalhe); v.put("valor", valor);
        return getWritableDatabase().insert("registros", null, v);
    }
    public ArrayList<String[]> listar(String tipo) {
        ArrayList<String[]> out = new ArrayList<>();
        Cursor c = getReadableDatabase().query("registros", new String[]{"id","titulo","detalhe","valor","criado_em"}, "tipo=?", new String[]{tipo}, null, null, "id DESC");
        try { while(c.moveToNext()) out.add(new String[]{c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4)}); } finally { c.close(); }
        return out;
    }
    public int contar(String tipo) {
        Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM registros WHERE tipo=?", new String[]{tipo});
        try { c.moveToFirst(); return c.getInt(0); } finally { c.close(); }
    }
    public double totalVendas() {
        Cursor c = getReadableDatabase().rawQuery("SELECT COALESCE(SUM(valor),0) FROM registros WHERE tipo='Venda'", null);
        try { c.moveToFirst(); return c.getDouble(0); } finally { c.close(); }
    }
    public void excluir(long id) { getWritableDatabase().delete("registros", "id=?", new String[]{String.valueOf(id)}); }
    public void atualizarValor(long id, double valor) {
        ContentValues v = new ContentValues(); v.put("valor", valor);
        getWritableDatabase().update("registros", v, "id=?", new String[]{String.valueOf(id)});
    }
}
