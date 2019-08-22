package com.luckyaf.kommon.extension



import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.luckyaf.kommon.component.SmartJump


/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-21
 *
 */


inline val FragmentActivity.smartJump
    get() = SmartJump.from(this)

inline val Fragment.smartJump
    get() = SmartJump.from(this)


inline fun <reified T : Activity> Activity.jumpTo(params: Bundle? = null) {
    val intent = Intent(this, T::class.java)
    params?.let { intent.putExtras(it) }
    startActivity(intent)
}

inline fun <reified T : Activity> Fragment.jumpTo(params: Bundle? = null) {
    val intent = Intent(this.activity, T::class.java)
    params?.let { intent.putExtras(it) }
    startActivity(intent)
}

inline fun <reified T : Activity> Fragment.jumpForResult(
        params: Bundle? = null,
        crossinline action: (Int, Intent?) -> Unit) {
    val intent = Intent(this.context, T::class.java)
    params?.let { intent.putExtras(it) }
    smartJump.startForResult(intent, object : SmartJump.ActivityResultCallback {
        override fun onActivityResult(resultCode: Int, data: Intent?) {
            action(resultCode, data)
        }
    })
}


inline fun <reified T : Activity> FragmentActivity.jumpForResult(
        params: Bundle? = null,
        crossinline action: (Int, Intent?) -> Unit
) {
    val intent = Intent(this, T::class.java)
    params?.let { intent.putExtras(it) }
    smartJump.startForResult(intent, object : SmartJump.ActivityResultCallback {
        override fun onActivityResult(resultCode: Int, data: Intent?) {
            action(resultCode, data)
        }
    })
}