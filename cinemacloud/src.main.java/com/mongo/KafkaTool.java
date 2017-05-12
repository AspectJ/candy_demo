package com.mongo;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @ClassName: KafkaTool
 * @Description: kafka消息推列推送
 * @author stone
 * @date 2016年10月25日 下午5:39:25
 */
@Service
public class KafkaTool extends Thread
{
	public static String TOPIC;

	public static String ZOOKEEPER_CONNECT;

	public static String BROKER_LIST;

	/**
	 * @Title: createProducer
	 * @Description: TODO
	 * @param @return
	 * @return Producer<String,String>
	 * @throws
	 */
	private static Producer<String, String> createProducer()
	{
		Properties properties = new Properties();
		properties.put("zookeeper.connect", ZOOKEEPER_CONNECT);// 声明zk
		properties.put("serializer.class", StringEncoder.class.getName());
		properties.put("metadata.broker.list", BROKER_LIST);// 声明kafka broker
		return new Producer<String, String>(new ProducerConfig(properties));
	}

	/**
	 * @Title: send
	 * @Description: 插入消息队列中
	 * @param @param message
	 * @return void
	 * @throws
	 */
	public static void send(String message)
	{
		if (queue.size() < 10000)
		{
			queue.add(message);
		}
	}

	@PostConstruct
	private void startKafkaSend()
	{
		this.start();
	}

	private static ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();

	private static Producer<String, String> producer;

	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				while (!queue.isEmpty())
				{
					if (producer == null)
					{
						try
						{
							producer = createProducer();
						} catch (Exception e)
						{
							Thread.sleep(30000);
							continue;
						}
					}
					producer.send(new KeyedMessage<String, String>(TOPIC, queue.poll()));
				}
				Thread.sleep(3000);
			} catch (Exception e)
			{
				producer = null;
			}
		}
	}
}
