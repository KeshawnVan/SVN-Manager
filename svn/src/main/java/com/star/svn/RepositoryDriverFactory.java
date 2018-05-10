package com.star.svn;

import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;

public class RepositoryDriverFactory {

    /**
    * 方法实现说明:加载相应协议驱动,默认加载SVN协议的
    * @author      作者姓名
    * @return
    * @exception
    * @date        2018/5/9 11:32
    */
    public static void loadDriver(Integer protocolFlag){

        if (ProtocolConts.HTTP_FLAG.equals(protocolFlag) || ProtocolConts.HTTPS_FLAG.equals(protocolFlag)){
            //http:// and https://
            DAVRepositoryFactory.setup();
            return;
        }
        //svn://
        SVNRepositoryFactoryImpl.setup();

    }
}
