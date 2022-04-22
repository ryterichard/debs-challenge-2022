import './Main.css';
import Table from './Table';
import Graph from './Graph';
import { useState } from "react";

let actions = ["Buy", "Sell", "Hold"];

let stocks = [
    { symbol: "AAPL", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "MSFT", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "GOOGL", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "AMZN", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "TSLA", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "BRK", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "FB", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "TSM", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "UNH", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "NVDA", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "JNJ", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "V", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "WMT", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "PG", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "JPM", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "XOM", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "MA", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "CVX", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "HD", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "BAC", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "KO", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "ABBV", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "PFE", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "LLY", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "COST", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "NVO", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "ASML", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "BHP", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "AVGO", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "BABA", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "PEP", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "TM", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "DIS", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "TMO", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "VZ", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "CSCO", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "MRK", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "ABT", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "SHEL", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
    { symbol: "CMCSA", price: Math.random()*300, action: actions[Math.floor(Math.random()*3)] },
]

const Main = ()=>{
    const [stock, setStock] = useState(2);

    //useEffect(() => {
	//if(stocks.length) document.querySelector(`tbody tr:nth-child(${stock+1})`).classList.add("makeTeal");
    //}, [stocks]);
    return (
	    <main>
	      <div className="tableWrapper disable-scrollbars">
	        <Table stocks={stocks} stock={stock} setStock={setStock}/>
	      </div>
	      <div className="graph">
                <Graph stock={stocks[stock].symbol}/> 
	      </div>
	    </main>
    );
}

export default Main;
