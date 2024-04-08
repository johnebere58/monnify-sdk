package com.teamapt.monnify.sdk.module.view.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import com.monnify.monnify_payment_sdk.R
import com.teamapt.monnify.sdk.customview.TouchableWebView
import com.teamapt.monnify.sdk.data.model.CardChargeProvider
import com.teamapt.monnify.sdk.module.vm.CardPaymentViewModel
import com.teamapt.monnify.sdk.util.DetectHtml
import com.teamapt.monnify.sdk.util.Logger
import java.net.URLEncoder

class CardPaymentSecure3DAuthenticationFragment : BaseFragment() {

    private lateinit var cardPaymentViewModel: CardPaymentViewModel

    private lateinit var webView: TouchableWebView
    private lateinit var webViewProgressBar: ProgressBar

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        cardPaymentViewModel =
            ViewModelProvider(requireParentFragment()).get(CardPaymentViewModel::class.java)
        cardPaymentViewModel.startListening()

        cardPaymentViewModel.liveError.observe(this) { event ->
            event?.getContentIfNotHandled()?.let {
                errorTextView?.setTextAndMakeVisible(it)
            }
        }

        cardPaymentViewModel.activeListeningState.observe(this) {
            if (it == false) {
                errorTextView?.setTextAndMakeVisibleWithTimeout(
                    getString(R.string.stomp_disconnect),
                    100
                )
            } else {
                errorTextView?.removeErrorView()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.plugin_fragment_card_secure_3d_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initWebView(view)
        prepareWebView()
        loadUrl()
    }

    private fun initWebView(view: View) {
        webView = view.findViewById(R.id.webView)
        webViewProgressBar = view.findViewById(R.id.webViewProgressBar)
    }

    @SuppressLint("NewApi", "SetJavaScriptEnabled")
    private fun prepareWebView() {

        val secure3dData = cardPaymentViewModel.cardPaymentResponse.secure3dData

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.isScrollbarFadingEnabled = false
        webView.isVerticalScrollBarEnabled = true
        webView.isHorizontalScrollBarEnabled = true
        webView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true

        try {
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        } catch (e: Exception) {

        }

        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                Logger.log(this, "Current $url loading in WebView")
                super.onPageStarted(view, url, favicon)
                webViewProgressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                webViewProgressBar.visibility = View.GONE

                if (url == secure3dData?.callBackUrl) {
                    cardPaymentViewModel.authorizeCardSecure3D()
                    parentFragmentManager.beginTransaction()
                        .remove(this@CardPaymentSecure3DAuthenticationFragment).commit()
                    return
                }

            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                webViewProgressBar.progress = progress
            }
        }

    }

    private fun loadUrl() {
        val cardPaymentResponse = cardPaymentViewModel.cardPaymentResponse

        val secure3dData = cardPaymentResponse.secure3dData

        when (cardPaymentViewModel.getCardPaymentProvider()) {

            CardChargeProvider.PAYU -> {
                if (secure3dData?.method == "GET") {
                    webView.loadUrl(secure3dData.redirectUrl!!)

                } else if (secure3dData?.method == "POST") {
                    webView.postUrl(secure3dData.redirectUrl!!, "".toByteArray())
                }
            }

            CardChargeProvider.IPG -> {
                if (secure3dData?.method == "GET") {
                    webView.loadUrl(secure3dData.redirectUrl!!)

                } else if (secure3dData?.method == "POST") {
                    val postData = "MD=${URLEncoder.encode(secure3dData.md, "UTF-8")}" +
                            "&PaReq=${URLEncoder.encode(secure3dData.paReq, "UTF-8")}" +
                            "&TermUrl=${URLEncoder.encode(secure3dData.termUrl, "UTF-8")}"

                    webView.postUrl(secure3dData.redirectUrl!!, postData.toByteArray())
                }
            }
            CardChargeProvider.UNKNOWN -> {
                if (secure3dData?.method == "GET") {
                    webView.loadUrl(secure3dData.redirectUrl!!)

                } else if (secure3dData?.method == "POST") {
                    webView.postUrl(secure3dData.redirectUrl!!, "".toByteArray())
                } else if (DetectHtml.isHtml(cardPaymentResponse.message)) {

                    webView.loadData(cardPaymentResponse.message!!, "text/html", "utf-8")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cardPaymentViewModel.stopListening()
    }
}
