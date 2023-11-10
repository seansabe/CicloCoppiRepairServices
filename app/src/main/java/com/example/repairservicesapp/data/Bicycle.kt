package com.example.repairservicesapp.data

import android.content.Context
import com.example.repairservicesapp.R

class Bicycle(val context: Context) {
    val type = arrayListOf(
        context.getString(R.string.txtBikeTypeMTB),
        context.getString(R.string.txtBikeTypeRoad),
        context.getString(R.string.txtBikeTypeUrban),
        context.getString(R.string.txtBikeTypeBMX),
        context.getString(R.string.txtBikeTypeAmateur),
        context.getString(R.string.txtBikeTypeElectric),
        context.getString(R.string.txtBikeTypeKids),
        context.getString(R.string.txtBikeTypeOther)
    )
    val color = arrayListOf(
        context.getString(R.string.txtColorBlack),
        context.getString(R.string.txtColorWhite),
        context.getString(R.string.txtColorRed),
        context.getString(R.string.txtColorBlue),
        context.getString(R.string.txtColorGreen),
        context.getString(R.string.txtColorYellow),
        context.getString(R.string.txtColorOrange),
        context.getString(R.string.txtColorPurple),
        context.getString(R.string.txtColorPink),
        context.getString(R.string.txtColorBrown),
        context.getString(R.string.txtColorGray),
        context.getString(R.string.txtColorGold),
        context.getString(R.string.txtColorSilver),
        context.getString(R.string.txtColorFuchsia),
        context.getString(R.string.txtColorMagenta),
        context.getString(R.string.txtColorCyan),
        context.getString(R.string.txtColorLime),
        context.getString(R.string.txtColorMaroon),
        context.getString(R.string.txtColorAqua)
    )
    val wheelSize = arrayListOf(
        "12",
        "16",
        "20",
        "24",
        "26",
        "27",
        "27.5",
        "28",
        "29",
        "700"
    )
}
