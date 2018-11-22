package com.luckyaf.kommon.widget.popup

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.annotation.*
import android.support.v4.widget.PopupWindowCompat
import android.transition.Transition
import android.util.Log
import android.view.*
import android.widget.PopupWindow

/**
 * 类描述：
 * @author Created by luckyAF on 2018/11/22
 *
 */
@Suppress("unused")
class SmartPopup(private val mContext: Context) : PopupWindow.OnDismissListener {

    companion object {
        private const val TAG = "SmartPopup"

         fun create(context: Context) : SmartPopup{
            return SmartPopup(context)
        }

    }

    /**
     * PopupWindow对象
     */
    private val mPopupWindow: PopupWindow by lazy { PopupWindow() }

    /**
     * popup内部view
     */
    private var mContentView: View? = null

    private var mLayoutId: Int = 0

    private var mWidth: Int = 0

    private var mHeight: Int = 0


    private var mOnDismissListener: PopupWindow.OnDismissListener? = null


    private var mRealMeasureCallback: OnRealMeasureCallback? = null


    /**
     * 弹出pop时，背景是否变暗
     */
    private var isBackgroundDim: Boolean = false


    /**
     * 背景变暗时透明度  默认0.7
     */
    private var mDimValue = 0.7f
    /**
     * 背景变暗颜色
     */
    @ColorInt
    private var mDimColor = Color.BLACK

    /**
     * 背景变暗的view
     */
    private var mDimView: ViewGroup? = null


    /**
     * 外部点击消失
     */
    private var mOutsideTouchable = true

    /**
     * 锚点view
     */
    private var mAnchorView: View? = null


    @YGravity
    private var mYGravity = YGravity.BELOW
    @XGravity
    private var mXGravity = XGravity.LEFT


    private var mOffsetX: Int = 0
    private var mOffsetY: Int = 0

    /**
     * 是否需要重新测量宽高
     */
    private var isNeedReMeasure = true
    /**
     * 是否根据锚点显示
     */
    private var isAtAnchorViewMethod = false


    /**
     * 是否正在显示
     *
     * @return 是否正在显示
     */
    val isShowing: Boolean
        get() = mPopupWindow.isShowing


    private var mOnViewListener: OnViewListener? = null

    /**
     * 获取PopupWindow中加载的view
     *
     * @return getContentView
     */
    private val contentView: View
        get() = mPopupWindow.contentView

    fun setOnViewListener(listener: OnViewListener): SmartPopup {
        this.mOnViewListener = listener
        return this
    }

