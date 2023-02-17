package com.easyo.pairalarm.web

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.webkit.*
import timber.log.Timber

class CommonWebView(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
) : WebView(context, attrs, defStyle) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        with(settings) {
            userAgentString += "Android_inApp"
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            cacheMode = WebSettings.LOAD_NO_CACHE
            domStorageEnabled = true
            allowFileAccess = true
            defaultTextEncodingName = "UTF-8"
            setSupportMultipleWindows(true)
            databaseEnabled = true
            // 웹뷰 안의 텍스트는 안드로이드 시스템에 의해 글꼴의 크기가 바뀔 수 있는데 이를 고정한다
            textZoom = 100
            //https, http 호환 여부(https에서 http컨텐츠도 보여질수 있도록 함)
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webViewClient = CommonWebViewClient()
        webChromeClient = WebChromeClient()
    }

    private var mWebViewListener: WebViewListener? = null

    fun setWebViewListener(webViewListener: WebViewListener) {
        mWebViewListener = webViewListener
    }

    inner class CommonWebViewClient : WebViewClient() {
        // 웹뷰에서 url이 로드될 때 호출되는 함수
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            mWebViewListener?.onPageStarted(url, favicon)
            Timber.d("onPageStarted : ${url}\n")
        }

        // 웹뷰에서 url이 로드를 완료했을 때 호출되는 함수
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            mWebViewListener?.onPageFinished(url)
            Timber.d("onPageFinished : ${url}\n")
        }

        // 파라미터로 넘어온 url 에 의해 특정 리소스를 load 할 때 호출되는 콜백 메소드
        override fun onLoadResource(view: WebView?, url: String?) {
            super.onLoadResource(view, url)
            mWebViewListener?.onLoadResource(url)
            Timber.d("onLoadResource : ${url}\n")
        }

        // 현재 페이지의 url을 읽어와서 컨트롤할 수 있다
        // 이를 사용해서 앱에서 어떤 컨트롤을 할 수 있음(a태그로된 것을 activity로 연다던가...)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            mWebViewListener?.shouldOverrideUrlLoading(request)
            Timber.d("shouldOverrideUrlLoading : ${request?.url.toString()}\n")
            // true는 호스트가 제어를 하고 처리했다를 반환
            // false을 반환하여 WebView가 평소와 같이 URL로드를 진행하도록 처리
            return super.shouldOverrideUrlLoading(view, request)
        }

        // request 에 대해 에러가 발생했을 때 호출되는 콜백 메소드. error 변수에 에러에 대한 정보가 담겨져있음
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            mWebViewListener?.onReceivedError(request, error)
            Timber.d("onReceivedError : ${error?.description.toString()}\n")
        }

        // resource request를 가로채서 응답을 내리기 전에 호출되는 메소드
        // 이 메소드를 활용하여 특정 요청에 대한 필터링 및 응답 값 커스텀 가능
        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            Timber.d("shouldInterceptRequest : ${request?.url.toString()}\n")
            return super.shouldInterceptRequest(view, request)
        }
    }

    interface WebViewListener {
        fun onPageStarted(url: String?, favicon: Bitmap?)
        fun onPageFinished(url: String?)
        fun onLoadResource(url: String?)
        fun shouldOverrideUrlLoading(request: WebResourceRequest?)
        fun onReceivedError(request: WebResourceRequest?, error: WebResourceError?)
    }
}
