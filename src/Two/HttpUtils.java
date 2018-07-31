package Two;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpUtils {

    private static Map<String, String> parameters = new HashMap<String, String>();

    static {
       /* parameters.put("output",OUTPUT);
        parameters.put("key",KEY);
        parameters.put("extensions",extensions);
        parameters.put("radius",radius +"");*/
    }

    /**
     * 发送GET请求
     *
     * @param url        目的地址
     * @param parameters 请求参数，Map类型。
     * @return 远程响应结果
     */
    public static String sendGet(String url, Map<String, String> parameters) {
        String result = "";
        BufferedReader in = null;// 读取响应输入流
        StringBuffer sb = new StringBuffer();// 存储参数
        String params = "";// 编码之后的参数
        try {
            // 编码请求参数
            if (parameters.size() == 1) {
                System.out.println("GET");
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name), "UTF-8"));
                }
                params = sb.toString();
            } else {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name), "UTF-8"))
                            .append("&");
                }
                String temp_params = sb.toString();
                params = temp_params.substring(0, temp_params.length() - 1);
            }
            String full_url = url + "?" + params;
            System.out.println(full_url);
            //System.out.println(full_url);
            // 创建URL对象
            java.net.URL connURL = new java.net.URL(full_url);
            // 打开URL连接
            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL.openConnection();
            // 设置通用属性
            httpConn.setRequestProperty("Accept", "*/*");//application/www-form-urlencoded
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
            // 建立实际的连接
            httpConn.connect();
            /*// 响应头部获取
            Map<String, List<String>> headers = httpConn.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : headers.keySet()) {
                System.out.println(key + "\t：\t" + headers.get(key));
            }*/
            // 定义BufferedReader输入流来读取URL的响应,并设置编码方式
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
            String line;
            // 读取返回的内容
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 发送POST请求
     *
     * @param url        目的地址
     * @param parameters 请求参数，Map类型。
     * @return 远程响应结果
     */
    public static String sendPost(String url, Map<String, String> parameters) {
        String result = "";// 返回的结果
        BufferedReader in = null;// 读取响应输入流
        PrintWriter out = null;
        StringBuffer sb = new StringBuffer();// 处理请求参数
        String params = "";// 编码之后的参数
        try {
            // 编码请求参数
            if (parameters.size() == 1) {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name), "UTF-8"));
                }
                params = sb.toString();
            } else {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name), "UTF-8"))
                            .append("&");
                }
                String temp_params = sb.toString();
                params = temp_params.substring(0, temp_params.length() - 1);
            }
            // 创建URL对象
            java.net.URL connURL = new java.net.URL(url);
            System.out.println(url);
            // 打开URL连接
            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL.openConnection();
            // 设置通用属性
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
            // 设置POST方式
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            // 获取HttpURLConnection对象对应的输出流
            out = new PrintWriter(httpConn.getOutputStream());
            // 发送请求参数
            out.write(params);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应，设置编码方式
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
            String line;
            // 读取返回的内容
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 主函数，测试请求
     *
     * @param args
     */

    public static void main(String[] args) {
        Map<String, String> parameters = new HashMap<String, String>();
//        parameters.put("location", "104.0657816,30.5503350");
        /*parameters.put("location", "0,0");
        parameters.put("output", OUTPUT);
        parameters.put("key", KEY);
        parameters.put("extensions", extensions);
        parameters.put("radius", radius + "");
        String result = sendGet(GET_LNG_LAT_URL, parameters);*/
        /*String url = "http://183.230.96.66:8087/v2/batchgprsusedbydate";
        String appid = "I1AZT4KU";
        String transid = "I1AZT4KU201801231529320000001";
        String ebid = "0001000000027";
        String token = "26eabbc6f15f6913194a66ee95f00e77c689ebb0a8012ac4bae727b0c8674ae3";
        String query_date = "20180122";
        String msisdns = "1064759696950";*/
        /*String url = "http://183.230.96.66:8087/v2/gprsusedinfosingle";
        String appid = "I1AZT4KU";
        String transid = "I1AZT4KU201801251529320000002";
        String ebid = "0001000000012";
        String token = "94bda9fa209862d78a7cf88b043069eb25fc7027915032df05b1e4b0704cce55";
        //String query_date = "20180125";
        String msisdns = "1064759696950";
        parameters.put("appid", appid);
        parameters.put("transid", transid);
        parameters.put("ebid", ebid);
        parameters.put("token", token);
       // parameters.put("query_date", query_date);
*/        
        String url = "http://183.230.96.66:8087/v2/gprsrealsingle";
        String appid = "I1AZT4KU";
        String transid = "I1AZT4KU201801251529320000002";
        String ebid = "0001000000008";
        String token = "94bda9fa209862d78a7cf88b043069eb25fc7027915032df05b1e4b0704cce55";
        //String query_date = "20180125";
        String msisdns = "1064759696950";
        parameters.put("appid", appid);
        parameters.put("transid", transid);
        parameters.put("ebid", ebid);
        parameters.put("token", token);
       // parameters.put("query_date", query_date);
        parameters.put("msisdn", msisdns);
        String post = sendGet(url, parameters);
        System.out.println("开始的数据：" + post);
    }


}
