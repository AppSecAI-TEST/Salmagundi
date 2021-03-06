package org.caojun.rcn

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.socks.library.KLog
import org.caojun.rcn.utils.ChineseNameUtils
import org.caojun.rcn.utils.DiaryUtils
import com.google.android.gms.ads.InterstitialAd
import org.caojun.widget.MultiRadioGroup


class MainActivity : AppCompatActivity(), RewardedVideoAdListener {

    private var btnGenerate:Button? = null
    private var isSurnameChecked: Boolean = false
    private var isNameChecked: Boolean = false
    private var mRewardedVideoAd: RewardedVideoAd? = null
    private var mInterstitialAd: InterstitialAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAd()

        var btnSurname: Button = findViewById(R.id.btnSurname)
        var btnName: Button = findViewById(R.id.btnName)
        var etSurname: EditText = findViewById(R.id.etSurname)
        var etName: EditText = findViewById(R.id.etName)
        var rgSurname: MultiRadioGroup = findViewById(R.id.rgSurname)
        var rgName: MultiRadioGroup = findViewById(R.id.rgName)
        var webView: WebView = findViewById(R.id.webView);
        btnGenerate = findViewById(R.id.btnGenerate)

        val surnameType: Array<out String> = resources.getStringArray(R.array.surname_type)
        var rbSurnames: Array<RadioButton?> = arrayOfNulls<RadioButton>(surnameType.size)
        for (i in surnameType.indices) {
            rbSurnames[i] = RadioButton(this)
            rbSurnames[i]?.text = surnameType[i]
            rgSurname.addView(rbSurnames[i])
        }

        val nameType: Array<out String> = resources.getStringArray(R.array.name_type)
        var rbNames: Array<RadioButton?> = arrayOfNulls<RadioButton>(nameType.size)
        for (i in nameType.indices) {
            rbNames[i] = RadioButton(this)
            rbNames[i]?.text = nameType[i]
            rgName.addView(rbNames[i])
        }

        rgSurname.setOnCheckedChangeListener({ group, _ ->
            isSurnameChecked = true
            checkButtonEnable(rgSurname, rgName)
            val index = group.indexOfChild(group.findViewById(group.checkedRadioButtonId))
            etSurname.isEnabled = index == group.childCount - 1
        })

        rgName.setOnCheckedChangeListener({ group, _ ->
            isNameChecked = true
            checkButtonEnable(rgSurname, rgName)
            val index = group.indexOfChild(group.findViewById(group.checkedRadioButtonId))
            etName.isEnabled = index == group.childCount - 1
        })

        btnSurname.setOnClickListener({ showExplain(webView, etSurname.text.toString()) })

        btnSurname.setOnLongClickListener({
            showFullName(etSurname, etName, webView)
            true
        })

        btnName.setOnClickListener({ showExplain(webView, etName.text.toString()) })

        btnName.setOnLongClickListener({
            showExplain(webView, etName.text.toString(), true)
            true
        })

