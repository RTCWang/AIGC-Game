
#ifndef __ZEGO_EXPRESS_MEDIA_DATA_PUBLISHER_H__
#define __ZEGO_EXPRESS_MEDIA_DATA_PUBLISHER_H__

#include "zego-express-defines.h"

ZEGO_BEGIN_DECLS
/// Creates a media data publisher instance.
///
/// Available since: 3.4.0
/// Description: Developers can use this interface to create ZegoMediaDataPublisher objects to push local media resource files to implement server-side streaming scenarios, such as AI classrooms.
/// Use cases: Often used in server-side publishing stream scenarios, such as AI classrooms.
/// When to call: After calling the [createEngine] function to create an engine.
/// Caution: It returns the instance object of the specified publish channel. It will return the instance object if it already exists.
/// Note: This function is only available in ZegoExpressVideo SDK!
///
/// @param config Config the media data publisher.
/// @param instance_index [in/out] return instance index, -1 will be returned when the maximum number is exceeded.
ZEGOEXP_API zego_error EXP_CALL zego_express_create_media_data_publisher(
    struct zego_media_data_publisher_config config, zego_instance *instance_index);

/// Destroys a media data publisher instance.
///
/// Available since: 2.17.0
/// Description: Destroys a media data publisher instance.
/// Use cases: Often used in server-side publishing stream scenarios, such as AI classrooms.
/// When to call: After calling the [createEngine] function to create an engine.
/// Note: This function is only available in ZegoExpressVideo SDK!
///
/// @param instance_index The media data publisher instance index.
ZEGOEXP_API zego_error EXP_CALL
zego_express_destroy_media_data_publisher(zego_instance instance_index);

/// Add media file to the publish queue. Currently, only mp4 / m4a / aac files are supported, and special conversion is required.
///
/// The mp4 file format must meet the following points：
/// The video must be encoded as H.264 and cannot contain B frames, only I and P frames. The I frame interval is 2s, that is, a single GOP value is 2s
/// The frame rate, bit rate, and resolution of the video are consistent with the frame rate, bit rate, and resolution set by [setVideoConfig] before publishing stream
/// Audio encoding must be MPEG-4 AAC
///
/// @param path Local absolute path to the media file.
/// @param is_clear Whether to clear the publish queue.
/// @param instance_index The media data publisher instance index.
ZEGOEXP_API zego_error EXP_CALL zego_express_media_data_publisher_add_media_file_path(
    const char *path, bool is_clear, zego_instance instance_index);

/// Add media file to the publish queue. Currently, only mp4 file are supported, and special conversion is required.
///
/// When you need to re-publish stream and do not need to continue publishing from the previous publish queue, you can call this function to reset the MediaDataPublisher state.
/// It needs to be invoked before [startPublishingStream], [startPlayingStream], [startPreview], [createMediaPlayer], [createAudioEffectPlayer] and [createRealTimeSequentialDataManager] to take effect.
/// When the developer calls [logoutRoom], the state is automatically reset.
///
/// @param instance_index The media data publisher instance index.
ZEGOEXP_API zego_error EXP_CALL
zego_express_media_data_publisher_reset(zego_instance instance_index);

/// Set to postpone the video playback time, mainly used to correct the fixed audio and picture asynchronous phenomenon that occurs during the publishing stream.
///
/// When this value is set, when publishing video file stream, the SDK will permanently delay the video to the set time value before sending
///
/// @param instance_index The media data publisher instance index.
ZEGOEXP_API zego_error EXP_CALL zego_express_media_data_publisher_set_video_send_delay_time(
    int delay_time, zego_instance instance_index);

/// Specify the starting point of the current video file publishing.
///
/// It can be called at any time between [OnFileOpen] and [OnFileClose]. For example, you can call this function directly in the [OnFileOpen] callback
///
/// @param millisecond The timestamp of the start of streaming (relative to the timestamp of the file currently being published, the starting value is 0). the unit is milliseconds
/// @param instance_index The media data publisher instance index.
ZEGOEXP_API zego_error EXP_CALL zego_express_media_data_publisher_seek_to(
    unsigned long long millisecond, zego_instance instance_index);

/// Get the total duration of the current file.
///
/// @param instance_index The media data publisher instance index.
/// @param total_duration [in/out] Get the total duration of the current file.
ZEGOEXP_API zego_error EXP_CALL zego_express_media_data_publisher_get_total_duration(
    zego_instance instance_index, unsigned long long *total_duration);

/// Get the progress of the current file.
///
/// @param instance_index The media data publisher instance index.
/// @param current_duration [in/out] Get the progress of the current file.
ZEGOEXP_API zego_error EXP_CALL zego_express_media_data_publisher_get_current_duration(
    zego_instance instance_index, unsigned long long *current_duration);

/// Call back when the media data publisher starts to process a file.
///
/// @param path Path of currently open file
/// @param instance_index Media data publisher instance index
/// @param user_context Context of user.
typedef void (*zego_on_media_data_publisher_file_open)(const char *path,
                                                       zego_instance instance_index,
                                                       void *user_context);

ZEGOEXP_API void EXP_CALL zego_register_media_data_publisher_file_open_callback(
    zego_on_media_data_publisher_file_open callback_func, void *user_context);

/// Call back when the media data publisher finishes to process a file.
///
/// @param error_code error code. 0 means closing the file normally.
/// @param path File path currently closed
/// @param instance_index Media data publisher instance index
/// @param user_context Context of user.
typedef void (*zego_on_media_data_publisher_file_close)(zego_error error_code, const char *path,
                                                        zego_instance instance_index,
                                                        void *user_context);

ZEGOEXP_API void EXP_CALL zego_register_media_data_publisher_file_close_callback(
    zego_on_media_data_publisher_file_close callback_func, void *user_context);

/// Call back when each file starts to output data.
///
/// @param path The file path where the streaming is currently started
/// @param instance_index Media data publisher instance index
/// @param user_context Context of user.
typedef void (*zego_on_media_data_publisher_file_data_begin)(const char *path,
                                                             zego_instance instance_index,
                                                             void *user_context);

ZEGOEXP_API void EXP_CALL zego_register_media_data_publisher_file_data_begin_callback(
    zego_on_media_data_publisher_file_data_begin callback_func, void *user_context);

ZEGO_END_DECLS

#endif
