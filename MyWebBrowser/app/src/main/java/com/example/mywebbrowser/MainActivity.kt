package com.example.mywebbrowser

import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.net.http.SslError
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.*
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.mywebbrowser.databinding.ActivityMainBinding
import java.net.URL

class MainActivity : AppCompatActivity() {
    companion object {
        const val NAVER_URL = "https://www.naver.com"
        const val GOOGLE_URL = "https://www.google.com"
        const val BING_URL = "https://www.bing.com"
        const val REQUEST_CODE = 1010
    }

    private val backMenuItem: ActionMenuItemView by lazy {
        findViewById(R.id.back)
    }

    private val forwardMenuItem: ActionMenuItemView by lazy {
        findViewById(R.id.forward)
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        requestPermission()
        initView()
    }


    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else
            super.onBackPressed()
    }



    private fun initView() {
        setSupportActionBar(binding.webToolbar)

        binding.refreshLayout.setOnRefreshListener {
            binding.webView.reload()
        }

        binding.contentLoadingProgressBar.hide()

        binding.webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                onPageLoadingStarted(url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                onPageLoadingFinished()
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return true
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.let {
                    it.proceed()
                }
            }

        }

        binding.searchEditText.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                var input = textView.text.toString()

                search(input)
            }


            true
        }



        binding.webView.apply {
            // 하이퍼링크 클릭시 새창 띄우기 방지
            webChromeClient =
                WebChromeClient() // 크롬환경에 맞는 세팅을 해줌. 특히, 알람등을 받기위해서는 꼭 선언해주어야함 (alert같은 경우)
            settings.javaScriptEnabled = true // 자바스크립트 허용
            settings.javaScriptCanOpenWindowsAutomatically = false
            // 팝업창을 띄울 경우가 있는데, 해당 속성을 추가해야 window.open() 이 제대로 작동 , 자바스크립트 새창도 띄우기 허용여부
            settings.setSupportMultipleWindows(true) // 새창 띄우기 허용 여부 (멀티뷰)
            settings.loadsImagesAutomatically = true // 웹뷰가 앱에 등록되어 있는 이미지 리소스를 자동으로 로드하도록 설정하는 속성
            settings.useWideViewPort = true // 화면 사이즈 맞추기 허용 여부
            settings.loadWithOverviewMode = true // 메타태그 허용 여부
            settings.setSupportZoom(true) // 화면 줌 허용여부
            settings.builtInZoomControls = true // 화면 확대 축소 허용여부
            settings.displayZoomControls = false // 줌 컨트롤 없애기.
            settings.cacheMode = WebSettings.LOAD_NO_CACHE // 웹뷰의 캐시 모드를 설정하는 속성으로써 5가지 모드
            /*
            (1) LOAD_CACHE_ELSE_NETWORK 기간이 만료돼 캐시를 사용할 수 없을 경우 네트워크를 사용합니다.
            (2) LOAD_CACHE_ONLY 네트워크를 사용하지 않고 캐시를 불러옵니다.
            (3) LOAD_DEFAULT 기본적인 모드로 캐시를 사용하고 만료된 경우 네트워크를 사용해 로드합니다.
            (4) LOAD_NORMAL 기본적인 모드로 캐시를 사용합니다.
            (5) LOAD_NO_CACHE 캐시모드를 사용하지 않고 네트워크를 통해서만 호출합니다.
             */
            settings.domStorageEnabled =
                true // 로컬 스토리지 사용 여부를 설정하는 속성으로 팝업창등을 '하루동안 보지 않기' 기능 사용에 필요
            settings.allowContentAccess // 웹뷰 내에서 파일 액세스 활성화 여부
            settings.userAgentString = "app" // 웹에서 해당 속성을 통해 앱에서 띄운 웹뷰로 인지 할 수 있도록 합니다.
            settings.defaultTextEncodingName = "UTF-8" // 인코딩 설정
            settings.databaseEnabled = true //Database Storage API 사용 여부 설정
        }


        binding.webView.loadUrl(GOOGLE_URL)
        registerForContextMenu(binding.webView)
    }

    private fun onPageLoadingStarted(url:String?){
        binding.contentLoadingProgressBar.show()
        binding.view.visibility = View.VISIBLE
        binding.searchEditText.setText(url)
    }


    private fun onPageLoadingFinished(){
        backMenuItem.isEnabled = binding.webView.canGoBack()
        if(!backMenuItem.isEnabled)
            backMenuItem.setTextColor(Color.parseColor("#CCCCCC"))

        forwardMenuItem.isEnabled = binding.webView.canGoForward()
        if(!forwardMenuItem.isEnabled)
            forwardMenuItem.setTextColor(Color.parseColor("#CCCCCC"))

        binding.refreshLayout.isRefreshing = false
        binding.contentLoadingProgressBar.hide()
        binding.view.visibility = View.INVISIBLE

    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.webview_menu, menu)

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.sharePage -> share()
            R.id.openDefaultBrowser -> openDefaultBrowser()
        }



        return super.onContextItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        when (item.itemId) {
            R.id.forward -> goForwardPage()

            R.id.back -> goBackPage()

            R.id.home -> goInputPage(GOOGLE_URL)

            R.id.google -> goInputPage(GOOGLE_URL)

            R.id.bing -> goInputPage(BING_URL)

            R.id.naver -> goInputPage(NAVER_URL)

            R.id.nextPage -> goForwardPage()

            R.id.phoneNumber -> call()

            R.id.sendMessage -> sendMessage()

            R.id.sendEmail -> sendEmail()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun goInputPage(url: String) {
        binding.webView.loadUrl(url)
    }


    private fun goForwardPage() {
        if (binding.webView.canGoForward())
            binding.webView.goForward()
    }


    private fun goBackPage() {
        if (binding.webView.canGoBack())
            binding.webView.goBack()
    }


    private fun search(input: String) {

        if (URLUtil.isValidUrl(input)) {
            binding.webView.loadUrl(input)
        } else {
            val prefix = "https://www.google.com/search?q="
            binding.webView.loadUrl(prefix + input)
        }


        binding.searchEditText.setText(binding.webView.url)
    }

    private fun openDefaultBrowser() {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(GOOGLE_URL)

        }

        if (intent.resolveActivity(packageManager) != null)
            startActivity(intent)
    }

    private fun share() {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(intent, null)

        if (intent.resolveActivity(packageManager) != null)
            startActivity(shareIntent)

    }

    private fun call() {
        val intent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:010-3832-7358")
        }

        if (intent.resolveActivity(packageManager) != null)
            startActivity(intent)

    }

    private fun sendMessage(number: String = "010-1111-1111", text: String = "text") {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("sms:$number")
            putExtra(Intent.EXTRA_TEXT, text)
        }

        if (intent.resolveActivity(packageManager) != null)
            startActivity(intent)

    }

    private fun sendEmail(
        email: String = "toJohn",
        subject: String = "subject",
        text: String = "text"
    ) {

        val intent = Intent().apply {
            action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }

        if (intent.resolveActivity(packageManager) != null)
            startActivity(intent)

    }

    private fun requestPermission() {

        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.CALL_PHONE,

                ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        applicationContext,
                        android.Manifest.permission.INTERNET,
                    ) == PackageManager.PERMISSION_GRANTED -> {
            }

            else -> {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.CALL_PHONE
                    ),
                    REQUEST_CODE
                )

            }


        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {

            } else
                finish()
        }
    }

}