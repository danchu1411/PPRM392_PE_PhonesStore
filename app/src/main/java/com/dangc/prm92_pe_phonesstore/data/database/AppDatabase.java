package com.dangc.prm92_pe_phonesstore.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.dangc.prm92_pe_phonesstore.data.converter.DateConverter;
import com.dangc.prm92_pe_phonesstore.data.dao.OrderDao;
import com.dangc.prm92_pe_phonesstore.data.dao.OrderItemDao;
import com.dangc.prm92_pe_phonesstore.data.dao.ProductDao;
import com.dangc.prm92_pe_phonesstore.data.dao.UserDao;
import com.dangc.prm92_pe_phonesstore.data.entity.Order;
import com.dangc.prm92_pe_phonesstore.data.entity.OrderItem;
import com.dangc.prm92_pe_phonesstore.data.entity.Product;
import com.dangc.prm92_pe_phonesstore.data.entity.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Product.class, Order.class, OrderItem.class}, version = 2, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract ProductDao productDao();
    public abstract OrderDao orderDao();
    public abstract OrderItemDao orderItemDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    public final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "phone_store_database")
                            .addMigrations(MIGRATION_1_2)
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // SỬA LỖI: Cập nhật tài khoản Admin đã tồn tại thay vì Insert
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Bước 1: Thêm cột 'role' (cho phép null ban đầu)
            database.execSQL("ALTER TABLE users ADD COLUMN role TEXT");

            // Bước 2: Cập nhật vai trò cho tài khoản admin đã có
            database.execSQL("UPDATE users SET role = 'Admin' WHERE email = 'admin@example.com'");
        }
    };

    // Callback này sẽ xử lý việc tạo Admin nếu database được tạo MỚI HOÀN TOÀN
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            if (INSTANCE != null) {
                INSTANCE.databaseWriteExecutor.execute(() -> {
                    UserDao dao = INSTANCE.userDao();
                    if (dao.getUserCount() == 0) {
                        User adminUser = new User(
                                0,
                                "Admin User",
                                "admin@example.com",
                                "admin123",
                                "Admin"
                        );
                        dao.insert(adminUser);
                    }
                });
            }
        }
    };
}