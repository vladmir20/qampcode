/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.MessagingModule;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.mesibo.emojiview.EmojiconEditText;


public class ChatEditText extends EmojiconEditText {
    private KeyImeChange keyImeChangeListener;

    public ChatEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setKeyImeChangeListener(KeyImeChange listener) {
        this.keyImeChangeListener = listener;
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (this.keyImeChangeListener == null) {
            return false;
        }
        this.keyImeChangeListener.onKeyIme(keyCode, event);
        return false;
    }

    public interface KeyImeChange {
        void onKeyIme(int i, KeyEvent keyEvent);
    }
}
