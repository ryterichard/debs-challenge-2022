// Our node kafka consumer... 
// For each message consumed, it writes it to the questdb database
const ip = require('ip')
const { Kafka, logLevel } = require('kafkajs')
const { Client } = require("pg") // for QuestDB

const host = process.env.HOST_IP || ip.address()

const kafka = new Kafka({
  logLevel: logLevel.INFO,
  // brokers: [`${host}:9092`],
  brokers: [`kafka:9093`],
  clientId: 'node-consumer',
})

const client1 = new Client({
    database: "qdb",
    host: "questdb",
    password: "quest",
    port: 8812,
    user: "admin",
})

const client2 = new Client({
    database: "qdb",
    host: "questdb",
    password: "quest",
    port: 8812,
    user: "admin",
})

const consumer = kafka.consumer({ groupId: 'kafka-node-consumer' })
const createQuery1Table = "CREATE TABLE IF NOT EXISTS query1 (ts TIMESTAMP, sym SYMBOL index, ema38 FLOAT, ema100 FLOAT) timestamp(ts) PARTITION BY DAY WITH maxUncommittedRows=250000;"


const run = async () =>
      {
	  await consumer.connect()
	  await consumer.subscribe({ topic: 'query1', fromBeginning: true })
	  await consumer.subscribe({ topic: 'query2', fromBeginning: true })
	  // Create database connections
	  await client1.connect()
	  await client2.connect()
	  await client1.query("create table if not exists query1 (ts timestamp, sym symbol index, ema38 float, ema100 float) timestamp(ts) partition by day with maxuncommittedrows=250000;")
	  console.log("table query1 created.")
	  await client2.query("create table if not exists query2 (ts timestamp, sym symbol index, action symbol) timestamp(ts) partition by day with maxuncommittedrows=250000;")
	  console.log("table query2 created.")
	  // this method is ran indefinitely...
	  await consumer.run({
	      eachMessage: async ({ topic, partition, message }) => {
		  if(topic=='query1') {
		      processQuery1(message)
		  } else {
		      processQuery2(message)
		  }
	      },
	  })
      }

const processQuery1 = async (message) => {
    //console.log(message.value.toString())
    const now = new Date().toISOString()
    const firstSplit = message.value.toString().replace(/(\r\n|\n|\r)/gm," ").replace(/\s+/g,' ').split(`"`)
    const secondSplit = firstSplit[2].split(" ")
    //console.log(`sending: ${[now, firstSplit[1], secondSplit[2], secondSplit[4]]}`)
    await client1.query(
     	"INSERT INTO query1 VALUES($1, $2, $3, $4);",
     	[now, firstSplit[1], secondSplit[2], secondSplit[4]],
    )
    await client1.query("COMMIT")
}

const processQuery2 = async (message) => {
    //console.log(`query2: ${message.value.toString()}`);
    const m = message.value.toString().split(" ")
    //console.log([new Date(m[2]*1000), m[0], m[1]])
    await client2.query("INSERT INTO query2 VALUES($1, $2, $3);", [new Date(m[2]*1000).toISOString(), m[0], m[1]])
    await client2.query("COMMIT")
}

run().catch(e => console.error(`[kafka-consumer] ${e.message}`, e))

const errorTypes = ['unhandledRejection', 'uncaughtException']
const signalTraps = ['SIGTERM', 'SIGINT', 'SIGUSR2']

errorTypes.forEach(type => {
  process.on(type, async e => {
    try {
      console.log(`process.on ${type}`)
      console.error(e)
      await consumer.disconnect()
      process.exit(0)
    } catch (_) {
      process.exit(1)
    }
  })
})

signalTraps.forEach(type => {
  process.once(type, async () => {
    try {
      await consumer.disconnect()
    } finally {
      process.kill(process.pid, type)
    }
  })
})
