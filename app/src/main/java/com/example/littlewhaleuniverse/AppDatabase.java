package com.example.littlewhaleuniverse;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// @Database注解，声明实体和版本号
@Database(entities = {DiaryEntry.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // DAO的抽象方法，Room会自动为我们实现它
    public abstract DiaryDao diaryDao();

    // volatile关键字确保INSTANCE在多线程环境下的可见性
    private static volatile AppDatabase INSTANCE;

    // 创建一个固定大小的线程池，用于在后台执行数据库操作
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // 获取数据库实例的静态方法（单例模式）
    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            // 同步锁，防止多个线程同时创建实例
            synchronized (AppDatabase.class) {
                // 双重检查锁定
                if (INSTANCE == null) {
                    // 创建数据库实例
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "diary_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}