    fun apply(): SmartPopup {

        // 初始化contentView
        initContentView()
        // 回调已初始化的contentView
        mContentView?.let {
            mOnViewListener?.drawView(it, this)

        }
        // 初始化外部点击的问题
        initFocusAndBack()
        // 设置消失监听
        mPopupWindow.setOnDismissListener(this)

        // 在相关的View被移除时，window也自动移除。避免当Fragment退出后，Fragment中弹出的PopupWindow还存在于界面上。

        mAnchorView?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {}
            override fun onViewDetachedFromWindow(v: View) {
                if (isShowing) {
                    dismiss()
                }
            }
        })

        return this
    }

    fun setContentView(contentView: View): SmartPopup {
        return setContentView(contentView, 0, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun setContentView(@LayoutRes layoutId: Int): SmartPopup {
        return setContentView(null, layoutId, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun setContentView(contentView: View, width: Int, height: Int): SmartPopup {
        return setContentView(contentView, 0, width, height)
    }

    fun setContentView(@LayoutRes layoutId: Int, width: Int, height: Int): SmartPopup {
        return setContentView(null, layoutId, width, height)
    }

    private fun setContentView(contentView: View?, @LayoutRes layoutId: Int, width: Int, height: Int): SmartPopup {
        this.mContentView = contentView
        this.mLayoutId = layoutId
        setWidth(width)
        setHeight(height)
        return this
    }


    /**
     * 设置出入动画
     *
     * @param animationStyle style
     * @return self
     */
    fun setAnimationStyle(@StyleRes animationStyle: Int): SmartPopup {
        if (animationStyle != 0) {
            mPopupWindow.animationStyle = animationStyle
        }
        return this
    }

    /**
     * 是否可以点击PopupWindow之外的地方dismiss
     *
     * @param outsideTouchable 外部点击消失
     * @return self
     */
    fun setOutsideTouchable(outsideTouchable: Boolean): SmartPopup {
        this.mOutsideTouchable = outsideTouchable
        return this
    }




    /**
     * 背景变暗支持api>=18
     *
     * @param isDim 背景是否变暗
     * @return self
     */
    fun setBackgroundDimEnable(isDim: Boolean): SmartPopup {
        this.isBackgroundDim = isDim
        return this
    }

    /**
     * 背景透明度
     *
     * @param dimValue 透明度
     * @return self
     */
    fun setDimValue(@FloatRange(from = 0.0, to = 1.0) dimValue: Float): SmartPopup {
        this.mDimValue = dimValue
        return this
    }

    /**
     * 背景颜色
     *
     * @param color 颜色
     * @return self
     */
    fun setDimColor(@ColorInt color: Int): SmartPopup {
        this.mDimColor = color
        return this
    }

    /**
     * 背景图
     *
     * @param dimView view
     * @return self
     */
    fun setDimView(dimView: ViewGroup): SmartPopup {
        this.mDimView = dimView
        return this
    }

    /**
     * 设置进入动画
     *
     * @param enterTransition 进入动画
     * @return self
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    fun setEnterTransition(enterTransition: Transition): SmartPopup {
        mPopupWindow.enterTransition = enterTransition
        return this
    }

    /**
     * 设置退出动画
     *
     * @param exitTransition 进入动画
     * @return self
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    fun setExitTransition(exitTransition: Transition): SmartPopup {
        mPopupWindow.exitTransition = exitTransition
        return this
    }


    /**
     * 设置消失监听器
     *
     * @param listener 监听
     * @return self
     */
    fun setOnDismissListener(listener: PopupWindow.OnDismissListener): SmartPopup {
        this.mOnDismissListener = listener
        return this
    }

    fun setOnRealMeasureCallback(callback: OnRealMeasureCallback): SmartPopup {
        this.mRealMeasureCallback = callback
        return this
    }


    /**
     * 消失
     */
    fun dismiss() {
        mPopupWindow.dismiss()
    }


    override fun onDismiss() {
        handleDismiss()
    }

    /**
     * PopupWindow消失后处理一些逻辑
     */
    private fun handleDismiss() {
        mOnDismissListener?.onDismiss()

        //清除背景变暗
        clearBackgroundDim()
        if (mPopupWindow.isShowing) {
            mPopupWindow.dismiss()
        }
    }

    /**
     * 检查是否调用了 apply() 方法
     *
     * @param isAtAnchorView 是否是 showAt
     */
    private fun checkIsApply(isAtAnchorView: Boolean) {
        if (this.isAtAnchorViewMethod != isAtAnchorView) {
            this.isAtAnchorViewMethod = isAtAnchorView
        }
        apply()

    }


    fun redrawView(listener: OnViewListener){
        listener.drawView(contentView,this)
    }


    /**
     * 使用此方法需要在创建的时候调用setAnchorView()等属性设置{@see setAnchorView()}
     */
    fun showAsDropDown() {
        mAnchorView ?: return
        showAsDropDown(mAnchorView, mOffsetX, mOffsetY)
    }

    /**
     * PopupWindow自带的显示方法  根据锚点和偏移量显示 popup
     *
     * @param anchor  锚点
     * @param offsetX x轴偏移
     * @param offsetY y轴偏移
     */
    fun showAsDropDown(anchor: View?, offsetX: Int, offsetY: Int) {
        anchor ?: return
        //防止忘记调用 apply() 方法
        checkIsApply(false)
        handleBackgroundDim()
        mAnchorView = anchor
        mOffsetX = offsetX
        mOffsetY = offsetY
        //是否重新获取宽高
        if (isNeedReMeasure) {
            registerOnGlobalLayoutListener()
        }
        mPopupWindow.showAsDropDown(anchor, mOffsetX, mOffsetY)
    }

    fun showAsDropDown(anchor: View?) {
        showAsDropDown(anchor, 0, 0)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun showAsDropDown(anchor: View?, offsetX: Int, offsetY: Int, gravity: Int) {
        anchor ?: return
        //防止忘记调用 apply() 方法
        checkIsApply(false)
        handleBackgroundDim()
        mAnchorView = anchor
        mOffsetX = offsetX
        mOffsetY = offsetY
        //是否重新获取宽高
        if (isNeedReMeasure) {
            registerOnGlobalLayoutListener()
        }
        PopupWindowCompat.showAsDropDown(mPopupWindow, anchor, mOffsetX, mOffsetY, gravity)
    }

    fun showAtLocation(parent: View, gravity: Int, offsetX: Int, offsetY: Int) {
        //防止忘记调用 apply() 方法
        checkIsApply(false)
        handleBackgroundDim()
        mAnchorView = parent
        mOffsetX = offsetX
        mOffsetY = offsetY
        //是否重新获取宽高
        if (isNeedReMeasure) {
            registerOnGlobalLayoutListener()
        }
        mPopupWindow.showAtLocation(parent, gravity, mOffsetX, mOffsetY)
    }

    /**
     * 相对anchor view显示
     *
     *
     * 使用此方法需要在创建的时候调用setAnchorView()等属性设置{@see setAnchorView()}
     *
     *
     * 注意：如果使用 VerticalGravity 和 HorizontalGravity 时，请确保使用之后 PopupWindow 没有超出屏幕边界，
     * 如果超出屏幕边界，VerticalGravity 和 HorizontalGravity 可能无效，从而达不到你想要的效果。
     */
    fun showAtAnchorView() {
        mAnchorView ?: return
        showAtAnchorView(mAnchorView!!, mYGravity, mXGravity)
    }

    /**
     * 相对anchor view显示，适用 宽高不为match_parent
     *
     *
     * 注意：如果使用 VerticalGravity 和 HorizontalGravity 时，请确保使用之后 PopupWindow 没有超出屏幕边界，
     * 如果超出屏幕边界，VerticalGravity 和 HorizontalGravity 可能无效，从而达不到你想要的效果。
     *
     * @param anchor   锚点
     * @param yGravity 垂直方向的对齐方式
     * @param xGravity 水平方向的对齐方式
     * @param x        水平方向的偏移
     * @param y        垂直方向的偏移
     */
    @JvmOverloads
    fun showAtAnchorView(anchor: View, @YGravity yGravity: Int, @XGravity xGravity: Int, x: Int = 0, y: Int = 0) {
        //防止忘记调用 apply() 方法
        checkIsApply(true)
        mAnchorView = anchor
        mOffsetX = x
        mOffsetY = y
        mYGravity = yGravity
        mXGravity = xGravity
        //处理背景变暗
        handleBackgroundDim()
        val newX = calculateX(anchor, xGravity, mWidth, mOffsetX)
        val newY = calculateY(anchor, yGravity, mHeight, mOffsetY)
        //是否重新获取宽高
        if (isNeedReMeasure) {
            registerOnGlobalLayoutListener()
        }
        PopupWindowCompat.showAsDropDown(mPopupWindow, anchor, newX, newY, Gravity.NO_GRAVITY)

    }


    /**
     * 设置宽度
     *
     * @param width width
     * @return self
     */
    fun setWidth(width: Int): SmartPopup {
        mWidth = width
        if (mWidth > 0 || mWidth == ViewGroup.LayoutParams.WRAP_CONTENT || mWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
            mPopupWindow.width = mWidth
        } else {
            mPopupWindow.width = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        return this
    }


    /**
     * 获取PopupWindow 宽
     *
     * @return 宽
     */
    fun getWidth(): Int {
        return mWidth
    }

    /**
     * 设置content 高度
     *
     * @param height 高度
     * @return self
     */
    fun setHeight(height: Int): SmartPopup {
        mHeight = height
        if (mHeight > 0 || mHeight == ViewGroup.LayoutParams.WRAP_CONTENT || mHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
            mPopupWindow.height = mHeight
        } else {
            mPopupWindow.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        return this
    }

    /**
     * 获取PopupWindow 高
     *
     * @return 高
     */
    fun getHeight(): Int {
        return mHeight
    }


    /*                内部方法                 */

    /**
     * 初始化contentView
     */
    private fun initContentView() {
        if (mContentView == null) {
            if (mLayoutId != 0) {
                mContentView = LayoutInflater.from(mContext).inflate(mLayoutId, null)
            } else {
                throw IllegalArgumentException("The content view is null,the layoutId=$mLayoutId,context=$mContext")
            }
        }
        mPopupWindow.contentView = mContentView

        //测量contentView大小
        //可能不准
        measureContentView()
        //获取contentView的精准大小
        registerOnGlobalLayoutListener()

    }

    /**
     * 是否需要测量 contentView的大小
     * 如果需要重新测量并为宽高赋值
     * 注：此方法获取的宽高可能不准确 MATCH_PARENT时无法获取准确的宽高
     */
    private fun measureContentView() {
        val contentView = contentView
        if (mWidth <= 0 || mHeight <= 0) {
            //测量大小
            contentView.measure(0, View.MeasureSpec.UNSPECIFIED)
            if (mWidth <= 0) {
                mWidth = contentView.measuredWidth
            }
            if (mHeight <= 0) {
                mHeight = contentView.measuredHeight
            }
        }
    }

    /**
     * 初始化 外部点击 返回等
     */
    private fun initFocusAndBack() {
        if (!mOutsideTouchable) {
            // 假如外部点击无效
            // 但是返回键还是可以dismiss
            //from https://github.com/pinguo-zhouwei/CustomPopwindow
            mPopupWindow.isFocusable = true
            mPopupWindow.isOutsideTouchable = false
            mPopupWindow.setBackgroundDrawable(null)
            //注意下面这三个是contentView 不是PopupWindow，响应返回按钮事件
            mPopupWindow.contentView.isFocusable = true
            mPopupWindow.contentView.isFocusableInTouchMode = true
            mPopupWindow.contentView.setOnKeyListener(View.OnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mPopupWindow.dismiss()
                    return@OnKeyListener true
                }
                false
            })
            //在Android 6.0以上 ，只能通过拦截事件来解决
            mPopupWindow.setTouchInterceptor(View.OnTouchListener { v, event ->
                val x = event.x.toInt()
                val y = event.y.toInt()
                val outTouch = x < 0 || x >= mWidth || y < 0 || y >= mHeight
                if (event.action == MotionEvent.ACTION_DOWN && outTouch) {
                    //outside
                    Log.d(TAG, "onTouch outside:mWidth=$mWidth,mHeight=$mHeight")
                    return@OnTouchListener true
                } else if (event.action == MotionEvent.ACTION_OUTSIDE) {
                    //outside
                    Log.d(TAG, "onTouch outside event:mWidth=$mWidth,mHeight=$mHeight")
                    return@OnTouchListener true
                }
                if (event.action == MotionEvent.ACTION_UP) {
                    v.performClick()
                }
                false
            })
        } else {
            mPopupWindow.isFocusable = true
            mPopupWindow.isOutsideTouchable = mOutsideTouchable
            mPopupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }


    /**
     * 注册GlobalLayoutListener 获取精准的宽高
     */
    private fun registerOnGlobalLayoutListener() {
        contentView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                contentView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                mWidth = contentView.width
                mHeight = contentView.height

                isNeedReMeasure = false

                mRealMeasureCallback?.call(this@SmartPopup, mWidth, mHeight,
                        if (mAnchorView == null) 0 else mAnchorView!!.width, if (mAnchorView == null) 0 else mAnchorView!!.height)

                if (isShowing && isAtAnchorViewMethod) {
                    updateLocation(mWidth, mHeight, mAnchorView!!, mYGravity, mXGravity, mOffsetX, mOffsetY)
                }

                contentView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    /**
     * 更新 PopupWindow 到精准的位置
     *
     * @param width   宽
     * @param height  高
     * @param anchor  锚点
     * @param yGravity 垂直方向的对齐方式
     * @param xGravity 水平方向的对齐方式
     * @param x        水平方向的偏移
     * @param y        垂直方向的偏移
     */
    private fun updateLocation(width: Int, height: Int, anchor: View, @YGravity yGravity: Int, @XGravity xGravity: Int, x: Int, y: Int) {
        val newX = calculateX(anchor, xGravity, width, x)
        val newY = calculateY(anchor, yGravity, height, y)
        mPopupWindow.update(anchor, newX, newY, width, height)
    }


    /**
     * 根据水平gravity计算x偏移
     *
     * @param anchor   锚点
     * @param xGravity  水平方向的对齐方式
     * @param measuredW  popWindow的宽
     * @param x          水平方向的偏移
     * @return  x轴偏移
     */
    private fun calculateX(anchor: View, xGravity: Int, measuredW: Int, x: Int): Int {
        var newX = x
        when (xGravity) {
            XGravity.LEFT ->
                //anchor view左侧
                newX -= measuredW
            XGravity.ALIGN_RIGHT ->
                //与anchor view右边对齐
                newX -= measuredW - anchor.width
            XGravity.CENTER ->
                //anchor view水平居中
                newX += anchor.width / 2 - measuredW / 2
            XGravity.ALIGN_LEFT -> {
            }
            XGravity.RIGHT ->
                //anchor view右侧
                newX += anchor.width
            else -> {
            }
        }//与anchor view左边对齐
        // Default position.

        return newX
    }


    /**
     * 根据垂直gravity计算y偏移
     *
     * @param anchor     锚点
     * @param yGravity   垂直方向的对齐方式
     * @param measuredH  popWindow的高度
     * @param y          垂直偏移
     * @return    y轴偏移
     */
    private fun calculateY(anchor: View, yGravity: Int, measuredH: Int, y: Int): Int {
        var newY = y
        when (yGravity) {
            YGravity.ABOVE ->
                //anchor view之上
                newY -= measuredH + anchor.height
            YGravity.ALIGN_BOTTOM ->
                //anchor view底部对齐
                newY -= measuredH
            YGravity.CENTER ->
                //anchor view垂直居中
                newY -= anchor.height / 2 + measuredH / 2
            YGravity.ALIGN_TOP ->
                //anchor view顶部对齐
                newY -= anchor.height
            YGravity.BELOW -> {
            }
            else -> {
            }
        }//anchor view之下
        // Default position.

        return newY
    }

    /**
     * 处理背景变暗
     * https://blog.nex3z.com/2016/12/04/%E5%BC%B9%E5%87%BApopupwindow%E5%90%8E%E8%AE%A9%E8%83%8C%E6%99%AF%E5%8F%98%E6%9A%97%E7%9A%84%E6%96%B9%E6%B3%95/
     */
    private fun handleBackgroundDim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (!isBackgroundDim) {
                return
            }
            if (mDimView != null) {
                applyDim(mDimView!!)
            } else {
                if (contentView.context != null &&
                        contentView.context is Activity) {
                    val activity = contentView.context as Activity
                    applyDim(activity)
                }
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun applyDim(activity: Activity) {
        val parent = activity.window.decorView.rootView as ViewGroup
        val dimDrawable = ColorDrawable(mDimColor)
        dimDrawable.setBounds(0, 0, parent.width, parent.height)
        dimDrawable.alpha = (255 * mDimValue).toInt()
        val overlay = parent.overlay
        overlay.add(dimDrawable)
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun applyDim(dimView: ViewGroup) {
        val dimDrawable = ColorDrawable(mDimColor)
        dimDrawable.setBounds(0, 0, dimView.width, dimView.height)
        dimDrawable.alpha = (255 * mDimValue).toInt()
        val overlay = dimView.overlay
        overlay.add(dimDrawable)
    }


    /**
     * 清除背景变暗
     */
    private fun clearBackgroundDim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (isBackgroundDim) {
                if (mDimView != null) {
                    clearDim(mDimView!!)
                } else {
                    val activity = contentView.context as Activity
                    clearDim(activity)
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun clearDim(activity: Activity) {
        val parent = activity.window.decorView.rootView as ViewGroup
        val overlay = parent.overlay
        overlay.clear()
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun clearDim(dimView: ViewGroup) {
        val overlay = dimView.overlay
        overlay.clear()
    }


    /**
     * PopupWindow是否显示在window中
     * 用于获取准确的PopupWindow宽高，可以重新设置偏移量
     */
    interface OnRealMeasureCallback {

        /**
         * 在 show方法之后 updateLocation之前执行
         * @param popup popWindow
         * @param popWidth  PopupWindow准确的宽
         * @param popHeight PopupWindow准确的高
         * @param anchorW   锚点View宽
         * @param anchorH   锚点View高
         */
        fun call(popup:SmartPopup, popWidth: Int, popHeight: Int, anchorW: Int, anchorH: Int)
    }




}

