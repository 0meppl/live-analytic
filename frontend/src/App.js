import React, { useEffect, useState } from 'react';
import './App.css'; // Will create this next
import { Line } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

function App() {
  const [data, setData] = useState({
    labels: [],
    datasets: [
      {
        label: 'Live Data',
        data: [],
        fill: false,
        borderColor: 'rgb(75, 192, 192)',
        tension: 0.1,
      },
    ],
  });
  const [socket, setSocket] = useState(null);

  useEffect(() => {
    // WebSocket connection
    const ws = new WebSocket('ws://localhost:8080/data-stream'); // Assuming backend runs on 8080
    setSocket(ws);

    ws.onopen = () => {
      console.log('WebSocket connection established');
      ws.send('Hello from React client!');
    };

    ws.onmessage = (event) => {
      console.log('Received data:', event.data);
      try {
        const newDataPoint = JSON.parse(event.data); // Assuming data is {label: 'timestamp', value: YYY}

        setData((prevData) => {
          const newLabels = [...prevData.labels, newDataPoint.label];
          const newValues = [...prevData.datasets[0].data, newDataPoint.value];

          // Keep a fixed window of data points, e.g., last 20 points
          const maxDataPoints = 20;
          if (newLabels.length > maxDataPoints) {
            newLabels.shift();
            newValues.shift();
          }

          return {
            ...prevData,
            labels: newLabels,
            datasets: [
              {
                ...prevData.datasets[0],
                data: newValues,
              },
            ],
          };
        });
      } catch (error) {
        console.error('Error parsing or processing WebSocket message:', error);
        // Handle non-JSON messages or other data formats if necessary
        // For now, we'll just update the chart with the raw string if it's not JSON
         setData((prevData) => {
          const newLabels = [...prevData.labels, new Date().toLocaleTimeString()];
          const newValues = [...prevData.datasets[0].data, event.data]; // Treat as a string value if not JSON

          const maxDataPoints = 20;
          if (newLabels.length > maxDataPoints) {
            newLabels.shift();
            newValues.shift();
          }

          return {
            ...prevData,
            labels: newLabels,
            datasets: [
              {
                ...prevData.datasets[0],
                label: 'Raw Data Stream', // Update label if data type changes
                data: newValues,
              },
            ],
          };
        });
      }
    };

    ws.onerror = (error) => {
      console.error('WebSocket error:', error);
    };

    ws.onclose = () => {
      console.log('WebSocket connection closed');
    };

    // Cleanup function
    return () => {
      if (ws) {
        ws.close();
      }
    };
  }, []); // Empty dependency array means this effect runs once on mount

  const chartOptions = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top',
      },
      title: {
        display: true,
        text: 'Real-time Data Stream',
      },
    },
    scales: {
        x: {
            title: {
                display: true,
                text: 'Time'
            }
        },
        y: {
            title: {
                display: true,
                text: 'Value'
            }
        }
    }
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>Real-time Dashboard</h1>
      </header>
      <div className="chart-container">
        {data.labels.length > 0 ? (
          <Line data={data} options={chartOptions} />
        ) : (
          <p>Connecting to data stream and waiting for data...</p>
        )}
      </div>
    </div>
  );
}

export default App;
