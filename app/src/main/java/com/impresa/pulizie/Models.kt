package com.impresa.pulizie

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "clienti")
data class Cliente(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val indirizzo: String,
    val telefono: String = ""
)

@Entity(tableName = "interventi")
data class Intervento(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clienteId: Long,
    val nomeCliente: String,
    val nomeOperatore: String,
    val dataOraInizio: Long,
    val dataOraFine: Long,
    val durataMinuti: Long,
    val numeroOperatori: Int
)

@Dao
interface ClienteDao {
    @Query("SELECT * FROM clienti ORDER BY nome ASC")
    fun getTuttiClienti(): Flow<List<Cliente>>

    @Insert
    suspend fun inserisciCliente(cliente: Cliente)
}

@Dao
interface InterventoDao {
    @Query("SELECT * FROM interventi WHERE dataOraInizio >= :inizioGiornataMs ORDER BY dataOraInizio DESC")
    fun getInterventiOggi(inizioGiornataMs: Long): Flow<List<Intervento>>

    @Insert
    suspend fun inserisciIntervento(intervento: Intervento)
}

@Database(entities = [Cliente::class, Intervento::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
    abstract fun interventoDao(): InterventoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pulizie_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
