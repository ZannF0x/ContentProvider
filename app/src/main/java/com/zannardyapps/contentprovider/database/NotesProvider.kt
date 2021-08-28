package com.zannardyapps.contentprovider.database

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.media.UnsupportedSchemeException
import android.net.Uri
import android.provider.BaseColumns._ID
import com.zannardyapps.contentprovider.database.NotesDataBaseHelper.Companion.TABLE_NOTES

class NotesProvider : ContentProvider() {

    private lateinit var mUriMatcher: UriMatcher
    private lateinit var dbHelper: NotesDataBaseHelper

    override fun onCreate(): Boolean {

        mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        mUriMatcher.addURI(AUTHORITY, "notes", NOTES)
        mUriMatcher.addURI(AUTHORITY, "notes/#", NOTES_BY_ID)

        if (context != null) {dbHelper = NotesDataBaseHelper(context as Context) }

        return true
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {

        if (mUriMatcher.match(uri) == NOTES_BY_ID){

            val db: SQLiteDatabase = dbHelper.writableDatabase
            val linesAffected: Int = db.delete(TABLE_NOTES, "$_ID =?", arrayOf(uri.lastPathSegment))
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return linesAffected

        } else {
            throw UnsupportedSchemeException("URI inválida para exclusão")
        }

    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {

        if (mUriMatcher.match(uri) == NOTES){
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val id: Long = db.insert(TABLE_NOTES, null, values)
            val insertURI: Uri = Uri.withAppendedPath(BASE_URI, id.toString())
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return insertURI

        } else {
            throw UnsupportedSchemeException("URI inválida para inserção")
        }
    }


    @SuppressLint("Recycle")
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {

        return when {
            mUriMatcher.match(uri) == NOTES -> {
                val db: SQLiteDatabase = dbHelper.writableDatabase
                val cursor: Cursor = db.query(
                    TABLE_NOTES,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder)
                cursor.setNotificationUri(context?.contentResolver, uri)

                cursor // return cursor
            }

            mUriMatcher.match(uri) == NOTES_BY_ID -> {
                val db: SQLiteDatabase = dbHelper.writableDatabase
                val cursor = db.query(
                    TABLE_NOTES,
                    projection,
                    "$_ID =?",
                    arrayOf(uri.lastPathSegment),
                    null,
                    null,
                    sortOrder)
                cursor.setNotificationUri((context as Context).contentResolver, uri)

                cursor // return cursor
            }

            else -> {
                throw UnsupportedSchemeException("URI não implementada")
            }
        }

    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        if (mUriMatcher.match(uri) == NOTES_BY_ID) {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val linesAffected: Int = db.update(TABLE_NOTES, values, "$_ID =?", arrayOf(uri.lastPathSegment))
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return linesAffected
        } else {
            throw UnsupportedSchemeException("URI não implementada")
        }
    }

    override fun getType(uri: Uri): String =
        throw UnsupportedSchemeException("URI não implementada")

    companion object {

        const val AUTHORITY: String = "com.zannardyapps.contentprovider.provider"
        val BASE_URI = Uri.parse("content://$AUTHORITY")
        val URI_NOTES = Uri.withAppendedPath(BASE_URI, "notes")
        //URI_NOTES = content://com.zannardyapps.contentprovider.provider/notes

        const val NOTES = 1
        const val NOTES_BY_ID = 2
    }
}