
#ifndef __ZEGO_EXPRESS_SCREEN_CAPTURE_H__
#define __ZEGO_EXPRESS_SCREEN_CAPTURE_H__

#include "zego-express-defines.h"

ZEGO_BEGIN_DECLS
/// Get a list of screens or windows in a screen.
///
/// Available since: 3.1.0
/// Description: Get a list of screens or windows in a screen.
/// Restrictions: Only support in Windows/macOS.
///
/// @param thumbnail_width Get the thumbnail width corresponding to the window, the thumbnail can be used to draw on the window selection interface. (unit is pixel)
/// @param thumbnail_height Get the thumbnail height corresponding to the window, the thumbnail can be used to draw on the window selection interface. (unit is pixel)
/// @param icon_width Get the width of the icon corresponding to the program. (unit is pixel)
/// @param icon_height Get the height of the icon corresponding to the program. (unit is pixel)
/// @param source_count Count of source
/// @return List of capture source info objects.
ZEGOEXP_API struct zego_screen_capture_source_info *EXP_CALL
zego_express_get_screen_capture_sources(int thumbnail_width, int thumbnail_height, int icon_width,
                                        int icon_height, int *source_count);

/// Release screen capture source list
///
/// Only for Windows / macOS / Linux
///
/// @param source_list Screen capture source list
/// @param source_count Screen capture source count
ZEGOEXP_API zego_error EXP_CALL zego_express_free_screen_capture_source_list(
    struct zego_screen_capture_source_info *source_list, int source_count);

/// Create the screen capture source
///
/// Available: since 3.1.0
/// Description: Creates a screen capture source object based on the provided source ID and source type.
/// Use cases: It is used when you need to record and share the screen or window.
/// When to call: It needs to be called after [createEngine].
///
/// @param source_id The specified screen ID or window ID.
/// @param source_type The specified screen source type.
/// @return The screen capture instance index, -1 will be returned when the maximum number is exceeded.
ZEGOEXP_API int EXP_CALL zego_express_create_screen_capture_source(
    void *source_id, enum zego_screen_capture_source_type source_type);

/// Destroy the screen capture source instance
///
/// Available: since 3.1.0
/// Description: Destroy the [ZegoScreenCaptureSource] instance object.
/// Use cases: When you no longer need to use the screen capture function, you can use this function to destroy the instance object created by the [createScreenCaptureSource] function
/// When to call: When you need to the screen capture source object needs to be destroyed
/// Restrictions: After destroy the instance, you need to release the [ZegoScreenCaptureSource] instance object you hold by yourself, and don’t call the function of this instance object after the destruction.
ZEGOEXP_API zego_error EXP_CALL zego_express_destroy_screen_capture_source(int instance_index);

/// Create the screen capture source
///
/// Available: since 3.1.0
/// Description: Update a screen capture source object based on the provided source ID and source type.
/// Use cases: It is used when you need to record and share the screen or window.
/// When to call: It can be called after the engine by [createScreenCaptureSource] has been initialized.
/// Restrictions: Only available on Windows/macOS.
///
/// @param source_id The specified screen ID or window ID.
/// @param source_type The specified screen source type.
/// @param instance_index The screen capture source instance index.
ZEGOEXP_API zego_error EXP_CALL zego_express_screen_capture_update_capture_source(
    void *source_id, enum zego_screen_capture_source_type source_type, int instance_index);

/// Start screen capture.
///
/// Available since: 3.1.0
/// Description: Start screen capture.
/// When to call: It can be called after the engine by [createScreenCaptureSource] has been initialized.
///
/// @param instance_index The screen capture source instance index.
ZEGOEXP_API zego_error EXP_CALL zego_express_screen_capture_start_capture(int instance_index);

/// Stop screen capture.
///
/// Available since: 3.1.0
/// Description: Stop screen capture.
///
/// @param instance_index The screen capture source instance index.
ZEGOEXP_API zego_error EXP_CALL zego_express_screen_capture_stop_capture(int instance_index);

/// Gets the rectangle of the screen capture source.
///
/// Available since: 3.6.0
/// Description: Gets the rectangle of the screen capture source.
/// Restrictions: For window type sources only, only support in Windows/macOS.
///
/// @param instance_index The screen capture source instance index.
/// @return Rect information about the window resource.
ZEGOEXP_API struct zego_rect EXP_CALL
zego_express_screen_capture_get_capture_source_rect(int instance_index);

