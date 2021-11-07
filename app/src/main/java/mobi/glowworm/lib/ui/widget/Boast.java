/*
 * Copyright (C) 2012 Glowworm Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mobi.glowworm.lib.ui.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

/**
 * {@link Toast} decorator allowing for easy cancellation of notifications. Use this class if you
 * want subsequent Toast notifications to overwrite current ones. </p>
 * <p/>
 * By default, a current {@link Boast} notification will be cancelled by a subsequent notification.
 * This default behaviour can be changed by calling certain methods like {@link #show(boolean)}.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Boast {
    /**
     * Keeps track of certain Boast notifications that may need to be cancelled. This functionality
     * is only offered by some of the methods in this class.
     * <p>
     * Uses a {@link WeakReference} to avoid leaking the activity context used to show the original {@link Toast}.
     */
    @Nullable
    private static volatile WeakReference<Boast> weakBoast = null;

    @Nullable
    private static Boast getGlobalBoast() {
        if (weakBoast == null) {
            return null;
        }

        //noinspection ConstantConditions
        return weakBoast.get();
    }

    private static void setGlobalBoast(@Nullable final Boast globalBoast) {
        Boast.weakBoast = new WeakReference<>(globalBoast);
    }


    // ////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Internal reference to the {@link Toast} object that will be displayed.
     */
    private final Toast internalToast;

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Private constructor creates a new {@link Boast} from a given {@link Toast}.
     *
     * @throws NullPointerException if the parameter is <code>null</code>.
     */
    private Boast(final Toast toast) {
        // null check
        if (toast == null) {
            throw new NullPointerException("Boast.Boast(Toast) requires a non-null parameter.");
        }

        internalToast = toast;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Make a standard {@link Boast} that just contains a text view.
     *
     * @param context  The context to use. Usually your {@link android.app.Application} or
     *                 {@link android.app.Activity} object.
     * @param text     The text to show. Can be formatted text.
     * @param duration How long to display the message. Either {@link Toast#LENGTH_SHORT} or
     *                 {@link Toast#LENGTH_LONG}
     */
    @SuppressLint("ShowToast")
    public static Boast makeText(final Context context, final CharSequence text, final int duration) {
        return new Boast(Toast.makeText(context, text, duration));
    }

    /**
     * Make a standard {@link Boast} that just contains a text view with the text from a resource.
     *
     * @param context  The context to use. Usually your {@link android.app.Application} or
     *                 {@link android.app.Activity} object.
     * @param resId    The resource id of the string resource to use. Can be formatted text.
     * @param duration How long to display the message. Either {@link Toast#LENGTH_SHORT} or
     *                 {@link Toast#LENGTH_LONG}
     * @throws Resources.NotFoundException if the resource can't be found.
     */
    @SuppressLint("ShowToast")
    public static Boast makeText(final Context context, final int resId, final int duration)
            throws Resources.NotFoundException {
        return new Boast(Toast.makeText(context, resId, duration));
    }

    /**
     * Make a standard {@link Boast} that just contains a text view. Duration defaults to
     * {@link Toast#LENGTH_SHORT}.
     *
     * @param context The context to use. Usually your {@link android.app.Application} or
     *                {@link android.app.Activity} object.
     * @param text    The text to show. Can be formatted text.
     */
    @SuppressLint("ShowToast")
    public static Boast makeText(final Context context, final CharSequence text) {
        return new Boast(Toast.makeText(context, text, Toast.LENGTH_SHORT));
    }

    /**
     * Make a standard {@link Boast} that just contains a text view with the text from a resource.
     * Duration defaults to {@link Toast#LENGTH_SHORT}.
     *
     * @param context The context to use. Usually your {@link android.app.Application} or
     *                {@link android.app.Activity} object.
     * @param resId   The resource id of the string resource to use. Can be formatted text.
     * @throws Resources.NotFoundException if the resource can't be found.
     */
    @SuppressLint("ShowToast")
    public static Boast makeText(final Context context, final int resId) throws Resources.NotFoundException {
        return new Boast(Toast.makeText(context, resId, Toast.LENGTH_SHORT));
    }

    /**
     * Make a custom {@link Boast} displays a given layout resource file.
     * Duration defaults to {@link Toast#LENGTH_SHORT}.
     *
     * @param context     The context to use. Usually your {@link android.app.Application} or
     *                    {@link android.app.Activity} object.
     * @param layoutResId The resource id of the layout resource to use.
     * @throws Resources.NotFoundException if the resource can't be found.
     */
    @SuppressLint("ShowToast")
    public static Boast makeCustom(final Context context, @LayoutRes final int layoutResId) throws Resources.NotFoundException {
        final Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        final LayoutInflater inflater = LayoutInflater.from(context);
        toast.setView(inflater.inflate(layoutResId, null));

        return new Boast(toast);
    }


    // ////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Show a standard {@link Boast} that just contains a text view.
     *
     * @param context  The context to use. Usually your {@link android.app.Application} or
     *                 {@link android.app.Activity} object.
     * @param text     The text to show. Can be formatted text.
     * @param duration How long to display the message. Either {@link Toast#LENGTH_SHORT} or
     *                 {@link Toast#LENGTH_LONG}
     */
    public static void showText(final Context context, final CharSequence text, final int duration) {
        Boast.makeText(context, text, duration).show();
    }

    /**
     * Show a standard {@link Boast} that just contains a text view with the text from a resource.
     *
     * @param context  The context to use. Usually your {@link android.app.Application} or
     *                 {@link android.app.Activity} object.
     * @param resId    The resource id of the string resource to use. Can be formatted text.
     * @param duration How long to display the message. Either {@link Toast#LENGTH_SHORT} or
     *                 {@link Toast#LENGTH_LONG}
     * @throws Resources.NotFoundException if the resource can't be found.
     */
    public static void showText(final Context context, final int resId, final int duration)
            throws Resources.NotFoundException {
        Boast.makeText(context, resId, duration).show();
    }

    /**
     * Show a standard {@link Boast} that just contains a text view. Duration defaults to
     * {@link Toast#LENGTH_SHORT}.
     *
     * @param context The context to use. Usually your {@link android.app.Application} or
     *                {@link android.app.Activity} object.
     * @param text    The text to show. Can be formatted text.
     */
    public static void showText(final Context context, final CharSequence text) {
        Boast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Show a standard {@link Boast} that just contains a text view with the text from a resource.
     * Duration defaults to {@link Toast#LENGTH_SHORT}.
     *
     * @param context The context to use. Usually your {@link android.app.Application} or
     *                {@link android.app.Activity} object.
     * @param resId   The resource id of the string resource to use. Can be formatted text.
     * @throws Resources.NotFoundException if the resource can't be found.
     */
    public static void showText(final Context context, final int resId) throws Resources.NotFoundException {
        Boast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * Show a custom {@link Boast} displays a given layout resource file.
     * Duration defaults to {@link Toast#LENGTH_SHORT}.
     *
     * @param context     The context to use. Usually your {@link android.app.Application} or
     *                    {@link android.app.Activity} object.
     * @param layoutResId The resource id of the layout resource to use.
     * @throws Resources.NotFoundException if the resource can't be found.
     */
    public static void showCustom(final Context context, @LayoutRes final int layoutResId) throws Resources.NotFoundException {
        Boast.makeCustom(context, layoutResId).show();
    }

    /**
     * Close the view if it's showing, or don't show it if it isn't showing yet. You do not normally
     * have to call this. Normally view will disappear on its own after the appropriate duration.
     * <p>
     * This method can be called when an {@link Activity} is destroyed to avoid leaking memory.
     */
    public static void cancel() {
        cancelGlobalBoast();
    }

    /**
     * Close the {@link #getGlobalBoast()} if we have a reference to it.
     */
    private static void cancelGlobalBoast() {
        final Boast cachedGlobalBoast = getGlobalBoast();
        if ((cachedGlobalBoast != null)) {
            cachedGlobalBoast.cancelInternalToast();
        }
    }


    // ////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Close the {@link #internalToast}.
     */
    private void cancelInternalToast() {
        internalToast.cancel();
    }

    /**
     * Show the view for the specified duration. By default, this method cancels any current
     * notification to immediately display the new one. For conventional {@link Toast#show()}
     * queueing behaviour, use method {@link #show(boolean)}.
     *
     * @see #show(boolean)
     */
    public void show() {
        show(true);
    }

    /**
     * Show the view for the specified duration. This method can be used to cancel the current
     * notification, or to queue up notifications.
     *
     * @param cancelCurrent <code>true</code> to cancel any current notification and replace it with this new
     *                      one
     * @see #show()
     */
    public void show(final boolean cancelCurrent) {
        // cancel current
        if (cancelCurrent) {
            cancelGlobalBoast();
        }

        // save an instance of this current notification
        setGlobalBoast(this);

        internalToast.show();
    }

}