"use strict"
// Our node kafka consumer... 
// For each message consumed, it writes it to the questdb database
const ip = require('ip')
const net = require("net")
const { Kafka, logLevel } = require('kafkajs')
const host = process.env.HOST_IP || ip.address()
const kafka = new Kafka({
  logLevel: logLevel.INFO,
  brokers: [`${host}:9092`],
  clientId: 'node-consumer',
})
const topic = 'query1'

const consumer = kafka.consumer({ groupId: 'kafka-node-consumer' })
let batch = []

const client = new net.Socket()

const HOST = "localhost"
const PORT = 9009

const consumeQuery1 = async () =>
      {
	  await consumer.connect()
	  await consumer.subscribe({ topic: 'query1', fromBeginning: true })

	  // this method is ran indefinitely...
	  await consumer.run({
	      eachMessage: async ({ topic, partition, message }) => {
		  processMessage(topic, partition, message)
	      },
	  })
      }

const processMessage = async (topic, partition, message) => {
    //const prefix = `${topic}[${partition} | ${message.offset}] / ${message.timestamp}`
    //console.log(`- ${prefix} "${message.value}"`)
    //console.log(message.value.toString())
    const now = new Date().toISOString()
    const firstSplit = message.value.toString().replace(/(\r\n|\n|\r)/gm," ").replace(/\s+/g,' ').split(`"`)
    const secondSplit = firstSplit[2].split(" ")
    console.log(`batched: ${[now, firstSplit[1], secondSplit[2], secondSplit[4]]}`)
    //console.log(`second split: ${secondSplit}`)
    batchSend(`query1,sym=${firstSplit[1]} ema38=${secondSplit[2]},ema100=${secondSplit[4]} ${now}`)
}

// const addToBatch = async (m) => {
//     if(batch.length == 100) {
// 	console.log("sending batch....")
// 	await batchSend();
//     }
//     batch.push(m);
// }


const batchSend = async (m) => {
    client.connect(PORT, HOST, () => {
	const rows = [m];

	function write(idx) {
	    if (idx === rows.length) {
		client.destroy()
		return
	    }

	    client.write(rows[idx] + "\n", (err) => {
		if (err) {
		    console.error(err)
		    process.exit(1)
		}
		write(++idx)
	    })
	}

	write(0)
	batch = [];
    })

    client.on("error", (err) => {
    console.error(err)
    process.exit(1)
  })

  client.on("close", () => {
    console.log("Connection closed")
  })
}


consumeQuery1().then(() => console.log("done consuming"))
