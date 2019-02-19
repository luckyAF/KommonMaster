package com.luckyaf.kommon.widget.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import com.luckyaf.kommon.R
import kotlinx.android.synthetic.main.activity_kommon_webview.*

/**
 * 类描述：
 * @author Created by luckyAF on 2019-02-19
 *
 */
@Suppress("unused")
class WebViewActivity : AppCompatActivity() {

    companion object {
        private const val KEY_INTENT_URL = "key_intent_url"
        fun openUrl(context: Context, url: String) {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(KEY_INTENT_URL, url)
            context.startActivity(intent)
        }
    }

    private var mUrl: String? = null
    private  var mWebView: WebView?= null
    private lateinit var mToolbar: Toolbar
    private lateinit var mProgressBar: ProgressBar
    private  var mWebViewSettings: WebSettings?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kommon_webview)
        mUrl = intent.getStringExtra(KEY_INTENT_URL)
        initView()
        initWebView()
        initWebSettings()
        initWebViewClient()
        initWebChromeClient()
    }



    private fun initView() {
        mWebView = findViewById(R.id.mWebView)
        mToolbar = findViewById(R.id.mToolbar)
        mProgressBar = findViewById(R.id.mProgressBar)
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initWebView() {

        mWebView?.loadUrl(mUrl)
        mWebViewSettings = mWebView?.settings
        mWebView?.setDownloadListener(MyDownloadListener())
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebSettings() {
        mWebViewSettings?.let {
            // 对于系统API在19以上的版本做了兼容。因为4.4以上的系统在
            // onPageFinished时再恢复图片加载，如果存在多张图片引用的
            // 是相同的src时，会只有一个image标签得到加载，因而对于这样的系统我们就先直接加载。
            it.loadsImagesAutomatically = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

            // 支持 JS
            it.javaScriptEnabled = true


            // 设置缓存
            // LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据。
            // LOAD_DEFAULT: 根据cache-control决定是否从网络上取数据。
            // LOAD_CACHE_NORMAL: API level 17中已经废弃, 从API level 11开始作用同LOAD_DEFAULT模式。
            // LOAD_NO_CACHE: 不使用缓存，只从网络获取数据。
            // LOAD_CACHE_ELSE_NETWORK：只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
            it.cacheMode = WebSettings.LOAD_DEFAULT

            //有时候网页需要自己保存一些关键数据,Android WebView 需要自己设置
            val cacheDir = applicationContext.cacheDir
            if (cacheDir != null) {
                val appCachePath = cacheDir.absolutePath

                // 开启DOM storage API 功能
                it.domStorageEnabled = true

                // 设置 数据库 缓存路径
                it.databaseEnabled = true
                it.setAppCacheEnabled(true)
                it.setAppCachePath(appCachePath)
            }

            // 设置默认编码
            it.defaultTextEncodingName = "utf-8"

            // 将图片调整到适合 webView 的大小
            it.useWideViewPort = false

            // 支持缩放
            it.setSupportZoom(true)

            // 支持内容重新布局
            it.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN

            // 多窗口
            it.setSupportMultipleWindows(true)

            // 设置可以访问文件
            it.allowFileAccess = true

            // 当 webView 调用 requestFocus 时为 webView 设置节点
            it.setNeedInitialFocus(true)

            // 设置支持缩放
            it.builtInZoomControls = true

            // 支持通过 JS 打开新窗口
            it.javaScriptCanOpenWindowsAutomatically = true

            // 缩放至至屏幕大小
            it.loadWithOverviewMode = true
        }

    }

    private fun initWebViewClient() {
        mWebView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

                view?.loadUrl(url)
                // return true: 代表在打开新的 url 是 WebView 就不会再加载这个 url 了
                //              所有处理都需要在 WebView中操作，包含加载
                // return false: 则系统就认为上层没有做处理， 接下来还是会继续加载这个 url
                //
                return super.shouldOverrideUrlLoading(view, url)
            }

            // 加载网页时替换某个资源
            override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
                val response: WebResourceResponse? = null
//                if (url.contains("logo")) {
//
//                }
                return response
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                mToolbar.title = "正在加载中..."
                showLoading()
                //                mProgress.setVisibility(View.VISIBLE);
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                hideLoading()
                mToolbar.title = view?.title
                //                mProgress.setVisibility(View.GONE);
                //                mTitle.setText(view.getTitle());
                val loadsImagesAutomatically = mWebViewSettings?.loadsImagesAutomatically?:false
                if (!loadsImagesAutomatically) {
                    mWebViewSettings?.loadsImagesAutomatically = true
                }
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                super.onReceivedError(view, request, error)
                Toast.makeText(this@WebViewActivity, "出错了", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initWebChromeClient() {
        mWebView?.webChromeClient = object : WebChromeClient() {
            //=========多窗口的问题==========================================================
            override fun onCreateWindow(view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message): Boolean {
                val result = view.hitTestResult
                val data = result.extra
                mWebView?.loadUrl(data)
                return true
            }
            //=========多窗口的问题==========================================================

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress > 80) {
                    hideLoading()
                }
            }

            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
                mToolbar.title = title
            }
        }
    }

    private fun showLoading() {
        mProgressBar.visibility = View.VISIBLE

    }

    private fun hideLoading(): Boolean {
        val result = mProgressBar.visibility == View.VISIBLE
        mProgressBar.visibility = View.INVISIBLE
        return result
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (hideLoading()) {
                return true
            }
            val goBack = mWebView?.canGoBack()?:false

            if(goBack){
                mWebView?.goBack()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        mWebView?.clearHistory()
        (mWebView?.parent as ViewGroup).removeView(mWebView)
        mWebView?.loadUrl("about:blank")
        mWebView?.stopLoading()
        mWebView?.webChromeClient = null
        mWebView?.webViewClient = null
        mWebView?.destroy()
        mWebView = null
    }

    private inner class MyDownloadListener : DownloadListener {
        override fun onDownloadStart(url: String, userAgent: String, contentDisposition: String, mimetype: String, contentLength: Long) {
            // 采用系统的download模块
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

}