package com.fenbeitong.es;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class EsApiApplicationTests {

	@Autowired
	@Qualifier("restHighLevelClient")
	private RestHighLevelClient client;

	@Test
	void contextLoads() {
	}

	/**
	 * 1、创建索引
	 */
	@Test
	void createIndex() throws IOException {
		//=======================一、创建索引=======================//
		//1、字符串形式
		IndexRequest request = new IndexRequest("posts1");
		request.id("1");
		String jsonString = "{" +
				"\"user\":\"kimchy\"," +
				"\"postDate\":\"2013-01-30\"," +
				"\"message\":\"trying out Elasticsearch\"" +
				"}";
		request.source(jsonString, XContentType.JSON);

		//2、map 形式
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("user", "kimchy");
		jsonMap.put("postDate", new Date());
		jsonMap.put("message", "trying out Elasticsearch");
		IndexRequest indexRequest = new IndexRequest("posts2")
				.id("2").source(jsonMap);


		try {
			IndexResponse response = client.index(request, RequestOptions.DEFAULT);
			IndexResponse index = client.index(indexRequest, RequestOptions.DEFAULT);

			System.out.println(response.toString());
			System.out.println(index.toString());
		} catch(ElasticsearchException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 2、获取索引
	 */
	@Test
	public void getIndex() throws IOException {


		GetRequest getRequest = new GetRequest("posts2","2");
		GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);


		//1、获取字段
		System.out.println(getResponse.getField("user"));
		System.out.println(getResponse.getField("type"));

		String index = getResponse.getIndex();
		String id = getResponse.getId();

		if (getResponse.isExists()) {
			long version = getResponse.getVersion();
			System.out.println("version:"+version);

			//以文本形式
			String sourceAsString = getResponse.getSourceAsString();
			System.out.println("sourceAsString:"+sourceAsString);

			//以map形式
			Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
			System.out.println(sourceAsMap.keySet());
			System.out.println(sourceAsMap.values());

			//以字节形式
			byte[] sourceAsBytes = getResponse.getSourceAsBytes();
			System.out.println("sourceAsBytes:"+new String(sourceAsBytes));
		}
	}

}
