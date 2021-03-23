package com.xlk.jbpaperless.model;

import android.os.Environment;

/**
 * @author Created by xlk on 2021/3/15.
 * @desc
 */
public class Constant {
    public static final String root_dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/JbPaperless/";
    public static final String logcat_dir = root_dir + "myLog";
    public static final String file_dir = root_dir + "文件/";
    public static final String download_dir = file_dir + "下载文件/";
    public static final String export_dir = file_dir + "导出文件/";

    /**
     * 各个界面的背景图和logo图标
     */
    public static final String MAIN_BG = "main_bg";
    public static final String MAIN_LOGO = "ic_logo_main";
    public static final String MEET_BG = "meet_bg";
    public static final String MEET_LOGO = "meet_logo";
    public static final String BULLETIN_BG = "bulletin_bg";
    public static final String BULLETIN_LOGO = "bulletin_logo";
    public static final String ROOM_BG = "room_bg";

    //固定的会议目录ID
    /**
     * 共享文件目录ID
     */
    public static final int SHARED_FILE_DIRECTORY_ID = 1;
    /**
     * 批注文件目录ID
     */
    public static final int ANNOTATION_FILE_DIRECTORY_ID = 2;


    //图片名称和下载标识

    /**
     * 主界面背景图
     */
    public static final String MAIN_BG_PNG_TAG = "main_bg";
    /**
     * 主界面logo图标
     */
    public static final String MAIN_LOGO_PNG_TAG = "main_logo";
    /**
     * 会议界面背景图
     */
    public static final String SUB_BG_PNG_TAG = "sub_bg";
    /**
     * 会议室背景图
     */
    public static final String ROOM_BG_PNG_TAG = "room_bg";
    /**
     * 公告背景图
     */
    public static final String NOTICE_BG_PNG_TAG = "notice_bg";
    /**
     * 公告logo图标
     */
    public static final String NOTICE_LOGO_PNG_TAG = "notice_logo";
    /**
     * 投影界面背景图
     */
    public static final String PROJECTIVE_BG_PNG_TAG = "projective_bg";
    /**
     * 投影界面logo图标
     */
    public static final String PROJECTIVE_LOGO_PNG_TAG = "projective_logo";

    //下载标识
    /**
     * 下载会议文件时的标识
     */
    public static final String DOWNLOAD_MEETING_FILE = "download_meeting_file";
    /**
     * 下载批注文件时的标识
     */
    public static final String DOWNLOAD_ANNOTATION_FILE = "download_annotation_file";
    /**
     * 下载会议议程文件的标识
     */
    public static final String DOWNLOAD_AGENDA_FILE = "download_agenda_file";
    /**
     * 下载完成应该打开的文件标识
     */
    public static final String DOWNLOAD_SHOULD_OPEN_FILE = "download_should_open_file";


    //上传文件标识
    public static final String UPLOAD_CHOOSE_FILE = "upload_choose_file";
    public static final String UPLOAD_DRAW_PIC = "upload_draw_pic";
    public static final String UPLOAD_WPS_FILE = "upload_wps_file";

    public static final int RESOURCE_ID_0 = 0;
    public static final int RESOURCE_ID_1 = 1;
    public static final int RESOURCE_ID_2 = 2;
    public static final int RESOURCE_ID_3 = 3;
    public static final int RESOURCE_ID_4 = 4;
    public static final int RESOURCE_ID_10 = 10;
    public static final int RESOURCE_ID_11 = 11;

    public static final int SCREEN_SUB_ID = 2;
    public static final int CAMERA_SUB_ID = 3;

    //自定义其它功能的功能码
    public static final int FUN_CODE = 200000;
    public static final int FUN_CODE_TERMINAL = FUN_CODE + 1;
    public static final int FUN_CODE_VOTE = FUN_CODE + 2;
    public static final int FUN_CODE_ELECTION = FUN_CODE + 3;
    public static final int FUN_CODE_VIDEO = FUN_CODE + 4;
    public static final int FUN_CODE_SCREEN = FUN_CODE + 5;
    public static final int FUN_CODE_BULLETIN = FUN_CODE + 6;
    public static final int FUN_CODE_SCORE = FUN_CODE + 7;


