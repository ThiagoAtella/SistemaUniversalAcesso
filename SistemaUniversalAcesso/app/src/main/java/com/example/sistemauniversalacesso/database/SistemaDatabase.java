package com.example.sistemauniversalacesso.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sistemauniversalacesso.models.usuario;

import java.util.ArrayList;
import java.util.List;



public class SistemaDatabase {
    private static final String DATABASE_NAME = "alunos.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ALUNOS = "alunos";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MATRICULA = "matricula";
    private static final String COLUMN_NOME = "nome";
    private static final String COLUMN_TURMA = "turma";

    public SistemaDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_ALUNOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MATRICULA + " INTEGER, "
                + COLUMN_NOME + " TEXT, " + COLUMN_TURMA + " TEXT) ";;
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALUNOS);
        onCreate(db);
    }

    public void inserirAluno(Aluno aluno) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MATRICULA, aluno.getMatricula());
        values.put(COLUMN_NOME, aluno.getNome());
        db.insert(TABLE_ALUNOS, null, values);
        db.close();
    }
    public List<Aluno> getTodosAlunos() {
        List<Aluno> alunos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ALUNOS, null);

        if (cursor.moveToFirst()) {
            do {
                Aluno aluno = new Aluno(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MATRICULA)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME))
                );
                alunos.add(aluno);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return alunos;
    }
}
