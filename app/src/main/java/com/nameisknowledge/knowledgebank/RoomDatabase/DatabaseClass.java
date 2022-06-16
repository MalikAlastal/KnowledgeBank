package com.nameisknowledge.knowledgebank.RoomDatabase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.nameisknowledge.knowledgebank.RoomDatabase.Daos.QuestionRMDao;
import com.nameisknowledge.knowledgebank.RoomDatabase.Entity.QuestionRMD;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Database(entities = QuestionRMD.class, version = 1 ,exportSchema = false)
public abstract class DatabaseClass extends RoomDatabase{
    public abstract QuestionRMDao questionRMDao();
    private static volatile DatabaseClass INSTANCE;
    private static final int NUMBER_OF_THREADS = 8;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static DatabaseClass getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DatabaseClass.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DatabaseClass.class, "DataBase")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static Callback sRoomDatabaseCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.

            });
        }
    };
}