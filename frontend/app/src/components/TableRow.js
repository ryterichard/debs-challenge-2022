import Action from './Action';

const TableRow = ({stock, index, setStock})=>{
    return (<tr key={stock.symbol} onClick={()=>setStock(index)}>
		  <td>{stock.symbol}</td>
	          <td>{stock.price.toFixed(2)}</td>
		  <Action action={stock.action}/>
	  </tr>);
}
export default TableRow;
