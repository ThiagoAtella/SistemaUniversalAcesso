package com.example.sistemauniversalacesso.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sistemauniversalacesso.models.Aluno;

import java.util.ArrayList;
import java.util.List;

public class SistemaDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sistema.db";
    private static final int DATABASE_VERSION = 1;

    // Tabela Usuarios (genérica)
    private static final String TABLE_USUARIOS = "usuarios";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOME = "nome";
    private static final String COLUMN_SENHA = "senha";
    private static final String COLUMN_IDADE = "idade";
    private static final String COLUMN_NIVEL = "nivel";
    private static final String COLUMN_EMAIL = "email";

    // Tabela Alunos (específica)
    private static final String TABLE_ALUNOS = "alunos";
    private static final String COLUMN_USUARIO_ID = "usuario_id"; // FK para usuarios
    private static final String COLUMN_MATRICULA = "matricula";
    private static final String COLUMN_TURMA = "turma";

    public SistemaDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criação da tabela usuarios
        String CREATE_TABLE_USUARIOS = "CREATE TABLE " + TABLE_USUARIOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NOME + " TEXT, "
                + COLUMN_SENHA + " TEXT, "
                + COLUMN_IDADE + " INTEGER, "
                + COLUMN_NIVEL + " TEXT, "
                + COLUMN_EMAIL + " TEXT)";
        db.execSQL(CREATE_TABLE_USUARIOS);

        // Criação da tabela alunos
        String CREATE_TABLE_ALUNOS = "CREATE TABLE " + TABLE_ALUNOS + "("
                + COLUMN_USUARIO_ID + " INTEGER, "
                + COLUMN_MATRICULA + " INTEGER UNIQUE, "
                + COLUMN_TURMA + " TEXT, "
                + "FOREIGN KEY (" + COLUMN_USUARIO_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_ID + "))";
        db.execSQL(CREATE_TABLE_ALUNOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALUNOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    // Inserir um aluno (insere em ambas as tabelas)
    public void inserirAluno(Aluno aluno) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Insere na tabela usuarios
        ContentValues usuarioValues = new ContentValues();
        usuarioValues.put(COLUMN_NOME, aluno.getNome());
        usuarioValues.put(COLUMN_SENHA, aluno.getSenha());
        usuarioValues.put(COLUMN_IDADE, aluno.getIdade());
        usuarioValues.put(COLUMN_NIVEL, aluno.getNivel());
        usuarioValues.put(COLUMN_EMAIL, aluno.getEmail());
        long usuarioId = db.insert(TABLE_USUARIOS, null, usuarioValues);

        // Insere na tabela alunos
        ContentValues alunoValues = new ContentValues();
        alunoValues.put(COLUMN_USUARIO_ID, usuarioId);
        alunoValues.put(COLUMN_MATRICULA, aluno.getMatricula());
        alunoValues.put(COLUMN_TURMA, aluno.getTurma());
        db.insert(TABLE_ALUNOS, null, alunoValues);

        db.close();
    }

    // Listar todos os alunos
    public List<Aluno> getTodosAlunos() {
        List<Aluno> alunos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIOS + " u INNER JOIN " + TABLE_ALUNOS + " a ON u." + COLUMN_ID + " = a." + COLUMN_USUARIO_ID;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Aluno aluno = new Aluno(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENHA)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IDADE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NIVEL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MATRICULA)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TURMA))
                );
                alunos.add(aluno);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return alunos;
    }
}
