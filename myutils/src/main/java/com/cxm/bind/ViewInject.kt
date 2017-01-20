package com.cxm.bind

/**
 * 視圖注入標記,參數傳入視圖的ID
 * @author 陈小默 16/8/17.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ViewInject(val value: Int)