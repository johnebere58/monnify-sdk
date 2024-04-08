package com.teamapt.monnify.monnify_payment_sdk.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.monnify.monnify_payment_sdk.R
import com.teamapt.monnify.monnify_payment_sdk.adapter.MonnifyDropDownAdapter

class MonnifyDropDownView<T> @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val background: Int
    private val dropDownListItemView: Int
    private val dropDownSelectedItemView: Int

    private val dropDownShowLabel: Boolean

    private val labelTextView: AppCompatTextView
    private val spinner: AppCompatSpinner
    private val spinnerGroupLayout: ConstraintLayout

    private var selectedItem: T? = null

    private var dropDownAdapter: MonnifyDropDownAdapter<T>? = null
    private var dropDownItems: List<T>? = null

    init {
        inflate(context, R.layout.plugin_monnify_dropdown_view, this)

        spinner = findViewById(R.id.spinner)
        labelTextView = findViewById(R.id.spinnerLabelText)
        spinnerGroupLayout = findViewById(R.id.spinnerGroupLayout)

        labelTextView.visibility = GONE

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MonnifyDropDownView)

        typedArray.apply {
            try {

                background = getResourceId(
                    R.styleable.MonnifyDropDownView_android_background,
                    R.drawable.bg_input
                )

                dropDownListItemView = getResourceId(
                    R.styleable.MonnifyDropDownView_dropDownListItemView,
                    R.layout.plugin_monnify_dropdown_list_item_view
                )

                dropDownSelectedItemView = getResourceId(
                    R.styleable.MonnifyDropDownView_dropDownSelectedItemView,
                    R.layout.plugin_monnify_dropdown_selected_item_view
                )

                dropDownShowLabel =
                    getBoolean(R.styleable.MonnifyDropDownView_dropDownShowLabel, true)

            } finally {
                recycle()
            }
        }

        spinnerGroupLayout.setBackgroundResource(background)
    }

    fun setupView(
        label: CharSequence,
        listItems: List<T>,
        hintItem: T,
        dropdownItemSelectedListener: OnDropdownItemSelectedListener<T>?
    ) {
        this.setOnClickListener {
            spinner.performClick()
        }

        this.dropDownItems = listItems

        labelTextView.text = label

        selectedItem = hintItem

        dropDownAdapter =
            MonnifyDropDownAdapter(
                context,
                dropDownListItemView,
                dropDownSelectedItemView,
                ArrayList()
            )
        dropDownAdapter?.replaceData(listItems, hintItem)

        spinner.adapter = dropDownAdapter


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    selectedItem = hintItem
                    return
                }

                if (dropDownShowLabel && labelTextView.visibility == GONE) {
                    labelTextView.visibility = VISIBLE
                }

                selectedItem = dropDownItems!![position - 1]
                dropdownItemSelectedListener?.onDropdownItemSelected(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    fun setSelectedItem(item: T) {
        if (dropDownAdapter == null)
            return
        val position: Int = dropDownAdapter?.getPosition(item) ?: -1

        spinner.setSelection(position)
    }

    fun getSelectedItemPosition(): Int {
        return spinner.selectedItemPosition
    }

    interface OnDropdownItemSelectedListener<T> {
        fun onDropdownItemSelected(selectedItem: T?)
    }
}