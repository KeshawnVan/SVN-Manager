package com.star.svn;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.star.svn.bean.AuthInfoPojo;
import com.star.svn.bean.FileChangePojo;
import com.star.svn.bean.VersionChangePojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: java类作用描述
 * @Author: yc
 * @CreateDate: 2018/5/9 11:12
 * @UpdateUser: yc
 * @UpdateDate: 2018/5/9 11:12
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class SvnKitUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SvnKitUtil.class);
    /**
     * 临时文件路径信息
     */
    private static final String TEMP_DIR = "D:/";
    /**
     * 驱动协议标志
     */
    private Integer protocolFlag = ProtocolConts.SVN_FLAG;
    private SVNRepository repository;
    private AuthInfoPojo authInfoPojo;
    private ISVNAuthenticationManager authManager;
    /**
     * 创建一个默认的运行时配置选项驱动程序，它使用默认的SVN的运行时配置区域。
     */
    private DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);

    public SvnKitUtil() {
    }

    public SvnKitUtil(Integer protocolFlag, AuthInfoPojo authInfoPojo) {
        this.protocolFlag = protocolFlag;
        this.authInfoPojo = authInfoPojo;
    }

    /**
     * 方法实现说明:初始化操作
     *
     * @return
     * @throws
     * @author 作者姓名
     * @date 2018/5/9 13:08
     */
    public void initialize() throws Exception {
        //加载驱动
        RepositoryDriverFactory.loadDriver(protocolFlag);
        //连接远程仓库
        repository = SVNRepositoryFactoryImpl.create(SVNURL
                .parseURIEncoded(authInfoPojo.getUrl()));
        //资格认证
        authManager = SVNWCUtil
                .createDefaultAuthenticationManager(authInfoPojo.getUsername(),
                        authInfoPojo.getPassword().toCharArray());
        repository.setAuthenticationManager(authManager);
        LOGGER.info("initialize success");
    }

    /**
     * 方法实现说明:在特定的时间点返回最近的存储库修订号（版本号）——最接近于指定的datestamp
     *
     * @return
     * @throws
     * @author 作者姓名
     * @date 2018/5/9 13:37
     */
    public long getRevisionByTime(Date time) throws SVNException {
        return repository.getDatedRevision(time);
    }

    /**
     * 方法实现说明:获得版本区间内的所有提交信息
     *
     * @return
     * @throws
     * @author 作者姓名
     * @date 2018/5/9 13:42
     */
    public List<SVNLogEntry> getLogByVersion(long startVersion, long endVersion) throws SVNException {
        @SuppressWarnings("unchecked")
        Collection<SVNLogEntry> logEntries = repository.log(new String[]{""}, null, startVersion, endVersion, true, true);
        return Lists.newArrayList(logEntries);
    }

    /**
    * 方法实现说明:根据时间区间获得多次提交的版本变动信息
    * @author      作者姓名
    * @return
    * @exception
    * @date        2018/5/10 14:11
    */
    public Map<Long,VersionChangePojo> getVersionChangeMapByTime(Date startDate , Date endDate){
        Map<Long , VersionChangePojo> versionChangePojoMap = null;
        try {
            long startRevision = getRevisionByTime(startDate);
            long endRevision = getRevisionByTime(endDate);
            //存放版本号，下面有顺序排列操作
            List<Long> versionList = Lists.newArrayList();

            //获得两个版本区间的所有版本
            List<SVNLogEntry> svnLogEntryList = getLogByVersion(startRevision, endRevision);
            //为空
            if (CollectionUtils.isEmpty(svnLogEntryList)){
                return Collections.emptyMap();
            }
            //获取相应版本基本信息
            versionChangePojoMap = getLongVersionChangePojoBasicMap(versionList, svnLogEntryList);
            //填充相应版本变动文件详细信息
            fillLongVersionChangePojoMap(versionChangePojoMap, versionList);

        } catch (SVNException e) {
            LOGGER.error(" getVersionChange exception" , e);
        }
        return versionChangePojoMap;
    }

    /**
    * 方法实现说明:获取相应版本基本信息
    * @author      作者姓名
    * @return
    * @exception
    * @date        2018/5/10 15:54
    */
    public Map<Long, VersionChangePojo> getLongVersionChangePojoBasicMap(List<Long> versionList, List<SVNLogEntry> svnLogEntryList) {
        Map<Long, VersionChangePojo> versionChangePojoMap = Maps.newHashMap();
        //获取相应版本基本信息
        for (SVNLogEntry svnLogEntry : svnLogEntryList) {
            long revision = svnLogEntry.getRevision();
            VersionChangePojo versionChangePojo = getVersionChangePojo(svnLogEntry);
            versionChangePojoMap.put(revision,versionChangePojo);
            versionList.add(revision);
        }
        return versionChangePojoMap;
    }

    /**
    * 方法实现说明:解析SVNLogEntry文件
    * @author      作者姓名
    * @return
    * @exception
    * @date        2018/5/10 16:37
    */
    public VersionChangePojo getVersionChangePojo(SVNLogEntry svnLogEntry) {
        VersionChangePojo versionChangePojo = new VersionChangePojo();
        BeanUtils.copyProperties(svnLogEntry,versionChangePojo);
        //获得相应版本对应变动文件信息集合
        Map<String , FileChangePojo> fileChangePojoMap = Maps.newHashMap();
        Map<String, SVNLogEntryPath> svnLogEntryPathMap = svnLogEntry.getChangedPaths();
        for (Map.Entry<String, SVNLogEntryPath> entry : svnLogEntryPathMap.entrySet()) {
            FileChangePojo fileChangePojo = new FileChangePojo();
            BeanUtils.copyProperties(entry.getValue(),fileChangePojo);
            fileChangePojoMap.put(entry.getKey(),fileChangePojo);
        }
        versionChangePojo.setFileChangePojoMap(fileChangePojoMap);
        return versionChangePojo;
    }

    /**
     * 方法实现说明:填充相应版本变动文件详细信息
     * @author      作者姓名
     * @return
     * @exception
     * @date        2018/5/10 15:56
     */
    private void fillLongVersionChangePojoMap(Map<Long, VersionChangePojo> versionChangePojoMap, List<Long> versionList) {
        //获得相应版本变动文件详细信息
        Collections.sort(versionList);
        int endNum = versionList.size() - 1;
        //下标编号
        int num = 0;
        do {
            Long version1 = versionList.get(num);
            Long version2 = versionList.get(num + 1);
            //获得比较文件
            File changeLogFile = getChangeLogFile(version1, version2);
            VersionChangePojo versionChangePojo = versionChangePojoMap.get(version2);
            versionChangePojo.setOldRevision(version1);
            statisticsCode(changeLogFile,versionChangePojo);
//            versionChangePojoMap.put(version2,versionChangePojo);
            num ++;
        }while (endNum <= num);
    }

    /**
     * 方法实现说明:获得两个版本比较临时文件
     *
     * @return
     * @throws
     * @author 作者姓名
     * @date 2018/5/9 14:04
     */
    public File getChangeLogFile(long version1, long version2) {

        //用指定的运行时配置和身份验证驱动程序构造和初始化svn扩散client对象
        SVNDiffClient diffClient = new SVNDiffClient(authManager, options);
        diffClient.setGitDiffFormat(true);
        File tempLogFile;
        String svnDiffFile;

        //创建临时文件
        do {
            svnDiffFile = TEMP_DIR + "/svn_diff_file_" + version1 + "_" + version2 + "_" + System.currentTimeMillis() + ".txt";
            tempLogFile = new File(svnDiffFile);
        } while (tempLogFile.exists() && tempLogFile != null);

        //使用Java7新增的自动关闭资源的try语句
        try (OutputStream outputStream = new FileOutputStream(svnDiffFile)) {
            tempLogFile.createNewFile();
            //在这里要注意他们的比对顺序，不同的比对顺序产生的比对文件是不同的，推荐使用version1对应老的版本，version2对应新的版本比较符合常规逻辑
            diffClient.doDiff(SVNURL.parseURIEncoded(authInfoPojo.getUrl()),
                    SVNRevision.create(version1),
                    SVNURL.parseURIEncoded(authInfoPojo.getUrl()),
                    SVNRevision.create(version2),
                    SVNDepth.UNKNOWN, true, outputStream);
        } catch (Exception e) {
            LOGGER.error("get file exception", e);
        }
        return tempLogFile;
    }

    /**
     * 方法实现说明:分析变更的代码，统计代码增量
     *
     * @return
     * @throws
     * @author 作者姓名
     * @date 2018/5/9 15:11
     */
    public Map<String, FileChangePojo> statisticsCode(File file,VersionChangePojo versionChangePojo){
        LOGGER.info("statistics begin");
        Map<String, FileChangePojo> fileChangePojoMap = Collections.EMPTY_MAP;
        int sum = 0;

        try (BufferedReader in = new BufferedReader(new FileReader(file))){
            fileChangePojoMap = versionChangePojo.getFileChangePojoMap();
            StringBuffer buffer = new StringBuffer(1024);
            boolean start = false;
            String line;

            while (!StringUtils.isEmpty(line = in.readLine())) {
                if (line.startsWith("Index:")) {
                    if (start) {
                        FileChangePojo fileChangePojo1 = parseFileChange(buffer);
                        String path = fileChangePojo1.getPath();
                        sum+=fileChangePojo1.getAddCode();
                        FileChangePojo fileChangePojo2 = fileChangePojoMap.get(path);
                        fileChangePojo2.setAddCode(fileChangePojo1.getAddCode());
                        buffer.setLength(0);
                    }
                    start = true;
                }
                buffer.append(line).append('\n');
            }
            if (buffer.length() > 0) {
                FileChangePojo fileChangePojo1 = parseFileChange(buffer);
                String path = fileChangePojo1.getPath();
                sum+=fileChangePojo1.getAddCode();
                FileChangePojo fileChangePojo2 = fileChangePojoMap.get(path);
                fileChangePojo2.setAddCode(fileChangePojo1.getAddCode());
            }
            versionChangePojo.setAddSumCode(sum);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        boolean deleteFile = file.delete();
//        System.out.println("-----delete file-----" + deleteFile);
        return fileChangePojoMap;
    }

    /**解析单个文件变更日志
     * @param str
     * @return
     */
    public FileChangePojo parseFileChange(StringBuffer str){
        int index = str.indexOf("\n@@");
        if(index > 0){
            String header = str.substring(0, index);
            String[] headers = header.split("\n");
            StringBuilder filePath = new StringBuilder("/").append(headers[0].substring(7));

            int bodyIndex = str.indexOf("@@\n")+3;
            String body = str.substring(bodyIndex);
            FileChangePojo fileChangePojo = new FileChangePojo();
            fileChangePojo.setPath(filePath.toString());
            int i = countAddLine(body);
            fileChangePojo.setAddCode(i);
            return fileChangePojo;
        }else{
            String[] headers = str.toString().split("\n");
            StringBuilder filePath = new StringBuilder("/").append(headers[0].substring(7));
            FileChangePojo fileChangePojo = new FileChangePojo();
            fileChangePojo.setPath(filePath.toString());
            return fileChangePojo;
        }
    }

//    public int staticOneFileChange(ChangeFilePojo changeFile){
//        char changeType = changeFile.getChangeType();
//        if(changeType == 'A'){
//            return countAddLine(changeFile.getFileContent());
//        }else if(changeType == 'M'){
//            return countAddLine(changeFile.getFileContent());
//        }
//        return 0;
//    }

    public int countAddLine(String content){
        int sum = 0;
        if(content !=null){
            content = '\n' + content +'\n';
            char[] chars = content.toCharArray();
            int len = chars.length;
            //判断当前行是否以+号开头
            boolean startPlus = false;
            //判断当前行，是否为空行（忽略第一个字符为加号）
            boolean notSpace = false;

            for(int i=0;i<len;i++){
                char ch = chars[i];
                if(ch =='\n'){
                    //当当前行是+号开头，同时其它字符都不为空，则行数+1
                    if(startPlus && notSpace){
                        sum++;
                        notSpace = false;
                    }
                    //为下一行做准备，判断下一行是否以+头
                    if(i < len-1 && chars[i+1] == '+'){
                        startPlus = true;
                        //跳过下一个字符判断，因为已经判断了
                        i++;
                    }else{
                        startPlus = false;
                    }
                }else if(startPlus && ch > ' '){//如果当前行以+开头才进行非空行判断
                    notSpace = true;
                }
            }
        }

        return sum;
    }

    public void sum(){
        String content = " cda.url=http://localhost/CDAEngineUI-1.0/CDAEngineUI.html\n" +
                " \n" +
                " #Xds服务器地址\n" +
                "-xds.iti18=xds-iti18://localhost/democam/xds-iti18\n" +
                "-xds.iti41=xds-iti41://localhost/democam/xds-iti41\n" +
                "-xds.iti42=xds-iti42://localhost/democam/xds-iti42\n" +
                "-xds.iti43=xds-iti43://localhost/democam/xds-iti43\n" +
                "+xds.iti18=xds-iti18://[图片]10.4.54.13:8082/hip_ipf/xds-iti18\n" +
                "+xds.iti41=xds-iti41://[图片]10.4.54.13:8082/hip_ipf/xds-iti41\n" +
                "+xds.iti42=xds-iti42://[图片]10.4.54.13:8082/hip_ipf/xds-iti42\n" +
                "+xds.iti43=xds-iti43://[图片]10.4.54.13:8082/hip_ipf/xds-iti43";
        if (!StringUtils.isEmpty(content)){
            String[] strings = content.split("\\n");
            System.out.println(Arrays.toString(strings));
            long sum = Arrays.asList(strings).stream().filter(s -> s.startsWith("+")).count();
            System.out.println(sum);
        }
    }

    public static void main(String[] args) {
        AuthInfoPojo authInfoPojo = new AuthInfoPojo();
        authInfoPojo.setUsername("liuna");
        authInfoPojo.setPassword("123");
        authInfoPojo.setUrl("svn://58.87.84.167");
        SvnKitUtil demo = new SvnKitUtil(0,authInfoPojo);
        Date now = new Date();
        Date before = new Date(now.getTime() - 4 * 24 * 60 * 60 * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        System.out.println(sdf.format(before));
        try {
            demo.initialize();
            Map<Long, VersionChangePojo> versionChangeMapByTime = demo.getVersionChangeMapByTime(before, now);
            System.out.println(versionChangeMapByTime);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
