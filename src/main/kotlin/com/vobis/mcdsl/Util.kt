package com.vobis.mcdsl

class Util {
    companion object {
        var nextId = 99999

        fun uniqueString()= "s" + (nextId++).toString(16)
    }
}
