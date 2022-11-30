package com.easyo.pairalarm.util

import timber.log.Timber

class MyTimber: Timber.DebugTree() {
    private lateinit var fileName: String
    private var lineNumber = 0
    private lateinit var methodName: String

    override fun createStackElementTag(element: StackTraceElement): String {
        this.fileName = element.fileName
        this.lineNumber = element.lineNumber
        this.methodName = element.methodName
        return element.fileName
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val newMessage = "($fileName:$lineNumber): $message"
        super.log(priority, tag, newMessage, t)
    }
}
