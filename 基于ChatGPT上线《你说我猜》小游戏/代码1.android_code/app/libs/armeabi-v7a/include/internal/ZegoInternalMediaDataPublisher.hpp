#pragma once

#include "../ZegoExpressDefines.h"
#include "../ZegoExpressEventHandler.h"
#include "../ZegoExpressInterface.h"

#include "ZegoInternalBase.h"
#include "ZegoInternalBridge.h"

ZEGO_DISABLE_DEPRECATION_WARNINGS

namespace ZEGO {
namespace EXPRESS {

class ZegoExpressMediaDataPublisherImp : public IZegoMediaDataPublisher {
  public:
    ZegoExpressMediaDataPublisherImp(zego_instance index) : instance_index_(index) {}

    void addMediaFilePath(const std::string &path, bool is_clear) override {
        oInternalOriginBridge->mediaDataPublisherAddMediaFilePath(path, is_clear, instance_index_);
    }

    void reset() override { oInternalOriginBridge->mediaDataPublisherReset(instance_index_); }

    void setVideoSendDelayTime(int delayTime) override {
        oInternalOriginBridge->mediaDataPublisherSetVideoSendDelayTime(delayTime, instance_index_);
    }

    void seekTo(unsigned long long millisecond) override {
        oInternalOriginBridge->mediaDataPublisherSeekTo(millisecond, instance_index_);
    }

    unsigned long long getTotalDuration() override {
        return oInternalOriginBridge->mediaDataPublisherGetTotalDuration(instance_index_);
    }

    unsigned long long getCurrentDuration() override {
        return oInternalOriginBridge->mediaDataPublisherGetCurrentDuration(instance_index_);
    }

    int getIndex() override { return instance_index_; }

    void setMediaDataPublisherEventHandler(
        std::shared_ptr<IZegoMediaDataPublisherEventHandler> handler) override {
        std::lock_guard<std::mutex> lock(media_data_publisher_mutex_);
        media_data_publisher_event_handler = handler;
    }

    void zego_on_media_data_publisher_file_open(const std::string &path,
                                                zego_instance instance_index) {
        std::lock_guard<std::mutex> lock(media_data_publisher_mutex_);
        if (media_data_publisher_event_handler) {
            auto weak_mdp_event_handler = std::weak_ptr<IZegoMediaDataPublisherEventHandler>(
                media_data_publisher_event_handler);
            ZEGO_SWITCH_THREAD_PRE
            auto handler_in_main = weak_mdp_event_handler.lock();
            if (handler_in_main) {
                handler_in_main->onMediaDataPublisherFileOpen(this, path);
            }
            ZEGO_SWITCH_THREAD_ING
        }
    }

    void zego_on_media_data_publisher_file_close(zego_error error_code, const std::string &path,
                                                 zego_instance instance_index) {
        std::lock_guard<std::mutex> lock(media_data_publisher_mutex_);
        if (media_data_publisher_event_handler) {
            auto weak_mdp_event_handler = std::weak_ptr<IZegoMediaDataPublisherEventHandler>(
                media_data_publisher_event_handler);
            ZEGO_SWITCH_THREAD_PRE
            auto handler_in_main = weak_mdp_event_handler.lock();
            if (handler_in_main) {
                handler_in_main->onMediaDataPublisherFileClose(this, error_code, path);
            }
            ZEGO_SWITCH_THREAD_ING
        }
    }

    void zego_on_media_data_publisher_file_data_begin(const std::string &path,
                                                      zego_instance instance_index) {
        std::lock_guard<std::mutex> lock(media_data_publisher_mutex_);
        if (media_data_publisher_event_handler) {
            auto weak_mdp_event_handler = std::weak_ptr<IZegoMediaDataPublisherEventHandler>(
                media_data_publisher_event_handler);
            ZEGO_SWITCH_THREAD_PRE
            auto handler_in_main = weak_mdp_event_handler.lock();
            if (handler_in_main) {
                handler_in_main->onMediaDataPublisherFileDataBegin(this, path);
            }
            ZEGO_SWITCH_THREAD_ING
        }
    }

  private:
    std::shared_ptr<IZegoMediaDataPublisherEventHandler> media_data_publisher_event_handler;
    std::mutex media_data_publisher_mutex_;
    zego_instance instance_index_ = -1;
};
} // namespace EXPRESS
} // namespace ZEGO

ZEGO_ENABLE_DEPRECATION_WARNINGS