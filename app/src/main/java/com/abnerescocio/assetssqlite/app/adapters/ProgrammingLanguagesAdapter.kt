package com.abnerescocio.assetssqlite.app.adapters

import android.content.Context
import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abnerescocio.assetssqlite.app.R
import com.abnerescocio.assetssqlite.app.databases.contracts.ProgrammingLanguagesEntries

import kotlinx.android.synthetic.main.item_programming_language.view.*

/**
 * Created by abnerESC on 02/03/2018
 */
class ProgrammingLanguagesAdapter(private val context: Context?, private val cursor: Cursor?)
    : RecyclerView.Adapter<ProgrammingLanguagesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_programming_language, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cursor?.moveToPosition(position)
        holder.itemView?.text_view_name?.text =
                cursor?.getString(cursor.getColumnIndex(ProgrammingLanguagesEntries.NAME))
        holder.itemView?.text_view_created_in?.text =
                cursor?.getString(cursor.getColumnIndex(ProgrammingLanguagesEntries.CREATED_IN))
    }

    override fun getItemCount(): Int {
        return cursor?.count!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}