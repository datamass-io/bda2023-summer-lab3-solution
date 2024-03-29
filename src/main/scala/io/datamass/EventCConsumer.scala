package io.datamass

import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.concurrent.Future

// this code was written based on this:
// https://github.com/Azure/azure-event-hubs-for-kafka/tree/master/tutorials/akka/scala/consumer

object EventCConsumer extends App{
  implicit val system:ActorSystem = ActorSystem.apply("akka-stream-kafka")
  implicit val materializer:ActorMaterializer = ActorMaterializer()

  // grab our settings from the resources/application.conf file
  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  // our topic to subscribe to for messages
  val topic = "topicc"

  // listen to our topic with our settings, until the program is exited
  Consumer.plainSource(consumerSettings, Subscriptions.topics(topic))
    .mapAsync(1) ( msg => {
      // print out our message once it's received
      println(s"Message Received from TopicC : ${msg.timestamp} - ${msg.value}")

      Future.successful(msg)
    }).runWith(Sink.ignore)

}
