package com.s515.rpc.invoker.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.s515.rpc.invoker.Invoker;
import com.s515.rpc.invoker.data.Request;
import com.s515.rpc.router.LoadBalance;
import com.s515.rpc.router.server.Node;
import com.s515.rpc.service.annotation.Path;
import com.s515.rpc.service.annotation.Service;

/**
 * Created by Administrator on 8/25/2016.
 */
public class HttpAsyncInvoker implements Invoker {
	private final static Logger logger = LoggerFactory.getLogger(HttpAsyncInvoker.class);

	private CloseableHttpAsyncClient httpAsyncClient;

	private String encoding = "utf-8";

	private long timeout = 5000;

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

		long start = System.currentTimeMillis();
		InputStream instream = null;
		HttpEntity entity = null;

		try {
			List<NameValuePair> nvps = buildValuePair(params);
			post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

			Future<HttpResponse> future = httpAsyncClient.execute(post, null);
			// Future<HttpResponse> future = httpAsyncClient.execute(post, new
			// FutureCallback<HttpResponse>() {
			//
			// @Override
			// public void cancelled() {
			// logger.info("This request has been cancelled!");
			// logger.info("It spends " + (System.currentTimeMillis() - start) +
			// " ms!");
			// }
			//
			// @Override
			// public void completed(final HttpResponse response) {
			// StatusLine status = response.getStatusLine();
			// if (status.getStatusCode() == 200) {
			// HttpEntity entity = response.getEntity();
			// try {
			// if (entity != null) {
			// final InputStream instream = entity.getContent();
			// try {
			// final StringBuilder sb = new StringBuilder();
			// final char[] tmp = new char[1024];
			// final Reader reader = new InputStreamReader(instream,encoding);
			// int l;
			// while ((l = reader.read(tmp)) != -1) {
			// sb.append(tmp, 0, l);
			// }
			// res.put("response", sb.toString());
			// } finally {
			// instream.close();
			// EntityUtils.consume(entity);
			// }
			// }
			// } catch (ParseException | IOException e) {
			// }
			// } else {
			// logger.info(response.getStatusLine().getReasonPhrase());
			// }
			// logger.info("It spends " + (System.currentTimeMillis() - start) +
			// " ms to finish this job!");
			// }
			//
			// @Override
			// public void failed(Exception arg0) {
			// logger.info("Failed to invoke this request!");
			// logger.info("It spends " + (System.currentTimeMillis() - start) +
			// " ms!");
			// }

			// });
			HttpResponse response = future.get(timeout, TimeUnit.MILLISECONDS);
			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() == 200) {
				entity = response.getEntity();
				if (entity != null) {
					instream = entity.getContent();
					final StringBuilder sb = new StringBuilder();
					final char[] tmp = new char[1024];
					final Reader reader = new InputStreamReader(instream, encoding);
					int l;
					while ((l = reader.read(tmp)) != -1) {
						sb.append(tmp, 0, l);
					}
					res = sb.toString();
				}
			} else {
				logger.info(response.getStatusLine().getReasonPhrase());
			}
			logger.info("It spends " + (System.currentTimeMillis() - start) + " ms to finish this job!");
		} catch (IOException e) {
		} catch (NoSuchAlgorithmException e) {
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			logger.error("Filed to invoke!", e);
		} finally {
			try {
				if (instream != null)
					instream.close();
				EntityUtils.consume(entity);
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

	public List<NameValuePair> buildValuePair(Map<String, Object> params)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		List<NameValuePair> nvps = null;
		if (params == null || params.size() == 0) {
			return nvps;
		}

		nvps = new ArrayList<>();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
		}
		// nvps.add(new BasicNameValuePair("sign", Signature.sign(params,
		// HttpConstants.secretKey)));
		return nvps;
	}

	public String selectAppNode() {
		Node appNode = loadBalance.select(nodes);

		if (appNode != null)
			return appNode.getNodeUrl();

		return null;
	}

	public CloseableHttpAsyncClient getHttpClient() {
		return httpAsyncClient;
	}

	public void setHttpClient(CloseableHttpAsyncClient httpClient) {
		this.httpAsyncClient = httpClient;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
	
	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public LoadBalance getLoadBalance() {
		return loadBalance;
	}

	public void setLoadBalance(LoadBalance loadBalance) {
		this.loadBalance = loadBalance;
	}

	@Override
	public void close() throws IOException {
		if (httpAsyncClient != null) {
			httpAsyncClient.close();
		}
	}

	@Override
	public void start() {
		if (httpAsyncClient != null && !httpAsyncClient.isRunning()) {
			httpAsyncClient.start();
		}
	}
}
