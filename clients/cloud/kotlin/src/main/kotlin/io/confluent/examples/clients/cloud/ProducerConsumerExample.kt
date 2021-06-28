/**
 * Copyright 2020 Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:JvmName("ProducerConsumerExample")

package io.confluent.examples.clients.cloud

import io.confluent.examples.clients.cloud.model.DataRecord
import io.confluent.examples.clients.cloud.util.loadConfig
import io.confluent.kafka.serializers.KafkaJsonDeserializer
import io.confluent.kafka.serializers.KafkaJsonDeserializerConfig.JSON_VALUE_TYPE
import io.confluent.kafka.serializers.KafkaJsonSerializer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import java.time.Duration.ofMillis
import java.util.*



fun main(args: Array<String>) {

  // Load properties from file
  val props = Properties(10)

  // Create topic if needed
  val topic = "CrossBoarderTesting"


  // Add additional properties.
  props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
  props[ACKS_CONFIG] = "all"
  props[KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.qualifiedName
  props[VALUE_SERIALIZER_CLASS_CONFIG] = KafkaJsonSerializer::class.qualifiedName
  props[KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
  props[VALUE_DESERIALIZER_CLASS_CONFIG] = KafkaJsonDeserializer::class.java.name
  props[JSON_VALUE_TYPE] = DataRecord::class.java
  props[GROUP_ID_CONFIG] = "kotlin_example_group_1"
  props[AUTO_OFFSET_RESET_CONFIG] = "earliest"

  // Produce sample data
  val numMessages = 10
  // `use` will execute block and close producer automatically  
  KafkaProducer<String, DataRecord>(props).use { producer ->
    repeat(numMessages) { _ ->
      val key = "alice"
      val record = DataRecord(System.currentTimeMillis())
      println("Producing record: $key\t$record")

      producer.send(ProducerRecord(topic, key, record)) { m: RecordMetadata, e: Exception? ->
        when (e) {
          // no exception, good to go!
          null -> println("Produced record to topic ${m.topic()} partition [${m.partition()}] @ offset ${m.offset()}")
          // print stacktrace in case of exception
          else -> e.printStackTrace()
        }
      }
    }

    producer.flush()
    println("$numMessages messages were produced to topic $topic")
  }

   val consumer = KafkaConsumer<String, DataRecord>(props).apply {
    subscribe(listOf(topic))
  }

  var totalCount = 0L

  consumer.use {
    while (true) {
      totalCount = consumer
          .poll(ofMillis(100))
          .fold(totalCount) { accumulator, record ->
            val newCount = accumulator + 1
            println("Consumed record with key ${record.key()} and value ${record.value()}, and updated total count to $newCount: Latency: ${System.currentTimeMillis() - record.value().count}")
            newCount
          }
    }
  }

}


