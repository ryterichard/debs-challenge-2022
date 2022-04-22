import { useMemo, useState, useEffect } from 'react';
import uPlot from 'uplot';
import UPlotReact from 'uplot-react';
import 'uplot/dist/uPlot.min.css';
import './Graph.css';
const { spline } = uPlot.paths;

function paths(u, seriesIdx, idx0, idx1, extendGap, buildClip) {
    let s = u.series[seriesIdx];
    let style = s.drawStyle;
    let interp = s.lineInterpolation;
    let renderer =  spline();
    return renderer(u, seriesIdx, idx0, idx1, extendGap, buildClip);
}

const Graph = ({stock}) => {
  const graphContainer = document.querySelector('.graph');
  const [options, setOptions] = useState(
      {
          title: `EMA's for ${stock}`,
          width: window.innerWidth <= 801 ? window.innerWidth - 58: window.innerWidth * 0.6 - 20,
          height: window.innerWidth <= 801 ? window.innerHeight * 0.8 * 0.6 - 85 : window.innerHeight * 0.8 - 130,
        series: [
          {
              label: "Date",
          },

	  {
	      label: "EMA1",
	      stroke:            "blue",
	      fill:              "#0000FF" + "1A",
	      paths,
	  },
	  {
	      label: "EMA2",
	      stroke:            "red",
	      fill:              "#FF0000" + "1A",
	      paths,
	  },
        ],
	axes: [
	  {
	      label: "",
	      labelSize: 0,
	      stroke: "white"
	  },
	  {
	      labelSize: 5,
	      label: "",
	      stroke: "white",
	  }],
        plugins: [],
        //scales: { x: { time: false } }
      });
    const now = (new Date()/1000)-100000;
					    
    const initialState = useMemo(() => [
        [...new Array(10)].map((_, i) => now + i*100),
	[...new Array(10)].map((_, i) => Math.random()*300),
	[...new Array(10)].map((_, i) => Math.random()*300)
    ], [stock]);
    const [data, setData] = useState(initialState);
			 

    
    return (<UPlotReact
              key="hooks-key"
              options={options}
              data={data}
              target={graphContainer}
              onDelete={(/* chart: uPlot */) => console.log("Deleted from hooks")}
              onCreate={(/* chart: uPlot */) => console.log("Created from hooks")}
            />);
}

export default Graph;
