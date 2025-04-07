package com.example.sistemauniversalacesso.database;

import android.content.Context;
import androidx.lifecycle.ViewModel;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.Room;
import com.example.sistemauniversalacesso.database.usuarioDAO;
import com.example.sistemauniversalacesso.models.usuario;

import java.util.ArrayList;
import java.util.List;


@Database(entities = {usuario.class}, version = 1, exportSchema = true)
public abstract class SistemaDatabase extends  RoomDatabase{
    private static SistemaDatabase instancia;

    public abstract usuarioDAO UuarioDao();

    public static synchronized SistemaDatabase getInstance(Context context){
        if (instancia == null){
            instancia = Room.databaseBuilder(context.getApplicationContext(),
                    SistemaDatabase.class, "SUA.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instancia;
    }
}

