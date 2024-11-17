package com.cs407.lingua

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class TagAdapter(context: Context, tagModelArrayList: ArrayList<TagModel>) :
    ArrayAdapter<TagModel>(context, 0, tagModelArrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var listItemView = convertView
        if (listItemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listItemView = LayoutInflater.from(context).inflate(R.layout.tag, parent, false)
        }

        val tagModel: TagModel? = getItem(position)
        val tagName = listItemView!!.findViewById<TextView>(R.id.tagName)

        tagName?.text = tagModel?.tagName
        return listItemView
    }
}