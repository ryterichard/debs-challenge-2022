import './App.css';
import Header from './components/Header';
import Main from './components/Main';
import Footer from './components/Footer';
import { QueryClient, QueryClientProvider, useQuery } from 'react-query'

const queryClient = new QueryClient()
 
function App() {
  return (
	  <QueryClientProvider client={queryClient}>
	      <div className="App">
	          <Header/>
	          <Main/>
	          <Footer/>
	      </div>
	  </QueryClientProvider>
  );
}

export default App;
