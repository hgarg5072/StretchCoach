package com.stretchcoach.`data`

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performInTransactionSuspending
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class StretchDao_Impl(
  __db: RoomDatabase,
) : StretchDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfStretchEntity: EntityInsertAdapter<StretchEntity>

  private val __deleteAdapterOfStretchEntity: EntityDeleteOrUpdateAdapter<StretchEntity>

  private val __updateAdapterOfStretchEntity: EntityDeleteOrUpdateAdapter<StretchEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfStretchEntity = object : EntityInsertAdapter<StretchEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `stretches` (`id`,`name`,`preparationSeconds`,`holdSeconds`,`notes`,`enabled`,`sortOrder`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: StretchEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.preparationSeconds.toLong())
        statement.bindLong(4, entity.holdSeconds.toLong())
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpNotes)
        }
        val _tmp: Int = if (entity.enabled) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        statement.bindLong(7, entity.sortOrder.toLong())
      }
    }
    this.__deleteAdapterOfStretchEntity = object : EntityDeleteOrUpdateAdapter<StretchEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `stretches` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: StretchEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfStretchEntity = object : EntityDeleteOrUpdateAdapter<StretchEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `stretches` SET `id` = ?,`name` = ?,`preparationSeconds` = ?,`holdSeconds` = ?,`notes` = ?,`enabled` = ?,`sortOrder` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: StretchEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.preparationSeconds.toLong())
        statement.bindLong(4, entity.holdSeconds.toLong())
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpNotes)
        }
        val _tmp: Int = if (entity.enabled) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        statement.bindLong(7, entity.sortOrder.toLong())
        statement.bindLong(8, entity.id)
      }
    }
  }

  public override suspend fun insert(stretch: StretchEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfStretchEntity.insertAndReturnId(_connection, stretch)
    _result
  }

  public override suspend fun delete(stretch: StretchEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfStretchEntity.handle(_connection, stretch)
  }

  public override suspend fun update(stretch: StretchEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfStretchEntity.handle(_connection, stretch)
  }

  public override suspend fun updateSortOrders(ids: List<Long>): Unit = performInTransactionSuspending(__db) {
    super@StretchDao_Impl.updateSortOrders(ids)
  }

  public override fun observeAll(): Flow<List<StretchEntity>> {
    val _sql: String = "SELECT * FROM stretches ORDER BY sortOrder ASC, id ASC"
    return createFlow(__db, false, arrayOf("stretches")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfPreparationSeconds: Int = getColumnIndexOrThrow(_stmt, "preparationSeconds")
        val _columnIndexOfHoldSeconds: Int = getColumnIndexOrThrow(_stmt, "holdSeconds")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfEnabled: Int = getColumnIndexOrThrow(_stmt, "enabled")
        val _columnIndexOfSortOrder: Int = getColumnIndexOrThrow(_stmt, "sortOrder")
        val _result: MutableList<StretchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: StretchEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpPreparationSeconds: Int
          _tmpPreparationSeconds = _stmt.getLong(_columnIndexOfPreparationSeconds).toInt()
          val _tmpHoldSeconds: Int
          _tmpHoldSeconds = _stmt.getLong(_columnIndexOfHoldSeconds).toInt()
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpEnabled: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfEnabled).toInt()
          _tmpEnabled = _tmp != 0
          val _tmpSortOrder: Int
          _tmpSortOrder = _stmt.getLong(_columnIndexOfSortOrder).toInt()
          _item = StretchEntity(_tmpId,_tmpName,_tmpPreparationSeconds,_tmpHoldSeconds,_tmpNotes,_tmpEnabled,_tmpSortOrder)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeEnabled(): Flow<List<StretchEntity>> {
    val _sql: String = "SELECT * FROM stretches WHERE enabled = 1 ORDER BY sortOrder ASC, id ASC"
    return createFlow(__db, false, arrayOf("stretches")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfPreparationSeconds: Int = getColumnIndexOrThrow(_stmt, "preparationSeconds")
        val _columnIndexOfHoldSeconds: Int = getColumnIndexOrThrow(_stmt, "holdSeconds")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfEnabled: Int = getColumnIndexOrThrow(_stmt, "enabled")
        val _columnIndexOfSortOrder: Int = getColumnIndexOrThrow(_stmt, "sortOrder")
        val _result: MutableList<StretchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: StretchEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpPreparationSeconds: Int
          _tmpPreparationSeconds = _stmt.getLong(_columnIndexOfPreparationSeconds).toInt()
          val _tmpHoldSeconds: Int
          _tmpHoldSeconds = _stmt.getLong(_columnIndexOfHoldSeconds).toInt()
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpEnabled: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfEnabled).toInt()
          _tmpEnabled = _tmp != 0
          val _tmpSortOrder: Int
          _tmpSortOrder = _stmt.getLong(_columnIndexOfSortOrder).toInt()
          _item = StretchEntity(_tmpId,_tmpName,_tmpPreparationSeconds,_tmpHoldSeconds,_tmpNotes,_tmpEnabled,_tmpSortOrder)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun count(): Int {
    val _sql: String = "SELECT COUNT(*) FROM stretches"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun names(): List<String> {
    val _sql: String = "SELECT name FROM stretches"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun setEnabled(id: Long, enabled: Boolean) {
    val _sql: String = "UPDATE stretches SET enabled = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (enabled) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateSortOrder(id: Long, sortOrder: Int) {
    val _sql: String = "UPDATE stretches SET sortOrder = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sortOrder.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