    /**
     * 发起播放的类型
     */
    public static final String EXTRA_VIDEO_ACTION = "extra_video_action";
    /**
     * 发起播放的设备ID
     */
    public static final String EXTRA_VIDEO_DEVICE_ID = "extra_video_device_id";
    /**
     * 发起播放的设备id流通道
     */
    public static final String EXTRA_VIDEO_SUB_ID = "extra_video_sub_id";
    /**
     * 发起播放的文件类型
     */
    public static final String EXTRA_VIDEO_SUBTYPE = "extra_video_subtype";
    /**
     * 播放的媒体id
     */
    public static final String EXTRA_VIDEO_MEDIA_ID = "extra_video_media_id";
    /**
     * 设备对讲
     */
    public static final String EXTRA_INVITE_FLAG = "extra_invite_flag";
    public static final String EXTRA_OPERATING_DEVICE_ID = "extra_operating_device_id";
    /**
     * 传入摄像头前置/后置
     */
    public static final String EXTRA_CAMERA_TYPE = "extra_camera_type";


    /**
     * 投票时提交，用于签到参与投票
     */
    public static final int PB_VOTE_SELFLAG_CHECKIN = 0x80000000;

    //文件类别

    //  大类
    /**
     * 音频
     */
    public static final int MEDIA_FILE_TYPE_AUDIO = 0x00000000;
    /**
     * 视频
     */
    public static final int MEDIA_FILE_TYPE_VIDEO = 0x20000000;
    /**
     * 录制
     */
    public static final int MEDIA_FILE_TYPE_RECORD = 0x40000000;
    /**
     * 图片
     */
    public static final int MEDIA_FILE_TYPE_PICTURE = 0x60000000;
    /**
     * 升级
     */
    public static final int MEDIA_FILE_TYPE_UPDATE = 0xe0000000;
    /**
     * 临时文件
     */
    public static final int MEDIA_FILE_TYPE_TEMP = 0x80000000;
    /**
     * 其它文件
     */
    public static final int MEDIA_FILE_TYPE_OTHER = 0xa0000000;
    public static final int MAIN_TYPE_BITMASK = 0xe0000000;

    //  小类
    /**
     * PCM文件
     */
    public static final int MEDIA_FILE_TYPE_PCM = 0x01000000;
    /**
     * MP3文件
     */
    public static final int MEDIA_FILE_TYPE_MP3 = 0x02000000;
    /**
     * WAV文件
     */
    public static final int MEDIA_FILE_TYPE_ADPCM = 0x03000000;
    /**
     * FLAC文件
     */
    public static final int MEDIA_FILE_TYPE_FLAC = 0x04000000;
    /**
     * MP4文件
     */
    public static final int MEDIA_FILE_TYPE_MP4 = 0x07000000;
    /**
     * MKV文件
     */
    public static final int MEDIA_FILE_TYPE_MKV = 0x08000000;
    /**
     * RMVB文件
     */
    public static final int MEDIA_FILE_TYPE_RMVB = 0x09000000;
    /**
     * RM文件
     */
    public static final int MEDIA_FILE_TYPE_RM = 0x0a000000;
    /**
     * AVI文件
     */
    public static final int MEDIA_FILE_TYPE_AVI = 0x0b000000;
    /**
     * bmp文件
     */
    public static final int MEDIA_FILE_TYPE_BMP = 0x0c000000;
    /**
     * jpeg文件
     */
    public static final int MEDIA_FILE_TYPE_JPEG = 0x0d000000;
    /**
     * png文件
     */
    public static final int MEDIA_FILE_TYPE_PNG = 0x0e000000;
    /**
     * 其它文件
     */
    public static final int MEDIA_FILE_TYPE_OTHER_SUB = 0x10000000;

    public static final int SUB_TYPE_BITMASK = 0x1f000000;


    /* **** **  权限码  ** **** */
    /**
     * 同屏权限
     */
    public static final int permission_code_screen = 1;
    /**
     * 投影权限
     */
    public static final int permission_code_projection = 2;
    /**
     * 上传权限
     */
    public static final int permission_code_upload = 4;
    /**
     * 下载权限
     */
    public static final int permission_code_download = 8;
    /**
     * 投票权限
     */
    public static final int permission_code_vote = 16;

    /**
     * 发送广播时的action和extra
     */
    public static final String ACTION_START_SCREEN_RECORD = "action_start_screen_record";
    public static final String ACTION_STOP_SCREEN_RECORD = "action_stop_screen_record";
    /**
     * 退出应用时发送广播通知停止掉屏幕录制
     */
    public static final String ACTION_STOP_SCREEN_RECORD_WHEN_EXIT_APP = "action_stop_screen_record_when_exit_app";
    /**
     * 要采集的类型，值为2或3，屏幕或摄像头
     */
    public static final String EXTRA_CAPTURE_TYPE = "extra_capture_type";
}
