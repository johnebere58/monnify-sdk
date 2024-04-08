package com.monnify.monnify_payment_sdk.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.monnify.monnify_payment_sdk.R
import com.monnify.monnify_payment_sdk.data.model.PaymentMethod

class PaymentMethodAdapter(context: Context, private val items: List<PaymentMethod>) :
    ArrayAdapter<PaymentMethod>(context, R.layout.plugin_payment_method_view, items) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val paymentMethod: PaymentMethod = items[position]

        var itemView = convertView
        if (itemView == null)
            itemView = inflater.inflate(R.layout.plugin_payment_method_list_item, parent, false)

        val paymentIconImageView: AppCompatImageView =
            itemView!!.findViewById(R.id.paymentMethodIconImageView)
        val paymentTitleTextView: AppCompatTextView =
            itemView.findViewById(R.id.paymentMethodTitleTextView)

        paymentIconImageView.setImageResource(paymentMethod.methodIcon)
        paymentTitleTextView.setText(paymentMethod.title)

        return itemView
    }

    override fun getItem(position: Int): PaymentMethod? {
        return items[position]
    }

    override fun getCount(): Int {
        return items.size
    }

}