/// Update the area captured by the screen.
///
/// Available since: 3.1.0
/// Description: Update the area captured by the screen.
/// When to call: It can be called after the engine by [createScreenCaptureSource] has been initialized.
/// Restrictions: Only support in Windows/macOS.
///
/// @param rect The position of the area to be captured relative to the entire screen or window.
/// @param instance_index The screen capture source instance index.
ZEGOEXP_API zego_error EXP_CALL
zego_express_screen_capture_update_capture_region(struct zego_rect rect, int instance_index);

/// Sets the filtered list of windows.
///
/// Available since: 3.1.0
/// Description: Specify a list of windows, and filter these windows when capturing the screen, and not display them on the screen.
/// When to call: It can be called after the engine by [createScreenCaptureSource] has been initialized.
/// Restrictions: Only available on Windows/macOS.
///
/// @param list List of IDs to filter windows.
/// @param count the number of lists.
/// @param instance_index The screen capture source instance index.
ZEGOEXP_API zego_error EXP_CALL
zego_express_screen_capture_set_exclude_window_list(void **list, int count, int instance_index);

/// Whether to activate the promotion of the window to the foreground.
///
/// Available since: 3.1.0
/// Description: When the capture target is a window, set whether to activate the window to be displayed in the foreground during the first capture.
/// When to call: It can be called after the engine by [createScreenCaptureSource] has been initialized.
/// Restrictions: Only available on Windows/macOS.
///
/// @param active Whether to activate the window. true to activate the window, false to not activate the window, the default is true.
/// @param instance_index The screen capture source instance index.
ZEGOEXP_API zego_error EXP_CALL
zego_express_screen_capture_enable_window_activate(bool active, int instance_index);

/// Set whether to show the cursor
///
/// Available since: 3.1.0
/// Description: Set whether to show the cursor.
/// When to call: It can be called after the engine by [createScreenCaptureSource] has been initialized.
/// Restrictions: Only available on Windows/macOS.
///
/// @param visible Whether to show the cursor. true to show the cursor, false to not show the cursor, the default is false.
/// @param instance_index The screen capture source instance index.
ZEGOEXP_API zego_error EXP_CALL
zego_express_screen_capture_enable_cursor_visible(bool visible, int instance_index);

/// Callback for screen capture data
///
/// Available since: 3.1.0
/// Description: Callback for screen capture data.
/// Trigger: This callback is fired after the screen starts capturing [startCapture].
/// Caution: The callback does not actually take effect until call [setEventHandler] to set.
///
/// @param data The screen captures the data of the image frame (eg: RGBA only needs to consider data[0], I420 needs to consider data[0,1,2]).
/// @param data_length Data length (eg: RGBA only needs to consider dataLength[0], I420 needs to consider dataLength[0,1,2]).
/// @param param Screen capture image frame parameters.
/// @param instance_index Screen capture source instance index.
/// @param user_context Context of user.
typedef void (*zego_on_screen_capture_source_available_frame)(
    const void *data, unsigned int data_length, const struct zego_video_frame_param param,
    int instance_index, void *user_context);

ZEGOEXP_API void EXP_CALL zego_register_screen_capture_source_available_frame_callback(
    zego_on_screen_capture_source_available_frame callback_func, void *user_context);

/// The callback triggered when a screen capture source exception occurred
///
/// Available since: 3.1.0
/// Description: The callback triggered when a screen capture source exception occurred.
/// Trigger: This callback is triggered when an exception occurs after the screen start capture.
/// Caution: The callback does not actually take effect until call [setEventHandler] to set.
///
/// @param exception_type Capture source exception type.
/// @param instance_index Screen capture source instance index.
/// @param user_context Context of user.
typedef void (*zego_on_screen_capture_source_exception_occurred)(
    enum zego_screen_capture_source_exception_type exception_type, int instance_index,
    void *user_context);

ZEGOEXP_API void EXP_CALL zego_register_screen_capture_source_exception_occurred_callback(
    zego_on_screen_capture_source_exception_occurred callback_func, void *user_context);

/// The callback will be triggered when the state of the capture target window change.
///
/// Available since: 3.4.0
/// Caution: The callback does not actually take effect until call [setEventHandler] to set.
/// Restrictions: Only available on Windows/macOS.
///
/// @param window_state Capture window state.
/// @param window_rect Capture window rect.
/// @param instance_index Screen capture source instance index.
/// @param user_context Context of user.
typedef void (*zego_on_screen_capture_window_state_changed)(
    enum zego_screen_capture_window_state window_state, struct zego_rect window_rect,
    int instance_index, void *user_context);

ZEGOEXP_API void EXP_CALL zego_register_screen_capture_window_state_changed_callback(
    zego_on_screen_capture_window_state_changed callback_func, void *user_context);

ZEGO_END_DECLS

#endif
