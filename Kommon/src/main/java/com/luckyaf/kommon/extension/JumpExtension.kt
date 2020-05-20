package com.luckyaf.kommon.extension



import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.luckyaf.kommon.component.SmartJump


/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-21
 *
 */


inline val androidx.fragment.app.FragmentActivity.smartJump
    get() = SmartJump.from(this)

inline val androidx.fragment.app.Fragment.smartJump
    get() = SmartJump.from(this)


inline fun <reified T : Activity> Activity.jumpTo(params: Bundle? = null) {
    val intent = Intent(this, T::class.java)
    params?.let { intent.putExtras(it) }
    startActivity(intent)
}

inline fun <reified T : Activity> androidx.fragment.app.Fragment.jumpTo(params: Bundle? = null) {
    val intent = Intent(this.activity, T::class.java)
    params?.let { intent.putExtras(it) }
    startActivity(intent)
}

inline fun <reified T : Activity> androidx.fragment.app.Fragment.jumpForResult(
        params: Bundle? = null,
        crossinline action: (Int, Intent?) -> Unit) {
    val intent = Intent(this.context, T::class.java)
    params?.let { intent.putExtras(it) }
    jumpForResult(intent,action)
}


inline fun <reified T : Activity> androidx.fragment.app.FragmentActivity.jumpForResult(
        params: Bundle? = null,
        crossinline action: (Int, Intent?) -> Unit
) {
    val intent = Intent(this, T::class.java)
    params?.let { intent.putExtras(it) }
    jumpForResult(intent,action)
}


inline fun androidx.fragment.app.Fragment.jumpForResult(
        intent: Intent,
        crossinline action: (Int, Intent?) -> Unit
) {
    smartJump.startForResult(intent, object : SmartJump.ActivityResultCallback {
        override fun onActivityResult(resultCode: Int, data: Intent?) {
            action(resultCode, data)
        }
    })
}

inline fun androidx.fragment.app.FragmentActivity.jumpForResult(
        intent: Intent,
        crossinline action: (Int, Intent?) -> Unit
) {
    smartJump.startForResult(intent, object : SmartJump.ActivityResultCallback {
        override fun onActivityResult(resultCode: Int, data: Intent?) {
            action(resultCode, data)
        }
    })
}