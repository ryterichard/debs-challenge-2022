const express = require('express');
const path = require('path');
const app = express(),
      bodyParser = require("body-parser");
const port = 3080;
const { Pool, Client } = require("pg")
const pool = new Pool({
    database: "qdb",
    host: "questdb",
    password: "quest",
    port: 8812,
    user: "admin",
})
// the pool will emit an error on behalf of any idle clients
// it contains if a backend error or network partition happens
pool.on('error', (err, client) => {
  console.error('Unexpected error on idle client', err)
  process.exit(-1)
})

const fetchQuery1 = async () => {
    //await client.connect()
    return await pool
	.connect()
	.then(client => {
	    return client
		.query('SELECT * FROM query1 LIMIT 5000;')
		.then(res => {
		    client.release()
		    return res;
		})
		.catch(err => {
		    client.release()
		    console.log(err.stack)
		    return null;
		})
	})
}



app.get('/db/', async (req, res) => {
    console.log('/db/ called!');
    // make db connection and request
    const response = await fetchQuery1().catch(console.error);
    const x = response.rows;
    console.log(x);
    // return db response
    res.json(x);
});

// app.post('/api/user', (req, res) => {
//   const user = req.body.user;
//   console.log('Adding user:::::', user);
//   users.push(user);
//   res.json("user addedd");
// });

app.use(express.static(path.join(__dirname, '../../app/build')));
app.get('/', (req,res) => {
    res.sendFile(path.join(__dirname, '../../app/build/index.html'));
});

app.listen(port, () => {
    console.log(`Server listening on the port::${port}`);
});
