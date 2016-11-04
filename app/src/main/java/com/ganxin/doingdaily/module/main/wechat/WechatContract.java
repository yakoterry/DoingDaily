package com.ganxin.doingdaily.module.main.wechat;

import com.ganxin.doingdaily.framework.BasePresenter;
import com.ganxin.doingdaily.framework.BaseView;

/**
 * Description : 契约类  <br/>
 * author : WangGanxin <br/>
 * date : 2016/11/4 <br/>
 * email : ganxinvip@163.com <br/>
 */
interface WechatContract {
    interface View extends BaseView {

    }

    abstract class Presenter extends BasePresenter<View> {

    }
}
