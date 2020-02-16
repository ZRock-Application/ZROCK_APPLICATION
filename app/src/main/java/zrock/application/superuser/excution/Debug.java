package zrock.application.superuser.excution;

import zrock.application.BuildConfig;

import android.os.Looper;
import android.util.Log;
import android.os.Process;

import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Utility class for logging and debug features that (by default) does nothing when not in debug mode
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "unused"})
@AnyThread
public class Debug {

    // ----- DEBUGGING -----

    private static boolean debug = BuildConfig.DEBUG;

    /**
     * <p>Enable or disable debug mode</p>
     *
     * <p>By default, debug mode is enabled for development
     * builds and disabled for exported APKs - see
     * BuildConfig.DEBUG</p>
     *
     * @param enable Enable debug mode ?
     */
    public static void setDebug(boolean enable) {
        debug = enable;
    }

    /**
     * <p>Is debug mode enabled ?</p>
     *
     * @return Debug mode enabled
     */
    public static boolean getDebug() {
        return debug;
    }

    // ----- LOGGING -----

    public interface OnLogListener {
        void onLog(int type, String typeIndicator, String message);
    }

    public static final String TAG = "libsuperuser";

    public static final int LOG_GENERAL = 0x0001;
    public static final int LOG_COMMAND = 0x0002;
    public static final int LOG_OUTPUT = 0x0004;
    public static final int LOG_POOL = 0x0008;

    public static final int LOG_NONE = 0x0000;
    public static final int LOG_ALL = 0xFFFF;

    private static int logTypes = LOG_ALL;

    @Nullable
    private static OnLogListener logListener = null;

    /**
     * <p>Log a message (internal)</p>
     *
     * <p>Current debug and enabled logtypes decide what gets logged -
     * even if a custom callback is registered</p>
     *
     * @param type          Type of message to log
     * @param typeIndicator String indicator for message type
     * @param message       The message to log
     */
    private static void logCommon(int type, @NonNull String typeIndicator, @NonNull String message) {
        if (debug && ((logTypes & type) == type)) {
            if (logListener != null) {
                logListener.onLog(type, typeIndicator, message);
            } else {
                Log.d(TAG, "[" + TAG + "][" + typeIndicator + "]" + (!message.startsWith("[") && !message.startsWith(" ") ? " " : "") + message);
            }
        }
    }

    /**
     * <p>Log a "general" message</p>
     *
     * <p>These messages are infrequent and mostly occur at startup/shutdown or on error</p>
     *
     * @param message The message to log
     */
    public static void log(@NonNull String message) {
        logCommon(LOG_GENERAL, "G", message);
    }

    /**
     * <p>Log a "per-command" message</p>
     *
     * <p>This could produce a lot of output if the client runs many commands in the session</p>
     *
     * @param message The message to log
     */
    public static void logCommand(@NonNull String message) {
        logCommon(LOG_COMMAND, "C", message);
    }

    /**
     * <p>Log a line of stdout/stderr output</p>
     *
     * <p>This could produce a lot of output if the shell commands are noisy</p>
     *
     * @param message The message to log
     */
    public static void logOutput(@NonNull String message) {
        logCommon(LOG_OUTPUT, "O", message);
    }

    /**
     * <p>Log pool event</p>
     *
     * @param message The message to log
     */
    public static void logPool(@NonNull String message) {
        logCommon(LOG_POOL, "P", message);
    }

    /**
     * <p>Enable or disable logging specific types of message</p>
     *
     * <p>You may | (or) LOG_* constants together. Note that
     * debug mode must also be enabled for actual logging to
     * occur.</p>
     *
     * @param type   LOG_* constants
     * @param enable Enable or disable
     */
    public static void setLogTypeEnabled(int type, boolean enable) {
        if (enable) {
            logTypes |= type;
        } else {
            logTypes &= ~type;
        }
    }

    /**
     * <p>Is logging for specific types of messages enabled ?</p>
     *
     * <p>You may | (or) LOG_* constants together, to learn if
     * <b>all</b> passed message types are enabled for logging. Note
     * that debug mode must also be enabled for actual logging
     * to occur.</p>
     *
     * @param type LOG_* constants
     * @return enabled?
     */
    public static boolean getLogTypeEnabled(int type) {
        return ((logTypes & type) == type);
    }

    /**
     * <p>Is logging for specific types of messages enabled ?</p>
     *
     * <p>You may | (or) LOG_* constants together, to learn if
     * <b>all</b> message types are enabled for logging. Takes
     * debug mode into account for the result.</p>
     *
     * @param type LOG_* constants
     * @return enabled and in debug mode?
     */
    public static boolean getLogTypeEnabledEffective(int type) {
        return getDebug() && getLogTypeEnabled(type);
    }

    /**
     * <p>Register a custom log handler</p>
     *
     * <p>Replaces the log method (write to logcat) with your own
     * handler. Whether your handler gets called is still dependent
     * on debug mode and message types being enabled for logging.</p>
     *
     * @param onLogListener Custom log listener or NULL to revert to default
     */
    public static void setOnLogListener(@Nullable OnLogListener onLogListener) {
        logListener = onLogListener;
    }

    /**
     * <p>Get the currently registered custom log handler</p>
     *
     * @return Current custom log handler or NULL if none is present
     */
    @Nullable
    public static OnLogListener getOnLogListener() {
        return logListener;
    }

    // ----- SANITY CHECKS -----

    private static boolean sanityChecks = true;

    /**
     * <p>Enable or disable sanity checks</p>
     *
     * <p>Enables or disables the library crashing when su is called
     * from the main thread.</p>
     *
     * @param enable Enable or disable
     */
    public static void setSanityChecksEnabled(boolean enable) {
        sanityChecks = enable;
    }

    /**
     * <p>Are sanity checks enabled ?</p>
     *
     * <p>Note that debug mode must also be enabled for actual
     * sanity checks to occur.</p>
     *
     * @return True if enabled
     */
    public static boolean getSanityChecksEnabled() {
        return sanityChecks;
    }

    /**
     * <p>Are sanity checks enabled ?</p>
     *
     * <p>Takes debug mode into account for the result.</p>
     *
     * @return True if enabled
     */
    public static boolean getSanityChecksEnabledEffective() {
        return getDebug() && getSanityChecksEnabled();
    }

    /**
     * <p>Are we running on the main thread ?</p>
     *
     * @return Running on main thread ?
     */
    public static boolean onMainThread() {
        return ((Looper.myLooper() != null) && (Looper.myLooper() == Looper.getMainLooper()) && (Process.myUid() != 0));
    }

}