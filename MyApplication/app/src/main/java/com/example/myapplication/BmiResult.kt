package com.example.myapplication

enum class BmiResult(val res:String,val emoji:String,val bgColor:String) {
    NORMAL("정상","\uD83D\uDE00","#33CC33"),
    UNDERWEIGHT("저체중","\uD83D\uDE2D","#FFCC33"),
    OVERWEIGHT("과체중","😫","#FF3366"),
    FAT("비만","\uD83D\uDE31","#CC0033")



}