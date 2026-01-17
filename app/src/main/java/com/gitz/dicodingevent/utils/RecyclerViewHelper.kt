package com.gitz.dicodingevent.utils

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.addBottomPaddingForLastItem(itemLayoutRes: Int, bottomNavHeight: Int) {
// Inflate one item view to measure height
    val itemView = LayoutInflater.from(context).inflate(itemLayoutRes, this, false)
    itemView.measure(
        View.MeasureSpec.makeMeasureSpec(this.width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )

    val itemHeight = itemView.measuredHeight / 2
    val dividedBottomNavHeight = bottomNavHeight / 2

    // Set bottom padding: item height + bottom nav
    this.setPadding(
        paddingLeft,
        paddingTop,
        paddingRight,
        itemHeight + dividedBottomNavHeight
    )
    this.clipToPadding = false
}
