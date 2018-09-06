//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.analytics;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import com.baidu.autoupdatesdk.ICallback;
import com.baidu.autoupdatesdk.protocol.BDPlatformRequest;
import com.baidu.autoupdatesdk.utils.ThreadPoolUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class TagRecorder {
    private ExecutorService es;
    private Object pendingListSync;
    private List<Tag> pendingList;
    private final Handler delayHandler;
    private static final long MAX_REQUEST_INTERVAL = 25000L;
    private volatile long lastRequestMillis;
    private static List<Long> sDelayList = new ArrayList(4);

    public static void onTag(Context context, Tag tag) {
    }

    public static void destroy() {
        getInstance().destroyPrivate();
    }

    private TagRecorder() {
        this.es = ThreadPoolUtils.newSingleThreadExecutor();
        this.pendingListSync = new Object();
        this.pendingList = new ArrayList();
        this.delayHandler = new Handler(Looper.getMainLooper());
        this.lastRequestMillis = SystemClock.elapsedRealtime();
    }

    private static TagRecorder getInstance() {
        return TagRecorder.TagRecorderHolder.instance;
    }

    private void destroyPrivate() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            this.delayHandler.post(new Runnable() {
                public void run() {
                    TagRecorder.this.destroyOnUIPrivate();
                }
            });
        } else {
            this.destroyOnUIPrivate();
        }

    }

    private void destroyOnUIPrivate() {
        this.removePendingTag();
        this.es.shutdownNow();
    }

    private List<Tag> removePendingTag() {
        Object var2 = this.pendingListSync;
        synchronized(this.pendingListSync) {
            List<Tag> result = this.pendingList;
            this.pendingList = new ArrayList();
            return result;
        }
    }

    private void record(Context context, Tag tag) {
        List<Tag> tags = null;
        if (tag != null) {
            tags = new ArrayList(1);
            tags.add(tag);
        }

        this.delayRetry(context, 0, tags);
    }

    private void delayRetry(Context context, int retryIndex, List<Tag> tags) {
        if (retryIndex < sDelayList.size()) {
            boolean mustRetry = true;
            if (tags != null && tags.size() > 0) {
                if (retryIndex == 0) {
                    mustRetry = this.addPendingTag(tags);
                    if (!mustRetry && SystemClock.elapsedRealtime() - this.lastRequestMillis >= 25000L) {
                        mustRetry = true;
                    }
                } else {
                    this.addPendingTag(tags);
                }
            }

            if (mustRetry) {
                this.delayHandler.postDelayed(new TagRecorder.Retry(context, retryIndex), (Long)sDelayList.get(retryIndex));
            }
        }
    }

    private static boolean syncPostTags(Context context, List<Tag> tags) {
        BDPlatformRequest request = BDPlatformRequest.newRequest();
        TagCoder coder = TagCoder.newInstance(context, tags);
        request.syncPost(coder, (ICallback)null);
        int resultCode = coder.getResultCode();
        return resultCode == 10000;
    }

    private boolean addPendingTag(List<Tag> tags) {
        Object var3 = this.pendingListSync;
        synchronized(this.pendingListSync) {
            boolean mustRetry = this.pendingList.size() == 0;
            this.pendingList.addAll(tags);
            return mustRetry;
        }
    }

    static {
        Collections.addAll(sDelayList, new Long[]{0L, 1000L, 3000L, 9000L});
    }

    private class Retry implements Runnable {
        private Context context;
        private int retryIndex;

        Retry(Context context, int retryIndex) {
            this.context = context;
            this.retryIndex = retryIndex;
        }

        public void run() {
            if (!TagRecorder.this.es.isTerminated()) {
                TagRecorder.this.es.submit(new Runnable() {
                    public void run() {
                        Retry.this.recordInBackgroud();
                    }
                });
            }
        }

        private boolean isCancel() {
            return Thread.currentThread().isInterrupted();
        }

        private void recordInBackgroud() {
            if (!this.isCancel()) {
                List<Tag> tags = TagRecorder.this.removePendingTag();
                if (tags.size() > 0) {
                    TagRecorder.this.lastRequestMillis = SystemClock.elapsedRealtime();
                    if (!TagRecorder.syncPostTags(this.context, tags)) {
                        if (!this.isCancel()) {
                            TagRecorder.this.delayRetry(this.context, ++this.retryIndex, tags);
                        }
                    }
                }
            }
        }
    }

    private static class TagRecorderHolder {
        static final TagRecorder instance = new TagRecorder();

        private TagRecorderHolder() {
        }
    }
}
