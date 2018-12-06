package com.luckyaf.kommon.extension

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import com.luckyaf.kommon.component.SmartJump

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/11
 *
 */
/**
 * The `fragment` is added to the container view with id `frameId`. The operation is
 * performed by the `fragmentManager`.
 */
fun AppCompatActivity.replaceFragmentInActivity(fragment: Fragment, frameId: Int) {
    supportFragmentManager.transact {
        replace(frameId, fragment)
    }
}

/**
 * The `fragment` is added to the container view with tag. The operation is
 * performed by the `fragmentManager`.
 */
fun AppCompatActivity.addFragmentToActivity(fragment: Fragment, tag: String) {
    supportFragmentManager.transact {
        add(fragment, tag)
    }
}

fun AppCompatActivity.setupActionBar(@IdRes toolbarId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(toolbarId))
    supportActionBar?.run {
        action()
    }
}
/**
 * Runs a FragmentTransaction, then calls commit().
 */
private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commit()
}


inline val FragmentActivity.smartJump
    get() = SmartJump.from(this)

inline fun <reified T : Activity> Activity.jumpTo(params: Bundle? = null) {
    val intent = Intent(this, T::class.java)
    params?.let { intent.putExtras(it) }
    startActivity(intent)
}

inline fun <reified T : Activity> FragmentActivity.jumpForResult(
        params: Bundle? = null,
        crossinline action: (Int, Intent?) -> Unit
) {
    val intent = Intent(this, T::class.java)
    params?.let { intent.putExtras(it) }
    smartJump.startForResult(intent, object : SmartJump.Callback {
        override fun onActivityResult(resultCode: Int, data: Intent?) {
            action(resultCode, data)
        }

    })
}