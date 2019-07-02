import java.util.concurrent._
import java.util.{Collections, Properties}


import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConversions._

class consumer(val brokers: String,
                           val groupId: String,
                           val topic: String){

  val conf = new SparkConf().setAppName("consumer")
  val sc = new SparkContext(conf)

  val props = createConsumerConfig(brokers, groupId)
  val consumer = new KafkaConsumer[String, String](props)
  var executor: ExecutorService = null

  def shutdown() = {
    if (consumer != null)
      consumer.close();
    if (executor != null)
      executor.shutdown();
  }

  def createConsumerConfig(brokers: String, groupId: String): Properties = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props
  }

  def run() = {
    consumer.subscribe(Collections.singletonList(this.topic))

    Executors.newSingleThreadExecutor.execute(    new Runnable {
      override def run(): Unit = {
        while (true) {
          val records = consumer.poll(1000)

          for (record <- records) {
            System.out.println("Received message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset())
          }
        }
      }
    })
  }
}

object consumer extends App {
  val brokers = "wn0-hdikaf.ugupek1mbeiupbtqtqt0x5ruug.gx.internal.cloudapp.net:9092,wn1-hdikaf.ugupek1mbeiupbtqtqt0x5ruug.gx.internal.cloudapp.net:9092,wn2-hdikaf.ugupek1mbeiupbtqtqt0x5ruug.gx.internal.cloudapp.net:9092,wn3-hdikaf.ugupek1mbeiupbtqtqt0x5ruug.gx.internal.cloudapp.net:9092"
  val group = "group1"
  val topic= "vikramtopic"
  val example = new consumer(brokers, group, topic)
  example.run()
}
