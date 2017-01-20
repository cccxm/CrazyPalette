package com.cxm.mvp

/**
 * MVP模式抽象主持人接口
 * 陈小默 16/8/19.
 */
interface IPresenter {
    /**
     * Don't hand view
     */
    fun start(): Unit
}