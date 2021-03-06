package com.ganxin.doingdaily.module.wechat.article;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ganxin.doingdaily.R;
import com.ganxin.doingdaily.common.constants.ConstantValues;
import com.ganxin.doingdaily.common.data.model.WechatContentlistBean;
import com.ganxin.doingdaily.common.share.ShareController;
import com.ganxin.doingdaily.common.utils.SnackbarUtil;
import com.ganxin.doingdaily.common.utils.SystemHelper;
import com.ganxin.doingdaily.framework.BaseFragment;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import butterknife.BindView;

/**
 * Description : 微信详情页面  <br/>
 * author : WangGanxin <br/>
 * date : 2017/1/17 <br/>
 * email : mail@wangganxin.me <br/>
 */
public class WechatArticleFragment extends BaseFragment<WechatArticleContract.View, WechatArticleContract.Presenter> implements WechatArticleContract.View, UMShareListener {

    @BindView(R.id.containerLayout)
    LinearLayout containerLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.webView)
    WebView webView;

    private WechatContentlistBean bean;

    /**
     * @param bean
     * @return
     */
    public static WechatArticleFragment newInstance(WechatContentlistBean bean) {
        WechatArticleFragment fragment = new WechatArticleFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantValues.KEY_BEAN, bean);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int setContentLayout() {
        return R.layout.fragment_wechat_article;
    }

    @Override
    public void setUpView(View view) {

        AppCompatActivity mAppCompatActivity = (AppCompatActivity) getActivity();
        mAppCompatActivity.setSupportActionBar(toolbar);

        ActionBar actionBar = mAppCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setHasOptionsMenu(true); //处理 onOptionsItemSelected方法不被调用

        bean = (WechatContentlistBean) getArguments().getSerializable(ConstantValues.KEY_BEAN);

        if (bean != null) {
            mAppCompatActivity.setTitle(bean.getUserName());

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setBuiltInZoomControls(false);
            webSettings.setSupportZoom(false);
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

            webView.setOnKeyListener(keyListerner);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url); //处理 url 跳转webview 内部执行
                    return false;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {

                }

                @Override
                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl) {
                }

                @Override
                public void onPageFinished(WebView view, String url) {

                }
            });
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    progressbar.setProgress(newProgress);
                    if (newProgress == 100) {
                        progressbar.setVisibility(View.INVISIBLE);

                    } else {
                        progressbar.setVisibility(View.VISIBLE);
                    }
                }
            });
            webView.loadUrl(bean.getUrl());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBack();
                return false;
            case R.id.action_share:
                share();
                break;
            case R.id.action_copy:
                SystemHelper.SystemCopy(getActivity(), bean.getWeixinNum());
                SnackbarUtil.shortSnackbar(containerLayout, getString(R.string.tips_copy_success), SnackbarUtil.Info);
                break;
            case R.id.action_browser:
                SystemHelper.SystemBrowser(getActivity(), bean.getUrl());
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_wechat_article, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    protected WechatArticleContract.Presenter setPresenter() {
        return new WechatArticlePresenter();
    }

    private void share() {
        if (bean != null) {
            ShareController.getInstance().shareLink(mActivity, bean.getUrl(), bean.getTitle(), this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webView.destroy();
        ShareController.getInstance().release(mActivity);
    }

    View.OnKeyListener keyListerner = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            try {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                    if (webView != null && webView.canGoBack()) {
                        webView.goBack();
                        return true;
                    } else {
                        onBack();
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    };

    @Override
    public void onResult(SHARE_MEDIA share_media) {
        SnackbarUtil.shortSnackbar(containerLayout, getString(R.string.tips_share_success), SnackbarUtil.Info);
    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {

    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {

    }
}
