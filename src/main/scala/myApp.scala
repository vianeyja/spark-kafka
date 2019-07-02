import java.util.{Date, Properties}

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.spark.{SparkConf, SparkContext}

object myApp extends App {


  val events = 10
  val topic = "vikramtopic"
  val brokers = "wn0-hdikaf.ugupek1mbeiupbtqtqt0x5ruug.gx.internal.cloudapp.net:9092,wn1-hdikaf.ugupek1mbeiupbtqtqt0x5ruug.gx.internal.cloudapp.net:9092,wn2-hdikaf.ugupek1mbeiupbtqtqt0x5ruug.gx.internal.cloudapp.net:9092,wn3-hdikaf.ugupek1mbeiupbtqtqt0x5ruug.gx.internal.cloudapp.net:9092"

  val conf = new SparkConf().setAppName("consumer")
  val sc = new SparkContext(conf)

  val props = new Properties()
  props.put("bootstrap.servers", brokers)
  props.put("client.id", "ScalaProducerExample")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")


  val producer = new KafkaProducer[String, String](props)
  val t = System.currentTimeMillis()
  for (nEvents <- Range(0, events)) {
    val runtime = new Date().getTime()
    val ip = "10.2.0.8:9092,10.2.0.9:9092,10.2.0.11:9092,10.2.0.15:9092"
    val msg = runtime + "," + nEvents + ",www.example.com," + ip
    val data = new ProducerRecord[String, String](topic, ip, msg)
    System.out.println("Running");
    //async
    //producer.send(data, (m,e) => {})
    //sync
    producer.send(data)
  }

  System.out.println("sent per second: " + events * 1000 / (System.currentTimeMillis() - t))
  producer.close()
}