package com.teamapt.monnify.monnify_payment_sdk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.monnify.monnify_payment_sdk.R

class MonnifyDropDownAdapter<T>(context: Context, private val resource: Int, private val view: Int, objects: MutableList<T>) :
    ArrayAdapter<T>(context, resource, objects) {




    private val data: MutableList<T> = objects

    override fun isEnabled(position: Int): Boolean {
        return position != 0 && super.isEnabled(position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view: View = LayoutInflater.from(parent.context)
            .inflate(resource, parent, false)

        val tv: AppCompatTextView = view as AppCompatTextView

        if (position == 0) {
            tv.setTextColor(ContextCompat.getColor(context, R.color.monnifyNeutralBlackTrans40))
        } else {
            tv.setTextColor(ContextCompat.getColor(context, R.color.monnifyNeutralBlackTrans100))
        }

        tv.text = getItem(position).toString()

        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view: View? = convertView

        if (view == null) {
            view = LayoutInflater.from(parent.context)
                .inflate(this.view, parent, false)
        }

        val tv: AppCompatTextView = view as AppCompatTextView

        if (position == 0) {
            tv.setPadding(0, context.resources.getDimensionPixelSize(R.dimen.dimen8dp), 0, context.resources.getDimensionPixelSize(R.dimen.dimen8dp))
            tv.setTextColor(ContextCompat.getColor(context, R.color.monnifyNeutralBlackTrans40))
        }

        tv.text = getItem(position).toString()

        return view
    }

    override fun getItem(position: Int): T? {
        return data[position]
    }

    fun getData() : List<T> {
        return data
    }

    fun replaceData(data: List<T>, hint: T) {
        this.data.clear()
        this.data.add(hint)
        this.data.addAll(data)
    }
}