        etSurname.isEnabled = false
        etName.isEnabled = false
        btnGenerate?.isEnabled = isSurnameChecked and isNameChecked
        btnGenerate?.setOnClickListener({ doGenerate(rgSurname, etSurname, rgName, etName) })
        checkButtonCount(false, false)
    }

    private fun doGenerate(rgSurname:MultiRadioGroup, etSurname:EditText, rgName:MultiRadioGroup, etName:EditText) {
        if (checkButtonCount(true, false)) {
            showAd()
            return
        }
        val surnameType = rgSurname.indexOfChild(rgSurname.findViewById(rgSurname.checkedRadioButtonId))
        if (surnameType != ChineseNameUtils.Type_Surname_Custom) {
            val surname = ChineseNameUtils.getSurname(this, surnameType)
            etSurname.setText(surname)
        }
        val nameType = rgName.indexOfChild(rgName.findViewById(rgName.checkedRadioButtonId))
        if (nameType != ChineseNameUtils.Type_Name_Custom) {
            val name = ChineseNameUtils.getName(nameType)
            etName.setText(name)
        }
    }

    private fun showExplain(webView: WebView, text: String) {
        showExplain(webView, text, false)
    }

    private fun showExplain(webView: WebView, text: String, isFullName: Boolean) {
        if (TextUtils.isEmpty(text)) {
            return
        }
        var url = "http://hanyu.baidu.com/zici/s?wd=" + text
        if (isFullName) {
            val urls = resources.getStringArray(R.array.search_url)
//            val index = ChineseNameUtils.getRandom(0, urls.size - 1)
            val index = 2
            url = urls[index] + text
            KLog.d("url", url)
        }
        webView.loadUrl(url)
    }

    private fun showFullName(etSurname: EditText, etName: EditText, webView: WebView) {
        val surname = etSurname.text.toString()
        if (TextUtils.isEmpty(surname)) {
            return
        }
        val name = etName.text.toString()
        if (TextUtils.isEmpty(name)) {
            return
        }
        showExplain(webView, surname + name, true)
    }

    private fun checkButtonEnable(rgSurname:MultiRadioGroup, rgName:MultiRadioGroup) {
        btnGenerate?.isEnabled = isSurnameChecked and isNameChecked
        if (btnGenerate!!.isEnabled) {
            val indexSurname = rgSurname.indexOfChild(rgSurname.findViewById(rgSurname.checkedRadioButtonId))
            val indexName = rgName.indexOfChild(rgName.findViewById(rgName.checkedRadioButtonId))
            if (indexSurname == rgSurname.childCount - 1 && indexName == rgName.childCount - 1) {
                //姓和名都选中自定义
                btnGenerate?.isEnabled = false
            }
        }
    }

    private fun checkButtonCount(isDone:Boolean, isAdd:Boolean): Boolean {
        val diary = DiaryUtils.queryToday(this)
        val count = diary!!.cntName
        if (isDone) {
            if (isAdd) {
                diary!!.cntName = (diary.cntName + getResources().getInteger(R.integer.add_count)).toByte()
            } else if (diary!!.cntName > 0) {
                diary!!.cntName--
            }
            DiaryUtils.update(this, diary)
        }
        btnGenerate?.text = String.format(getString(R.string.generate), diary!!.cntName.toString())
        return count <= 0
    }

    //广告
    override fun onResume() {
        mRewardedVideoAd?.resume(this)
        super.onResume()
    }

    override fun onPause() {
        mRewardedVideoAd?.pause(this)
        super.onPause()
    }

    override fun onDestroy() {
        mRewardedVideoAd?.destroy(this)
        super.onDestroy()
    }

    private fun initAd() {
//        MobileAds.initialize(this, getString(R.string.admob_app_id));
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        mRewardedVideoAd?.rewardedVideoAdListener = this

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd?.adUnitId = getString(R.string.admob_page_unit_id)
        mInterstitialAd?.adListener = object : AdListener() {
            override fun onAdClosed() {
                KLog.d("InterstitialAd", "onAdClosed")
                checkButtonCount(true, true)
            }

            override fun onAdImpression() {
                KLog.d("InterstitialAd", "onAdImpression")
            }

            override fun onAdLeftApplication() {
                KLog.d("InterstitialAd", "onAdLeftApplication")
            }

            override fun onAdClicked() {
                KLog.d("InterstitialAd", "onAdClicked")
            }

            override fun onAdFailedToLoad(p0: Int) {
                KLog.d("InterstitialAd", "onAdFailedToLoad: " + p0)
                loadAd(false)
            }

            override fun onAdOpened() {
                KLog.d("InterstitialAd", "onAdOpened")
            }

            override fun onAdLoaded() {
                KLog.d("InterstitialAd", "onAdLoaded")
            }
        }
        loadAd(true)
    }

    private fun loadAd(loadVideo:Boolean) {
        val adRequest = AdRequest.Builder().build()
//        val adRequest = AdRequest.Builder().setRequestAgent("android_studio:ad_template").build()
//        val adRequest = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build()
        if (loadVideo) {
            if (!mRewardedVideoAd!!.isLoaded) {
                mRewardedVideoAd?.loadAd(getString(R.string.admob_video_unit_id), adRequest)
            }
        } else if (!mInterstitialAd!!.isLoaded) {
            mInterstitialAd?.loadAd(adRequest)
        }
    }

    private fun showAd() {
        if (mRewardedVideoAd!!.isLoaded) {
            mRewardedVideoAd?.show()
        } else if (mInterstitialAd!!.isLoaded) {
            mInterstitialAd?.show()
        } else {
            loadAd(true)
            Toast.makeText(this, R.string.ad_no_loaded, Toast.LENGTH_SHORT).show()
        }
    }

    //视频式广告
    override fun onRewardedVideoAdClosed() {
        KLog.d("RewardedVideoAd", "onRewardedVideoAdClosed")
    }

    override fun onRewardedVideoAdLeftApplication() {
        KLog.d("RewardedVideoAd", "onRewardedVideoAdLeftApplication")
    }

    override fun onRewardedVideoAdLoaded() {
        KLog.d("RewardedVideoAd", "onRewardedVideoAdLoaded")
    }

    override fun onRewardedVideoAdOpened() {
        KLog.d("RewardedVideoAd", "onRewardedVideoAdOpened")
    }

    override fun onRewarded(p0: RewardItem?) {
        KLog.d("RewardedVideoAd", "onRewarded")
        checkButtonCount(true, true)
    }

    override fun onRewardedVideoStarted() {
        KLog.d("RewardedVideoAd", "onRewardedVideoStarted")
    }

    override fun onRewardedVideoAdFailedToLoad(p0: Int) {
        KLog.d("RewardedVideoAd", "onRewardedVideoAdFailedToLoad: " + p0)
        loadAd(false)
    }
}
