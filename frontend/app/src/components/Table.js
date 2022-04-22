import "./Table.css";
import { useEffect } from "react";
import TableRow from './TableRow';

const Table = ({stocks, stock, setStock})=>{
    const handleClick = (i) => {
	document.querySelector(`tbody tr:nth-child(${stock+1})`).classList.remove("makeTeal");
	document.querySelector(`tbody tr:nth-child(${i+1})`).classList.add("makeTeal");
	setStock(i);
    }

    
    return (
	  <table>
	  <thead>
            <tr>
              <th><div>Stock</div></th>
              <th><div>$</div></th>
              <th><div>Action</div></th>
          </tr>
	  </thead>
	  <tbody>
	    {stocks.map((s,i) => <TableRow key={s.symbol} stock={s} index={i} setStock={handleClick}/>)}
	  </tbody>
          </table>
  );
}

export default Table;
