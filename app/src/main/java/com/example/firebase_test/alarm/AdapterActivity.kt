package com.example.firebase_test.alarm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Switch
import android.widget.TextView
import com.example.firebase_test.R
import com.example.firebase_test.alarm.models.Time

class AdapterActivity : BaseAdapter() {
    private val listviewitem = ArrayList<Time>()
    private val arrayList = listviewitem

    override fun getCount(): Int = arrayList.size

    override fun getItem(position: Int): Any = arrayList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            holder = ViewHolder()
            view = LayoutInflater.from(parent.context).inflate(R.layout.round_theme, parent, false)

            val switchWidget: Switch? = convertView?.findViewById(R.id.switchBtn)
            switchWidget?.isFocusable = false
            switchWidget?.isFocusableInTouchMode = false

            holder.hourText = view.findViewById(R.id.textTime1)
            holder.minuteText = view.findViewById(R.id.textTime2)
            holder.am_pm = view.findViewById(R.id.am_pm)
            holder.month = view.findViewById(R.id.time_month)
            holder.day = view.findViewById(R.id.time_day)

            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val time = arrayList[position]
        holder.am_pm.text = time.am_pm
        holder.hourText.text = "${time.hour}시"
        holder.minuteText.text = "${time.minute}분"
        holder.month.text = "${time.month}월 "
        holder.day.text = "${time.day}일"

        return view
    }

    fun addItem(hour: Int, minute: Int, am_pm: String, month: String, day: String) {
        val time = Time()
        time.hour = hour
        time.minute = minute
        time.am_pm = am_pm
        time.month = month
        time.day = day

        listviewitem.add(time)
    }

    // "arrayList 백업
    fun removeItem(position: Int) {
        if (listviewitem.isNotEmpty()) {
            listviewitem.removeAt(position)
        }
    }

    fun removeLastItem() {
        if (listviewitem.isNotEmpty()) {
            listviewitem.removeAt(listviewitem.size - 1)
        }
    }

    fun removeItem() {
        //TODO("Not yet implemented")
    }

    fun setItemList(list: List<Time>) {
        listviewitem.clear()
        listviewitem.addAll(list)
    }

    fun getItemList() = listviewitem

    private class ViewHolder {
        lateinit var hourText: TextView
        lateinit var minuteText: TextView
        lateinit var am_pm: TextView
        lateinit var month: TextView
        lateinit var day: TextView
    }
}