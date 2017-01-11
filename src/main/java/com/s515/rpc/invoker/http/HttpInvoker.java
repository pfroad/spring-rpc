package com.s515.rpc.invoker.http;

import com.s515.rpc.invoker.Invoker;
import com.s515.rpc.invoker.data.Request;
import com.s515.rpc.router.LoadBalance;
import com.s515.rpc.router.server.Node;
import com.s515.rpc.service.annotation.Path;
import com.s515.rpc.service.annotation.Service;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 8/25/2016.
 */
public class HttpInvoker implements Invoker {
    private final static Logger logger = LoggerFactory.getLogger(HttpInvoker.class);

    private CloseableHttpClient httpClient;

    /**
     * hostname:port
     */
    private List<Node> nodes;

    private LoadBalance loadBalance;

    @Override
    public Object execute(Method method, Object[] args) {
        String res = null;

        Map<String, Object> params = getParametersMap(method, args);

        HttpPost post = new HttpPost(selectAppNode() + getInterface(method));
        CloseableHttpResponse response = null;
        try {
            List<NameValuePair> nvps = buildValuePair(params);
            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

            long start = System.currentTimeMillis();
            response = httpClient.execute(post);
            logger.info("It spends " + (System.currentTimeMillis() - start) + " ms to finish this job!");
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                try {
                    res = EntityUtils.toString(entity);
                    EntityUtils.consume(entity);
                } catch (ParseException e) {
                }
            } else {
                logger.info(response.getStatusLine().getReasonPhrase());
            }
        } catch (IOException e) {
        } catch (NoSuchAlgorithmException e) {
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
            }
        }

        return res;
    }

    public Map<String, Object> getParametersMap(Method method, Object[] args) {
        if (args != null && args.length > 0) {
            Parameter[] parameters = method.getParameters();

            Request request = getRequestIfExisted(args);

            if (request != null) {
                for (int i = 0; i < parameters.length; i++) {
                    if (args[i] instanceof Request)
                        continue;

                    request.getParameters().put(parameters[i].getName(), args[i]);
                }
            } else {
                request = new Request(new HashMap<String, Object>());
                for (int i = 0; i < parameters.length; i++) {
                    request.getParameters().put(parameters[i].getName(), args[i]);
                }
            }

            return request.getParameters();
        } else {
            return new HashMap<>();
        }
    }
    
    private Request getRequestIfExisted(Object[] args) {
        for (Object obj : args) {
            if (obj instanceof Request)
                return (Request) obj;
        }

        return null;
    }

    public String getInterface(Method method) {
        Service service = method.getDeclaringClass().getAnnotation(Service.class);
        Path path = method.getAnnotation(Path.class);
        
        StringBuffer res = new StringBuffer();
        
        if (service != null) {
        	res.append("/");
        	res.append(service.value());
        }
        
        res.append("/");
        if (path != null) {
        	res.append(path.path());
        } else {
        	res.append(method.getName());
        }
        
        return res.toString();
    }

    public List<NameValuePair> buildValuePair(Map<String, Object> params) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        List<NameValuePair> nvps = null;
        if (params == null || params.size() == 0) {
            return nvps;
        }

        nvps = new ArrayList<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        }
//        nvps.add(new BasicNameValuePair("sign", Signature.sign(params, HttpConstants.secretKey)));
        return nvps;
    }

    public String selectAppNode() {
        Node appNode = loadBalance.select(nodes);
        
        if (appNode != null)
        	return appNode.getNodeUrl();
        
        return null;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public LoadBalance getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

	@Override
	public void close() throws IOException {
		if (httpClient != null) {
			httpClient.close();
		}
	}

	@Override
	public void start() {
		
	}
}
