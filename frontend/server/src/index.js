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

const fetchQuery = async (n, symbol) => {
    const select = `SELECT * FROM query${n} `;
    const where = symbol ? `WHERE sym='${symbol}' ` : ``;
    const query = select + where + 'LIMIT 5000;';
    console.log(`query: ${query}`);
    return await pool
	.connect()
	.then(client => {
	    return client
		.query(query)
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

app.get('/db/query1/', async (req, res) => {
    const response = await fetchQuery(1, null).catch(console.error);
    const x = response.rows;
    console.log(x);
    res.json(x);
});


app.get('/db/query2/', async (req, res) => {
    const response = await fetchQuery(2, null).catch(console.error);
    const x = response.rows;
    console.log(x);
    res.json(x);
});

app.get('/db/query1/:symbol', async (req, res) => {
    console.log(req.params.symbol);
    const response = await fetchQuery(1, req.params.symbol).catch(console.error);
    const x = response.rows;
    console.log(x);
    res.json(x);
});

app.use(express.static(path.join(__dirname, '../../app/build')));
app.get('/', (req,res) => {
    res.sendFile(path.join(__dirname, '../../app/build/index.html'));
});

app.listen(port, () => {
    console.log(`Server listening on the port::${port}`);
});
