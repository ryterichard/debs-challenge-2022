const ip = require('ip')
const { Kafka, logLevel } = require('kafkajs')

const host = process.env.HOST_IP || ip.address()
const kafka = new Kafka({
  logLevel: logLevel.INFO,
  brokers: [`${host}:9092`],
  clientId: 'node-consumer',
})
const topic = 'input'
const producer = kafka.producer()

const run = async () => {
    console.log(`Connecting to ${host}:9092...`);
    await producer.connect()
    const prompt = require('prompt-sync')({sigint: true});

    while(true) {
	const message = prompt('> ');
	await producer.send({topic: topic, messages: [{value: message}]});
    }
    await producer.disconnect();
    
}


run().catch(e => console.error(`[console/producer] ${e.message}`, e))

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
