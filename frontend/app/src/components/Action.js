
const Action = ({action}) => {
    const a = action.trim().toLowerCase();
    if(a === "buy") {
	return (<td><span className="buy">Buy</span></td>);
    } else if (a === "sell") {
	return (<td><span className="sell">Sell</span></td>);
    } else {
	return (<td><span className="hold">Hold</span></td>);
    }
	
}

export default Action;
