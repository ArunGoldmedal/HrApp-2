package com.goldmedal.hrapp.data.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.goldmedal.hrapp.data.db.entities.*


@Database(
        version = 2,
    entities = [User::class, BirthdayData::class, AnniversaryData::class, HolidayData::class, GetAllAttendanceData::class, GetCurrentAttendanceData::class, AllHolidayData::class,EmployeeAttendanceData::class,LeaveBalanceData::class,MyTeamData::class],
        autoMigrations = [
            AutoMigration (from = 1, to = 2)],
        exportSchema = true
)


abstract class AppDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao
    abstract fun getBirthDateDao(): BirthdayDao
    abstract fun getAnniversaryDateDao(): AnniversaryDao
    abstract fun getHolidayDao(): HolidayDao
    abstract fun getAllHolidayDao(): AllHolidayDao
    abstract fun getAllAttendanceDao(): AttendanceDao
    abstract fun getCurrentAttendanceDao(): CurrentAttendanceDao
    abstract fun getEmployeeAttendanceDao(): EmployeeAttendanceDao
    abstract fun getLeaveBalanceDao(): LeaveBalanceDao
    abstract fun getMyTeamDao(): MyTeamDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()


        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "room-database.db")
                   // .fallbackToDestructiveMigration()
                    .build()
    }
}