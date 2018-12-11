package siralmat.madrpg.model

import java.util.*

import androidx.room.Entity
import androidx.room.Index
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "areas", primaryKeys = ["x", "y"], foreignKeys = [],
        indices = [Index("id", unique = true)])
data class Area (
        var x: Int,
        var y: Int,
        val town: Boolean = false,
        var description: String = "",
        var explored: Boolean = false,
        var starred: Boolean = false,
        val id: String = UUID.randomUUID().toString()
): Parcelable