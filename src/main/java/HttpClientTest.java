/**
 * Created by Administrator on 2017/6/2 0002.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.internal.parser.JSONParser;


import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class HttpClientTest {
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpPost httppost = new HttpPost("http://dev.datahunter.cn/api/upload");

    @Test
    public void jUnitTest() {
        List<File> listFiles = getListFiles();

        for(File file:listFiles){
        String name1 = file.getName();

        String key="mrocker";
        upload(name1,file,key);

    }
    }
 /**
     * 上传文件
     */
    public void upload(String name,File file,String key) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://dev.datahunter.cn/api/upload");
        try {
            //获取传进来的File文件
           FileBody bin = new FileBody(file);
            //StringBody传入文件的格式
            StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);

            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("bin", bin).addPart("comment", comment).setCharset(CharsetUtils.get("UTF-8")).build();
            //httppost url 的链接和post请求的File拼装成post提交
            httppost.setEntity(reqEntity);

            System.out.println("执行请求 request " + httppost.getRequestLine());
           //执行post请求，返回response
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                System.out.println("----------------------------------------");
                System.out.println("获取返回状态---->"+response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    System.out.println("获取返回内容---->"+"Response content length: " + resEntity.getContentLength());
                    // 打印响应内容,必须要转换格式！！！不然获取不到msg的内容！！！
                    /*System.out.println(EntityUtils.toString(resEntity,
                            Charset.forName("UTF-8")));*/
                    String s = EntityUtils.toString(resEntity,
                            Charset.forName("UTF-8"));
                    //String转成JSON
                    JSONObject jsStr = JSONObject.parseObject(s);
                    JSONObject msg = jsStr.getJSONObject("msg");
                    //获取JSon的url值
                    String url= msg.getString("url");
                    System.out.print("------获取url的值-->"+url.toString());
                //上传url和name,和key
                    Post(name,url,key);

                }
                EntityUtils.consume(resEntity);
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送 post请求访问本地应用并根据传递参数不同返回不同结果
     */

    public void Post(String name,String url,String key){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost post = new HttpPost("http://dev.datahunter.cn/api/pub");          //这里用上本机的某个工程做测试
            //创建参数列表
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            //添加上post的请求携带的参数
            list.add(new BasicNameValuePair("name", name));
            list.add(new BasicNameValuePair("url", url));
            list.add(new BasicNameValuePair("key",key));
            //url格式编码
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(list,"UTF-8");
            post.setEntity(uefEntity);
            System.out.println("\n"+"POST 请求...." + post.getURI());

            //执行请求
            CloseableHttpResponse httpResponse = httpclient.execute(post);
            try{
                System.out.println("获取返回状态---->"+httpResponse.getStatusLine());

                HttpEntity entity = httpResponse.getEntity();
                System.out.println(EntityUtils.toString(entity,
                        Charset.forName("UTF-8")));
                if (null != entity){
                    System.out.println("-------------------------------------------------------");
                    System.out.println(EntityUtils.toString(uefEntity));
                    System.out.println("-------------------------------------------------------");
                }
            } finally{
                httpResponse.close();
            }

        } catch( UnsupportedEncodingException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            try{
                if(httpclient !=null){
                    httpclient.close();
                }


            } catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取所有的csv的文件列表。
     *
     */
    public List<File>  getListFiles(){
        List<File> files=new ArrayList<File>();
        files.add(new File("E:\\InterfaceAPI\\地区居民季度数据.csv"));
        files.add(new File("E:\\InterfaceAPI\\地区居民年度数据.csv"));
        files.add(new File("E:\\InterfaceAPI\\各地区农副产品价格指数.csv"));
        files.add(new File("E:\\InterfaceAPI\\全国GDP每年的数据.csv"));
        files.add(new File("E:\\InterfaceAPI\\全国居民季度收支表.csv"));
        files.add(new File("E:\\InterfaceAPI\\全国居民年度数据.csv"));
        files.add(new File("E:\\InterfaceAPI\\人民币汇率.csv"));
        files.add(new File("E:\\InterfaceAPI\\省份GDP每年的数据.csv"));
        files.add(new File("E:\\InterfaceAPI\\industry\\工业指数.csv"));
        files.add(new File("E:\\InterfaceAPI\\maincity\\主要城市消费价格指数.csv"));
        files.add(new File("E:\\InterfaceAPI\\nonindustry\\非制造业指数.csv"));
        files.add(new File("E:\\InterfaceAPI\\populationall\\全国分类总人数.csv"));
        files.add(new File("E:\\InterfaceAPI\\populationcity\\全国大型市总人数.csv"));
        files.add(new File("E:\\InterfaceAPI\\populationprovince\\全国各省总人数.csv"));
        files.add(new File("E:\\InterfaceAPI\\postCode\\全国邮编.csv"));
        files.add(new File("E:\\InterfaceAPI\\province\\居民消费价格指数.csv"));
        files.add(new File("E:\\InterfaceAPI\\regioncode\\全国行政区位码.csv"));


        return files;

    }

}