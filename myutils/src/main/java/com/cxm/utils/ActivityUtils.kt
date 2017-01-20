package com.cxm.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.cxm.mvp.BaseActivity
import java.util.*

/**
 * 該類用來管理Activity,提供了集中管理Activity的方法。(僅對繼承了BaseActivity的類有效)
 * 陈小默 16/8/19.
 */
class ActivityUtils {
    companion object {
        @JvmStatic val activities = ArrayList<BaseActivity<*>>()
        fun <A : Activity> startActivity(context: Context, activityClass: Class<A>) {
            val intent = Intent()
            intent.setClass(context, activityClass)
            context.startActivity(intent)
        }

        fun registerActivity(activity: BaseActivity<*>) {
            activities.add(activity)
        }

        fun unregisterActivity(activity: BaseActivity<*>) {
            activities.remove(activity)
        }

        inline fun finishActivities(runnable: () -> Unit) {
            synchronized(ActivityUtils::class.java, {
                val listIterator = activities.listIterator()
                while (listIterator.hasNext()) {
                    val next = listIterator.next()
                    listIterator.remove()
                    next.finish()
                }
            })
            runnable()
        }
    }
}