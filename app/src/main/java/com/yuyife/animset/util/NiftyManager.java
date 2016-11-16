package com.yuyife.animset.util;
/*
 * Copyright 2014 gitonway
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.yuyife.animset.wedgit.NiftyNotificationView;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class NiftyManager extends Handler {
    private static final class Messages {
        private Messages() {
        }

        public static final int DISPLAY_NOTIFICATION = 0x20140813;
        public static final int ADD_TO_VIEW = 0x20140814;
        public static final int REMOVE_NOTIFICATION = 0x20140815;
        public static final int REMOVE_NOTIFICATION_VIEW = 0x20140816;
    }

    private static NiftyManager INSTANCE;

    private final Queue<NiftyNotificationView> notifyQueue;

    private boolean isSticky=false;

    private NiftyManager() {
        notifyQueue = new LinkedBlockingQueue<NiftyNotificationView>();
    }
    public static synchronized NiftyManager getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new NiftyManager();
        }

        return INSTANCE;
    }

    public void add(NiftyNotificationView crouton, boolean repeat) {
        if (notifyQueue.size() < 1||repeat) {
            notifyQueue.add(crouton);
            displayNotify(false);
        }

    }
    public void addSticky(NiftyNotificationView crouton) {
        Log.i("count",crouton.getViewGroup().getChildCount()+"   "+notifyQueue.size());

        if (crouton.getViewGroup().getChildCount()==0&&notifyQueue.size()==1){
            removeSticky();
        }

        if (notifyQueue.size() < 1) {
            notifyQueue.add(crouton);
            displayNotify(true);
        }

    }
    public void removeSticky(){
        NiftyNotificationView notify = notifyQueue.peek();
        if(notify!=null) {
            sendMessageDelayed(notify, Messages.REMOVE_NOTIFICATION,
                    0
            );
        }
    }

    private long calculateCroutonDuration(NiftyNotificationView notify) {
        long notifyDuration = notify.getDispalyDuration();
        notifyDuration += notify.getNiftyEffects().getAnimator().getDuration();
        return notifyDuration;
    }

    private void sendMessage(NiftyNotificationView crouton, final int messageId) {
        final Message message = obtainMessage(messageId);
        message.obj = crouton;
        sendMessage(message);
    }

    private void sendMessageDelayed(NiftyNotificationView crouton, final int messageId, final long delay) {
        Message message = obtainMessage(messageId);
        message.obj = crouton;
        sendMessageDelayed(message, delay);
    }

    @Override
    public void handleMessage(Message msg) {
        final NiftyNotificationView notify = (NiftyNotificationView) msg.obj;
        if (null == notify) {
            return;
        }
        switch (msg.what) {
            case Messages.DISPLAY_NOTIFICATION: {
                displayNotify(false);
                break;
            }

            case Messages.ADD_TO_VIEW: {
                addNotifyToView(notify);
                break;
            }

            case Messages.REMOVE_NOTIFICATION: {
                removeNotify(notify);
                break;
            }

            case Messages.REMOVE_NOTIFICATION_VIEW: {
                removeNotifyView(notify);
                break;
            }

            default: {
                super.handleMessage(msg);
                break;
            }
        }


        super.handleMessage(msg);
    }


    private void displayNotify(boolean sticky) {

        if (notifyQueue.isEmpty()) {
            return;
        }

        isSticky=sticky;

        final NiftyNotificationView currentNotify = notifyQueue.peek();

        if (null == currentNotify.getActivity()) {
            notifyQueue.poll();
        }

        if (!currentNotify.isShowing()) {
            sendMessage(currentNotify, Messages.ADD_TO_VIEW);
        } else {
            sendMessageDelayed(currentNotify, Messages.DISPLAY_NOTIFICATION,
                    calculateCroutonDuration(currentNotify));
        }
    }
    private void addNotifyToView(final NiftyNotificationView notify) {
        if (notify.isShowing()) {
            return;
        }
        final View notifyView = notify.getView();

        if (null == notifyView.getParent()) {
            ViewGroup.LayoutParams params = notifyView.getLayoutParams();
            if (null == params) {
                params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            if (null != notify.getViewGroup()) {

                if (notify.getViewGroup() instanceof FrameLayout) {
                    notify.getViewGroup().addView(notifyView, params);
                } else {
                    notify.getViewGroup().addView(notifyView, 0, params);
                }

            } else {
                Activity activity = notify.getActivity();
                if (null == activity || activity.isFinishing()) {
                    return;
                }

                activity.addContentView(notifyView, params);
            }
        }
        notifyView.requestLayout();

        ViewTreeObserver observer = notifyView.getViewTreeObserver();
        if (null != observer) {
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                @TargetApi(16)
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        notifyView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        notifyView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                    notify.getNiftyEffects().getAnimator().setDuration(notify.getNiftyConfiguration().animDuration).in(notify.getView());
                    if(!isSticky){
                        sendMessageDelayed(notify, Messages.REMOVE_NOTIFICATION,
                                notify.getDispalyDuration()
                                        + notify.getInDuration());
                    }

                }
            });
        }

    }
    public void removeNotify(NiftyNotificationView notify) {
        View notifyView = notify.getView();
        ViewGroup notifyParentView = (ViewGroup) notifyView.getParent();
        if (null != notifyParentView) {
            notify.getNiftyEffects().getAnimator().setDuration(notify.getNiftyConfiguration().animDuration).out(notify.getView());
            sendMessageDelayed(notify, Messages.REMOVE_NOTIFICATION_VIEW, notify.getOutDuration());

            sendMessageDelayed(notify, Messages.DISPLAY_NOTIFICATION, notify.getOutDuration());
        }
    }
    protected void removeNotifyView(NiftyNotificationView notify) {
        View notifyView = notify.getView();
        ViewGroup notifyParentView = (ViewGroup) notifyView.getParent();
        if (null != notifyParentView) {

            NiftyNotificationView removed = notifyQueue.poll();
            notifyParentView.removeView(notifyView);
            if (null != removed) {
                removed.detachActivity();
                removed.detachViewGroup();
            }
        }
    }

}
