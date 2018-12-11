package siralmat.madrpg.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import android.content.Context
import android.os.AsyncTask

import siralmat.madrpg.model.Area
import siralmat.madrpg.model.Item
import siralmat.madrpg.model.Player

@Database (entities = [Player::class, Item::class, Area::class], version = 1, exportSchema = false)
abstract class GameDb: RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun playerDao(): PlayerDao
    abstract fun areaDao(): AreaDao

    companion object {
        private var INSTANCE: GameDb? = null
        fun getInstance(context: Context): GameDb {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDb(context).also { INSTANCE = it }
            }
        }

        private fun buildDb(context: Context): GameDb {
            // https://codelabs.developers.google.com/codelabs/android-training-livedata-viewmodel/index.html?index=..%2F..%2Fandroid-training#11
            return Room.databaseBuilder(context,
                    GameDb::class.java, "game_database")
                    .fallbackToDestructiveMigration() // just destroy DB on version change
//                    .addCallback(object : RoomDatabase.Callback() {
//                        override fun onCreate(db: SupportSQLiteDatabase) {
//                            super.onCreate(db)
//                            PopulateDb(context).execute()
//                        }})
                    .build()
        }
    }


    internal class PopulateDb(val context: Context): AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            getInstance(context).apply {
                playerDao().insert(Player())
            }
            return null
        }
    }

}

