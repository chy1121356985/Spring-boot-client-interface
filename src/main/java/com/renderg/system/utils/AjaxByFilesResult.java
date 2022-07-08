package com.renderg.system.utils;



import com.renderg.system.constant.HttpStatus;

import java.util.HashMap;

/**
 * 操作消息提醒
 *
 * @author ruoyi
 */
public class AjaxByFilesResult extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    public static final String CODE_TAG = "code";

    /**
     * 返回内容
     */
    public static final String MSG_TAG = "msg";

    /**
     * 数据对象
     */
    public static final String DATA_TAG = "paths";

    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */

    /**
     * 解压、压缩操作随机返回一个id
     */
    public static final String ZIP_ID_TAG="zipId";


    public AjaxByFilesResult() {
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     */
    public AjaxByFilesResult(int code, String msg) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     * @param data 数据对象
     */
    public AjaxByFilesResult(int code, String msg, Object data) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (StringUtils.isNotNull(data)) {
            super.put(DATA_TAG, data);
        }
    }


    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static AjaxByFilesResult success() {
        return AjaxByFilesResult.success("操作成功");
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static AjaxByFilesResult success(Object data) {
        return AjaxByFilesResult.success("操作成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static AjaxByFilesResult success(String msg) {
        return AjaxByFilesResult.success(msg, null);
    }

    /**
     * 返回成功消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static AjaxByFilesResult success(String msg, Object data) {
        return new AjaxByFilesResult(HttpStatus.SUCCESS, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @return
     */
    public static AjaxByFilesResult error() {
        return AjaxByFilesResult.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static AjaxByFilesResult error(String msg) {
        return AjaxByFilesResult.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static AjaxByFilesResult error(String msg, Object data) {
        return new AjaxByFilesResult(HttpStatus.ERROR, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @return 警告消息
     */
    public static AjaxByFilesResult error(int code, String msg) {
        return new AjaxByFilesResult(code, msg, null);
    }
}
