//package com.imooc;
//
//import ResourceConfig;
//import com.imooc.enums.BGMOperatorTypeEnum;
//import com.imooc.utils.JsonUtils;
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.curator.RetryPolicy;
//import org.apache.curator.framework.CuratorFramework;
//import org.apache.curator.framework.CuratorFrameworkFactory;
//import org.apache.curator.framework.recipes.cache.PathChildrenCache;
//import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
//import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
//import org.apache.curator.retry.ExponentialBackoffRetry;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.File;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.Map;
//
//@Component
//public class zkCuratorClient {
//    //zookeeper客户端
//    private CuratorFramework client=null;
//
//    @Autowired
//    private ResourceConfig resourceConfig;
//
//    //private  static  final String ZOOKEEPERSERVER="192.168.43.172:2181";
//    public  void Init() throws Exception {
//        if(client!=null){
//            return;
//        }
//        //重试策略
//        RetryPolicy retryPolicy=new ExponentialBackoffRetry(1000,5);
//
//        client = CuratorFrameworkFactory.builder().connectString(resourceConfig.getZookeeperServer())
//                .sessionTimeoutMs(10000).retryPolicy(retryPolicy).namespace("admin").build();
//        //启动客户端
//        client.start();
//       // String testData= new String(client.getData().forPath("/bgm/180927GP2ANN679P"));
//        //System.out.println(testData+"------------------------"+"\n");
//        //因为上面定义了命名空间是amdin，所以下面的监听地址不要加，实际监听的地址是 /amdin/bgm/
//        addChildWatch("/bgm");
//
//    }
//    //添加子节点
//    public void addChildWatch(String nodePath) throws Exception {
//
//        final PathChildrenCache cache = new PathChildrenCache(client, nodePath, true);
//
//        cache.start();
//
//        cache.getListenable().addListener(new PathChildrenCacheListener() {
//
//            @Override
//            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
//                    throws Exception {
//
//                if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
//                    System.out.println("监听到事件 CHILD_ADDED 监听到事件增加！！！");
//
//                    // 获取监听的路径
//                    String path = event.getData().getPath();
//                    //获取监听路径对应保存的数据
//                    String operatorObjStr = new String(event.getData().getData());
//                    //因为保存的数据是JSON格式，我们要提取里面的数据到一个Map中
//                    Map<String, String> map = JsonUtils.jsonToPojo(operatorObjStr, Map.class);
//                    //operType有两个值， 1代表添加 2代表删除
//                    String operatorType = map.get("operType");
//                    //取得路径值
//
//                    String songPath = map.get("path");
//                   //songPath = new String(songPath.getBytes("GBK"), "GBK");
//                    System.out.println(songPath);
//
//                    //保存到本地的bgm路径
//                    String filePath = resourceConfig.getFileSpace() + songPath;
//
//                    //\\\\表示的是\\，因为要转义
//                    String arrPath[] = songPath.split("\\\\");
//
//                    String finalPath = "";
//                    //  将\\变成/
//                    for(int i = 0; i < arrPath.length ; i ++) {
//                        //按给定规则切割出来都是数组，数组的第一项是空的！！
//                        if (StringUtils.isNotBlank(arrPath[i])) {
//                            finalPath += "/";
//                            finalPath += URLEncoder.encode(arrPath[i], "UTF-8") ;
//                        }
//                    }
//                    //下载bgm的Url地址
//                    String bgmUrl = resourceConfig.getBgmServer() + finalPath;
//                    //添加
//                    if (operatorType.equals(BGMOperatorTypeEnum.ADD.type)) {
//                        // 下载bgm到spingboot服务器
//                        URL url = new URL(bgmUrl);
//                        File file = new File(filePath);
//                        FileUtils.copyURLToFile(url, file);
//                        //添加同步完成要手动删除，他是无法覆盖的，他会一直显示第一次存入的数据
//                        client.delete().forPath(path);
//                        //删除
//                    } else if (operatorType.equals(BGMOperatorTypeEnum.DELETE.type)) {
//                        File file = new File(filePath);
//                        FileUtils.forceDelete(file);
//                        client.delete().forPath(path);
//                    }
//                }
//            }
//        });
//    }}
//
//
