package com.xlk.jbpaperless.view.agenda;

import com.xlk.jbpaperless.base.BaseContract;

/**
 * @author Created by xlk on 2021/3/22.
 * @desc
 */
interface AgendaContract  {
    interface View extends BaseContract.View{
        /**
         * 更新议程文本内容
         * @param agendaContent 议程文本内容
         */
        void updateAgendaContent(String agendaContent);

        /**
         * 展示议程文件
         * @param filePath 文件路径
         */
        void displayAgendaFile(String filePath);
    }
    interface Presenter extends BaseContract.Presenter{}
}
