import './Header.css';

function Header(){
  return (
    <header>
	  <img src="/doge.png" alt="Doge dog logo"/>
	  <div className="header-text">
	      <h1>Doge Traders</h1>
	      <h2>DEBS Challenge 2022</h2>
	  </div>
    </header>
  );
}

export default Header;
